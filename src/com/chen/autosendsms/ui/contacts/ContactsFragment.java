package com.chen.autosendsms.ui.contacts;

import java.util.List;

import com.chen.autosendsms.R;
import com.chen.autosendsms.db.dao.PersonDao;
import com.chen.autosendsms.db.entities.Person;
import com.chen.autosendsms.ui.localcontact.LocalContactActivity;
import com.chen.autosendsms.ui.view.sort.MySortFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ContactsFragment extends MySortFragment implements OnClickListener {

	private RelativeLayout mRelLayoutSort;
	private LinearLayout mLinearAddMenu;
	private Button mButtonAddContact;
	private Button mButtonImportLocalContact;
	private Button mButtonImportFileContact;
	private ImageButton mImageButtonToggleMenu;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_contacts, container, false);
		initViews(view);
		return view;
	}
	
	@Override
	public void initViews(View view) {
		mRelLayoutSort=(RelativeLayout)view.findViewById(R.id.include_sort_contact_layout);
		super.initViews(mRelLayoutSort);
		mLinearAddMenu=(LinearLayout)view.findViewById(R.id.rel_add_menu);
		mLinearAddMenu.setVisibility(View.GONE);
		
		mImageButtonToggleMenu=(ImageButton)view.findViewById(R.id.addContactMenu);
		mButtonAddContact=(Button)view.findViewById(R.id.add_contact);
		mButtonImportLocalContact=(Button)view.findViewById(R.id.import_local_contact);
		mButtonImportFileContact=(Button)view.findViewById(R.id.importFile);

		mImageButtonToggleMenu.setOnClickListener(this);
		mButtonAddContact.setOnClickListener(this);
		mButtonImportLocalContact.setOnClickListener(this);
		mButtonImportFileContact.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addContactMenu:
			toggleMenu();
			break;

		case R.id.add_contact:
			hiddenMenu();
			startActivity(new Intent(getActivity(), AddAndEditContactActivity.class));
			break;
		case R.id.importFile:
			hiddenMenu();
			startActivity(new Intent(getActivity(), ImportContactsActivity.class));
			break;
		case R.id.import_local_contact:
			hiddenMenu();
			startActivity(new Intent(getActivity(), LocalContactActivity.class));
			break;
		}

	}

	/**
	 * 显示和隐藏添加联系人菜单
	 */
	private void toggleMenu() {
		if (mLinearAddMenu.getVisibility() == View.GONE) {
			showMenu();
		} else {
			hiddenMenu();
		}
	}

	/**
	 * 显示菜单
	 */
	private void showMenu() {
		mLinearAddMenu.setVisibility(View.VISIBLE);
	}

	private void hiddenMenu() {
		mLinearAddMenu.setVisibility(View.GONE);
	}

	@Override
	public List<Person> getDataFromDatabase() {
		PersonDao personDao=new PersonDao(getActivity());
		return personDao.queryAll();
	}

}
