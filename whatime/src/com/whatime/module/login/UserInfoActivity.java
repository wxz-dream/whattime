package com.whatime.module.login;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.whatime.R;
import com.whatime.db.User;
import com.whatime.framework.application.MyApp;
import com.whatime.framework.network.pojo.ResponseCons;
import com.whatime.framework.network.service.RemoteApiImpl;
import com.whatime.framework.ui.view.ToastMaster;

public class UserInfoActivity extends Activity
{
    private Uri photoUri;
    
    private ImageView photo_iv;
    
    private MyOnclickListener listener;
    
    private TextView userName;
    
    private EditText nickName;
    
    private Spinner sex;
    
    private EditText email;
    
    private EditText realName;
    
    private TextView city;
    
    private EditText phone;
    
    private EditText identityCard;
    
    private EditText qq;
    
    private Button save;
    
    private Button login_reback_btn;
    
    private User user = MyApp.getInstance().getUser();
    
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
                        Toast toast = Toast.makeText(UserInfoActivity.this, "修改成功", Toast.LENGTH_SHORT);
                        ToastMaster.setToast(toast);
                        toast.show();
                        UserInfoActivity.this.finish();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(UserInfoActivity.this, "修改失败，请重新尝试", Toast.LENGTH_SHORT);
                        ToastMaster.setToast(toast);
                        toast.show();
                    }
                    break;
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo);
        listener = new MyOnclickListener();
        initUi();
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        user = MyApp.getInstance().getUser();
        logedUi();
    }
    
    private void logedUi()
    {
        if (user != null)
        {
            if (user.getUserphotoUri() != null && user.getUserphotoUri().length() > 0)
            {
                photoUri = Uri.parse(user.getUserphotoUri());
                Log.e("uri", photoUri.toString());
                ContentResolver cr = this.getContentResolver();
                try
                {
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(photoUri));
                    /* 将Bitmap设定到ImageView */
                    photo_iv.setImageBitmap(bitmap);
                }
                catch (FileNotFoundException e)
                {
                    Log.e("Exception", e.getMessage(), e);
                }
            }
            else
            {
                
            }
            if(user.getUserName()!=null)
            {
                userName.setText(user.getUserName());
            }
            if(user.getNickName()!=null)
            {
                nickName.setText(user.getNickName());
            }
            if(user.getSex()!=null)
            {
                sex.setSelection(user.getSex());
            }
            if(user.getEmail()!=null)
            {
                email.setText(user.getEmail());
            }
            if(user.getRealName()!=null)
            {
                realName.setText(user.getRealName());
            }
            if(user.getCity()!=null)
            {
                city.setText(user.getCity());
            }
            if(user.getTelphone()!=null)
            {
                phone.setText(user.getTelphone());
            }
            if(user.getIdentityCard()!=null)
            {
                identityCard.setText(user.getIdentityCard());
            }
            if(user.getQq()!=null)
            {
                qq.setText(user.getQq());
            }
            
        }
    }
    
    private void initUi()
    {
        initPhoto();
        userName = (TextView)findViewById(R.id.username);
        nickName = (EditText)findViewById(R.id.nickName);
        sex = (Spinner)findViewById(R.id.sex);
        email = (EditText)findViewById(R.id.email);
        realName = (EditText)findViewById(R.id.realName);
        city = (TextView)findViewById(R.id.city);
        phone = (EditText)findViewById(R.id.phone);
        identityCard = (EditText)findViewById(R.id.identityCard);
        qq = (EditText)findViewById(R.id.qq);
        initSave();
        login_reback_btn = (Button)findViewById(R.id.login_reback_btn);
        login_reback_btn.setOnClickListener(listener);
    }
    
    private void initSave()
    {
        save = (Button)findViewById(R.id.userinfo_save);
        save.setOnClickListener(listener);
    }
    
    private void initPhoto()
    {
        photo_iv = (ImageView)findViewById(R.id.photo_iv);
        photo_iv.setOnClickListener(listener);
    }
    
    class MyOnclickListener implements OnClickListener
    {
        
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.photo_iv:
                    Intent intent = new Intent();
                    /* 开启Pictures画面Type设定为image */
                    intent.setType("image/*");
                    /* 使用Intent.ACTION_GET_CONTENT这个Action */
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    /* 取得相片后返回本画面 */
                    startActivityForResult(intent, 1);
                    break;
                case R.id.login_reback_btn:
                    finish();
                    break;
                case R.id.userinfo_save:
                    if(photoUri!=null)
                    {
                        user.setUserphotoUri(photoUri.toString());
                    }
                    user.setNickName(nickName.getText().toString());
                    user.setSex(sex.getSelectedItemPosition());
                    user.setEmail(email.getText().toString());
                    user.setRealName(realName.getText().toString());
                    user.setCity(city.getText().toString());
                    user.setTelphone(phone.getText().toString());
                    user.setIdentityCard(identityCard.getText().toString());
                    user.setQq(qq.getText().toString());
                    new RemoteApiImpl().uptUserInfo(user, myHandler);
                    break;
            }
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            photoUri = data.getData();
            Log.e("uri", photoUri.toString());
            ContentResolver cr = this.getContentResolver();
            try 
            {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(photoUri));
                /* 将Bitmap设定到ImageView */
                photo_iv.setImageBitmap(bitmap);
            }
            catch (FileNotFoundException e)
            {
                Log.e("Exception", e.getMessage(), e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
