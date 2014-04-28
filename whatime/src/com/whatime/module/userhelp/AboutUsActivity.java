package com.whatime.module.userhelp;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import com.whatime.R;

import android.app.Activity;
@EActivity(R.layout.about_us)
public class AboutUsActivity extends Activity
{
    @Click
    void login_reback_btn()
    {
        onBackPressed();
    }
}
