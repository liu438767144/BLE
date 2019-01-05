package com.lyz.ble.adapter;import java.util.ArrayList;import android.content.Context;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.BaseAdapter;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.TextView;import com.lyz.ble.R;import com.lyz.ble.params.SetItem;/** *  *  */public class SetAdapter extends BaseAdapter {	ArrayList<SetItem> arraySource;	private LayoutInflater factory;	private Context context;	public SetAdapter(Context context, ArrayList<SetItem> arraySource) {		// TODO Auto-generated constructor stub		this.context = context;		this.arraySource = arraySource;		factory = LayoutInflater.from(this.context);	}	@Override	public int getCount() {		// TODO Auto-generated method stub		return arraySource.size();	}	@Override	public Object getItem(int position) {		// TODO Auto-generated method stub		return position;	}	@Override	public long getItemId(int position) {		// TODO Auto-generated method stub		return position;	}	@SuppressWarnings("static-access")	@Override	public View getView(int position, View convertView, ViewGroup parent) {		// TODO Auto-generated method stub		Item item = null;		if (convertView == null) {			convertView = factory.inflate(R.layout.set_list_item, null);			item = new Item();			item.icon = (ImageView) convertView					.findViewById(R.id.setitem_image);			item.name = (TextView) convertView					.findViewById(R.id.setitem_name);			item.arror = (ImageView) convertView					.findViewById(R.id.arror_image);			item.layout = (LinearLayout) convertView.findViewById(R.id.setitem_layout);			convertView.setTag(item);		} else {			item = (Item) convertView.getTag();		}		item.name.setText(this.arraySource.get(position).name);		if (position == 1) {			item.arror.setVisibility(View.GONE);//			item.layout.setBackground(null);		}		return convertView;	}	public class Item {		TextView name;		ImageView icon, arror;		LinearLayout layout;	}}