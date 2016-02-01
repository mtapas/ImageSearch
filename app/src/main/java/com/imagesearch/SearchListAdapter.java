package com.imagesearch;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchListAdapter extends BaseAdapter {

    private Context context;
    public ImageDownloader imageDownloader;
    private ArrayList<ListItemHolder> listData = new ArrayList<ListItemHolder>();
    private LayoutInflater inflater;

    public SearchListAdapter(Context context) {
        this.context = context;
        imageDownloader = new ImageDownloader(context.getApplicationContext());
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListData(ArrayList<ListItemHolder> listData) {
        this.listData = listData;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String url = listData.get(position).getUrl();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.search_result_row, null);
        }
        if (position % 2 == 0) {
            convertView.setBackgroundResource(R.color.rowblue);
        } else {
            convertView.setBackgroundResource(R.color.rowgreen);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.title);
        textView.setText(listData.get(position).getTitle());
        ImageView imageView = (ImageView) convertView.findViewById(R.id.thumbImage);

        //   new ImageDownloader(imageView).execute(url);


        imageDownloader.DownloadImage(url, imageView);


        return convertView;
    }
}
