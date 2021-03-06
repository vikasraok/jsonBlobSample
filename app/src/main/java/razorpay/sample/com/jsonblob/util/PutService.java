package razorpay.sample.com.jsonblob.util;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import razorpay.sample.com.jsonblob.R;

/**
 * Created by SESA249903 on 11/16/2015.
 */
public class PutService extends IntentService {
    private RequestQueue mQueue;
    private Context mContent;
    public static final String TAG = PutService.class.getSimpleName();

    public PutService() {
        super("PutService");
    }

    private RequestQueue getRequestQueue() {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContent);
        }
        return mQueue;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContent = getApplicationContext();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        {
            Log.d(TAG, "Put service running");
            final ResultReceiver receiver = intent.getParcelableExtra("receiver");

            final String param = intent.getStringExtra("json");

            final Bundle bundle = new Bundle();

            String URL = mContent.getResources().getString(R.string.json_url) + mContent.getResources().getString(R.string.blob_id);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, URL, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());
                    try {
                        JSONArray expenseArray = response.getJSONArray("expenses");
                        bundle.putString("response", expenseArray.toString());
                        receiver.send(Activity.RESULT_OK, bundle);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, error.toString());
                }
            }) {
                //volley doesn't have an inbuilt method to handle put requests
                @Override
                public byte[] getBody() {
                    if ((getMethod() == Method.PUT) && (param != null)) {
                        return param.getBytes();  // Encoding required?????
                    } else {
                        return super.getBody();
                    }
                }
            };
            getRequestQueue().add(request);

        }

    }
}
