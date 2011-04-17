package com.vivissi.oneDay.thingSetting;

import static com.vivissi.oneDay.util.OneDay.*;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.vivissi.oneDay.R;
import com.vivissi.oneDay.R.id;
import com.vivissi.oneDay.R.layout;
import com.vivissi.oneDay.util.DbAdapter;

/**
 * 绑定事件设置list中的每一个item的显示方式和功能
 * @author Agassi
 *
 */
public class ThingsSettingRowAdapter extends CursorAdapter {

	private final String TAG = "ThingsSettingRowAdapter";
	private TextView nameTextView;
	private TextView commentTextView;
	private TextView shareTextView;
	private TextView visTextView;

	CheckBox visBox;
	CheckBox shareBox;
	private View view;
	private int nameIndex;
	private int commentIndex;
	private int shareIndex;
	private int visibleIndex;
	private int idIndex;
	
	private DbAdapter mdb;
	private LayoutInflater mInflater;


	public ThingsSettingRowAdapter(Context context, Cursor c,
			boolean autoRequery) {
		super(context, c, autoRequery);
		mdb = new DbAdapter(context);
		mInflater = LayoutInflater.from(context);
		nameIndex = c.getColumnIndex(THINGS_NAME);
		commentIndex = c.getColumnIndex(THINGS_COMMENT);
		shareIndex = c.getColumnIndex(THINGS_SHARE_FLAG);
		visibleIndex = c.getColumnIndex(THINGS_VISIBLE_FLAG);
		idIndex = c.getColumnIndex(ID);
		mdb.open();
	}


	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		view = mInflater.inflate(R.layout.things_setting_row, null);
		return view;
	}
	
	

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {
		nameTextView = (TextView) view.findViewById(id.tv_thingSet_name);
		commentTextView = (TextView) view.findViewById(id.tv_thingSet_commnet);
		shareTextView = (TextView) view.findViewById(id.tv_thingSet_share);
		visTextView = (TextView) view.findViewById(id.tv_thingSet_vis);
		
		nameTextView.setText(cursor.getString(nameIndex));
		commentTextView.setText(cursor.getString(commentIndex));
		
		LinearLayout setTools = (LinearLayout)view.findViewById(id.set_btn_bar);
		Button delButton = (Button)setTools.findViewById(id.btn_del_things);
		visBox = (CheckBox)setTools.findViewById(id.cb_visible);
		shareBox = (CheckBox)setTools.findViewById(id.cb_share);
		
		visBox.setChecked(cursor.getInt(visibleIndex) == 1);
		shareBox.setChecked(cursor.getInt(shareIndex)==1);
		
		visBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				int flag;
				if(isChecked){
					flag = 1;
				}else {
					flag = 0;
				}
				
				mdb.changeVisFlag(flag, view.getId());
			}
		});
		
		
		delButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder builder = new Builder(context);
				builder.setTitle("delete thing?");
				AlertDialog alertDialog = builder.create();
				alertDialog.setButton("sure", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						mdb.deleteThins(cursor.getInt(idIndex));
						Log.w(TAG, ""+cursor.getInt(idIndex)+"..."+view.getId());
					}
				});
				alertDialog.show();
				
			}
		});

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, null, parent);
	}
	

}