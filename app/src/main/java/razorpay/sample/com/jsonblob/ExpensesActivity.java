package razorpay.sample.com.jsonblob;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import razorpay.sample.com.jsonblob.util.DataReceiver;
import razorpay.sample.com.jsonblob.util.PollService;

public class ExpensesActivity extends AppCompatActivity {

    private String TAG = ExpensesActivity.class.getSimpleName();
    public DataReceiver mReceiver;
    private Timer timer;
    private TimerTask timerTask;
    final Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        //setting to null to handle memory leaks
        mReceiver.setReceiver(null);
        stopTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //callback function upon receiving response
        mReceiver.setReceiver(new DataReceiver.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == RESULT_OK) {
                    String resultValue = resultData.getString("response");
                    Log.d(TAG,"response received");
                }
            }
        });
        startTimer();
        timer.schedule(timerTask,100,30*1000);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_expenses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}