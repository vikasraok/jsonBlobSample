package razorpay.sample.com.jsonblob.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import razorpay.sample.com.jsonblob.R;
import razorpay.sample.com.jsonblob.holder.ExpensesHolder;
import razorpay.sample.com.jsonblob.model.ExpensesModel;
import razorpay.sample.com.jsonblob.util.DataBridge;
import razorpay.sample.com.jsonblob.util.PutService;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesHolder> {

    private ArrayList<ExpensesModel> data;
    private Context context;
    private View view;
    private DataBridge bridge;

    public ExpensesAdapter(ArrayList<ExpensesModel> mData, Context expensesActivity, DataBridge putBridge) {
        data = mData;
        context = expensesActivity;
        bridge = putBridge;
    }

    @Override
    public ExpensesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_layout, parent, false);
        ExpensesHolder expensesHolder = new ExpensesHolder(view);
        this.view = parent;
        return expensesHolder;
    }

    @Override
    public void onBindViewHolder(final ExpensesHolder holder, final int position) {
        holder.tvExpenseDescription.setText(data.get(position).getDescription());
        holder.tvCategory.setText(data.get(position).getCategory());
        holder.tvAmount.setText(context.getResources().getString(R.string.string_amount, data.get(position).getAmount()));
        holder.tvDate.setText(data.get(position).getDate());
        holder.tvTime.setText(data.get(position).getTime());
        if (data.get(position).getState().equals("unverified"))
            holder.rgState.check(R.id.rb_unverified);
        else if (data.get(position).getState().equals("verified"))
            holder.rgState.check(R.id.rb_verified);
        else
            holder.rgState.check(R.id.rb_fraud);
        //I could have used radio groups oncheckchangedlistener and I did but had issues
        //when the view was scrolled and data was populated so used onclickedlistener
        //for the child elements
        //Then you would say there is no need for a compound button, yes
        holder.rgState.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.rb_unverified) {
                    data.get(position).setState("Unverified");
                    showSnackBar();
                }
            }
        });
        holder.rgState.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.rb_verified) {
                    data.get(position).setState("Verified");
                    showSnackBar();
                }
            }
        });
        holder.rgState.getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.rb_fraud) {
                    data.get(position).setState("Fraud");
                    showSnackBar();
                }
            }
        });
    }
    private void showSnackBar() {
        final int paddingBottom = (int) (context.getResources().getDimension(R.dimen.snackbar_padding));
        //adding padding to move the view up so as to not obustruct the last element
        view.setPadding(0, 0, 0, paddingBottom);
        //Wanted to use snackbar and it just looks cleaner
        Snackbar.make(view, "Save transaction changes?", Snackbar.LENGTH_INDEFINITE)
                .setAction("Save", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.setPadding(0,0,0,0);
                        Gson gson = new Gson();
                        JsonElement element = gson.toJsonTree(data, new TypeToken<ArrayList<ExpensesModel>>() {
                        }.getType());
                        JsonArray jsonArray = element.getAsJsonArray();
                        JsonObject jsonObj = new JsonObject();
                        jsonObj.add("expenses", jsonArray);
                        Intent updateData = new Intent(context, PutService.class);
                        //sending callback function to service
                        updateData.putExtra("receiver", bridge);
                        updateData.putExtra("json", jsonObj.toString());
                        context.startService(updateData);
                    }
                }).setCallback(new Snackbar.Callback() {
            //need this information to block/unblock the update event
            @Override
            public void onShown(Snackbar snackbar) {
                super.onShown(snackbar);
                bridge.send(1001,null);
            }

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                bridge.send(1002,null);
            }
        }).show();
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
}
