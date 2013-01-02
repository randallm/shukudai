package com.randallma.whatsthehomework;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NewsEntriesAdapter extends BaseAdapter {
	private static ArrayList<NewsEntry> newsEntriesArrayList;

	private final LayoutInflater mInflater;

	public NewsEntriesAdapter(Context context, ArrayList<NewsEntry> entries) {
		newsEntriesArrayList = entries;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return newsEntriesArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return newsEntriesArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.assignment_list_row_view,
					null);
			holder = new ViewHolder();
			holder.txtPhoto = (TextView) convertView.findViewById(R.id.photo);
			holder.txtDateDue = (TextView) convertView
					.findViewById(R.id.dateDue);
			holder.txtDateAssigned = (TextView) convertView
					.findViewById(R.id.dateAssigned);
			holder.txtDescription = (TextView) convertView
					.findViewById(R.id.description);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.txtPhoto.setText(newsEntriesArrayList.get(position).getPhoto());
		holder.txtDateDue.setText(newsEntriesArrayList.get(position)
				.getDateDue());
		holder.txtDateAssigned.setText(newsEntriesArrayList.get(position)
				.getDateAssigned());
		holder.txtDescription.setText(newsEntriesArrayList.get(position)
				.getDescription());

		return convertView;
	}

	static class ViewHolder {
		TextView txtPhoto;
		TextView txtDateDue;
		TextView txtDateAssigned;
		TextView txtDescription;
	}

}