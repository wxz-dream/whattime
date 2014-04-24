package com.whatime.module.userheler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import com.whatime.R;

import android.app.Activity;
@EActivity(R.layout.user_helper)
public class UserHelperActivity extends Activity
{
    @Click
    void login_reback_btn()
    {
        onBackPressed();
    }
}
