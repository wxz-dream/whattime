package com.whatime.module.thirdLogin;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import cn.sharesdk.framework.CustomPlatform;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.TitleLayout;
import cn.sharesdk.framework.utils.UIHandler;

import com.whatime.R;

/**
 * @author yangyu
 *	功能描述：授权和取消授权Activity，由于UI显示需要授权过的平台显示账户的名称，
 *	  因此此页面事实上展示的是“获取用户资料”和“取消授权”两个功能。
 */
public class AuthActivity extends Activity implements Callback,OnClickListener, PlatformActionListener {

    private TitleLayout llTitle;
    private AuthAdapter adapter;
    private static Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_auth);
		mContext = this;
		//初始化ShareSDK
		ShareSDK.initSDK(this);
		llTitle = (TitleLayout) findViewById(R.id.llTitle);
        llTitle.getBtnBack().setOnClickListener(this);
        llTitle.getTvTitle().setText(R.string.thrid_auth);
		ListView lvPlats = (ListView) findViewById(R.id.lvPlats);
        lvPlats.setSelector(new ColorDrawable());
        adapter = new AuthAdapter(AuthActivity.this);
        lvPlats.setAdapter(adapter);
        lvPlats.setOnItemClickListener(adapter);
	}
	protected void onDestroy() {
        //结束ShareSDK的统计功能并释放资源
	    ShareSDK.stopSDK(this);
        super.onDestroy();
    }

    public void onComplete(Platform plat, int action,
            HashMap<String, Object> res) {
        Message msg = new Message();
        msg.arg1 = 1;
        msg.arg2 = action;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);
    }

    public void onError(Platform plat, int action, Throwable t) {
        t.printStackTrace();

        Message msg = new Message();
        msg.arg1 = 2;
        msg.arg2 = action;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);
    }

    public void onCancel(Platform plat, int action) {
        Message msg = new Message();
        msg.arg1 = 3;
        msg.arg2 = action;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);
    }

    /**
     * 处理操作结果
     * <p>
     * 如果获取到用户的名称，则显示名称；否则如果已经授权，则显示
     *平台名称
     */
    public boolean handleMessage(Message msg) {
        switch (msg.arg1) {
            case 1: {
                // 成功
                Toast.makeText(mContext, "授权成功", Toast.LENGTH_SHORT).show();
            }
            break;
            case 2: {
                // 失败
                Toast.makeText(mContext, "授权失败", Toast.LENGTH_SHORT).show();
                return false;
            }
            case 3: {
                // 取消
                Toast.makeText(mContext, "取消成功", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        adapter.notifyDataSetChanged();
        return false;
    }

    private static class AuthAdapter extends BaseAdapter implements OnItemClickListener {
        private ArrayList<Platform> platforms;
        private AuthActivity act;
        public AuthAdapter(AuthActivity act) {
            this.act = act;
            // 获取平台列表
            Platform[] tmp = ShareSDK.getPlatformList(mContext);
            platforms = new ArrayList<Platform>();
            if (tmp == null) {
                return;
            }

            for (Platform p : tmp) {
                String name = p.getName();
                if ((p instanceof CustomPlatform)
                        || !ShareCore.canAuthorize(p.getContext(), name)) {
                    continue;
                }
                platforms.add(p);
            }
        }

        public int getCount() {
            return platforms == null ? 0 : platforms.size();
        }

        public Platform getItem(int position) {
            return platforms.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.auth_page_item, null);
            }

            int count = getCount();
            View llItem = convertView.findViewById(R.id.llItem);
            int dp_10 = cn.sharesdk.framework.utils.R.dipToPx(parent.getContext(), 10);
            if (count == 1) {
                llItem.setBackgroundResource(R.drawable.list_item_single_normal);
                llItem.setPadding(0, 0, 0, 0);
                convertView.setPadding(dp_10, dp_10, dp_10, dp_10);
            }
            else if (position == 0) {
                llItem.setBackgroundResource(R.drawable.list_item_first_normal);
                llItem.setPadding(0, 0, 0, 0);
                convertView.setPadding(dp_10, dp_10, dp_10, 0);
            }
            else if (position == count - 1) {
                llItem.setBackgroundResource(R.drawable.list_item_last_normal);
                llItem.setPadding(0, 0, 0, 0);
                convertView.setPadding(dp_10, 0, dp_10, dp_10);
            }
            else {
                llItem.setBackgroundResource(R.drawable.list_item_middle_normal);
                llItem.setPadding(0, 0, 0, 0);
                convertView.setPadding(dp_10, 0, dp_10, 0);
            }

            Platform plat = getItem(position);
            ImageView ivLogo = (ImageView) convertView.findViewById(R.id.ivLogo);
            Bitmap logo = getIcon(plat);
            if (logo != null && !logo.isRecycled()) {
                ivLogo.setImageBitmap(logo);
            }
            CheckedTextView ctvName = (CheckedTextView) convertView.findViewById(R.id.ctvName);
            ctvName.setChecked(plat.isValid());
            if (plat.isValid()) {
                String userName = plat.getDb().get("nickname");
                if (userName == null || userName.length() <= 0
                        || "null".equals(userName)) {
                    // 如果平台已经授权却没有拿到帐号名称，则自动获取用户资料，以获取名称
                    userName = getName(plat);
                    plat.setPlatformActionListener(act);
                    plat.showUser(null);
                }
                ctvName.setText(userName);
            }
            else {
                ctvName.setText(R.string.not_yet_authorized);
            }
            return convertView;
        }

        private Bitmap getIcon(Platform plat) {
            if (plat == null) {
                return null;
            }

            String name = plat.getName();
            if (name == null) {
                return null;
            }

            String resName = "logo_" + plat.getName();
            int resId = cn.sharesdk.framework.utils.R.getResId(R.drawable.class, resName);
            return BitmapFactory.decodeResource(mContext.getResources(), resId);
        }

        private String getName(Platform plat) {
            if (plat == null) {
                return "";
            }

            String name = plat.getName();
            if (name == null) {
                return "";
            }

            int resId = cn.sharesdk.framework.utils.R.getStringRes(mContext, plat.getName());
            return mContext.getString(resId);
        }

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Platform plat = getItem(position);
            CheckedTextView ctvName = (CheckedTextView) view.findViewById(R.id.ctvName);
            if (plat == null) {
                ctvName.setChecked(false);
                ctvName.setText(R.string.not_yet_authorized);
                return;
            }

            if (plat.isValid()) {
                plat.removeAccount();
                ctvName.setChecked(false);
                ctvName.setText(R.string.not_yet_authorized);
                return;
            }

            plat.setPlatformActionListener(act);
            plat.showUser(null);
        }

    }

    @Override
    public void onClick(View v)
    {
        if (v.equals(llTitle.getBtnBack())) {
            this.finish();
        }
    }
}
