package com.nordicsemi.nrfUARTv2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;

public class Hooks {

    private final static String ACQUIRE = "R";
    @SuppressLint("StaticFieldLeak")
    private static MainActivity activity;
    private static Runnable onDisconnect;
    private static Rice onReceived;

    public static void received(String text) {
        if (text.contains("[") && text.contains("]")) {
            String[] t = text.substring(text.indexOf('['), text.indexOf(']')).split(" ");
            float[] brusciutto = new float[512];
            if (t.length < 512) {
                for (int i = 0; i < t.length; i++) {
                    brusciutto[i] = Integer.parseInt(t[i].toUpperCase(), 16);
                }

                if (onReceived != null)
                    onReceived.vuto(text, brusciutto, t.length);
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
            activity.send(ACQUIRE);
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

    interface Rice {
        void vuto(String text, float[] valori, int length);
    }
}
