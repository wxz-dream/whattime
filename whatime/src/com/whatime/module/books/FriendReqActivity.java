package com.whatime.module.books;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.whatime.R;
import com.whatime.framework.application.MyApp;
import com.whatime.framework.network.pojo.ResponseCons;
import com.whatime.framework.network.service.RemoteApiImpl;
import com.whatime.framework.ui.view.MyListView;
import com.whatime.framework.ui.view.ToastMaster;

@EActivity(R.layout.friend_req)
public class FriendReqActivity extends Activity
{
    @ViewById
    MyListView firend_req_lv;
    
    private Context context;
    
    private MyAdapter adapter;
    
    private List<FriendReq> reqs = new ArrayList<FriendReq>();
    
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
                        ArrayList list = msg.getData().getParcelableArrayList(ResponseCons.RESINFO);
                        if(list!=null)
                        {
                            reqs = (List<FriendReq>)list.get(0);
                            firend_req_lv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    else
                    {
                        Toast toast = Toast.makeText(FriendReqActivity.this, "操作失败，请重新尝试", Toast.LENGTH_SHORT);
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
        adapter = new MyAdapter();
        new RemoteApiImpl().findMyAddFriendsReq(myHandler);
    }
    
    @AfterViews
    void initView()
    {
        firend_req_lv.setAdapter(adapter);
    }
    
    @Click
    void firend_add()
    {
        context.startActivity(new Intent(context, FriendAddActivity_.class));
    }
    
    @Click
    void login_reback_btn()
    {
        finish();
    }
    
    class MyAdapter extends BaseAdapter
    {
        
        @Override
        public int getCount()
        {
            return reqs.size();
        }
        
        @Override
        public Object getItem(int arg0)
        {
            return reqs.get(arg0);
        }
        
        @Override
        public long getItemId(int arg0)
        {
            return arg0;
        }
        
        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2)
        {
            final FriendReq req = reqs.get(arg0);
            View v = LayoutInflater.from(context).inflate(R.layout.friend_req_item, null);
            ImageView item_friend_photo = (ImageView)v.findViewById(R.id.item_friend_photo);
            String photo = req.getUserphotoUri();
            
            TextView item_friend_nickName = (TextView)v.findViewById(R.id.item_friend_nickName);
            item_friend_nickName.setText(req.getNickName());
            TextView item_friend_remark = (TextView)v.findViewById(R.id.item_friend_remark);
            item_friend_remark.setText(req.getRemark());
            Button bt = (Button)v.findViewById(R.id.firend_get);
            bt.setOnClickListener(new OnClickListener()
            {
                
                @Override
                public void onClick(View arg0)
                {
                    new RemoteApiImpl().operaMyFriendReq(FriendAccessCons.ACCESS_AGREE,req.getUuid(), myHandler);
                }
            });
            switch(req.getAccess())
            {
                case FriendAccessCons.ACCESS_AGREE:
                    bt.setEnabled(false);
                    bt.setText("已接受");
                    break;
                case FriendAccessCons.ACCESS_REFUSAL:
                    bt.setEnabled(false);
                    bt.setText("已拒绝");
                    break;
                
            }
            
            return v;
        }
        
    }
}
