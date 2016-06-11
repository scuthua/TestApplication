package com.flower.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by flower on 2016/3/26.
 */
public class MyAdapter extends BaseAdapter {

    private List<String>songs;
    private Context context;

    public MyAdapter(Context context, int res, List<String>songs) {
        this.songs = songs;
        this.context = context;
    }
    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item, null);
                holder.textView = (TextView) convertView.findViewById(R.id.item_singer_and_song_name);
//            holder.button = (Button) convertView.findViewById(R.id.delete);
                convertView.setBackgroundResource(R.drawable.convertview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();

        }
        holder.textView.setText(songs.get(position));
        return convertView;
    }
    class ViewHolder {
        TextView textView;
        Button button;
    }
}
