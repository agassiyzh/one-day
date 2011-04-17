package com.vivissi.oneDay.thingSetting;

import android.app.ListActivity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.vivissi.oneDay.R;
import com.vivissi.oneDay.R.id;
import com.vivissi.oneDay.util.DbAdapter;
import com.vivissi.oneDay.util.OneDay;
/**
 * 事件选项
 * 1.删除
 * 2.是否显示（现在已经不再关注这件事情。不删除以前的数据。在打分的时候不再显示，但是可以通过查找以往的月份来看这件是的记录）
 * 3.是否分享，通过短信，twitter等方式告诉自己的好今天一天的情况。这个可以为每件事单独设置是否作为分享内容
 * @author Agassi
 *
 */
public class ThingsSettingActivity extends ListActivity {
	
	private Cursor thingsCursor;
	private DbAdapter mdb;
	private ThingsSettingRowAdapter adapter;
	private final String TAG = "ThingsSettingActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mdb = new DbAdapter(ThingsSettingActivity.this);
		mdb.open();
		fillData();
		getListView().setSmoothScrollbarEnabled(true);
		getListView().setAlwaysDrawnWithCacheEnabled(true);
		
	}
	
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		int x = l.getChildCount();
		for(int i = 0; i < x; i++){
			if(i != position)
				l.getChildAt(i).findViewById(R.id.set_btn_bar).setVisibility(View.GONE);
		}
		v.findViewById(R.id.set_btn_bar).setVisibility(View.VISIBLE);
		Log.w(TAG, ""+l.getPositionForView(v));
	}

	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		fillData();
	}
	
	private void fillData(){
		thingsCursor = mdb.fetchAllThings();
		startManagingCursor(thingsCursor);
		thingsCursor.moveToFirst();
		adapter = new ThingsSettingRowAdapter(this, thingsCursor, true);
		setListAdapter(adapter);
		
	}

}
