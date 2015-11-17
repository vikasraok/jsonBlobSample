package razorpay.sample.com.jsonblob;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

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
    private boolean dataManipulated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        //initialise data
        mData = new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // want to do something with this shall think on it and show it in the next round if I get selected
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        //receiver to receive get request data
        getBridge = new DataBridge(new Handler());
        //receiver to receive put request data
        putBridge = new DataBridge(new Handler());
        //setting recycler view
        expenseList = (RecyclerView) findViewById(R.id.rv_expenses_list);
        expenseList.setLayoutManager(new LinearLayoutManager(this));
        //adapter is loaded with empty data here
        adapter = new ExpensesAdapter(mData, ExpensesActivity.this, putBridge);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //setting to null to handle memory leaks
        getBridge.setReceiver(null);
        putBridge.setReceiver(null);
        //stopping timer activity as there is  no need for it
        stopTimer();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //data is restored and list if created on rotation and resume
        jsonResponse = savedInstanceState.getString("list_data", "");
        if (mData.size() == 0 && !jsonResponse.isEmpty())
            createList(jsonResponse);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //data is saved on rotation and pause
        outState.putString("list_data", jsonResponse);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (jsonResponse == null)
            showDialog();
        //callback function upon receiving get response
        getBridge.setReceiver(new DataBridge.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == RESULT_OK) {
                    if (jsonResponse == null) {
                        //list is populated when the string is null meaning it has run for the first time
                        jsonResponse = resultData.getString("response");
                        createList(jsonResponse);
                    } else {
                        jsonResponse = resultData.getString("response");
                        // data is not updated if the user has made unsaved changes to the data
                        if (!dataManipulated)
                            updateData();
                    }
                    Log.d(TAG, "response received");
                    hideDialog();
                }
            }
        });
        //start poll timer to pull data every 30 second after the 1/10th of a second
        startTimer();
        //fresh data is polled every 30 seconds
        timer.schedule(timerTask, 100, 30 * 1000);
        //callback function upon receiving put response
        putBridge.setReceiver(new DataBridge.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == RESULT_OK) {
                    jsonResponse = resultData.getString("response");
                    updateData();
                }
                if (resultData == null) {
                    if (resultCode == 1001)
                        dataManipulated = true;
                    if (resultCode == 1002)
                        dataManipulated = false;

                }
            }
        });
    }

    private void updateData() {
        ArrayList<ExpensesModel> update = new Gson().fromJson(jsonResponse, new TypeToken<ArrayList<ExpensesModel>>() {
        }.getType());
        //if the array sizes are same then previous data is over written by new data
        if (mData.size() == update.size())
            for (int i = 0; i < mData.size(); i++) {
                mData.set(i, update.get(i));
            }
        //if the new data is bigger than the old one then old data is updated/added with new data
        else if (update.size() > mData.size())
            for (int i = 0; i < update.size(); i++) {
                if (i < mData.size())
                    mData.set(i, update.get(i));
                else
                    mData.add(update.get(i));
            }
        //if new data is smaller than the old one then the old data is updated/truncated
        else
            for (int i = 0; i < mData.size(); i++) {
                if (i < update.size())
                    mData.set(i, update.get(i));
                else
                    mData.remove(i);
            }
        //adapter is informed of the update
        adapter.notifyDataSetChanged();
    }

    private void createList(String jsonResponse) {
        //to populate the pojo array and set the adapter
        mData = new Gson().fromJson(jsonResponse, new TypeToken<ArrayList<ExpensesModel>>() {
        }.getType());
        adapter = new ExpensesAdapter(mData, ExpensesActivity.this, putBridge);
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
