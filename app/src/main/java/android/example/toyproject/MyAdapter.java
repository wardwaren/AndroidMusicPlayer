package android.example.toyproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private List<String> timeData;
    private List<String> nameData;
    private boolean sortedByName = false;

    // data is passed into the constructor
    MyAdapter(Context context, List<String> data, TreeMap<Long,String> createTime) {
        this.mInflater = LayoutInflater.from(context);
        timeData = getValues(createTime);
        this.mData = timeData;
        Collections.sort(data);
        nameData = data;
    }

    private List<String> getValues(TreeMap<Long, String> createTime) {
        List<String> keys = new ArrayList<String>();
        for(Map.Entry<Long,String> entry : createTime.entrySet()) {
            keys.add(entry.getValue());
        }
        Collections.reverse(keys);
        return keys;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.my_text_view, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    public void onBindViewHolder(ViewHolder holder, int position) {
        String animal = mData.get(position);
        holder.myTextView.setText(animal);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    public void notifyAdapterDataSetChanged() {
        //do your sorting here
        if(sortedByName == false){
            mData = nameData;
            sortedByName = true;
        }
        else{
            mData = timeData;
            sortedByName = false;
        }
        notifyDataSetChanged();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.music_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                try {
                    mClickListener.onItemClick(view, getAdapterPosition());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position) throws IOException;
    }
}