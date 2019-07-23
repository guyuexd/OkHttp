package com.example.okhttptest;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;

public abstract class LBaseActivity extends AppCompatActivity {

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(getContentViewId() != 0){
            setContentView(getContentViewId());
        }
        ButterKnife.bind(this);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("requesting...");
        initView();
        initData();

    }

    protected abstract int getContentViewId();

    protected abstract void initView();

    protected abstract void initData();

    /**
     * show loading dialog
     */
    public void showDialog(){
        showDialog(null);
    }

    /**
     * show loading dialog
     */
    public void showDialog(String message){
        if(!TextUtils.isEmpty(message)){
            dialog.setMessage(message);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
    }

    public void hideDialog(){
        if(dialog.isShowing()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            });
        }
    }


    /**
     * toast tip
     */
    public void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }

    /**
     * init ActionBar
     */
    public void setTitle(String title){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar == null){
            return;
        }
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(title);
        actionBar.show();
    }


}
