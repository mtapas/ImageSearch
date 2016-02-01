package com.imagesearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ListItemHolder> listData;
    private SearchListAdapter searchListAdapter;
    private EditText editText;
    private Timer timer;
    private TextWatcher textChangedListener;
    private final int DELAY = 1000;
    private WebServiceCall webServiceCall = null;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        showProgressbar(false);

        editText = (EditText) findViewById(R.id.editText);
        final ListView listView = (ListView) findViewById(R.id.searchresult);
        listData = new ArrayList<ListItemHolder>();
        searchListAdapter = new SearchListAdapter(this);
        listView.setAdapter(searchListAdapter);

        textChangedListener = new TextWatcher() {
            @Override
            public void afterTextChanged(final Editable arg0) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!arg0.toString().equals("")) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showProgressbar(true);
                                    updateListData(arg0.toString());
                                }
                            });

                        }
                    }
                }, DELAY);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null) {
                    timer.cancel();
                }
            }
        };

        editText.addTextChangedListener(textChangedListener);

    }

    public SearchListAdapter getSearchListAdapter() {
        return searchListAdapter;
    }

    private void updateListData(String gpssearch) {
        if (webServiceCall != null && !webServiceCall.isCancelled()) {
            webServiceCall.cancel(true);
        }
        webServiceCall = new WebServiceCall(this);
        webServiceCall.execute(gpssearch);

    }

    public void showProgressbar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
