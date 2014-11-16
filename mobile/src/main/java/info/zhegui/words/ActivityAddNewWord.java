package info.zhegui.words;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class ActivityAddNewWord extends ActionBarActivity {
    private Spinner spinnerLesson, spinnerType;
    private EditText etKey;
    private Button btnAdd;

    private SharedPreferences prefs;

    private final int WHAT_SHOW_INSERT_RESULT = 101;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_SHOW_INSERT_RESULT:
                    if (msg.arg1 > 0) {
                        Utils.toast(ActivityAddNewWord.this, "new word added successfully", Toast.LENGTH_SHORT);
                    } else {
                        Utils.toast(ActivityAddNewWord.this, "new word added failed", Toast.LENGTH_SHORT);
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
        btnAdd = (Button) findViewById(R.id.button);

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
                if (TextUtils.isEmpty(key)) {
                    Utils.toast(ActivityAddNewWord.this, "请输入生词", Toast.LENGTH_SHORT);
                    return;
                }

                new Thread() {
                    public void run() {
                        String type = spinnerType.getSelectedItem().toString();
                        log("type:" + type);
                        String lesson = spinnerLesson.getSelectedItem().toString();
                        log("lesson:" + lesson);
                        Word word = new Word(0, key,"", false, 0, Integer.parseInt(lesson), type);
                        DatabaseHelper dbHelper = new DatabaseHelper(ActivityAddNewWord.this);
                        long rows = dbHelper.insert(word);

                        Message msg = mHandler.obtainMessage(WHAT_SHOW_INSERT_RESULT, (int) rows, 0);
                        msg.sendToTarget();
                    }
                }.start();
            }
        });
    }

    private void log(String msg) {
        Log.d("ActivityAddNewWord", msg);
    }
}
