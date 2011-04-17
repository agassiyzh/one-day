package com.vivissi.oneDay.chart;

import static com.vivissi.oneDay.util.OneDay.DAY_OF_MONTH;
import static com.vivissi.oneDay.util.OneDay.MONTH;
import static com.vivissi.oneDay.util.OneDay.TABLE_WSTIME;
import static com.vivissi.oneDay.util.OneDay.WSTIME_SLEEP_TIME;
import static com.vivissi.oneDay.util.OneDay.WSTIME_WAKEUP_TIME;
import static com.vivissi.oneDay.util.OneDay.YEAR;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vivissi.oneDay.R.id;
import com.vivissi.oneDay.R.layout;
import com.vivissi.oneDay.util.DbAdapter;
import static com.vivissi.oneDay.util.OneDay.*;
/**
 * 图表显示的activity
 * @author Agassi
 *
 */
public class ChartActivity extends Activity {

	private LinearLayout chartLayout;
	private Button itemButton;
	private Button dateButton;
	private DbAdapter mAdapter;
	private Cursor monthCursor;
	private Button timeButton;
	
	private int yearIndex;
	private int monthIndex;
	private String[] yearMonth;
	
	private int chartYear;
	private int chartMonth;
	private GraphicalView mGraphicalView;
	/**
	 * 图表绘制的帮助类
	 * @author Agassi
	 *
	 */
	private class ChartViewHelper extends AbstractChart{}

