package razorpay.sample.com.jsonblob.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import razorpay.sample.com.jsonblob.R;
import razorpay.sample.com.jsonblob.holder.ExpensesHolder;
import razorpay.sample.com.jsonblob.model.ExpensesModel;
import razorpay.sample.com.jsonblob.util.DataBridge;
import razorpay.sample.com.jsonblob.util.GetService;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesHolder>{

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
        if (data.get(position).getState().toLowerCase().equals("unverified"))
            holder.rgState.check(R.id.rb_unverified);
        else if (data.get(position).getState().toLowerCase().equals("verified"))
            holder.rgState.check(R.id.rb_verified);
        else
            holder.rgState.check(R.id.rb_fraud);
        holder.rgState.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_unverified)
                    data.get(position).setState("Unverified");
                else if(checkedId == R.id.rb_verified)
                    data.get(position).setState("Verified");
                else
                    data.get(position).setState("Fraud");

                Snackbar.make(view, "Save transaction state", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Save", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Gson gson = new Gson();
                                JsonElement element = gson.toJsonTree(data, new TypeToken<ArrayList<ExpensesModel>>(){}.getType());
                                JsonArray jsonArray = element.getAsJsonArray();
                                Intent poll = new Intent(context, GetService.class);
                                //sending callback function to service
                                poll.putExtra("receiver", bridge);
                                poll.putExtra("json",jsonArray.toString());
                                context.startService(poll);
                            }
                        }).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
