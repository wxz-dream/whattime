/*
 * Copyright (C) 2012 yueyueniao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.whatime.framework.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.whatime.R;
import com.whatime.db.Category;
import com.whatime.db.DBHelper;
import com.whatime.framework.ui.activity.MainActivity;

public class LeftFragment extends Fragment implements OnItemClickListener
{
    private ListView mListView;
    
    private MyAdapter myAdapter;
    
    private List<Category> data = new ArrayList<Category>();
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        
        View view = inflater.inflate(R.layout.left, null);
        mListView = (ListView)view.findViewById(R.id.left_listview);
        data = DBHelper.getInstance().getAllCateByParentId(0);
        Category c = new Category();
        c.setName("我的日程");
        data.add(0, c);
        myAdapter = new MyAdapter(data);
        mListView.setAdapter(myAdapter);
        mListView.setOnItemClickListener(this);
        myAdapter.setSelectPosition(0);
        
        return view;
    }
    
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }
    
    private class MyAdapter extends BaseAdapter
    {
        
        private List<Category> data;
        
        private int selectPosition;
        
        MyAdapter(List<Category> list)
        {
            this.data = list;
            
        }
        
        @Override
        public int getCount()
        {
            return data.size();
        }
        
        @Override
        public Object getItem(int position)
        {
            return data.get(position);
        }
        
        @Override
        public long getItemId(int position)
        {
            return position;
        }
        
        public void setSelectPosition(int position)
        {
            selectPosition = position;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View row = LayoutInflater.from(getActivity()).inflate(R.layout.left_list, null);
            TextView textView = (TextView)row.findViewById(R.id.left_list_text);
            textView.setText(data.get(position).getName());
            ImageView img = (ImageView)row.findViewById(R.id.left_list_image);
            switch (position)
            {
                case 0:
                    img.setBackgroundResource(R.drawable.biz_navigation_tab_news);
                    break;
                case 1:
                    img.setBackgroundResource(R.drawable.biz_navigation_tab_local_news);
                    break;
                case 2:
                    img.setBackgroundResource(R.drawable.biz_navigation_tab_ties);
                    break;
                case 3:
                    img.setBackgroundResource(R.drawable.biz_navigation_tab_pics);
                    break;
                case 4:
                    img.setBackgroundResource(R.drawable.biz_navigation_tab_ugc);
                    break;
                case 5:
                    img.setBackgroundResource(R.drawable.biz_navigation_tab_vote);
                    break;
                case 6:
                    img.setBackgroundResource(R.drawable.biz_navigation_tab_micro);
                case 7:
                    img.setBackgroundResource(R.drawable.biz_navigation_tab_micro);
                    break;
                default:
                    break;
            }
            
            if (position == selectPosition)
            {
                row.setBackgroundResource(R.drawable.ic_top_back);
                textView.setSelected(true);
            }
            return row;
        }
        
    }
    
    private IChangeFragment iChangeFragment;
    
    public void setChangeFragmentListener(IChangeFragment listener)
    {
        iChangeFragment = listener;
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (iChangeFragment != null)
        {
            iChangeFragment.changeFragment(position);
        }
        
        myAdapter.setSelectPosition(position);
        myAdapter.notifyDataSetChanged();
        ((MainActivity)getActivity()).showLeft();
    }
    
}
