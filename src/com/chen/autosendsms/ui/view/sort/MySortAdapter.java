package com.chen.autosendsms.ui.view.sort;

import java.util.List;
import java.util.Locale;

import com.chen.autosendsms.R;
import com.chen.autosendsms.db.entities.Person;
import com.chen.autosendsms.ui.contacts.AddAndEditContactActivity;
import com.chen.autosendsms.utils.Utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class MySortAdapter extends BaseAdapter implements SectionIndexer {
	private List<SortModel<Person>> myList = null;
	private Context mContext;
	private OnListChangeListener mOnListChangeListener;
	/**是否允许删除*/
	private boolean mEnableDelete=true;
	/**是否允许修改*/
	private boolean mEnableEdit=true;
	
	public MySortAdapter(Context mContext, List<SortModel<Person>> list,OnListChangeListener onListChangeListener) {
		this.mContext = mContext;
		this.myList = list;
		mOnListChangeListener=onListChangeListener;
	}
	
	public MySortAdapter(Context mContext, List<SortModel<Person>> list,
			OnListChangeListener onListChangeListener,boolean enableDelete,
			boolean enabledEdit) {
		this.mContext = mContext;
		this.myList = list;
		this.mEnableDelete=enableDelete;
		this.mEnableEdit=enabledEdit;
		mOnListChangeListener=onListChangeListener;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<SortModel<Person>> list) {
		this.myList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return myList == null ? 0 : myList.size();
	}

	@Override
	public Object getItem(int position) {
		return myList == null ? null : myList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	public View getView(final int position, View convertView, ViewGroup arg2) {
		final SortModel<Person> mContent = myList.get(position);
		final Person person = (Person) mContent.getModel();
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_contactitem, null, false);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
		holder.tv_birthday = (TextView) convertView.findViewById(R.id.birthday);
		holder.tv_department = (TextView) convertView.findViewById(R.id.department);
		holder.tv_name = (TextView) convertView.findViewById(R.id.name);
		holder.tv_phoneNumber = (TextView) convertView.findViewById(R.id.phonenumber);

		holder.tv_name.setText(person.getLastName() + " " + person.getFirstName());
		holder.tv_department.setText(person.getDepartment());
		if(person.getBirthday()!=0){
			holder.tv_birthday.setVisibility(View.VISIBLE);
			holder.tv_birthday.setText(Utils.getBirthdayStr(person.getBirthday()));
		}else{
			holder.tv_birthday.setVisibility(View.GONE);
		}
		holder.tv_phoneNumber.setText(person.getPhoneNumber());
		holder.tv_phoneNumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.dialDialog(person.getPhoneNumber(),mContext);
			}
		});
		
		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			holder.tvLetter.setVisibility(View.VISIBLE);
			holder.tvLetter.setText(mContent.getSortLetters());
		} else {
			holder.tvLetter.setVisibility(View.GONE);
		}
		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
//				Message msg=new Message();
//				msg.what=Parameters.MYSORTADAPTER_DELETE_MSG;
//				msg.obj=position;
//				mHandler.sendMessage(msg);
				if(mEnableDelete){
					deleteAlert(position);
				}
				return true;
			}

		});
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mEnableEdit){
					Intent intent = new Intent();
					intent.setClass(mContext, AddAndEditContactActivity.class);
					intent.putExtra("person", person);
					mContext.startActivity(intent);
				}
			}

		});
		return convertView;
	}

	final class ViewHolder {
		private TextView tv_name;
		private TextView tv_phoneNumber;
		private TextView tv_birthday;
		private TextView tv_department;
		private TextView tvLetter;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	@Override
	public int getSectionForPosition(int position) {
		return myList.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = myList.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase(Locale.getDefault()).charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	/**
	 * 长按删除联系人
	 * 
	 * @param position
	 */
	private void deleteAlert(final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("要删除这个联系人码？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//提醒ui
				mOnListChangeListener.removeItem(position);
				notifyDataSetChanged();
				dialog.dismiss();
			}

		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}

		});
		builder.create().show();
	}
	
	public interface OnListChangeListener{
		public void removeItem(int position);
	}
}