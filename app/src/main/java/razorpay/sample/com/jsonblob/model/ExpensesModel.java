package razorpay.sample.com.jsonblob.model;


public class ExpensesModel {

    int amount;
    String category;
    String time;
    String description;
    String id;
    String state;

    public void setDescription(String str) {
        description = str;
    }

    public void setCategory(String str) {
        this.category = str;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }


    public void setTime(String str) {
        this.time = str;
    }

    public void setState(String str) {
        this.state = str.toLowerCase();
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getAmount() {
        return String.valueOf(amount);
    }

    public String getDate() {
        return time.substring(0, time.indexOf("T"));
    }

    public String getTime() {
        return time.substring(time.indexOf("T") + 1, time.indexOf("."));
    }

    public String getState() {
        return state;
    }
     public String getId() {
         return id;
     }
}
