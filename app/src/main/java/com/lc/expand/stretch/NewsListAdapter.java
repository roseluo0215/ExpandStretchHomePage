package com.lc.expand.stretch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by luocheng on 18/6/17.
 */

public class NewsListAdapter extends BaseAdapter {

  private List<ListData> mData;
  private LayoutInflater mInflater;
  public NewsListAdapter(Context context){
    mInflater = LayoutInflater.from(context);
  }

  public void setData(List<ListData> data){
    mData = data;
  }
  @Override
  public int getCount() {
    return mData.size();
  }

  @Override
  public Object getItem(int position) {
    return mData.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View rootView;
    ViewHolder holder;
    if (convertView == null) {
      rootView = mInflater.inflate(R.layout.news_item_view,null);
      holder = new ViewHolder();
      holder.mTitle = rootView.findViewById(R.id.tv_title);
      rootView.setTag(holder);
    }else {
      rootView = convertView;
      holder = (ViewHolder) rootView.getTag();
    }
    holder.mTitle.setText(mData.get(position).getTitle());
    return rootView;
  }


  public static class ViewHolder{
    TextView mTitle;
  }
}
