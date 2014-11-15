package info.zhegui.words;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class ActivityMain extends ActionBarActivity {

    private ArrayList<Word> listWord = new ArrayList<Word>();
    private ArrayList<String> listString = new ArrayList<String>();

    private final int REQUEST_WORD = 201;
    private final int WHAT_SHOW_WORDS = 101;

    private ListView mListView;
    private ArrayAdapter<String> mAdapter;

    private int currentPosition = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_SHOW_WORDS:
                    listString.clear();
                    for (Word word : listWord) {
                        if (!word.remember)
                            listString.add(word.key);
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

        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, listString);
        mListView.setAdapter(mAdapter);

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWord();
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
            while (currentPosition < listWord.size() && listWord.get(currentPosition).remember) {
                currentPosition++;
            }
            if (currentPosition < listWord.size()) {
                startActivityWord();
            } else {
//                toast("no more");

            }

        }
    }

    private void startActivityWord() {
        if (currentPosition < listWord.size()) {
            String currentWord = listWord.get(currentPosition).key;
            log("---curent word:" + currentWord);
            Intent intent = new Intent(ActivityMain.this, ActivityWord.class);
            intent.putExtra("word", currentWord);
            startActivityForResult(intent, REQUEST_WORD);
            overridePendingTransition(R.anim.fade_in_slow, R.anim.fade_out_slow);
        }
    }

    private void loadWords() {
        new Thread() {
            public void run() {
                try {
                    InputStream is = ActivityMain.this.getResources().openRawResource(R.raw.words);
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String str = null;
                    listWord.clear();
                    ;
                    while ((str = br.readLine()) != null) {
                        log(str);
                        listWord.add(new Word(str, false));
                    }
                    br.close();
                    is.close();
                    mHandler.sendEmptyMessage(WHAT_SHOW_WORDS);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
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

    public class Word {
        public String key;
        public boolean remember;

        public Word(String key, boolean remember) {
            this.key = key;
            this.remember = remember;
        }
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

    private void log(String msg) {
        Log.d("ActivityMain", msg);
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
