package razorpay.sample.com.jsonblob;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import razorpay.sample.com.jsonblob.adapter.ExpensesAdapter;
import razorpay.sample.com.jsonblob.model.ExpensesModel;
import razorpay.sample.com.jsonblob.util.DataBridge;
import razorpay.sample.com.jsonblob.util.GetService;

public class ExpensesActivity extends AppCompatActivity {

    private String TAG = ExpensesActivity.class.getSimpleName();
    public DataBridge getBridge, putBridge;
    private Timer timer;
    private ProgressDialog pDialog;
    private TimerTask timerTask;
    final Handler handler = new Handler();
    private ArrayList<ExpensesModel> mData;
    private ExpensesAdapter adapter;
    private String jsonResponse;
    private RecyclerView expenseList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        mData = new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getBridge = new DataBridge(new Handler());
        putBridge = new DataBridge(new Handler());
        expenseList = (RecyclerView) findViewById(R.id.rv_expenses_list);
        expenseList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExpensesAdapter(mData, getApplicationContext(), putBridge);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //setting to null to handle memory leaks
        getBridge.setReceiver(null);
        putBridge.setReceiver(null);
        stopTimer();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        jsonResponse = savedInstanceState.getString("list_data", "");
        if (mData.size() == 0 && !jsonResponse.isEmpty())
            createList(jsonResponse);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("list_data", jsonResponse);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (jsonResponse == null)
            showDialog();
        //callback function upon receiving response
        getBridge.setReceiver(new DataBridge.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == RESULT_OK) {
                    if (jsonResponse == null) {
                        jsonResponse = resultData.getString("response");
                        createList(jsonResponse);
                    }
                    Log.d(TAG, "response received");
                    hideDialog();
                }
            }
        });
        //start poll timer to pull data every 30 second after the 1/10th of a second
        startTimer();
        timer.schedule(timerTask, 100, 30 * 1000);
        //callback function upon receiving response from

    }

    private void createList(String jsonResponse) {
        this.jsonResponse = jsonResponse;
        mData = new Gson().fromJson(jsonResponse, new TypeToken<ArrayList<ExpensesModel>>() {
        }.getType());
        adapter = new ExpensesAdapter(mData, getApplicationContext(), putBridge);
        expenseList.setAdapter(adapter);

    }

    public void startTimer() {
        timer = new Timer();
        startPoll();
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void startPoll() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent poll = new Intent(getApplicationContext(), GetService.class);
                        //sending callback function to service
                        poll.putExtra("receiver", getBridge);
                        startService(poll);
                    }
                });
            }
        };
    }

    public void showDialog() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
    }

    public void hideDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
