package com.example.administrator.test;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class PersonAdapter extends BaseAdapter {

	private List<Person> mList;
	private Context mContext;
	private LayoutParams lp;

	public PersonAdapter(List<Person> list, Context ctx) {
		mList = list;
		mContext = ctx;
		lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new TextView(mContext);
		}
		TextView tv = (TextView) convertView;
		tv.setTextSize(25);
		tv.setTextColor(Color.BLACK);
		tv.setLayoutParams(lp);
		Person p = (Person) getItem(position);
		tv.setText(p.getName());
		return tv;
	}
}
