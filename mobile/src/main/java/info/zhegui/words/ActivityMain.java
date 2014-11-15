package info.zhegui.words;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class ActivityMain extends Activity {

    private ArrayList<Word> listWord = new ArrayList<Word>();
    private ArrayList<String> listString = new ArrayList<String>();

    private final int REQUEST_WORD = 201;
    private final int WHAT_SHOW_WORDS = 101;

    private TextView tvFanyiTitle, tvFanyi;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;

    /**
     * 当前要展示的生词位置
     */
    private int currentPosition = 0;

    /**
     * 当前要翻译的内容
     */
    private String fanyiKey;

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
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadWords();

        tvFanyiTitle = (TextView) findViewById(R.id.tv_fanyi_title);
        tvFanyi = (TextView) findViewById(R.id.tv_fanyi);

        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, listString);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = listString.get(position);
                key = key.substring(key.indexOf(".") + 1);
                key = key.replaceAll("\\（[^}]*\\）", "");
                log("-->key:" + key);
                fanyiKey = key;
                tvFanyiTitle.setText(fanyiKey);
                new TaskFanyi().execute(key);
            }
        });

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition = 0;

                findNextForget();

                startActivityWord();
            }
        });

        findViewById(R.id.btn_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int index = 0; index < listWord.size(); index++) {
                    Word word = listWord.get(index);
                    word.remember = false;
                    listWord.set(index, word);
                }

                mHandler.sendEmptyMessage(WHAT_SHOW_WORDS);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_WORD) {
            if (resultCode == RESULT_OK) {
                log("-->remember");
                Word word = listWord.get(currentPosition);
                word.remember = true;
                listWord.set(currentPosition, word);
                mHandler.sendEmptyMessage(WHAT_SHOW_WORDS);
            } else {
                log("-->forget");

            }

            currentPosition++;
            findNextForget();

            startActivityWord();


        }
    }

    private void findNextForget() {
        while (currentPosition < listWord.size() && listWord.get(currentPosition).remember) {
            currentPosition++;
        }
    }

    private void startActivityWord() {
        if (currentPosition < listWord.size()) {
            String currentWord = listWord.get(currentPosition).key;
            log("---curent word:" + currentWord);
            Intent intent = new Intent(ActivityMain.this, ActivityWord.class);
            intent.putExtra("word", currentWord);
            startActivityForResult(intent, REQUEST_WORD);
            overridePendingTransition(0, 0);
        } else {
            toast("no more");
        }
    }

    private void loadWords() {
        new Thread() {
            public void run() {
                try {
//                    DatabaseHelper dbHelper=new DatabaseHelper(ActivityMain.this);
//                    InputStream is = ActivityMain.this.getResources().openRawResource(R.raw.words);
//                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
//                    String str = null;
//                    listWord.clear();
//                    while ((str = br.readLine()) != null) {
//                        log(str);
////                        listWord.add(new Word(str, false));
//                        String[] arr=str.split(",");
//                        dbHelper.insert(new Word(0,arr[0],false,0,4,arr[1]));
//                        SystemClock.sleep(10);
//                    }
//                    br.close();
//                    is.close();

                    DatabaseHelper dbHelper = new DatabaseHelper(ActivityMain.this);
                    SQLiteDatabase db = null;
                    Cursor cursor = null;
                    String sortOrder = DatabaseHelper.COL_ID + " DESC";

                    db = dbHelper.getWritableDatabase();
                    cursor = db.query(DatabaseHelper.TBL_NAME, null,
                            DatabaseHelper.COL_REMEMBER + "=? "
                            ,
                            new String[]{DatabaseHelper.FORGET + ""},
                            null, null, sortOrder);
                    listWord.clear();
                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(cursor
                                .getColumnIndexOrThrow(DatabaseHelper.COL_ID));
                        String key = cursor.getString(cursor
                                .getColumnIndexOrThrow(DatabaseHelper.COL_KEY));
                        int remember = cursor.getInt(cursor
                                .getColumnIndexOrThrow(DatabaseHelper.COL_REMEMBER));
                        int lesson = cursor.getInt(cursor
                                .getColumnIndexOrThrow(DatabaseHelper.COL_LESSON));
                        int times = cursor.getInt(cursor
                                .getColumnIndexOrThrow(DatabaseHelper.COL_TIMES));
                        String type = cursor.getString(cursor
                                .getColumnIndexOrThrow(DatabaseHelper.COL_TYPE));
                        Word word = new Word((int) id, key, remember == DatabaseHelper.REMEMBER ? true : false, times, lesson, type);
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

    class TaskFanyi extends AsyncTask<String, Integer, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            publishProgress(1);
            String str = params[0];
            try {
                str = URLEncoder.encode(params[0], "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            String url = "http://openapi.baidu.com/public/2.0/bmt/translate?client_id=" + Constants.BAIDU_FANYI.API_KEY + "&q=" + str + "&from=jp&to=zh";
            String result = Utils.doHttpGet(ActivityMain.this, url);
            log(result);
            JSONObject obj = null;
            try {
                obj = new JSONObject(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return obj;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            tvFanyi.setText("querying...");
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            String text = "error, try again";
            if (jsonObject != null) {
                try {
                    JSONObject obj = jsonObject.getJSONArray("trans_result").getJSONObject(0);
                    String src = obj.getString("src");
                    String dst = obj.getString("dst");
                    text = dst;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                tvFanyi.setText(text);
            } else {
                tvFanyi.setText(text);
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
