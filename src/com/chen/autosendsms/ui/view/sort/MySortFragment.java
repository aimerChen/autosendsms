package com.chen.autosendsms.ui.view.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.chen.autosendsms.R;
import com.chen.autosendsms.db.dao.PersonDao;
import com.chen.autosendsms.db.entities.Person;
import com.chen.autosendsms.ui.view.sort.MySortAdapter.OnListChangeListener;
import com.chen.autosendsms.ui.view.sort.SideBar.OnTouchingLetterChangedListener;
import com.chen.autosendsms.utils.Utils;

import android.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * <p>描述：模板Fragment
 * <p>使用：使用此Fragment必须
 * <li>1.在layout文件中include 布局文件R.layout.fragment_contact_sort_list
 * <li>2.重写并执行父类initViews来初始化views
 * @author chen
 *
 */

public abstract class MySortFragment extends Fragment {
	// listview
	private ListView mListView;
	private List<Person> mList;
	private List<SortModel<Person>> mSourceDateList;
	private List<SortModel<Person>> mFilterDateList;
	private boolean isFilter=false;
	// search
	private ClearEditText mClearEditText;
	private MySortAdapter mAdapter;

	private TextView txt_contactsNumber;
	// sidebar
	private SideBar sideBar;
	private TextView text_Dialog;

	private PersonDao mPersonDao = null;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	private boolean mEnableDelete=true;
	private boolean mEnableEdit=true;
	
	/**
	 * need to be override to set the view for this views
	 * @param view
	 */
	public void initViews(View view) {
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
		mSourceDateList=new ArrayList<SortModel<Person>>();
		mFilterDateList=new ArrayList<SortModel<Person>>();
		mAdapter = new MySortAdapter(getActivity(),mSourceDateList,mOnListChangeListener,mEnableDelete,mEnableEdit);
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
	 * 从数据库获取出所有联系人
	 */
	public abstract List<Person> getDataFromDatabase();
	
	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private List<SortModel<Person>> filledData(List<Person> data) {
		List<SortModel<Person>> mSortList = new ArrayList<SortModel<Person>>();
		if(data==null){
			return mSortList;
		}
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
	
	private OnListChangeListener mOnListChangeListener=new OnListChangeListener() {
		
		@Override
		public void removeItem(int position) {
			if(isFilter){
				deleteItem(mFilterDateList,position);
				mFilterDateList.remove(position);
				setContactNumberText(mFilterDateList.size());
			}else{
				deleteItem(mSourceDateList,position);
				mSourceDateList.remove(position);
				setContactNumberText(mSourceDateList.size());
			}
		}
	};
	
	private void deleteItem(List<SortModel<Person>> list,int position){
		mPersonDao.delete(list.get(position).getModel());
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		if (TextUtils.isEmpty(filterStr)) {
			mFilterDateList = mSourceDateList;
		} else {
			mFilterDateList.clear();
			for (SortModel<Person> sortModel : mSourceDateList) {
				StringBuilder name =new StringBuilder();
				name.append(sortModel.getModel().getLastName()).append(sortModel.getModel().getFirstName());
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name.toString()).startsWith(filterStr.toString())) {
					mFilterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(mFilterDateList, pinyinComparator);
		setContactNumberText(mFilterDateList.size());
		mAdapter.updateListView(mFilterDateList);
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
	 * 显示共有多少联系人
	 */
	private void setContactNumberText(int size) {
		txt_contactsNumber.setText("" + size);
	}

	/**
	 *	回复初始状态
	 */
	private void resetListView() {
		mList=getDataFromDatabase();
		mSourceDateList = filledData(mList);
		Collections.sort(mSourceDateList, pinyinComparator);
		setContactNumberText(mSourceDateList.size());
		mAdapter.updateListView(mSourceDateList);
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
				isFilter=false;
				resetListView();//有可能在搜索是，已经删除一些联系人
				return;
			}
			isFilter=true;
			filterData(temp.toString());
		}
	};
	
	/**
	 * <p>是否允许删除功能
	 * <p>必须在函数initViews之前调用
	 * @param enabled
	 */
	public void setEnableDelete(boolean enabled){
		this.mEnableDelete=enabled;
	}
	
	/**
	 * <p>是否允许删除功能
	 * <p>必须在函数initViews之前调用
	 * @param enabled
	 */
	public void setEnableEdit(boolean enabled){
		this.mEnableEdit=enabled;
	}
	
}

