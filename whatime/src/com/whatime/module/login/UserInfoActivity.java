package com.whatime.module.login;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.whatime.R;
import com.whatime.db.User;
import com.whatime.framework.application.MyApp;
import com.whatime.framework.network.pojo.ResponseCons;
import com.whatime.framework.network.service.RemoteApiImpl;
import com.whatime.framework.ui.view.ToastMaster;

public class UserInfoActivity extends Activity
{
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
    
    private File photoFile;
    
    private Bitmap photo;
    
    private User user = MyApp.getInstance().getUser();
    
    private BitmapUtils bitmapUtils;
    
    private List<String> provinces;
    
    private List<String> citys;
    
    private Spinner province_spinner;
    
    private Spinner city_spinner;
    
    private Context context;
    
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
        context = this;
        setContentView(R.layout.userinfo);
        bitmapUtils = new BitmapUtils(this);
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
            // 加载网络图片
            if (photo != null)
            {
                photo_iv.setImageBitmap(photo);
            }
            else if (user.getUserphotoUri() != null)
            {
                bitmapUtils.display(photo_iv, user.getUserphotoUri());
            }
            if (user.getUserName() != null)
            {
                userName.setText(user.getUserName());
            }
            if (user.getNickName() != null)
            {
                nickName.setText(user.getNickName());
            }
            if (user.getSex() != null)
            {
                sex.setSelection(user.getSex());
            }
            if (user.getEmail() != null)
            {
                email.setText(user.getEmail());
            }
            if (user.getRealName() != null)
            {
                realName.setText(user.getRealName());
            }
            if (user.getCity() != null)
            {
                city.setText(user.getCity());
            }
            if (user.getTelphone() != null)
            {
                phone.setText(user.getTelphone());
            }
            if (user.getIdentityCard() != null)
            {
                identityCard.setText(user.getIdentityCard());
            }
            if (user.getQq() != null)
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
        city.setOnClickListener(listener);
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
    
