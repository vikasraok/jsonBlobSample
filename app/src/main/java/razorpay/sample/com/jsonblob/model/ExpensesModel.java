package razorpay.sample.com.jsonblob.model;

/**
 * Created by SESA249903 on 11/14/2015.
 */
public class ExpensesModel {

    String expenseDecription, expenseCategory, expenseAmount, expenseDate, expenseTime, expenseState;

    public void setDescription(String str) {
        expenseDecription = str;
    }

    public void setCategory(String str) {
        expenseCategory = str;
    }

    public void setAmount(String str) {
        expenseAmount = str;
    }

    public void setDate(String str) {
        expenseDate = str;
    }

    public void setTime(String str) {
        expenseTime = str;
    }

    public void setState(String str) {
        expenseState = str.toLowerCase();
    }

    public String getDescription() {
        return expenseDecription;
    }

    public String getCategory() {
        return expenseCategory;
    }

    public String getAmount() {
        return expenseAmount;
    }

    public String getDate() {
        return expenseDate;
    }

    public String getTime() {
        return expenseTime;
    }

    public String getState() {
        return expenseState;
    }
}
