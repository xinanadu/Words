package info.zhegui.words;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;


public class ActivityMain extends ActionBarActivity {

    private ArrayList<Word> listWord = new ArrayList<Word>();
    private ArrayList<String> listString = new ArrayList<String>();

    private Spinner spinnerLesson;

    private SharedPreferences prefs;

    private final int REQUEST_VIEW = 201, REQUEST_UPDATE=202;
    private final int WHAT_SHOW_WORDS = 101;

    private TextView tvFanyiTitle, tvFanyi,tvNoRecord;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;

    private int state = Constants.STATE.STOPPED;

    /**
     * 当前要展示的生词位置
     */
    private int currentPosition = 0;

    /**
     * 当前要翻译的内容
     */
    private String fanyiKey;

    private final String POSITION = "position";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_SHOW_WORDS:
                    listString.clear();
                    int count = 0;
                    for (Word word : listWord) {
                        if (!word.remember) {
                            listString.add((++count) + ". " + word.key);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    if(listString.size()==0){
                        tvNoRecord.setVisibility(View.VISIBLE);
                    }else{
                        tvNoRecord.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate(" + savedInstanceState + ")");
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
        currentPosition = prefs.getInt(POSITION, 0);
        log("currentPosition:" + currentPosition);

        int startCount = prefs.getInt(Constants.PREFS.START_COUNT, 0);
        prefs.edit().putInt(Constants.PREFS.START_COUNT, startCount + 1).commit();


        tvFanyiTitle = (TextView) findViewById(R.id.tv_fanyi_title);
        tvFanyi = (TextView) findViewById(R.id.tv_fanyi);
        tvNoRecord = (TextView) findViewById(R.id.tv_no_record);

        spinnerLesson = (Spinner) findViewById(R.id.spinner_lesson);

        ArrayAdapter<CharSequence> adapterLesson = ArrayAdapter.createFromResource(this,
                R.array.lesson, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapterLesson.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLesson.setAdapter(adapterLesson);
        spinnerLesson.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                loadWords();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        int lesson = prefs.getInt(Constants.PREFS.LESSON, 0);
        spinnerLesson.setSelection(lesson);


        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, listString);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = listString.get(position);
                key = key.substring(key.indexOf(". ") + 2);
                fanyiKey = key;
                key = key.replaceAll("\\（[^}]*\\）", ""); //日文括号
                key = key.replaceAll("\\([^}]*\\)", "");//英文括号
                log("-->key:" + key);
                tvFanyiTitle.setText(fanyiKey);
                new TaskFanyi().execute(key);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(ActivityMain.this, ActivityUpdateWord.class);
                intent.putExtra("key",listWord.get(position).key);
                startActivityForResult(intent, REQUEST_UPDATE);
                return true;
            }
        });

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state != Constants.STATE.PAUSED) {
                    //未暂停，从0开始
                    currentPosition = 0;

                    Collections.shuffle(listWord);
                }

                state = Constants.STATE.RUNNING;

                findNextForget();

                startActivityWord();
            }
        });

        findViewById(R.id.btn_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        String lesson = spinnerLesson.getSelectedItem().toString();
                        DatabaseHelper dbHelper = new DatabaseHelper(ActivityMain.this);
                        dbHelper.reset(Integer.parseInt(lesson));

                        loadWords();
                    }
                }.start();
            }
        });


