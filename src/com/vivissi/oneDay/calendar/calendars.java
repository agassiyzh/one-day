package com.vivissi.oneDay.calendar;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
//TODO 将每天的情况作为一个事件，写到google 日历的应用这。这样可以同步的日历的网页，方便回顾
public class calendars{
	private String[] projection;
	private Uri calendars;
	private Uri event;
	private ContentResolver mContentResolver;
	
	public calendars(Context context){
		mContentResolver = context.getContentResolver();
		projection = new String[]{"_id","name"};
		calendars = Uri.parse("content://calendar/calendars");
		event = Uri.parse("content://calendar/event");
	}
	
	Cursor fetchCalendars(){
		Cursor cursor = mContentResolver.query(calendars, 
				projection, null, null, null);
		
		return cursor;
	}
}
