package com.nordicsemi.nrfUARTv2;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ShowActivity extends Activity implements Hooks.Ricevuto {

    private boolean on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        findViewById(R.id.acqloop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hooks.acquire(false);
            }
        });

        ((ToggleButton)findViewById(R.id.acqloop)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Hooks.acquire(b);
            }
        });

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
    public void ricevuto(final String text, final int length) {
        findViewById(R.id.textid).setBackgroundColor(0xFFFFFFFF);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File folder = new File(Environment.getExternalStorageDirectory(), "PhysioREC/");
                    //noinspection ResultOfMethodCallIgnored
                    folder.mkdirs();
                    String id = new SimpleDateFormat("yyyyMMdd", Locale.ITALY).format(new Date()).toString() + '_';
                    id += ((EditText) findViewById(R.id.textid)).getText().toString();
                    File f = new File(folder, String.format(Locale.ENGLISH, "%s_rilevazioni.csv", id));
                    FileOutputStream y = new FileOutputStream(f, true);
                    String s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()) +
                            ", " +
                            text.replace(",", "\\s") +
                            "\n";
                    y.write(s.getBytes());
                    y.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.wtf("TAG", "Problema!!! 445");
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
