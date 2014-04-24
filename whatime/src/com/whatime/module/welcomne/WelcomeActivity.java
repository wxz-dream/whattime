package com.whatime.module.welcomne;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.whatime.R;
import com.whatime.controller.center.AlarmController;
import com.whatime.framework.aservice.AlarmCenterAService;
import com.whatime.framework.ui.activity.MainActivity;

@EActivity(R.layout.welcome)
public class WelcomeActivity extends Activity
{
    
    private Context context;
    
    private AlarmController controller = new AlarmController();
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context = this;
        new Thread()
        {
            public void run()
            {
                startService(new Intent(context, AlarmCenterAService.class));
            };
        }.start();
        controller.sync();
    }
    @AfterViews
    void initView()
    {
        context.startActivity(new Intent(context, MainActivity.class));
        finish();
    }
}
