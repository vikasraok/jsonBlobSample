package razorpay.sample.com.jsonblob.util;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;

/**
 * Created by SESA249903 on 11/14/2015.
 */
@SuppressLint("ParcelCreator")
public class DataBridge extends ResultReceiver {
    private Receiver receiver;
    //used to send data from the intent service to the calling activity
    public DataBridge(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (receiver != null) {
            receiver.onReceiveResult(resultCode, resultData);
        }
    }
}
