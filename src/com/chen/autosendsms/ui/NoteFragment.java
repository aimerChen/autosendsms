package com.chen.autosendsms.ui;

import java.sql.SQLException;

import com.chen.autosendsms.R;
import com.chen.autosendsms.db.dao.NoteDao;
import com.chen.autosendsms.db.entities.Note;
import com.chen.autosendsms.utils.Utils;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NoteFragment extends Fragment{

	private Note mNote=null;
	private EditText tv_note;
	private Button btn_save;
//	private DaoFactory mDaoFactory;
	private NoteDao mNoteDao=null;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup view=(ViewGroup) inflater.inflate(R.layout.activity_note, container,false);

		mNoteDao=new NoteDao(getActivity());
		intial(view);
		return view;
	}

	//	@Override
	//	public void onCreate(Bundle savedInstanceState){
	//		super.onCreate(savedInstanceState);
	//		setContentView(R.layout.activity_note);
	//		intial();
	//	}

	private void intial(View view){
		tv_note=(EditText)view.findViewById(R.id.note);
		btn_save=(Button)view.findViewById(R.id.save);
		btn_save.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(mNoteDao!=null){
					String noteStr=tv_note.getText().toString();
					if(noteStr==null||noteStr.equalsIgnoreCase("")){
						Toast.makeText(getActivity(), "这是送出的祝福的，请多输入一些哦！", Toast.LENGTH_LONG).show();
						return;
					}
					if(mNote==null){
						mNote=new Note();
					}
					mNote.setNote(noteStr);
						if(mNote.getId()<=0){
							mNoteDao.save(mNote);
						}else{
							mNoteDao.update(mNote);
						}
						
					Toast.makeText(getActivity(), "保存成功！", Toast.LENGTH_LONG).show();
					setNote();
					Utils.hiddenKeyBoard(getActivity(), tv_note);
				}
			}

		});
		setNote();
	}
	
	private void setNote(){
		if(mNoteDao==null){
			Log.e("NoteActivity","mDaoFactory==null");
		}else{
			mNote = mNoteDao.queryForTheFirst();
			if(mNote!=null){
				if(mNote.getNote()!=null&&mNote.getNote().length()>0){
					tv_note.setText(mNote.getNote());
				}	
			}
		}
	}
}
