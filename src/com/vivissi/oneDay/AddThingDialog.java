package com.vivissi.oneDay;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vivissi.oneDay.R.id;
import com.vivissi.oneDay.R.layout;
import com.vivissi.oneDay.R.string;
import com.vivissi.oneDay.util.DateANDTime;
import com.vivissi.oneDay.util.DbAdapter;

import static com.vivissi.oneDay.util.OneDay.*;
/**
 * 添加事件的dialog。
 * @author Agassi
 *
 */
public class AddThingDialog extends Dialog {

	private EditText titleEditText;
	private EditText commentEditText;
	private Button saveButton;
	private Button cancelButton;
	private Button addAnotherThingButton;

	private Context mContext;

	private DbAdapter mAdapter;

	private DateANDTime dateANDTime;

	public AddThingDialog(Context context) {
		super(context);
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mAdapter = new DbAdapter(mContext);
		dateANDTime = new DateANDTime();
		super.onCreate(savedInstanceState);
		setContentView(layout.add_things_dialog);
		setTitle(string.add_things_dialog_title);
		titleEditText = (EditText) findViewById(id.thingsName);
		titleEditText.setText(null);
		commentEditText = (EditText) findViewById(id.thingComment);
		commentEditText.setText(null);
		saveButton = (Button) findViewById(id.btnSave);
		cancelButton = (Button) findViewById(id.btnCancel);
		addAnotherThingButton = (Button) findViewById(id.btnAddNew);
		saveButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				mAdapter.open();
				Cursor afterDateCursor = mAdapter.fetchAfterDate(dateANDTime
						.getDayOfMonth(), dateANDTime.getYear(), dateANDTime
						.getMonth());
				afterDateCursor.moveToNext();

				int dayIndex = afterDateCursor.getColumnIndex(DAY_OF_MONTH);
				int monthIndex = afterDateCursor.getColumnIndex(MONTH);
				int yearIndex = afterDateCursor.getColumnIndex(YEAR);
				
				if (!titleEditText.getText().toString().equals("")) {
					
					long rowID = mAdapter.createThing(titleEditText.getText()
							.toString(), commentEditText.getText().toString());
					if (afterDateCursor.getCount() == 0) {
						mAdapter.createDailything(-1, rowID, 
								dateANDTime.getDayOfMonth(),
								dateANDTime.getMonth(),
								dateANDTime.getYear());
						clearText();
					} else {
						while (!afterDateCursor.isAfterLast()) {
							mAdapter.createDailything(-1, rowID,
									afterDateCursor.getInt(dayIndex),
									afterDateCursor.getInt(monthIndex),
									afterDateCursor.getInt(yearIndex));
							afterDateCursor.moveToNext();
						}
					}

					afterDateCursor.close();
					clearText();
					
				} else {
					Toast.makeText(mContext, "Thing name can't be null!",
							Toast.LENGTH_SHORT).show();
				}
				dismiss();
				mAdapter.close();
			}
		});

		cancelButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		addAnotherThingButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!titleEditText.getText().toString().equals("")) {
					mAdapter.open();
					long rowID = mAdapter.createThing(titleEditText.getText()
							.toString(), commentEditText.getText().toString());
					mAdapter.createDailything(-1, rowID, dateANDTime
							.getDayOfMonth(), dateANDTime.getMonth(),
							dateANDTime.getYear());
					clearText();
					mAdapter.close();
					clearText();
				}

				else {
					Toast.makeText(mContext, "Thing name can't be null!",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	private void clearText() {
		titleEditText.setText(null);
		commentEditText.setText(null);
	}

	
}
