package com.chen.autosendsms.ui.list;

import java.util.List;

import com.chen.autosendsms.R;
import com.chen.autosendsms.db.dao.PersonDao;
import com.chen.autosendsms.db.entities.Person;
import com.chen.autosendsms.ui.view.sort.MySortFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class ListContactFragment extends MySortFragment {

	private RelativeLayout mRelLayoutSort;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_contact_birthday_list, container, false);
		initViews(view);
		return view;
	}

	@Override
	public void initViews(View view) {
		mRelLayoutSort = (RelativeLayout) view.findViewById(R.id.include_sort_contact_layout_list);
		super.initViews(mRelLayoutSort);
	}

	@Override
	public List<Person> getDataFromDatabase() {
		PersonDao personDao = new PersonDao(getActivity());
		return personDao.getOneDayPerson(System.currentTimeMillis() / 1000);
	}
}
