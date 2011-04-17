package com.vivissi.oneDay;

import static com.vivissi.oneDay.util.OneDay.THINGS_NAME;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.vivissi.oneDay.R.id;
import com.vivissi.oneDay.util.DbAdapter;
import com.vivissi.oneDay.util.OneDay;
/**
 * 
 * @author Agassi
 *
 */
public class ThingsRowAdapter extends CursorAdapter {

	private LayoutInflater mInflater;
	private int nameIndex;
	private int ratingIndex;
	private int rowIdIndex;
	private DbAdapter db;

	public ThingsRowAdapter(Context context, Cursor c) {
		super(context, c);
		nameIndex = c.getColumnIndex(THINGS_NAME);
		rowIdIndex = c.getColumnIndex(OneDay.ID);
		ratingIndex = c.getColumnIndex(OneDay.RATING_RATING);
		mInflater = LayoutInflater.from(context);
		db = new DbAdapter(context);
	}

	@Override
	public void bindView(View view, Context context, final Cursor cursor) {
		final long rowID = cursor.getLong(rowIdIndex);
		final String name = cursor.getString(nameIndex);
		final float ratings = cursor.getFloat(ratingIndex);
		final TextView nameTextView = (TextView) view
				.findViewById(id.thing_row_name);
		final RatingBar mRatingBar = (RatingBar) view
				.findViewById(id.ratingBar);
		nameTextView.setText(name);
		mRatingBar.setRating(ratings);

		mRatingBar
				.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

					@Override
					public void onRatingChanged(RatingBar ratingBar,
							float rating, boolean fromUser) {
						if (fromUser) {
							db.open();
							db.updateDay(rowID, rating);
							db.close();
						}
					}
				});

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final View view = mInflater.inflate(R.layout.thing_row_view, null);
		return view;
	}

}
