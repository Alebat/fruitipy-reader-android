package com.nordicsemi.nrfUARTv2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;

public class Hooks {

    private final static String ACQUIRE = "R";
    private final static String INFO = "?";
    private final static int LENGTH_WHITE = 256;
    private final static int LENGTH_IR = 288;
    @SuppressLint("StaticFieldLeak")
    private static MainActivity activity;
    private static Runnable onDisconnect;
    private static Rice onReceived;
    private static Pro onProgress;
    private static final boolean test = false;
    private static StringBuilder result = null;
    private static final int DIM_PACKETS = 5;
    private static int received = 0;

    public static void received(String text) {
        if (text.startsWith("[")) {
            if (result != null)
                Log.e("WASTED", result.toString());
            result = new StringBuilder();
            result.append(text);
            received = 0;
        } else if (text.endsWith("]")) {
            result.append(text);
            complete(result.toString());
            received = LENGTH_IR / DIM_PACKETS;
        } else {
            result.append(text);
        }
        received++;
        onProgress.gresso(received, LENGTH_IR / DIM_PACKETS);
    }

    public static void complete(String text) {
        Log.i("TAG", "received " + text);
        if (text.contains("[") && text.contains("]")) {
            String[] t = text.substring(text.indexOf('[') + 3, text.indexOf(']')).split(" ");
            int[] brusciutto = new int[512];
            if (t.length < 512) {
                for (int i = 0; i < t.length; i++) {
                    brusciutto[i] = Integer.parseInt(t[i].toUpperCase(), 16);
                }
                char code = t.length == LENGTH_IR ? 'I' : t.length == LENGTH_WHITE ? 'W' : 'X';
                if (onReceived != null) {
                    onReceived.vuto(text, code, brusciutto, t.length);
                }
            } else
                Log.w("TAG", "Merda!!");
        } else
            Log.i("TAG", "Merda!");
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
            Log.wtf("TAG", "Merda!!!");
    }

    public static void acquire() {
        if (activity != null) {
            if (!test)
                activity.send(ACQUIRE);
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
                        i.putExtra(UartService.EXTRA_DATA, "[bc f2 e5 46 ae 89 e7 7d 53 bc 35 fd 78 ab 5f e8 54 cf 6b 23 fd 46 ef 8e a8 e5 f6 df ca dc 73 c4 bc f2 e5 46 ae 89 e7 7d 53 bc 35 fd 78 ab 5f e8 54 cf 6b 23 fd 46 ef 8e a8 e5 f6 df ca dc 73 c4]".getBytes());
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

    public static void setOnReceived(Rice callback) {
        Hooks.onReceived = callback;
    }

    public static void setOnProgress(Pro callback) {
        Hooks.onProgress = callback;
    }

    interface Rice {
        void vuto(String text, char code, int[] valori, int length);
    }

    interface Pro {
        void gresso(int num, int outof);
    }
}
