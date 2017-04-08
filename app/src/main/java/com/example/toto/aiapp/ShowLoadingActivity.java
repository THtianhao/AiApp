package com.example.toto.aiapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.CubeGrid;

/**
 * Created by toto on 17/4/8.
 */

public class ShowLoadingActivity extends Activity {
    private SpinKitView mLoadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_view);
        initView();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initView() {
        mLoadingView = (SpinKitView) findViewById(R.id.loading_view);
        mLoadingView.setBackgroundColor(Color.BLACK);
        mLoadingView.setIndeterminateDrawable(new CubeGrid());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
