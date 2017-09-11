package com.tigcal.lbs.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tigcal.lbs.R;

public class LoadingAdapter extends BaseAdapter {

	public LoadingAdapter() {
		super();
	}

	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return LayoutInflater.from(parent.getContext()).inflate(
				R.layout.list_item_loading, parent, false);
	}

}
