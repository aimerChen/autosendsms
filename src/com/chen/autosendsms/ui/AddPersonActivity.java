package com.chen.autosendsms.ui;

import java.sql.SQLException;

import com.chen.autosendsms.R;
import com.chen.autosendsms.db.dao.BaseDao;
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

/**
 * 添加员工activity
 * @author chen
 *
 */
public class AddPersonActivity extends Activity{
   
	private Button bt_cancel;
	private Button bt_ok;

//	private ImageButton bt_cancel;
//	private ImageButton bt_ok;
	
	private EditText edt_lastName;
	private EditText edt_firstName;
	private EditText edt_phonenumber;
	private EditText edt_birthday;
	
//	private ServiceFactory mServiceFactory=null;
	private BaseDao<Person,Integer> mPersonDao=null;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addperson);
        inital();
        
        
    }
    
    private void inital(){
//    	mServiceFactory=ServiceFactory.getServiceFactory(getApplicationContext());
//    	mPersonService=mServiceFactory.getPersonService();
    	mPersonDao=new PersonDao(getApplicationContext());
    	
    	bt_cancel=(Button)findViewById(R.id.cancel);
    	bt_ok=(Button)findViewById(R.id.ok);
    	
    	edt_lastName=(EditText)findViewById(R.id.lastname);
    	edt_firstName=(EditText)findViewById(R.id.firstname);
    	edt_phonenumber=(EditText)findViewById(R.id.phonenumber);
    	edt_birthday=(EditText)findViewById(R.id.birthday);
    	
    	
    	bt_cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
    		
    	});
    	bt_ok.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String lastName=edt_lastName.getText().toString().trim();
				String firstName=edt_firstName.getText().toString().trim();
				String phoneNumber=edt_phonenumber.getText().toString().trim();
				String birthday=edt_birthday.getText().toString().trim();
				if(lastName==null||phoneNumber==null||birthday==null){
					Toast.makeText(getApplicationContext(), "信息不完整", Toast.LENGTH_SHORT).show();
					return;
				}
				if(firstName==null){
					firstName="";
				}
				
				if(mPersonDao!=null){
					Person person=new Person();
					try{
						long resultTs=Utils.stringToLong(birthday);
						if(resultTs==0){
							Toast.makeText(getApplicationContext(), "生日格式不对,需写成1988-10-30", Toast.LENGTH_SHORT).show();
							return;
						}else{
							person.setBirthday(resultTs);
						}
					}catch(Exception e){
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), "生日格式不对", Toast.LENGTH_SHORT).show();
						return;
					}
					person.setFirstName(firstName);
					person.setLastName(lastName);
					person.setPhoneNumber(phoneNumber);
					
					mPersonDao.save(person);
					Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();

					Utils.hiddenKeyBoard(getApplicationContext(), edt_birthday);
				}else{
					Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_SHORT).show();
				}
				finish();
			}
    		
    	});
    	
    }
}
