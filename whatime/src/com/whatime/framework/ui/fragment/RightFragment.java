/*
 * Copyright (C) 2012 yueyueniao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.whatime.framework.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.whatime.R;
import com.whatime.db.User;
import com.whatime.framework.application.MyApp;
import com.whatime.module.books.FriendReqActivity_;
import com.whatime.module.history.HistoryActivity_;
import com.whatime.module.login.LoginActivity;
import com.whatime.module.login.UserInfoActivity;
import com.whatime.module.thirdLogin.AuthActivity;

public class RightFragment extends Fragment
{
    private ImageView login_iv;
    
    private TextView user_name;
    
    private View view;
    
    private Context mContext;
    
    private TextView share_iv;
    
    private TextView friend_iv;
    
    private TextView history_iv;
    
    private TextView help_iv;
    
    private TextView about_iv;
    
    private MyOnclickListener listener;
    
    private BitmapUtils bitmapUtils;
    
    private User user = MyApp.getInstance().getUser();
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.right, null);
        mContext = inflater.getContext();
        listener = new MyOnclickListener();
        bitmapUtils = new BitmapUtils(mContext);
        initUi();
        
        return view;
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        logedUI();
    }
    
    private void initUi()
    {
        login_iv = (ImageView)view.findViewById(R.id.login_iv);
        user_name = (TextView)view.findViewById(R.id.user_name);
        logedUI();
        login_iv.setOnClickListener(listener);
        share_iv = (TextView)view.findViewById(R.id.share_iv);
        friend_iv = (TextView)view.findViewById(R.id.friend_iv);
        history_iv = (TextView)view.findViewById(R.id.history_iv);
        help_iv = (TextView)view.findViewById(R.id.help_iv);
        about_iv = (TextView)view.findViewById(R.id.about_iv);
        share_iv.setOnClickListener(listener);
        friend_iv.setOnClickListener(listener);
        history_iv.setOnClickListener(listener);
        help_iv.setOnClickListener(listener);
        about_iv.setOnClickListener(listener);
    }
    
    private void logedUI()
    {
        user = MyApp.getInstance().getUser();
        if (user != null)
        {
            user_name.setText(user.getNickName());
            bitmapUtils.display(login_iv, user.getUserphotoUri());
        }
    }
    
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }
    
    class MyOnclickListener implements OnClickListener
    {
        
        @Override
        public void onClick(View arg0)
        {
            switch (arg0.getId())
            {
                case R.id.login_iv:
                    
                    if (user != null)
                    {
                        startActivity(new Intent(mContext, UserInfoActivity.class));
                    }
                    else
                    {
                        startActivity(new Intent(mContext, LoginActivity.class));
                    }
                    break;
                case R.id.share_iv:
                    startActivity(new Intent(mContext, AuthActivity.class));
                    break;
                case R.id.friend_iv:
                    startActivity(new Intent(mContext, FriendReqActivity_.class));
                    break;
                case R.id.history_iv:
                    startActivity(new Intent(mContext, HistoryActivity_.class));
                    break;
                case R.id.help_iv:
                    break;
                case R.id.about_iv:
                    break;
            }
        }
    }
}
