package info.zhegui.words;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


public class ActivitySettings extends ActionBarActivity {

    private LinearLayout containerScale;
    private SeekBar seekBar;

    private SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs=getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);

        containerScale = (LinearLayout) findViewById(R.id.container_scale);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        int duration= prefs.getInt(Constants.PREFS.DURATION, Constants.PREFS.DEFAULT_DURATION);
        seekBar.setProgress(duration-1);
        updateScale();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                log("onProgressChanged(" + progress + "," + fromUser + ")");
                prefs.edit().putInt(Constants.PREFS.DURATION, progress+1).commit();
                updateScale();
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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

    private void log(String msg) {
        Log.d("ActivitySettings", msg);
    }
}
