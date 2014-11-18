package info.zhegui.words;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class ActivityUpdateWord extends ActionBarActivity {
    private Spinner spinnerLesson, spinnerType;
    private EditText etKey, etContent;
    private Button btnAdd;

    private SharedPreferences prefs;

    private final int WHAT_SHOW_UPDATE_RESULT = 101, WHAT_SHOW_WORD = 102;

    private Word word;

    private DatabaseHelper mDatabaseHelper;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_SHOW_UPDATE_RESULT:
                    if (msg.arg1 > 0) {
                        Utils.toast(ActivityUpdateWord.this, "word updated successfully", Toast.LENGTH_SHORT);
                        setResult(RESULT_OK);
                    } else {
                        Utils.toast(ActivityUpdateWord.this, "word updated failed", Toast.LENGTH_SHORT);
                    }
                    finish();
                    break;
                case WHAT_SHOW_WORD:
                    if (word != null) {
                        spinnerLesson.setSelection(word.lesson-1);
                        spinnerType.setSelection(msg.arg1);
                        etKey.setText(word.key);
                        etContent.setText(word.content);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_word);

        prefs = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);

        spinnerLesson = (Spinner) findViewById(R.id.spinner_lesson);
        spinnerType = (Spinner) findViewById(R.id.spinner_type);
        etKey = (EditText) findViewById(R.id.et_key);
        etContent = (EditText) findViewById(R.id.et_content);
        btnAdd = (Button) findViewById(R.id.button);
        btnAdd.setText("edit");

        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(this,
                R.array.type, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);

        ArrayAdapter<CharSequence> adapterLesson = ArrayAdapter.createFromResource(this,
                R.array.lesson, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapterLesson.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLesson.setAdapter(adapterLesson);

        int lesson = prefs.getInt(Constants.PREFS.LESSON, 0);
        spinnerLesson.setSelection(lesson);
        spinnerLesson.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefs.edit().putInt(Constants.PREFS.LESSON, position).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String key = etKey.getText().toString();
                final String content = etContent.getText().toString();
                if (TextUtils.isEmpty(key)) {
                    Utils.toast(ActivityUpdateWord.this, "请输入生词", Toast.LENGTH_SHORT);
                    return;
                }

                new Thread() {
                    public void run() {
                        String type = spinnerType.getSelectedItem().toString();
                        log("type:" + type);
                        String lesson = spinnerLesson.getSelectedItem().toString();
                        log("lesson:" + lesson);
                        word.key= key;
                        word.content=content;
                        word.remember=false;
                        word.lesson= Integer.parseInt(lesson);
                        word.type=type;
                        DatabaseHelper dbHelper = new DatabaseHelper(ActivityUpdateWord.this);
                        long rows = dbHelper.update(word, word.id);

                        Message msg = mHandler.obtainMessage(WHAT_SHOW_UPDATE_RESULT, (int) rows, 0);
                        msg.sendToTarget();
                    }
                }.start();
            }
        });

        mDatabaseHelper = new DatabaseHelper(this);
        final String key = getIntent().getStringExtra("key");
        if (!TextUtils.isEmpty(key)) {
            new Thread() {
                public void run() {
                    word = mDatabaseHelper.query(key);
                    if (word != null) {
                        int pos = -1;
                        for (int i = 0; i < spinnerType.getChildCount(); i++) {
                            TextView tv = (TextView) spinnerType.getChildAt(i);
                            if (TextUtils.equals(tv.getText().toString(), word.type)) {
                                pos = i;
                                break;
                            }
                        }
                        Message msg = mHandler.obtainMessage(WHAT_SHOW_WORD, pos, -1);
                        msg.sendToTarget();
                    }
                }
            }.start();
        }
    }

    private void log(String msg) {
        Log.d("ActivityAddNewWord", msg);
    }
}
