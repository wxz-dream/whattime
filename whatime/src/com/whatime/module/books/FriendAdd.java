package com.whatime.module.books;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Toast;

import com.whatime.R;
import com.whatime.db.User;
import com.whatime.framework.network.pojo.ResponseCons;
import com.whatime.framework.network.service.RemoteApiImpl;
import com.whatime.framework.ui.view.ToastMaster;

@EActivity(R.layout.friend_add)
public class FriendAdd extends Activity
{
    @ViewById
    EditText username;
    
    private Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case 0x001:
                    int state = msg.getData().getInt(ResponseCons.STATE);
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        User user = (User)msg.getData().getSerializable(ResponseCons.RESINFO);
                        if(user!=null)
                        {
                            startActivity(new Intent(FriendAdd.this, FriendInfo_.class).putExtra("user", user));
                            login_reback_btn();
                        }
                        else
                        {
                            Toast toast = Toast.makeText(FriendAdd.this, "查无此人", Toast.LENGTH_SHORT);
                            ToastMaster.setToast(toast);
                            toast.show();
                        }
                    }
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    
    @Click
    void search()
    {
        new RemoteApiImpl().getUserByUserName(username.getText().toString(),handler);
    }
    
    @Click
    void login_reback_btn()
    {
        finish();
    }
}
