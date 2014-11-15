package info.zhegui.words;

import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;


public class ActivityWord extends ActionBarActivity {

    private GestureDetectorCompat mDetector;

    private CountDownTimer mCountDownTimer;

    private final int WHAT_SHOW_COUNTDOWN = 101;

    private TextView tvCountDown;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_SHOW_COUNTDOWN:
                    int countDown = msg.arg1;
                    tvCountDown.setText("Fade out in " + countDown + " seconds");
                    break;
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

        tvCountDown = (TextView) findViewById(R.id.tv_count_down);

        String word = getIntent().getStringExtra("word");
        TextView tvWord = (TextView) findViewById(R.id.tv_word);
        tvWord.setText(word);

        LinearLayout layoutWordContainer = (LinearLayout) findViewById(R.id.layout_word_container);
        int gravity = Gravity.TOP;
        Random random=new Random();
        int randomInt=random.nextInt(9);
        if (randomInt<3) {
            gravity=Gravity.BOTTOM;
        } else if(randomInt>5){
            gravity=Gravity.TOP;
        } else {
            gravity=Gravity.CENTER;
        }
        layoutWordContainer.setGravity(gravity);

        findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(RESULT_OK);
            }
        });
        findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(RESULT_CANCELED);
            }
        });

        mCountDownTimer = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int countDown = (int) (millisUntilFinished + 500) / 1000;
                Message msg = mHandler.obtainMessage(WHAT_SHOW_COUNTDOWN, countDown, 0);
                msg.sendToTarget();
            }

            @Override
            public void onFinish() {
                int countDown = 0;
                Message msg = mHandler.obtainMessage(WHAT_SHOW_COUNTDOWN, countDown, 0);
                msg.sendToTarget();
                finish(RESULT_CANCELED);
            }
        };
        mCountDownTimer.start();
    }

    private void finish(int result) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            setResult(result);
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.fade_in_slow, R.anim.fade_out_slow);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";


        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            log("onFling: " + event1.toString() + event2.toString());

            log("event2.getY()-event1.getY():" + (event2.getY() - event1.getY()));
            final int EX = 20;
            if (event2.getY() - event1.getY() < -1 * EX) {
                //向上滑动，表示否
                log("up");
                finish(RESULT_CANCELED);
            } else if (event2.getY() - event1.getY() > 1 * EX) {
                //向下，表示是
                log("down");
                finish(RESULT_OK);
            }
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_word, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void log(String msg) {
        Log.d("ActivityWord", msg);
    }
}
