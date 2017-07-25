package com.nordicsemi.nrfUARTv2;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ShowActivity extends Activity implements Hooks.Rice {

    private boolean on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        findViewById(R.id.graph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hooks.acquire();
            }
        });

        ((EditText)findViewById(R.id.textid)).setText(new SimpleDateFormat("yyyyMMdd", Locale.ITALY).format(new Date()));

        Hooks.setOnDisconnect(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (on)
                            onBackPressed();
                    }
                });
            }
        });

        Hooks.setOnReceived(this);
    }

    @Override
    protected void onResume() {
        on = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        on = false;
    }

    @Override
    public void vuto(String text, final float[] valori, final int length) {
        DataPoint[] points = new DataPoint[length];
        for (int i = 0; i < length; i++) {
            points[i] = new DataPoint(i, valori[i]);
            Log.v("TAG", "point" + valori[i]);
        }

        GraphView graph = (GraphView) findViewById(R.id.graph);

        //Set manually the bounds because auto range is not so accurate
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(length);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(256);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setGridColor(0xAAAAAAAA);
        graph.getGridLabelRenderer().setNumHorizontalLabels(20);
        graph.getGridLabelRenderer().setNumVerticalLabels(20);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        series.setThickness(2);
        graph.addSeries(series);

        findViewById(R.id.textid).setBackgroundColor(0xFFFFFFFF);
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder b = new StringBuilder("power\n");
                for (int i = 0; i < length; i++)
                    b.append(valori[i]).append("\n");
                try {
                    File folder = new File(Environment.getExternalStorageDirectory(), "PhysioREC/Spectrum/");
                    //noinspection ResultOfMethodCallIgnored
                    folder.mkdirs();
                    String id = ((EditText) findViewById(R.id.textid)).getText().toString();
                    File f;
                    int progressive = 0;
                    do {
                        f = new File(folder, String.format(Locale.ENGLISH, "%s_%05d.csv", id, progressive++));
                    } while (f.exists());
                    FileOutputStream y = new FileOutputStream(f, true);
                    y.write(b.toString().getBytes());
                    y.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.wtf("TAG", "Merda!!!");
                    findViewById(R.id.textid).setBackgroundColor(0xFFFF0000);
                }

            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
