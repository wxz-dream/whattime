package com.whatime.module.books;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.whatime.R;
import com.whatime.db.User;
import com.whatime.framework.network.pojo.ResponseCons;
import com.whatime.framework.network.service.RemoteApiImpl;
import com.whatime.framework.ui.view.ToastMaster;

@EActivity(R.layout.friend_info)
public class FriendInfoActivity extends Activity
{
    private User user;
    
    @ViewById
    ImageView login_iv;
    
    @ViewById
    TextView nick_name;
    
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
                        Toast toast = Toast.makeText(FriendInfoActivity.this, "请求已发送", Toast.LENGTH_SHORT);
                        ToastMaster.setToast(toast);
                        toast.show();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(FriendInfoActivity.this, "发送失败，请重新尝试", Toast.LENGTH_SHORT);
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
        final EditText remark = new EditText(context);
        remark.setHint("请输入验证信息...");
        remark.setLines(5);
        new AlertDialog.Builder(context).setTitle("添加好友")
            .setIcon(android.R.drawable.ic_dialog_info)
            .setView(remark)
            .setPositiveButton("确定", new OnClickListener()
            {
                
                @Override
                public void onClick(DialogInterface arg0, int arg1)
                {
                    new RemoteApiImpl().addFriendReq(user.getUuid(), remark.getText().toString(), myHandler);
                    login_reback_btn();
                }
            })
            .setNegativeButton("取消", null)
            .show();
    }
    
    @Click
    void login_reback_btn()
    {
        finish();
    }
    
}
