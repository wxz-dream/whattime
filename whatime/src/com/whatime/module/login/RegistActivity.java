package com.whatime.module.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.whatime.R;
import com.whatime.framework.network.pojo.ResponseCons;
import com.whatime.framework.network.service.RemoteApiImpl;
import com.whatime.framework.ui.view.ToastMaster;

public class RegistActivity extends Activity {
	private EditText mUser;
	private EditText mPassword;
	private Button back;
	private Button regist_btn;
	private MyOnclickListener listener;
	private Handler myHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch(msg.what)
            {
                case 0x001:
                    int state = msg.getData().getInt(ResponseCons.STATE);
                    if(state==ResponseCons.STATE_SUCCESS)
                    {
                        Toast toast = Toast.makeText(RegistActivity.this, "注册成功", Toast.LENGTH_SHORT);
                        ToastMaster.setToast(toast);
                        toast.show();
                        RegistActivity.this.finish();
                        startActivity(new Intent(RegistActivity.this,UserInfoActivity.class));
                    }
                    else
                    {
                        Toast toast = Toast.makeText(RegistActivity.this, "注册失败，请重新尝试", Toast.LENGTH_SHORT);
                        ToastMaster.setToast(toast);
                        toast.show();
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist);
        
        mUser = (EditText)findViewById(R.id.login_user_edit);
        mPassword = (EditText)findViewById(R.id.login_passwd_edit);
        back = (Button)findViewById(R.id.regist_reback_btn);
        regist_btn = (Button)findViewById(R.id.regist_btn);
        listener = new MyOnclickListener();
        back.setOnClickListener(listener);
        regist_btn.setOnClickListener(listener);
    }
    class MyOnclickListener implements View.OnClickListener{

        @Override
        public void onClick(View v)
        {
            switch(v.getId())
            {
                case R.id.regist_reback_btn:
                    RegistActivity.this.finish();
                    break;
                case R.id.regist_btn:
                    String userName = mUser.getText().toString();
                    String password = mPassword.getText().toString();
                    new RemoteApiImpl().registUser(userName, password,myHandler);
                    break;
            }
        }
        
    }
}
