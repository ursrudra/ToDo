package tech.apps.rudrakishore.todo.database.model;

/**
 * Created by Rudra Kishore on 21-03-2018.
 */

public class Item {
    public static final String TABLE_NAME = "Todo_Items";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOTE = "item";
    public static final String COLUMN_QTY = "qty";
    public static final String COULMN_QTY_METRIC = "metric";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_STATUS = "status";
    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NOTE + " TEXT,"
                    + COLUMN_QTY + " INTEGER,"
                    + COULMN_QTY_METRIC + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + COLUMN_STATUS + " INTEGER DEFAULT 0"
                    + ")";
    private int id;
    private String item;
    private double qty;
    private String metric;
    private String timestamp;
    private int status;

    public Item() {

    }

    public Item(int id, String item, double qty, String metric, String timestamp, int status) {
        this.id = id;
        this.item = item;
        this.qty = qty;
        this.metric = metric;
        this.timestamp = timestamp;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
