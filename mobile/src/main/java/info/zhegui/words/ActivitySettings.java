package info.zhegui.words;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;


public class ActivitySettings extends ActionBarActivity {

    private LinearLayout containerScale;
    private SeekBar seekBar;

    private SharedPreferences prefs;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);

        containerScale = (LinearLayout) findViewById(R.id.container_scale);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        int duration = prefs.getInt(Constants.PREFS.DURATION, Constants.PREFS.DEFAULT_DURATION);
        seekBar.setProgress(duration - 1);
        updateScale();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                log("onProgressChanged(" + progress + "," + fromUser + ")");
                prefs.edit().putInt(Constants.PREFS.DURATION, progress + 1).commit();
                updateScale();
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        findViewById(R.id.btn_export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TaskExport().execute();
            }
        });
    }

    private void updateScale() {
        for (int index = 0; index < containerScale.getChildCount(); index++) {
            TextView tv = (TextView) containerScale.getChildAt(index);
            if (index == seekBar.getProgress())
                tv.setTextColor(Color.RED);
            else
                tv.setTextColor(Color.BLACK);
        }
    }


    class TaskExport extends AsyncTask<Void, Integer, Long> {

        @Override
        protected Long doInBackground(Void... params) {
            publishProgress(1);
            long size = 0;
            try {
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();

                if (sd.canWrite()) {
                    String currentDBPath = "//data//" + getPackageName() + "//databases//" + DatabaseHelper.DB_NAME;
                    String backupDBPath = "words";
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);

                    if (currentDB.exists()) {
                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        size = dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                        log("transfered " + size + " bytes");
                    } else {
                        log("db not found");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return size;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            mProgressDialog = ProgressDialog.show(ActivitySettings.this, "exporting, please wait...", null, false, false, null);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            String text = "export error";
            if (aLong > 0) text = "database exported successfully!";
            Utils.toast(ActivitySettings.this, text, Toast.LENGTH_SHORT);
        }
    }

    private void log(String msg) {
        Log.d("ActivitySettings", msg);
    }
}
