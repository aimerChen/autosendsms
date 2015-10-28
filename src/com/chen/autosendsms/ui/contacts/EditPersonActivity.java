package com.chen.autosendsms.ui.contacts;

import java.util.Calendar;

import com.chen.autosendsms.R;
import com.chen.autosendsms.db.dao.PersonDao;
import com.chen.autosendsms.db.entities.Person;
import com.chen.autosendsms.utils.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

/**
 * 添加员工activity
 * @author chen
 *
 */
public class EditPersonActivity extends Activity{
   
//	private Button bt_cancel;
	private Button bt_ok;
	
	private EditText edt_lastName;
	private EditText edt_firstName;
	private EditText edt_phonenumber;
	private WheelView mViewYear;
	private WheelView mViewMonth;
	private WheelView mViewDay;
	private int MIN_DAY=1;
	private int MIN_MONTH=1;
	private int MIN_YEAR=1950;
	private int MAX_YEAR=2015;
	private int DEFAULT_YEAR=1988;
	private NumericWheelAdapter m29DayAdapter;
	private NumericWheelAdapter m30DayAdapter;
	private NumericWheelAdapter m31DayAdapter;
	
//	private DaoFactory mDaoFactory=null;
	private PersonDao mPersonDao=null;
	private Person mPerson=null;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editperson);
        try{
        	mPerson=(Person)getIntent().getSerializableExtra("person");
        }catch(Exception e){
        	e.printStackTrace();
        	finish();
        }
        inital();
    	setValue();
    }
    
    private void inital(){
//    	mDaoFactory=DaoFactory.getDaoFactory(getApplicationContext());
//    	mPersonDao=mDaoFactory.getPersonDao();
    	mPersonDao=new PersonDao(getApplicationContext());
//    	bt_cancel=(Button)findViewById(R.id.cancel);
    	bt_ok=(Button)findViewById(R.id.ok);
    	
    	edt_lastName=(EditText)findViewById(R.id.lastname);
    	edt_firstName=(EditText)findViewById(R.id.firstname);
    	edt_phonenumber=(EditText)findViewById(R.id.phonenumber);
    	mViewYear=(WheelView)findViewById(R.id.year);
    	mViewMonth=(WheelView)findViewById(R.id.month);
    	mViewDay=(WheelView)findViewById(R.id.day);
    	
    	mViewYear.setViewAdapter(new NumericWheelAdapter(this, MIN_YEAR, MAX_YEAR));
//    	mViewDay.setVisibleItems(8);
//		int curYear = Calendar.getInstance().get(Calendar.YEAR);
		mViewYear.setCurrentItem(DEFAULT_YEAR-MIN_YEAR);
    	
		mViewMonth.setViewAdapter(new NumericWheelAdapter(this, MIN_MONTH, 12));
        OnWheelChangedListener listener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
            	changeDayViewAdapter(newValue);
            }
        };
		mViewMonth.addChangingListener(listener);
//    	mViewDay.setVisibleItems(8);
		int curMonth = Calendar.getInstance().get(Calendar.MONTH);
		mViewMonth.setCurrentItem(curMonth);//item的序号从0开始，月份也是从0开始，不需要改变

		
		m29DayAdapter=new NumericWheelAdapter(this, MIN_DAY, 29);
		m30DayAdapter=new NumericWheelAdapter(this, MIN_DAY, 30);
		m31DayAdapter=new NumericWheelAdapter(this, MIN_DAY, 31);
		
		changeDayViewAdapter(curMonth);
		int curDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    	mViewDay.setCurrentItem(curDay-MIN_DAY);//item的序号从0开始，需要-1
    	
//    	bt_cancel.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//    		
//    	});
    	bt_ok.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				savePerson();
			}
    		
    	});
    	
    }
    
    private void setValue(){
    	if(mPerson!=null){
        	edt_lastName.setText(mPerson.getLastName());
        	edt_firstName.setText(mPerson.getFirstName());
        	edt_phonenumber.setText(mPerson.getPhoneNumber());
        	int[] birthdayArray=Utils.getBirthdayArray(mPerson.getBirthday());
        	mViewYear.setCurrentItem(birthdayArray[0]-MIN_YEAR);
        	mViewMonth.setCurrentItem(birthdayArray[1]-MIN_MONTH);
        	mViewDay.setCurrentItem(birthdayArray[2]-MIN_DAY);
    	}
    }
    
    private void savePerson(){
    	String lastName=edt_lastName.getText().toString().trim();
		String firstName=edt_firstName.getText().toString().trim();
		String phoneNumber=edt_phonenumber.getText().toString().trim();
		if(lastName==null||lastName.length()==0||phoneNumber==null||phoneNumber.length()==0){
			Toast.makeText(getApplicationContext(), "信息不完整", Toast.LENGTH_SHORT).show();
			return;
		}
		if(firstName==null){
			firstName="";
		}
		
		if(mPersonDao!=null){
			long resultTs=Utils.stringToLong(createBirthdayString());
			if(resultTs==0){
				Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_SHORT).show();
				return;
			}else{
				mPerson.setBirthday(resultTs);
				mPerson.setFirstName(firstName);
				mPerson.setLastName(lastName);
				mPerson.setPhoneNumber(phoneNumber);
				int re=mPersonDao.update(mPerson);
				if(re>0){
					Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
					Utils.hiddenKeyBoard(getApplicationContext(), edt_phonenumber);
					finish();
				}else{
					Toast.makeText(getApplicationContext(), "修改保存失败", Toast.LENGTH_SHORT).show();
				}
			}
		}else{
			Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_SHORT).show();
		}
    }
    
    
    /**
     * 创建日期格式字符串，以便存入数据库
     * @return
     */
    private String createBirthdayString(){
    	StringBuilder strBuild=new StringBuilder();
    	strBuild.append(mViewYear.getCurrentItem()+MIN_YEAR);
    	int month=mViewMonth.getCurrentItem()+MIN_MONTH;
    	if(month<10){
        	strBuild.append("0");
    	}
    	strBuild.append(month);
    	int day=mViewDay.getCurrentItem()+MIN_DAY;
    	if(day<10){
    		strBuild.append("0");	
    	}
    	strBuild.append(day);
//    	Toast.makeText(getApplicationContext(), strBuild, Toast.LENGTH_LONG).show();
    	return strBuild.toString();
    }

    /**
     * 根据月份改变天数的长度
     * @param month
     */
    private void changeDayViewAdapter(int month){
		if(month==1){
			//二月只有29天
	    	mViewDay.setViewAdapter(m29DayAdapter);
		}else if(month==3||month==5||month==8||month==10){
			//4，6，9，10月只有30天
	    	mViewDay.setViewAdapter(m30DayAdapter);
		}else{
			//其他有31天
	    	mViewDay.setViewAdapter(m31DayAdapter);
		}
		//可能刚才滑到31号，现在 没有31了，需要自动滑到30
		if(mViewDay.getCurrentItem()>=30){
			mViewDay.setCurrentItem(29);
		}
    }

}