//        loadWords();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIEW) {
            if (resultCode == RESULT_OK) {
                log("-->remember");
                Word word = listWord.get(currentPosition);
                word.remember = true;
                listWord.set(currentPosition, word);
                mHandler.sendEmptyMessage(WHAT_SHOW_WORDS);

                updateRemember(word, true);
                currentPosition++;
            } else if (resultCode == RESULT_CANCELED) {
                log("-->forget");
                currentPosition++;
            } else if (resultCode == Constants.ACTIVITY_RESULT.PAUSE) {
                log("-->pause");
                state = Constants.STATE.PAUSED;
            } else if (resultCode == Constants.ACTIVITY_RESULT.STOP) {
                log("-->stop");
                state = Constants.STATE.STOPPED;
                currentPosition = 0;
            }

            findNextForget();

            startActivityWord();


        }else if(requestCode==REQUEST_UPDATE){
            if (resultCode == RESULT_OK) {
                //更新listWord
                loadWords();
            }
        }
    }

    private void findNextForget() {
        while (state == Constants.STATE.RUNNING && currentPosition < listWord.size() && listWord.get(currentPosition).remember) {
            currentPosition++;
        }
    }

    private void startActivityWord() {
        if (state == Constants.STATE.RUNNING) {
            if (currentPosition < listWord.size()) {
                String currentWord = listWord.get(currentPosition).key;
                log("---curent word:" + currentWord);
                Intent intent = new Intent(ActivityMain.this, ActivityWord.class);
                intent.putExtra("word", currentWord);
                startActivityForResult(intent, REQUEST_VIEW);
                overridePendingTransition(0, 0);

                updateTimes(currentWord);
            } else {
                toast("no more");
            }
        } else if (state == Constants.STATE.PAUSED) {
            toast("paused");
        } else if (state == Constants.STATE.STOPPED) {
            toast("stopped");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        log("onSaveInstanceState()");

        outState.putInt(POSITION, currentPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreatePanelView(int featureId) {
        return super.onCreatePanelView(featureId);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        log("onRestoreInstanceState(" + savedInstanceState + ")");

        currentPosition = savedInstanceState.getInt(POSITION);
        state = Constants.STATE.PAUSED;

        log("currentPosition:" + currentPosition);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("onDestroy()");
        log("currentPosition:" + currentPosition);
        prefs.edit().putInt(POSITION, currentPosition).commit();
    }

    private void updateRemember(final Word word, final boolean remember) {
        new Thread() {
            public void run() {
                DatabaseHelper dbHelper = new DatabaseHelper(ActivityMain.this);
                word.remember = remember;
                dbHelper.update(word, word.id);
            }
        }.start();
    }

    private void updateTimes(final String currentWord) {
        new Thread() {
            public void run() {
                DatabaseHelper dbHelper = new DatabaseHelper(ActivityMain.this);
                Word word = dbHelper.query(currentWord);
                word.times = word.times + 1;
                dbHelper.update(word, word.id);
            }
        }.start();
    }

    private void loadWords() {
        final String DATABASE_INITAILIZED = "database_initialized";
        new Thread() {
            public void run() {
                try {
                    boolean databaseInitialized = prefs.getBoolean(DATABASE_INITAILIZED, false);
                    log("databaseInitialized:" + databaseInitialized);
                    if (!databaseInitialized) {
                        DatabaseHelper dbHelper = new DatabaseHelper(ActivityMain.this);

                        AssetManager assets = ActivityMain.this.getAssets();
                        String fileNameArray[]=assets.list("");
                        for(String fileName:fileNameArray) {
                            if(fileName!=null && fileName.startsWith("lesson")) {
                                log("--->"+fileName);
                                String lessonStr=fileName.replace("lesson","");
                                lessonStr=lessonStr.replace(".txt","");
                                int lesson=Integer.parseInt(lessonStr);
                                InputStream is =assets.open(fileName);
                                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                                String str = null;
                                listWord.clear();
                                while ((str = br.readLine()) != null) {
                                    log(str);
//                        listWord.add(new Word(str, false));
                                    String[] arr = str.split(",");
                                    dbHelper.insert(new Word(0, arr[0], "", false, 0, lesson, arr[1]));
                                    SystemClock.sleep(5);
                                }
                                br.close();
                                is.close();
                            }
                        }

                        prefs.edit().putBoolean(DATABASE_INITAILIZED, true).commit();
                    }

                    String lesson = spinnerLesson.getSelectedItem().toString();

                    DatabaseHelper dbHelper = new DatabaseHelper(ActivityMain.this);
                    SQLiteDatabase db = null;
                    Cursor cursor = null;
                    String sortOrder = DatabaseHelper.COL_ID + " DESC";

                    db = dbHelper.getWritableDatabase();
                    cursor = db.query(DatabaseHelper.TBL_NAME, null,
                            DatabaseHelper.COL_REMEMBER + "=? and " +
                                    DatabaseHelper.COL_LESSON + " =? ",
                            new String[]{DatabaseHelper.FORGET + "", lesson},
                            null, null, sortOrder);
                    listWord.clear();
                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(cursor
                                .getColumnIndexOrThrow(DatabaseHelper.COL_ID));
                        String key = cursor.getString(cursor
                                .getColumnIndexOrThrow(DatabaseHelper.COL_KEY));
                        String content = cursor.getString(cursor
                                .getColumnIndexOrThrow(DatabaseHelper.COL_CONTENT));
                        int remember = cursor.getInt(cursor
                                .getColumnIndexOrThrow(DatabaseHelper.COL_REMEMBER));
                        int times = cursor.getInt(cursor
                                .getColumnIndexOrThrow(DatabaseHelper.COL_TIMES));
                        String type = cursor.getString(cursor
                                .getColumnIndexOrThrow(DatabaseHelper.COL_TYPE));
                        Word word = new Word((int) id, key, content, remember == DatabaseHelper.REMEMBER ? true : false, times, Integer.parseInt(lesson), type);
                        listWord.add(word);
                    }
                    if (cursor != null) {
                        cursor.close();
                        cursor = null;
                    }
                    if (db != null) {
                        db.close();
                        db.close();
                    }


                    Collections.shuffle(listWord);

                    mHandler.sendEmptyMessage(WHAT_SHOW_WORDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, ActivitySettings.class));
            return true;
        } else if (id == R.id.action_add_new_word) {
            startActivity(new Intent(this, ActivityAddNewWord.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    static class ViewHolder {
        TextView tvKey;
    }

    public class WordAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return listString.size();
        }

        @Override
        public Object getItem(int position) {
            return listString.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                holder.tvKey = (TextView) LayoutInflater.from(ActivityMain.this).inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvKey.setText(listString.get(position));
            return holder.tvKey;
        }
    }

    class TaskFanyi extends AsyncTask<String, Integer, Word> {
        @Override
        protected Word doInBackground(String... params) {
            publishProgress(1);
            String str = params[0];
            Word word = null;
            int index = -1;
            for (int i = 0; i < listWord.size(); i++) {
                Word wordTemp = listWord.get(i);
                if (TextUtils.equals(fanyiKey, wordTemp.key)) {
                    index = i;
                    word = wordTemp;
                    break;
                }
            }
            final int indexFinal = index;

            final Word wordFinal = word;
            if (wordFinal == null || TextUtils.isEmpty(wordFinal.content)) {
                try {
                    str = URLEncoder.encode(str, "utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String url = "http://openapi.baidu.com/public/2.0/bmt/translate?client_id=" + Constants.BAIDU_FANYI.API_KEY + "&q=" + str + "&from=jp&to=zh";
                String result = Utils.doHttpGet(ActivityMain.this, url);
                log(result);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String text = "error, try again";
                if (jsonObject != null) {
                    log("online content");
                    try {
                        JSONObject obj = jsonObject.getJSONArray("trans_result").getJSONObject(0);
                        final String src = obj.getString("src");
                        final String dst = obj.getString("dst");
                        text = dst;

                        if (wordFinal != null) {
                            wordFinal.content = dst;
                            new Thread() {
                                public void run() {

                                    DatabaseHelper dbHelper = new DatabaseHelper(ActivityMain.this);
                                    dbHelper.update(wordFinal, wordFinal.id);

                                    listWord.set(indexFinal, wordFinal);
                                }
                            }.start();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                log("local content");
            }


            return wordFinal;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            tvFanyi.setText("querying...");
        }

        @Override
        protected void onPostExecute(Word word) {
            if (word != null) {
                tvFanyi.setText("[" + word.type + "] " + word.content);
            }
        }
    }


    private void log(String msg) {
        Log.d("ActivityMain", msg);
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
