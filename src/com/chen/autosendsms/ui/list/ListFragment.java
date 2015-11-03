package com.chen.autosendsms.ui.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.chen.autosendsms.R;
import com.chen.autosendsms.db.dao.PersonDao;
import com.chen.autosendsms.db.entities.Person;
import com.chen.autosendsms.ui.contacts.AddAndEditContactActivity;
import com.chen.autosendsms.ui.view.sort.CharacterParser;
import com.chen.autosendsms.ui.view.sort.ClearEditText;
import com.chen.autosendsms.ui.view.sort.PinyinComparator;
import com.chen.autosendsms.ui.view.sort.SideBar;
import com.chen.autosendsms.ui.view.sort.SideBar.OnTouchingLetterChangedListener;
import com.chen.autosendsms.ui.view.sort.SortModel;
import com.chen.autosendsms.utils.Utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ListFragment extends Fragment {
	// listview
	private ListView mListView;
	private List<Person> mList;
	private List<SortModel<Person>> SourceDateList;
	// search
	private ClearEditText mClearEditText;
	private MySortAdapter mAdapter;

	private TextView txt_contactsNumber;
	// sidebar
	private SideBar sideBar;
	private TextView text_Dialog;

	private PersonDao mPersonDao = null;
	private AlertDialog dialog = null;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_birthday_contact_list, container, false);
		initViews(view);
		return view;
	}

	private void initViews(View view) {

		mPersonDao = new PersonDao(getActivity());

		// class for converting Chinese to pinyin
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();

		// side bar
		sideBar = (SideBar) view.findViewById(R.id.sidrbar);
		text_Dialog = (TextView) view.findViewById(R.id.dialog);
		sideBar.setTextView(text_Dialog);
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = mAdapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					mListView.setSelection(position);
				}
			}
		});

		// listview
		mListView = (ListView) view.findViewById(R.id.searchList);
		mListView.setDividerHeight(0);
		// adapter
		mList = new ArrayList<Person>();
		SourceDateList=new ArrayList<SortModel<Person>>();
		mAdapter = new MySortAdapter(getActivity(),SourceDateList);
		mListView.setAdapter(mAdapter);

		// edit textview
		mClearEditText = (ClearEditText) view.findViewById(R.id.filter_edit);
		mClearEditText.addTextChangedListener(mTextWatcher);
		// add contact menu

		txt_contactsNumber = (TextView) view.findViewById(R.id.contactsNumber);
		txt_contactsNumber.setText("0");

		// hide keyboard when click white space
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.hiddenKeyBoard(getActivity(), mClearEditText);
			}
		});
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private List<SortModel<Person>> filledData(List<Person> data) {
		List<SortModel<Person>> mSortList = new ArrayList<SortModel<Person>>();
		for (Person person : data) {
			SortModel<Person> sortModel = new SortModel<Person>();
			sortModel.setModel(person);
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(person.getLastName());
			String sortString = pinyin.substring(0, 1).toUpperCase(Locale.getDefault());

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase(Locale.getDefault()));
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<SortModel<Person>> filterDateList = new ArrayList<SortModel<Person>>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel<Person> sortModel : SourceDateList) {
				StringBuilder name =new StringBuilder();
				name.append(sortModel.getModel().getLastName()).append(sortModel.getModel().getFirstName());
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name.toString()).startsWith(filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		setContactNumberText(filterDateList.size());
		mAdapter.updateListView(filterDateList);
	}

	/**
	 * 每次刷新列表
	 */
	@Override
	public void onResume() {
		super.onResume();
		// 从数据库读取联系人，并赋值给mList
		resetListView();
	}

	/**
	 * 从数据库获取出所有联系人
	 */
	private void getDataFromDatabase() {
		if (mPersonDao == null) {
			return;
		}
		mList=mPersonDao.getOneDayPerson(System.currentTimeMillis()/1000);
	}

	/**
	 * 显示共有多少联系人
	 */
	private void setContactNumberText(int size) {
		txt_contactsNumber.setText("" + size);
	}

	/**
	 *	回复初始状态
	 */
	private void resetListView() {
		getDataFromDatabase();
		SourceDateList = filledData(mList);
		Collections.sort(SourceDateList, pinyinComparator);
		setContactNumberText(SourceDateList.size());
		mAdapter.updateListView(SourceDateList);
	}
	
	/**
	 * 切换时关闭键盘
	 */
	@Override
	public void onStop() {
		super.onStop();
		Utils.hiddenKeyBoard(getActivity(), mClearEditText);
		mClearEditText.setText("");
	}

	/**
	 * 搜索框
	 */
	private TextWatcher mTextWatcher = new TextWatcher() {
		private CharSequence temp;
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			temp = s;
			if(temp.length()>0){
				sideBar.setVisibility(View.GONE);
			}else{
				sideBar.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			
			// TODO Auto-generated method stub
			if (temp == null || temp.length() == 0) {
				// 不搜索，则回到初始状态
				resetListView();//有可能在搜索是，已经删除一些联系人
				return;
			}
			filterData(temp.toString());
		}
	};
	
	/**
	 * 长按删除联系人
	 * 
	 * @param position
	 */
	private void deleteAlert(final List<SortModel<Person>> list, final int position) {
		if (list.get(position) != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("要删除这个联系人码？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (mPersonDao != null) {
						if (mPersonDao.delete(list.get(position).getModel()) > 0) {
							list.remove(position);
							setContactNumberText(list.size());
							mAdapter.notifyDataSetChanged();
						}
					}
					dialog.dismiss();
				}

			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}

			});
			dialog = builder.create();
			dialog.show();
		}
	}

	private class MySortAdapter extends BaseAdapter implements SectionIndexer {
		private List<SortModel<Person>> myList = null;
		private Context mContext;

		public MySortAdapter(Context mContext, List<SortModel<Person>> list) {
			this.mContext = mContext;
			this.myList = list;
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
			holder.tv_birthday.setText(Utils.getBirthdayStr(person.getBirthday()));
			holder.tv_phoneNumber.setText(person.getPhoneNumber());
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
					deleteAlert(myList,position);
					return true;
				}

			});
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(mContext, AddAndEditContactActivity.class);
					intent.putExtra("person", person);
					mContext.startActivity(intent);
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
	}
}