    /**
     * 选择提示对话框
     */
    private void ShowPickDialog()
    {
        new AlertDialog.Builder(this).setTitle("设置头像...").setNegativeButton("相册", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                Intent intent =
                    new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                
                /* 取得相片后返回本画面 */
                startActivityForResult(intent, 1);
                
            }
        })
            .setPositiveButton("拍照", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    dialog.dismiss();
                    /**
                     * 下面这句还是老样子，调用快速拍照功能，至于为什么叫快速拍照，大家可以参考如下官方
                     * 文档，you_sdk_path/docs/guide/topics/media/camera.html
                     * 我刚看的时候因为太长就认真看，其实是错的，这个里面有用的太多了，所以大家不要认为
                     * 官方文档太长了就不看了，其实是错的，这个地方小马也错了，必须改正
                     */
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //下面这句指定调用相机拍照后的照片存储的路径
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "xiaoma.jpg")));
                    startActivityForResult(intent, 2);
                }
            })
            .show();
    }
    
    class MyOnclickListener implements OnClickListener
    {
        
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.photo_iv:
                    ShowPickDialog();
                    break;
                case R.id.login_reback_btn:
                    finish();
                    break;
                case R.id.userinfo_save:
                    user.setNickName(nickName.getText().toString());
                    user.setSex(sex.getSelectedItemPosition());
                    user.setEmail(email.getText().toString());
                    user.setRealName(realName.getText().toString());
                    user.setCity(city.getText().toString());
                    user.setTelphone(phone.getText().toString());
                    user.setIdentityCard(identityCard.getText().toString());
                    user.setQq(qq.getText().toString());
                    new RemoteApiImpl().uptUserInfo(user, myHandler);
                    if (photoFile != null)
                    {
                        new RemoteApiImpl().putUserPhotoFile(photoFile, myHandler);
                    }
                    break;
                case R.id.city:
                    // 取得city_layout.xml中的视图
                    final View view = LayoutInflater.from(context).inflate(R.layout.city_layout, null);
                    
                    // 省份Spinner
                    province_spinner = (Spinner)view.findViewById(R.id.province_spinner);
                    // 城市Spinner
                    city_spinner = (Spinner)view.findViewById(R.id.city_spinner);
                    
                    // 省份列表
                    provinces = MyApp.getInstance().getProvince();
                    ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, provinces);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    final String scope = user.getCity();
                    province_spinner.setAdapter(adapter);
                    // 省份Spinner监听器
                    province_spinner.setOnItemSelectedListener(new OnItemSelectedListener()
                    {
                        
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3)
                        {
                            citys = MyApp.getInstance().getCitys().get(provinces.get(position));
                            ArrayAdapter adapter1 =
                                new ArrayAdapter(context, android.R.layout.simple_spinner_item, citys);
                            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            city_spinner.setAdapter(adapter1);
                            if (scope.contains("|"))
                            {
                                city_spinner.setSelection(citys.indexOf(scope.split("\\|")[1]));
                            }
                        }
                        
                        @Override
                        public void onNothingSelected(AdapterView<?> arg0)
                        {
                        }
                    });
                    // 选择城市对话框
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("请选择所属城市");
                    dialog.setView(view);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            String scope = "";
                            scope =
                                provinces.get(province_spinner.getSelectedItemPosition()) + "|"
                                    + citys.get(city_spinner.getSelectedItemPosition());
                            user.setCity(scope);
                            logedUi();
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener()
                    {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                            
                        }
                    });
                    province_spinner.setSelection(provinces.indexOf(scope.split("\\|")[0]));
                    dialog.show();
                    break;
            }
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
        // 如果是直接从相册获取
            case 1:
                if (data != null)
                {
                    startPhotoZoom(data.getData());
                }
                break;
            // 如果是调用相机拍照时
            case 2:
                File temp = new File(Environment.getExternalStorageDirectory() + "/xiaoma.jpg");
                startPhotoZoom(Uri.fromFile(temp));
                break;
            // 取得裁剪后的图片
            case 3:
                /**
                 * 非空判断大家一定要验证，如果不验证的话，
                 * 在剪裁之后如果发现不满意，要重新裁剪，丢弃
                 * 当前功能时，会报NullException，小马只
                 * 在这个地方加下，大家可以根据不同情况在合适的
                 * 地方做判断处理类似情况
                 * 
                 */
                if (data != null)
                {
                    setPicToView(data);
                }
                break;
            default:
                break;
        
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    /**
     * 裁剪图片方法实现
     * @param uri
     */
    public void startPhotoZoom(Uri uri)
    {
        /*
         * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
         * yourself_sdk_path/docs/reference/android/content/Intent.html
         * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能,
         * 是直接调本地库的，小马不懂C C++  这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么
         * 制做的了...吼吼
         */
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }
    
    /**
     * 保存裁剪之后的图片数据
     * @param picdata
     */
    private void setPicToView(Intent picdata)
    {
        Bundle extras = picdata.getExtras();
        if (extras != null)
        {
            photo = extras.getParcelable("data");
            /**
             * 下面注释的方法是将裁剪之后的图片以Base64Coder的字符方式上
             * 传到服务器，QQ头像上传采用的方法跟这个类似
             */
            
            /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            
            byte[] b = stream.toByteArray();
            // 将图片流以字符串形式存储下来
            
            String tp = new String(Base64Coder.encodeLines(b));
            *//**这个地方大家可以写下给服务器上传图片的实现，直接把tp直接上传就可以了，
                                服务器处理的方法是服务器那边的事了，吼吼
              
                                如果下载到的服务器的数据还是以Base64Coder的形式的话，可以用以下方式转换
                                为我们可以用的图片类型就OK啦...吼吼
                                */
            /*
            Bitmap dBitmap = BitmapFactory.decodeFile(tp);
            Drawable drawable = new BitmapDrawable(dBitmap);*/
            File f = new File(Environment.getExternalStorageDirectory() + "/ttyy/myPhoto.jpg");
            FileOutputStream fOut = null;
            try
            {
                f.createNewFile();
                fOut = new FileOutputStream(f);
            }
            catch (Exception e1)
            {
            }
            photo.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            try
            {
                if (fOut != null)
                {
                    fOut.flush();
                    fOut.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            photoFile = f;
            //photoUri = Uri.fromFile(new File(tp));
            photo_iv.setImageBitmap(photo);
        }
    }
    
}
