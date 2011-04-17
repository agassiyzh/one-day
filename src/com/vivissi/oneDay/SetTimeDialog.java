package com.vivissi.oneDay;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;
/**
 * 
 * 设置时间的dialog
 * @author Agassi
 *
 */
public class SetTimeDialog extends TimePickerDialog {
	private float ws;
	public float getWs() {
		return ws;
	}

	public SetTimeDialog(Context context,
			OnTimeSetListener callBack, int hourOfDay, int minute,
			boolean is24HourView) {
		super(context, callBack, hourOfDay, minute, is24HourView);
		ws = hourOfDay+(float)minute/60;
	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		super.onTimeChanged(view, hourOfDay, minute);
		ws = hourOfDay+(float)minute/60;
	}
}
