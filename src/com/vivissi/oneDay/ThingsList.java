package com.vivissi.oneDay;

import static com.vivissi.oneDay.util.OneDay.ID;
import static com.vivissi.oneDay.util.OneDay.WSTIME_SLEEP_TIME;
import static com.vivissi.oneDay.util.OneDay.WSTIME_WAKEUP_TIME;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.vivissi.oneDay.R.id;
import com.vivissi.oneDay.util.DateANDTime;
import com.vivissi.oneDay.util.DbAdapter;
/**
 * 打开软件的第一个界面。可以添加要关注的事情，并可以打分选择日期
 * @author Agassi
 *
 */
public class ThingsList extends Activity {

	private Button addButton;
	private Button setDateButton;
	private Button wsTimeButton;
	private Button chartButton;
	private Button thingsSettingButton;
	private Cursor thingsCursor;
	private DbAdapter mDbAdapter;
	private ListView mListView;
	private TextView dateTextView;

	private DateANDTime dateANDTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mDbAdapter = new DbAdapter(this);
		mDbAdapter.open();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.things_list);

		addButton = (Button) findViewById(id.btnCallAddThingDialog);
		setDateButton = (Button) findViewById(id.btnChangeDate);
		wsTimeButton = (Button) findViewById(id.btnWSTime);//wakeup and sleep time button
		mListView = (ListView) findViewById(id.thingsList);
		dateTextView = (TextView) findViewById(id.dateTextView);
		chartButton = (Button) findViewById(id.btnChart);
		thingsSettingButton = (Button)findViewById(id.btnCallThingsSetting);
		dateANDTime = new DateANDTime();
		dateTextView.setText(dateANDTime.getDateFiled());
		mListView.setSmoothScrollbarEnabled(true);
		fillData();

		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AddThingDialog addThingDialog = new AddThingDialog(
						ThingsList.this);
				addThingDialog.show();
			}
		});

		setDateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DatePickerDialog datePickerDialog = new DatePickerDialog(
						ThingsList.this, dateSetListener, dateANDTime
								.getYearNow(), dateANDTime.getMonthNow(),
						dateANDTime.getDayOfMonthNow());
				datePickerDialog.show();
			}
		});

		wsTimeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final SetTimeDialog setTimeDialog = new SetTimeDialog(
						ThingsList.this, null, dateANDTime.getHourNow(),
						dateANDTime.getMinuteNow(), true);
				setTimeDialog.setButton("wakeup",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mDbAdapter.createWSTime(setTimeDialog
										.getWs(), dateANDTime.getDayOfMonth(),
										dateANDTime.getMonth(), 
										dateANDTime.getYear(),WSTIME_WAKEUP_TIME);
							}
						});
				setTimeDialog.setButton2("sleep",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mDbAdapter.createWSTime(setTimeDialog
										.getWs(), dateANDTime.getDayOfMonth(),
										dateANDTime.getMonth(), dateANDTime
												.getYear(),WSTIME_SLEEP_TIME);
							}
						});
				setTimeDialog.show();
			}

		});

		chartButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ThingsList.this,
						com.vivissi.oneDay.chart.ChartActivity.class);
				startActivity(intent);
			}
		});
		
		thingsSettingButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ThingsList.this,
						com.vivissi.oneDay.thingSetting.ThingsSettingActivity.class);
				startActivity(intent);
			}
		});

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		super.onWindowFocusChanged(hasFocus);
		if (hasFocus)
			fillData();
	}
	//填入list的items
	private void fillData() {
		Cursor tempCursor;
		if ((tempCursor = mDbAdapter.fetchVisibleThings()).getCount() != 0) {
			startManagingCursor(tempCursor);
			thingsCursor = mDbAdapter.fetch(dateANDTime.getDayOfMonth(),
					dateANDTime.getMonth(), dateANDTime.getYear());
			if (thingsCursor.getCount() != tempCursor.getCount()) {
				tempCursor.moveToFirst();
				while (!tempCursor.isAfterLast()) {
					
					long thingId = tempCursor.getLong(tempCursor
							.getColumnIndex(ID));
					mDbAdapter.createDailything(-1, thingId, dateANDTime
							.getDayOfMonth(), dateANDTime.getMonth(),
							dateANDTime.getYear());
					tempCursor.moveToNext();

				}
				thingsCursor = mDbAdapter.fetch(dateANDTime.getDayOfMonth(),
						dateANDTime.getMonth(), dateANDTime.getYear());
			}
			thingsCursor.moveToFirst();
			startManagingCursor(thingsCursor);
			ThingsRowAdapter adapter = new ThingsRowAdapter(this, thingsCursor);

			mListView.setAdapter(adapter);
		}
	}

	private OnDateSetListener dateSetListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			dateANDTime.setDayOfMonth(dayOfMonth);
			dateANDTime.setMonth(monthOfYear);
			dateANDTime.setYear(year);
			dateTextView.setText(dateANDTime.getDateFiled());
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbAdapter.close();
		
	}

}