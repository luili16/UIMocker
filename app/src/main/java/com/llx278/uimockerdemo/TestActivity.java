package com.llx278.uimockerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by llx on 2018/2/22.
 */

public class TestActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_getter_test);
        Button bt = findViewById(R.id.container_1_unique_button_1);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"hello world!!",Toast.LENGTH_SHORT).show();
            }
        });
        Button bt1 = findViewById(R.id.container_1_button_1);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"button1 is clicked!",Toast.LENGTH_SHORT).show();
            }
        });
        ListView listView = findViewById(R.id.container_4_list_view1);
        MyAdapter myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);

        ScrollView scrollView = findViewById(R.id.container_4_scroll_view2);
        LinearLayout container = new LinearLayout(this);
        container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        container.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0;i<20;i++) {
            View v = View.inflate(this,R.layout.list_item_2,null);
            TextView tv = v.findViewById(R.id.scroll_item_text);
            String txt = "hello Scroll" + i;
            tv.setText(txt);
            container.addView(v);
        }
        scrollView.addView(container);

        RecyclerView recyclerView = findViewById(R.id.container_4_recycler_view4);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter();
        recyclerView.setAdapter(recyclerAdapter);
    }

    private class MyAdapter extends BaseAdapter {

        private int mCount = 20;

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView;

            if (convertView == null) {
                itemView = View.inflate(parent.getContext(),R.layout.list_item_1,null);
            } else {
                itemView = convertView;
            }
            TextView updateView = itemView.findViewById(R.id.list_item_text);
            String txt = "text" + position;
            updateView.setText(txt);
            return itemView;
        }
    }
    private class MyHolder extends RecyclerView.ViewHolder {

        final TextView textView;

        public MyHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.lis_item_3_container_1);
        }
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<MyHolder> {

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_3,null);
            return new MyHolder(item);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            String text = "recycler" + position;
            holder.textView.setText(text);
        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }
}
