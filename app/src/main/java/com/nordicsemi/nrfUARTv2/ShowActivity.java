package com.nordicsemi.nrfUARTv2;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class ShowActivity extends Activity implements Hooks.Rice {

    private boolean on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        ButterKnife.bind(this);

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
    public void vuto(String text, float[] valori, int length) {
        DataPoint[] points = new DataPoint[length];
        for (int i = 0; i < length; i++) {
            points[i] = new DataPoint(i, valori[i]);
        }

        GraphView graph = (GraphView) findViewById(R.id.graph);

        //Set manually the bounds because auto range is not so accurate
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(287);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(1);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(points);
        graph.addSeries(series);
    }

    @OnClick(R.id.edit_button)
    public void acquire(View view) {
        Hooks.acquire();
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
