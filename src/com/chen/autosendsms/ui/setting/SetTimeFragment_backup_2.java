package com.chen.autosendsms.ui.setting;

import java.util.Calendar;

import com.chen.autosendsms.R;
import com.chen.autosendsms.db.dao.TimeDao;
import com.chen.autosendsms.db.entities.MyTime;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import kankan.wheel.widget.OnWheelClickedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

public class SetTimeFragment_backup_2 extends Fragment {
	private Button btn_save;
	private WheelView hourWheel;
	private MyTime mMyTime = null;
	private TimeDao mTimeDao = null;
	private String[] hourContent = null;
	private TextView txt_timeset;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.activity_changetime, container, false);

		mTimeDao = new TimeDao(getActivity());
		intial(view);
		setWheelTime();
		return view;
	}

	private void intial(View view) {
		btn_save = (Button) view.findViewById(R.id.save);
		hourWheel = (WheelView) view.findViewById(R.id.hourwheel);
		txt_timeset = (TextView) view.findViewById(R.id.timeset);
		txt_timeset.setText("当前未设定");
		// wheel view中显示的字符
		hourContent = new String[24];
		for (int i = 0; i < 24; i++) {
			hourContent[i] = String.valueOf(i);
			if (hourContent[i].length() < 2) {
				hourContent[i] = "0" + hourContent[i];
			}
		}
		int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		hourWheel.setViewAdapter(new NumericWheelAdapter(getActivity(), 0, 23));
		hourWheel.setVisibleItems(8);
		hourWheel.setCurrentItem(curHour);
		// hourWheel.setCyclic(true);
		// hourWheel.setInterpolator(new AnticipateOvershootInterpolator());
		// OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
		// public void onChanged(WheelView wheel, int oldValue, int newValue) {
		// if (!timeScrolled) {
		// timeChanged = true;
		// picker.setCurrentHour(hours.getCurrentItem());
		// picker.setCurrentMinute(mins.getCurrentItem());
		// timeChanged = false;
		// }
		// }
		// };
		// hourWheel.addChangingListener(wheelListener);
		OnWheelClickedListener click = new OnWheelClickedListener() {
			public void onItemClicked(WheelView wheel, int itemIndex) {
				wheel.setCurrentItem(itemIndex, true);
			}
		};
		hourWheel.addClickingListener(click);
		// OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
		// public void onScrollingStarted(WheelView wheel) {
		// timeScrolled = true;
		// }
		// public void onScrollingFinished(WheelView wheel) {
		// timeScrolled = false;
		// timeChanged = true;
		// picker.setCurrentHour(hours.getCurrentItem());
		// picker.setCurrentMinute(mins.getCurrentItem());
		// timeChanged = false;
		// }
		// };
		//
		// hourWheel.addScrollingListener(scrollListener);

		// 保存按钮
		btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mTimeDao == null) {
					Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_SHORT).show();
					return;
				}
				if (mMyTime == null) {
					mMyTime = new MyTime();
				}
				int time = 0;
				time = hourWheel.getCurrentItem();
				if (time < 0 || time > 23) {
					Toast.makeText(getActivity(), "时间超出范围", Toast.LENGTH_SHORT).show();
					return;
				}
				mMyTime.setTime(time);
				int saved = 0;
				if (mMyTime.getId() <= 0) {
					saved = mTimeDao.save(mMyTime);
				} else {
					saved = mTimeDao.update(mMyTime);
				}
				if (saved > 0) {
					setTextViewTime();
					Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
				}
			}

		});
	}

	private void setWheelTime() {
		if (mTimeDao == null) {
			Log.e("NoteActivity", "mDaoFactory==null");
		} else {
			MyTime myTime = null;
			myTime = mTimeDao.queryForTheFirst();
			mMyTime = myTime == null ? null : myTime;
			if (mMyTime != null) {
				hourWheel.setCurrentItem(mMyTime.getTime());
				setTextViewTime();
			}
		}
	}

	private void setTextViewTime() {
		if (mMyTime.getTime() > 12) {
			txt_timeset.setText("下午" + (mMyTime.getTime() - 12) + "点");
		} else {
			txt_timeset.setText("上午" + mMyTime.getTime() + "点");
		}
	}

	/**
	 * Adds changing listener for wheel that updates the wheel label
	 * 
	 * @param wheel
	 *            the wheel
	 * @param label
	 *            the wheel label
	 */
	// private void addChangingListener(final WheelView wheel, final String
	// label) {
	// wheel.addChangingListener(new OnWheelChangedListener() {
	// public void onChanged(WheelView wheel, int oldValue, int newValue) {
	// //wheel.setLabel(newValue != 1 ? label + "s" : label);
	// }
	// });
	// }
}
