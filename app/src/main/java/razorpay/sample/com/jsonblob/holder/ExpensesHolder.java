package razorpay.sample.com.jsonblob.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import razorpay.sample.com.jsonblob.R;

/**
 * Created by SESA249903 on 11/15/2015.
 */
public class ExpensesHolder extends RecyclerView.ViewHolder {

    public TextView tvExpenseDescription, tvCategory, tvAmount, tvTime, tvDate;
    public RadioGroup rgState;

    public ExpensesHolder(View itemView) {
        super(itemView);
        tvExpenseDescription = (TextView) itemView.findViewById(R.id.tv_expense_description);
        tvCategory = (TextView) itemView.findViewById(R.id.tv_expense_category);
        tvAmount = (TextView) itemView.findViewById(R.id.tv_expense_amount);
        tvTime = (TextView) itemView.findViewById(R.id.tv_expense_time);
        tvDate = (TextView) itemView.findViewById(R.id.tv_expense_date);
    }
}
