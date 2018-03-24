package tech.apps.rudrakishore.todo.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import tech.apps.rudrakishore.todo.R;
import tech.apps.rudrakishore.todo.database.DatabaseHelper;
import tech.apps.rudrakishore.todo.database.model.Item;

/**
 * Created by Rudra Kishore on 21-03-2018.
 */

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyViewHolder> {

    private Context context;
    private List<Item> notesList;

    public ItemsAdapter(Context context, List<Item> notesList) {
        this.context = context;
        this.notesList = notesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Item item = notesList.get(position);
        if (item.getStatus() == 1) {
            holder.rootlayout.setBackgroundColor(Color.parseColor("#69F0AE"));
            holder.note.setTextColor(Color.WHITE);
            holder.qtymetric.setTextColor(Color.WHITE);
            holder.dot.setTextColor(Color.WHITE);


        } else {
            holder.rootlayout.setBackgroundColor(Color.WHITE);
            holder.note.setTextColor(Color.BLACK);
            holder.qtymetric.setTextColor(Color.BLACK);

            holder.dot.setTextColor(context.getResources().getColor(R.color.colorPrimary));

        }

        holder.note.setText(item.getItem());
        holder.tItems.setText(totalItems(item.getTimestamp()) + " Items");
        holder.taItems.setText(totalAvailableItems(item.getTimestamp()) + " Items");
        holder.qtymetric.setText(item.getQty() + " " + item.getMetric());
        // Displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml("&#8226;"));


        holder.timestamp.setText(formatDate(item.getTimestamp()) + "");
        if (position > 0) {
            Item preItem = notesList.get(position - 1);
            if (!(formatDate(item.getTimestamp())).equals(formatDate(preItem.getTimestamp()))) {
                holder.timeHeader.setVisibility(View.VISIBLE);
                holder.timestamp.setText(formatDate(item.getTimestamp()));

            } else if (formatDate(item.getTimestamp()).equals(formatDate(preItem.getTimestamp()))) {
                holder.timeHeader.setVisibility(View.GONE);
                holder.timestamp.setText("");
            }

        } else {
            holder.timeHeader.setVisibility(View.VISIBLE);

        }


    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public int totalItems(String date) {
        DatabaseHelper db = new DatabaseHelper(context);
        int count = db.getItemsCountParm(date);
        return count;
    }

    public int totalAvailableItems(String date) {
        DatabaseHelper db = new DatabaseHelper(context);
        int count = db.getAvailbleItemsCount(date);
        return count;
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView note;
        public TextView qtymetric;
        public TextView dot;
        public TextView timestamp, tItems, taItems;
        public LinearLayout rootlayout;
        public RelativeLayout timeHeader;

        public MyViewHolder(View view) {
            super(view);
            note = view.findViewById(R.id.note);
            qtymetric = view.findViewById(R.id.qtymetric);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
            rootlayout = view.findViewById(R.id.row_layout);
            timeHeader = view.findViewById(R.id.headTime);
            tItems = view.findViewById(R.id.tItems);
            taItems = view.findViewById(R.id.taItems);
        }
    }
}
