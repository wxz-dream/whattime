package com.whatime.framework.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.whatime.R;

public class SetAlertTypeAdapter extends BaseAdapter
{
    
    private Context ctx;
    
    // 定义整型数组 即图片源
    private Integer[] mImageIds = {R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher,
        R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher};
    
    public SetAlertTypeAdapter(Context ctx)
    {
        this.ctx = ctx;
    }
    
    @Override
    public int getCount()
    {
        return mImageIds.length;
    }
    
    @Override
    public Object getItem(int arg0)
    {
        return null;
    }
    
    @Override
    public long getItemId(int arg0)
    {
        return arg0;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView imageView;
        if (convertView == null)
        {
            imageView = new ImageButton(ctx);
        }
        else
        {
            imageView = (ImageView)convertView;
        }
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        
        imageView.setImageResource(mImageIds[position]);
        return imageView;
    }
    
}
