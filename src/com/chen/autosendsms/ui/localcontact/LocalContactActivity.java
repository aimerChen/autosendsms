package com.chen.autosendsms.ui.localcontact;

import java.util.List;

import com.chen.autosendsms.R;
import com.chen.autosendsms.db.entities.Person;
import com.chen.autosendsms.ui.view.sort.MySortFragment;
import com.chen.autosendsms.utils.Utils;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LocalContactActivity extends Activity {

	private LocalContactFragment mFragmentSort;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import_local_contacts);
		initial();
	}

	private void initial() {
		mFragmentSort = new LocalContactFragment();
		mFragmentSort.setEnableDelete(false);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, mFragmentSort);
		transaction.commit();

	}

	private class LocalContactFragment extends MySortFragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_contact_sort_list, container, false);
			setEnableDelete(false);
			setEnableEdit(false);
			initViews(view);
			return view;
		}

		@Override
		public void initViews(View view) {
			super.initViews(view);
		}

		@Override
		public List<Person> getDataFromDatabase() {
			return Utils.readLocalContacts(getApplicationContext());
		}
	}

}
