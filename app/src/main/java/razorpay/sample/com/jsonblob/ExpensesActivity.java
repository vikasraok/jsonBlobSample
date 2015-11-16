package razorpay.sample.com.jsonblob;

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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import razorpay.sample.com.jsonblob.adapter.ExpensesAdapter;
import razorpay.sample.com.jsonblob.model.ExpensesModel;
import razorpay.sample.com.jsonblob.util.DataReceiver;
import razorpay.sample.com.jsonblob.util.PollService;

public class ExpensesActivity extends AppCompatActivity {

    private String TAG = ExpensesActivity.class.getSimpleName();
    public DataReceiver mReceiver;
    private Timer timer;
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
        mReceiver = new DataReceiver(new Handler());
        expenseList = (RecyclerView) findViewById(R.id.rv_expenses_list);
        expenseList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExpensesAdapter(mData);
        expenseList.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //setting to null to handle memory leaks
        mReceiver.setReceiver(null);
        stopTimer();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        jsonResponse = savedInstanceState.getString("list_data", null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("list_data", jsonResponse);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //callback function upon receiving response
        mReceiver.setReceiver(new DataReceiver.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == RESULT_OK) {

                    if (jsonResponse != resultData.getString("response")) {
                        jsonResponse = resultData.getString("response");
                        addData(jsonResponse);
                    }
                    Log.d(TAG, "response received");
                }
            }
        });
        startTimer();
        timer.schedule(timerTask, 100, 30 * 1000);
    }

    private void addData(String jsonResponse) {
        this.jsonResponse = jsonResponse;
        try {
            JSONArray expenseArray = new JSONArray(jsonResponse);
            ExpensesModel model = new ExpensesModel();
            for (int i = 0; i < expenseArray.length(); i++) {
                model.setDescription(expenseArray.getJSONObject(i).getString("description"));
                model.setAmount(String.valueOf(expenseArray.getJSONObject(i).getInt("amount")));
                model.setCategory(expenseArray.getJSONObject(i).getString("category"));
                String str = expenseArray.getJSONObject(i).getString("time");
                model.setDate(str.substring(0, str.indexOf("T")));
                model.setTime(str.substring(str.indexOf("T") + 1, str.indexOf(".")));
                model.setState(expenseArray.getJSONObject(i).getString("state"));
                mData.add(model);
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                        Intent poll = new Intent(getApplicationContext(), PollService.class);
                        //sending callback function to service
                        poll.putExtra("receiver", mReceiver);
                        startService(poll);
                    }
                });
            }
        };
    }
}
