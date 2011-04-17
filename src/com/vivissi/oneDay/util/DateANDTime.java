package com.vivissi.oneDay.util;

import android.text.format.Time;

/**
 * 事件日期的公用类。开获取当前系统时间，和设定的时间
 * @author Agassi
 *
 */
public final class DateANDTime {
	private Time t;//google 官方说用这个类系统占用少
	
	private int time;
	private int hour;
	private int minute;
	
	private int dayOfMonth;
	private int month;
	private int year;
	
	public DateANDTime(){
		t = new Time();
		t.setToNow();
		dayOfMonth = t.monthDay;
		year = t.year;
		month = t.month;
		time = t.hour*60+t.minute;
	}

	public int getTime() {
		return time;
	}
	
	public int getTimeNow() {
		t.setToNow();
		return t.hour*60+t.minute;
	}

	public int getDayOfMonth() {
		return dayOfMonth;
	}
	
	public int getDayOfMonthNow() {
		t.setToNow();
		return t.monthDay;
	}

	public int getMonth() {
		return month;
	}
	
	public int getMonthNow() {
		t.setToNow();
		return t.month;
	}

	public int getYear() {
		return year;
	}
	
	public int getYearNow() {
		t.setToNow();
		return t.year;
	}
	
	public String getDateFiled(){
		return year+"-"+(month+1)+"-"+dayOfMonth;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public void setDayOfMonth(int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getHour() {
		return hour;
	}

	public int getHourNow() {
		t.setToNow();
		return t.hour;
	}
	
	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}
	
	public int getMinuteNow() {
		t.setToNow();
		return t.minute;
	}

	public void setMinute(int monute) {
		this.minute = monute;
	}
}