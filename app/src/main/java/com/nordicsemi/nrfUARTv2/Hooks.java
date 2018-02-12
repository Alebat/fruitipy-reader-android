package com.nordicsemi.nrfUARTv2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;

public class Hooks {

    private final static String ACQUIRE = "MISURA SINGOLA";
    private final static String ACQUIRE_LOOP = "MISURA CONTINUA";
    @SuppressLint("StaticFieldLeak")
    private static MainActivity activity;
    private static Runnable onDisconnect;
    private static Ricevuto onReceived;
    private static final boolean test = true;

    public static void received(String text) {
        complete(text);
    }

    public static void complete(String text) {
        Log.i("TAG", "received " + text);
        if (onReceived != null) {
            onReceived.ricevuto(text, text.length());
        }
    }

    public static void connected(final MainActivity activity) {
        Hooks.activity = activity;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.startActivity(new Intent(activity, ShowActivity.class));
            }
        });
    }

    public static void disconnected(MainActivity mainActivity) {
        if (mainActivity == activity) {
            if (onDisconnect != null)
                onDisconnect.run();
        } else
            Log.wtf("TAG", "Problema!!! 253");
    }

    public static void acquire(boolean loop) {
        if (activity != null) {
            if (!test) {
                activity.send(loop ? ACQUIRE_LOOP : ACQUIRE);
            }
            else
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent i = new Intent(UartService.ACTION_DATA_AVAILABLE);
                        i.putExtra(UartService.EXTRA_DATA, "Rlevazione test,\n1234567890123".getBytes());
                        activity.UARTStatusChangeReceiver.onReceive(activity, i);
                    }
                }).start();
        }
    }

    public static void onDestroy() {
        activity = null;
    }

    public static void setOnDisconnect(Runnable callback) {
        Hooks.onDisconnect = callback;
    }

    public static void setOnReceived(Ricevuto callback) {
        Hooks.onReceived = callback;
    }

    interface Ricevuto {
        void ricevuto(String text, int length);
    }
}
