package com.whatime.module.login;

import java.util.HashMap;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.renren.Renren;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;

import com.whatime.R;
import com.whatime.controller.cons.UserCons;
import com.whatime.db.User;
import com.whatime.framework.application.MyApp;
import com.whatime.framework.network.pojo.ResponseCons;
import com.whatime.framework.network.service.RemoteApiImpl;
import com.whatime.framework.ui.view.ToastMaster;
import com.whatime.module.thirdLogin.MyPlatformActionListener;

public class LoginActivity extends Activity implements Callback, OnClickListener, PlatformActionListener
{
    //定义Handler对象
    private Handler handler;
    
    private EditText mUser;
    
    private EditText mPassword;
    
    private Button back;
    
    private Button regist;
    
    private Button login_login_btn;
    
    private ImageView qq;
    
    private ImageView sina;
    
    private ImageView renren;
    
    private ImageView tecent;
    
    private TextView qq_name;
    
    private TextView sina_name;
    
    private TextView renren_name;
    
    private TextView tecent_name;
    
    private MyOnclickListener listener;
    
    private Context mContext;
    
    private Handler myHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0x001:
                    int state = msg.getData().getInt(ResponseCons.STATE);
                    if (state == ResponseCons.STATE_SUCCESS)
                    {
                        Toast toast = Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT);
                        ToastMaster.setToast(toast);
                        toast.show();
                        LoginActivity.this.finish();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(LoginActivity.this, "登录失败，请重新尝试", Toast.LENGTH_SHORT);
                        ToastMaster.setToast(toast);
                        toast.show();
                    }
                    break;
            }
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mContext = this;
        //初始化ShareSDK
        ShareSDK.initSDK(this);
        //实例化Handler对象并设置信息回调监听接口
        handler = new Handler(this);
        mUser = (EditText)findViewById(R.id.login_user_edit);
        mPassword = (EditText)findViewById(R.id.login_passwd_edit);
        back = (Button)findViewById(R.id.login_reback_btn);
        regist = (Button)findViewById(R.id.regist);
        login_login_btn = (Button)findViewById(R.id.login_btn);
        qq = (ImageView)findViewById(R.id.login_qq_bt);
        sina = (ImageView)findViewById(R.id.login_sina_bt);
        renren = (ImageView)findViewById(R.id.login_renren_bt);
        tecent = (ImageView)findViewById(R.id.login_tecent_bt);
        qq_name = (TextView)findViewById(R.id.login_qq_tv);
        sina_name = (TextView)findViewById(R.id.login_sina_tv);
        renren_name = (TextView)findViewById(R.id.login_renren_tv);
        tecent_name = (TextView)findViewById(R.id.login_tecent_tv);
        
        listener = new MyOnclickListener();
        back.setOnClickListener(listener);
        regist.setOnClickListener(listener);
        login_login_btn.setOnClickListener(listener);
        qq.setOnClickListener(this);
        sina.setOnClickListener(this);
        renren.setOnClickListener(this);
        tecent.setOnClickListener(this);
        initData();
        
    }
    
    private void initData()
    {
        ShareSDK.initSDK(this);
        Platform[] tmp = ShareSDK.getPlatformList(mContext);
        if (tmp == null)
        {
            return;
        }
        
        for (Platform wb : tmp)
        {
            if (wb.isValid())
            {
                String name = wb.getName();
                if (SinaWeibo.NAME.equals(name))
                {
                    sina_name.setText(wb.getDb().get("nickname"));
                }
                else if (TencentWeibo.NAME.equals(name))
                {
                    tecent_name.setText(wb.getDb().get("nickname"));
                }
                else if (Renren.NAME.equals(name))
                {
                    renren_name.setText(wb.getDb().get("nickname"));
                }
                else if (QZone.NAME.equals(name))
                {
                    qq_name.setText(wb.getDb().get("nickname"));
                }
            }
        }
        
    }
    
    class MyOnclickListener implements View.OnClickListener
    {
        
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.login_reback_btn:
                    LoginActivity.this.finish();
                    break;
                case R.id.regist:
                    startActivity(new Intent(LoginActivity.this, RegistActivity.class));
                    LoginActivity.this.finish();
                    break;
                case R.id.login_btn:
                    String userName = mUser.getText().toString();
                    String password = mPassword.getText().toString();
                    new RemoteApiImpl().login(userName, password, myHandler);
                    break;
            }
        }
        
    }
    
    /**
     * 点击按钮获取授权用户的资料
     */
    @Override
    public void onClick(View v)
    {
        
        String name = null;
        
        switch (v.getId())
        {
            case R.id.login_sina_bt:
                name = SinaWeibo.NAME;
                break;
            case R.id.login_tecent_bt:
                name = TencentWeibo.NAME;
                break;
            case R.id.login_renren_bt:
                name = Renren.NAME;
                break;
            case R.id.login_qq_bt:
                name = QZone.NAME;
                break;
        }
        
        if (name != null)
        {
            Platform weibo = ShareSDK.getPlatform(this, name);
            weibo.setPlatformActionListener(this);
            weibo.showUser(null);
        }
    }
    /** 
     * 处理从授权页面返回的结果
     * 
     * 如果获取到用户的名称，则显示名称；否则如果已经授权，则显示平台名称
     */
    @Override
    public boolean handleMessage(Message msg)
    {
        switch (msg.arg1)
        {
            case 1:
            { // 成功
                Platform weibo = (Platform)msg.obj;
                cn.sharesdk.tencent.qzone.QZone.ShareParams sp = new cn.sharesdk.tencent.qzone.QZone.ShareParams();
                sp.setText("测试分享的文本");
                // 执行图文分享
                weibo.setPlatformActionListener(new MyPlatformActionListener());
                weibo.share(sp);
                User u = MyApp.getInstance().getUser();
                if (u == null)
                {
                    u = new User();
                    u.setNickName(weibo.getDb().get("nickname"));
                    u.setUuid(UUID.randomUUID().toString());
                    u.setUserName(weibo.getDb().get("token"));
                    String name = weibo.getName();
                    int type = UserCons.AUTH_OUR;
                    if (SinaWeibo.NAME.equals(name))
                    {
                        type = UserCons.AUTH_SINA;
                    }
                    else if (TencentWeibo.NAME.equals(name))
                    {
                        type = UserCons.AUTH_TECENT;
                    }
                    else if (Renren.NAME.equals(name))
                    {
                        type = UserCons.AUTH_RENREN;
                    }
                    else if (QZone.NAME.equals(name))
                    {
                        type = UserCons.AUTH_QQ;
                    }
                    u.setAuthType(type);
                    new RemoteApiImpl().registUser(u.getUserName(), "000000", myHandler);
                    this.finish();
                }
                
            }
                break;
            case 2:
            { // 失败
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
                return false;
            }
            case 3:
            { // 取消
                return false;
            }
        }
        initData();
        return false;
    }
    
    protected void onDestroy()
    {
        //结束ShareSDK的统计功能并释放资源
        ShareSDK.stopSDK(this);
        super.onDestroy();
    }
    
    @Override
    public void onComplete(Platform plat, int action, HashMap<String, Object> res)
    {
        Message msg = new Message();
        msg.arg1 = 1;
        msg.arg2 = action;
        msg.obj = plat;
        handler.sendMessage(msg);
    }
    
    @Override
    public void onError(Platform platform, int i, Throwable throwable)
    {
        throwable.printStackTrace();
        Message msg = new Message();
        msg.arg1 = 2;
        msg.arg2 = i;
        msg.obj = platform;
        handler.sendMessage(msg);
    }
    
    @Override
    public void onCancel(Platform platform, int i)
    {
        Message msg = new Message();
        msg.arg1 = 3;
        msg.arg2 = i;
        msg.obj = platform;
        handler.sendMessage(msg);
    }
}
