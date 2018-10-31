package com.tencent.cos.xml;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LogActivity extends AppCompatActivity{

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private CustomerAdapter customerAdapter;
    private OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            super.onScrollStateChanged(recyclerView, newState);
            if(newState == RecyclerView.SCROLL_STATE_IDLE){

            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        recyclerView = findViewById(R.id.item_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); //添加分割线
        customerAdapter = new CustomerAdapter(this);
        recyclerView.setAdapter(customerAdapter);
        recyclerView.addOnScrollListener(onScrollListener);
    }

    private void initItems(){}

    private void updateItmes(){

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(recyclerView != null && onScrollListener != null){
            recyclerView.removeOnScrollListener(onScrollListener);
        }
    }

    private static class CustomerAdapter extends RecyclerView.Adapter<CustomerViewHolder>{
        private List<String> items = new ArrayList<>(10);
        private Context context;

        public CustomerAdapter(Context context){
            this.context = context;
        }

        private void share(String filePath){
            //调用分享
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("*/*");
            fileIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(filePath));
            context.startActivity(Intent.createChooser(fileIntent, "分享"));
        }

        @NonNull
        @Override
        public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //创建 viewHolder
            View view = LayoutInflater.from(context).inflate(R.layout.item_log, parent, false);
            final CustomerViewHolder customerViewHolder = new CustomerViewHolder(view);
            customerViewHolder.actionImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //弹出share dialog
                    share(customerViewHolder.filePathTextView.getText().toString().trim());
                }
            });
            return customerViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
            //绑定数据
            holder.filePathTextView.setText(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

    }

    private static class CustomerViewHolder extends RecyclerView.ViewHolder{

        private TextView filePathTextView;
        private ImageView actionImageView;

        public CustomerViewHolder(View itemView) {
            super(itemView);
            filePathTextView = itemView.findViewById(R.id.pathId);
            actionImageView = itemView.findViewById(R.id.actionId);
        }
    }
}
