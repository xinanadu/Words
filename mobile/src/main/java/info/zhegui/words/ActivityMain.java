package info.zhegui.words;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class ActivityMain extends ActionBarActivity {

    private ArrayList<String> listWordsTemp = new ArrayList<String>();
    private ArrayList<String> listWords = new ArrayList<String>();

    private final int WHAT_SHOW_WORDS = 101;

    private ListView mListView;
    private ArrayAdapter<String> mAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_SHOW_WORDS:
                    listWords.clear();
                    for (String word : listWordsTemp) {
                        listWords.add(word);
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
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, listWords);
        mListView.setAdapter(mAdapter);
    }

    private void loadWords() {
        new Thread() {
            public void run() {
                try {
                    InputStream is = ActivityMain.this.getResources().openRawResource(R.raw.words);
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String str = null;
                    listWordsTemp.clear();
                    ;
                    while ((str = br.readLine()) != null) {
                        log(str);
                        listWordsTemp.add(str);
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

    private void log(String msg) {
        Log.d("ActivityMain", msg);
    }
}
