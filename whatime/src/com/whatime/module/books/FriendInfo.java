package com.whatime.module.books;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.whatime.R;
import com.whatime.db.User;
@EActivity(R.layout.friend_info)
public class FriendInfo extends Activity
{
    private User user;
    
    @ViewById
    ImageView login_iv;
    
    @ViewById 
    TextView nick_name;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        user = (User)getIntent().getSerializableExtra("user");
    }
    
    @AfterViews
    void initView()
    {
        nick_name.setText(user.getNickName());
    }
    
    @Click
    void add_friend()
    {
        
        login_reback_btn();
    }
    
    @Click
    void login_reback_btn()
    {
        finish();
    }
    
}
