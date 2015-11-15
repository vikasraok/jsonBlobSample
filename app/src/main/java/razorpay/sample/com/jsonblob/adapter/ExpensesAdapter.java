package razorpay.sample.com.jsonblob.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import razorpay.sample.com.jsonblob.R;
import razorpay.sample.com.jsonblob.holder.ExpensesHolder;
import razorpay.sample.com.jsonblob.model.ExpensesModel;

/**
 * Created by SESA249903 on 11/14/2015.
 */
public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesHolder> {

    private ArrayList<ExpensesModel> data;

    public ExpensesAdapter(ArrayList<ExpensesModel> mData) {
        data = mData;
    }

    @Override
    public ExpensesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_layout, parent, false);
        ExpensesHolder expensesHolder = new ExpensesHolder(view);
        return expensesHolder;
    }

    @Override
    public void onBindViewHolder(ExpensesHolder holder, int position) {
        holder.tvExpenseDescription.setText(data.get(position).getDescription());
        holder.tvCategory.setText(data.get(position).getCategory());
        holder.tvAmount.setText(data.get(position).getAmount());
        holder.tvDate.setText(data.get(position).getDate());
        holder.tvTime.setText(data.get(position).getTime());
        if (data.get(position).getState() == "unverified")
            holder.rgState.check(R.id.rb_unverified);
        else if (data.get(position).getState() == "verified")
            holder.rgState.check(R.id.rb_verified);
        else
            holder.rgState.check(R.id.rb_fraud);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