	private ChartViewHelper chartViewHelper = new ChartViewHelper();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout.chart);
		
		chartLayout= (LinearLayout)findViewById(id.chartView);
		itemButton = (Button)findViewById(id.btnChartItem);
		dateButton = (Button)findViewById(id.btnChartDate);
		timeButton = (Button)findViewById(id.btnChartTime);
		
		mAdapter = new DbAdapter(ChartActivity.this);
		mAdapter.open();
		monthCursor = mAdapter.fetchDiffMonth();
		startManagingCursor(monthCursor);
		if (monthCursor.getCount() != 0) {
			monthCursor.moveToFirst();
			yearIndex = monthCursor.getColumnIndex(YEAR);
			monthIndex = monthCursor.getColumnIndex(MONTH);
			chartYear = monthCursor.getInt(yearIndex);
			chartMonth = monthCursor.getInt(monthIndex);
			yearMonth = new String[monthCursor.getCount()];
			int i = 0;
			while (!monthCursor.isAfterLast()) {
				yearMonth[i++] = monthCursor.getInt(yearIndex) + "-"
						+ (monthCursor.getInt(monthIndex) + 1);
				monthCursor.moveToNext();
			}
			dateButton.setText(yearMonth[0]);
		}
		
		
		itemButton.setOnClickListener(itemClickListener);
		
		dateButton.setOnClickListener(dateBtnclickListener);
		
		timeButton.setOnClickListener(timeBtnClickListener);
	}
	
	private OnClickListener dateBtnclickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(ChartActivity.this);
			builder.setTitle("Pick a month");
			builder.setSingleChoiceItems(yearMonth, 0, dialogClickListener);
			
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
		}
	};
	/**
	 * 需要显示事件的图表按钮的监听器
	 */
	private OnClickListener itemClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final Cursor monthThingNameCursor;
			Log.w("ym","year:"+chartYear+"---"+"month:"+chartMonth);
			monthThingNameCursor = mAdapter.fetchMonthThingsName(chartYear, chartMonth);
			startManagingCursor(monthThingNameCursor);
			monthThingNameCursor.moveToFirst();
			
			AlertDialog.Builder builder = new AlertDialog.Builder(ChartActivity.this);
			builder.setTitle("Pick some things");
			int selectIndex = monthThingNameCursor.getColumnIndex(THINGS_CHART_FLAG);
			int nameIndex = monthThingNameCursor.getColumnIndex(THINGS_NAME);
			String[] monthThingsName = new String[monthThingNameCursor.getCount()];
			boolean[] isSelect = new boolean[monthThingNameCursor.getCount()];
			int pos = 0;
			while(!monthThingNameCursor.isAfterLast()){
				if(monthThingNameCursor.getInt(selectIndex) == 0 )
					isSelect[pos] = false;
				else
					isSelect[pos] = true;
				monthThingsName[pos++] = monthThingNameCursor.getString(nameIndex);
				monthThingNameCursor.moveToNext();
			}
			
			//setMultiChoiceItems 本来是用cursor来做第一个参数的。这样比较简单
			//但是1.6的SDK有个bug（CursorAdapter的autoRequery 参数默认是false的）。用这个做参数不能显示选中状态。
			builder.setMultiChoiceItems(monthThingsName, isSelect, new OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					int idIndex = monthThingNameCursor.getColumnIndex(ID);
					
					int needChartThingsNum = mAdapter.getNeedChartThingsName().getCount();
					monthThingNameCursor.moveToPosition(which);
					if(isChecked){
						mAdapter.updateThingIsChart(monthThingNameCursor.getInt(idIndex), 1);
						needChartThingsNum++;
					}else{
						mAdapter.updateThingIsChart(monthThingNameCursor.getInt(idIndex), 0);
						needChartThingsNum--;
					}
					//如果用户要求显示的事件太多，由于手机屏幕尺寸的限制，会造成图表杂乱。
					//于是，在选择事件多余8件的时候会有一个TOAST的提示
					if(needChartThingsNum > 8){
						Toast.makeText(ChartActivity.this, "too many things will make the chart bring!!!",
								Toast.LENGTH_SHORT).show();
					}
					
				}
			});
			
			AlertDialog alertDialog = builder.create();
			//显示时间chart
			alertDialog.setButton("show chart", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					int[] allColors = new int[]{Color.CYAN,Color.DKGRAY,Color.GRAY,
							Color.GREEN,Color.LTGRAY,Color.MAGENTA,Color.RED,Color.WHITE,Color.YELLOW,Color.BLUE};
					Cursor thingsMonthRaingCursor;
					List<double[]> x = new ArrayList<double[]>();
					List<double[]> values = new ArrayList<double[]>();
					
					
					int needChartThingsNum = mAdapter.getNeedChartThingsName().getCount();
					Cursor thingsNameCursor = mAdapter.getNeedChartThingsName();
					startManagingCursor(thingsNameCursor);
					thingsNameCursor.moveToFirst();
					String[] title = new String[needChartThingsNum];
					
					int[] useColors = new int[needChartThingsNum];
					int chartThingsNameIndex = thingsNameCursor.getColumnIndex(THINGS_NAME);
					PointStyle[] pointStyle = new PointStyle[needChartThingsNum];
					for(int i=0;i < needChartThingsNum; i++){
						useColors[i] = allColors[i%10];
						pointStyle[i] = PointStyle.POINT;
						
						title[i] = thingsNameCursor.getString(chartThingsNameIndex);
						thingsNameCursor.moveToNext();
					}
					for(String name : title){
						thingsMonthRaingCursor = mAdapter.fetchNeedChartThingsRating(chartYear, chartMonth, name);
						startManagingCursor(monthThingNameCursor);
						thingsMonthRaingCursor.moveToFirst();
						int ratingIndex = thingsMonthRaingCursor.getColumnIndex(RATING_RATING);
						int dayIndex = thingsMonthRaingCursor.getColumnIndex(DAY_OF_MONTH);
						double[] ratings = new double[thingsMonthRaingCursor.getCount()];
						double[] days = new double[thingsMonthRaingCursor.getCount()];
						int p = 0;
						while(!thingsMonthRaingCursor.isAfterLast()){
							ratings[p] = thingsMonthRaingCursor.getDouble(ratingIndex);
							days[p++] = thingsMonthRaingCursor.getDouble(dayIndex);
							Log.w("test", ratings[p-1]+"--"+days[p-1]);
							thingsMonthRaingCursor.moveToNext();
						}
						x.add(days);
						values.add(ratings);
					}
					
					XYMultipleSeriesRenderer renderer = chartViewHelper.buildRenderer(useColors, pointStyle);
					chartViewHelper.setChartSettings(renderer, "", "day", "Rating", 
							1, 31, 0, 5, Color.GRAY, Color.LTGRAY);
					renderer.setXLabels(10);
				    renderer.setYLabels(10);
				    renderer.setShowGrid(true);
				    mGraphicalView = ChartFactory.getLineChartView(ChartActivity.this, chartViewHelper.buildDataset(title, x, values), renderer);
				    mGraphicalView.repaint();
				    chartLayout.removeAllViews();
					chartLayout.addView(mGraphicalView, new LayoutParams(LayoutParams.FILL_PARENT,
							LayoutParams.FILL_PARENT));
					ChartActivity.this.setTitle("Rating chart");
				    dialog.cancel();
				}
			});
			alertDialog.setButton2("cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			alertDialog.show();
		}
	};
	
	private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			monthCursor.moveToPosition(which);
			chartYear = monthCursor.getInt(yearIndex);
			chartMonth = monthCursor.getInt(monthIndex);
			
			dateButton.setText(chartYear+"-"+(chartMonth+1));
			
			dialog.cancel();
			
		}
	};
	/**
	 * 显示起床睡觉时间图表按钮的监听器
	 */
	private OnClickListener timeBtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			Cursor wakeupCursor = mAdapter.fetchTimeByMonth(chartYear, chartMonth,WSTIME_WAKEUP_TIME);
			Cursor sleepCursor = mAdapter.fetchTimeByMonth(chartYear, chartMonth,WSTIME_SLEEP_TIME);
			startManagingCursor(wakeupCursor);
			startManagingCursor(sleepCursor);
			wakeupCursor.moveToFirst();
			sleepCursor.moveToFirst();
			
			int wakeupIndex = wakeupCursor.getColumnIndex(WSTIME_WAKEUP_TIME);
			int wdayIndex = wakeupCursor.getColumnIndex(DAY_OF_MONTH);
			int sleepIndex = sleepCursor.getColumnIndex(WSTIME_SLEEP_TIME);
			int sdayIndex = sleepCursor.getColumnIndex(DAY_OF_MONTH);
			
			
			XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
			XYMultipleSeriesRenderer multipleSeriesRenderer = new XYMultipleSeriesRenderer();
			XYSeries wSeries = new XYSeries("wakeup");
			XYSeries sseries = new XYSeries("sleep");
			while(!wakeupCursor.isAfterLast()){
				wSeries.add(wakeupCursor.getInt(wdayIndex), 
						wakeupCursor.getFloat(wakeupIndex));
				wakeupCursor.moveToNext();
			}
			while(!sleepCursor.isAfterLast()){
				sseries.add(sleepCursor.getInt(sdayIndex), 
						sleepCursor.getFloat(sleepIndex));
				sleepCursor.moveToNext();
			}
			
			XYSeries healthyWakeupTime = new XYSeries("Healthy wakeup time");
			XYSeries healthySleepTime = new XYSeries("Healthy sleep time");
			for(float p =0 ; p != 62 ;p +=0.5){
				healthyWakeupTime.add(p, 7);
				healthySleepTime.add(p, 23);
			}
			
			
			dataset.addSeries(healthyWakeupTime);
			dataset.addSeries(healthySleepTime);
			dataset.addSeries(wSeries);
			dataset.addSeries(sseries);
			
			XYSeriesRenderer renderer1 = new XYSeriesRenderer();
			renderer1.setColor(Color.GREEN);
			renderer1.setPointStyle(PointStyle.POINT);
			multipleSeriesRenderer.addSeriesRenderer(renderer1);
			
			XYSeriesRenderer renderer2 = new XYSeriesRenderer();
			renderer2.setColor(Color.WHITE);
			renderer2.setPointStyle(PointStyle.POINT);
			multipleSeriesRenderer.addSeriesRenderer(renderer2);
			
			XYSeriesRenderer renderer3 = new XYSeriesRenderer();
			renderer3.setColor(Color.CYAN);
			renderer3.setPointStyle(PointStyle.TRIANGLE);
			multipleSeriesRenderer.addSeriesRenderer(renderer3);
			
			XYSeriesRenderer renderer4 = new XYSeriesRenderer();
			renderer4.setColor(Color.RED);
			renderer4.setPointStyle(PointStyle.CIRCLE);
			multipleSeriesRenderer.addSeriesRenderer(renderer4);
			
			
			
			chartViewHelper.setChartSettings(multipleSeriesRenderer, "wsTime", "day", "time", 0, 31, 0, 24,  Color.GRAY, Color.LTGRAY);
			multipleSeriesRenderer.setXLabels(6);
			multipleSeriesRenderer.setYLabels(12);
			((XYSeriesRenderer)multipleSeriesRenderer.getSeriesRendererAt(0)).setFillPoints(true);
			((XYSeriesRenderer)multipleSeriesRenderer.getSeriesRendererAt(1)).setFillPoints(true);
			((XYSeriesRenderer)multipleSeriesRenderer.getSeriesRendererAt(2)).setFillPoints(true);
			((XYSeriesRenderer)multipleSeriesRenderer.getSeriesRendererAt(3)).setFillPoints(true);
			mGraphicalView = ChartFactory.getScatterChartView(getApplicationContext(), dataset, multipleSeriesRenderer);
			mGraphicalView.repaint();
			chartLayout.removeAllViews();
			chartLayout.addView(mGraphicalView, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
		}
	};
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAdapter.close();
	}
}
