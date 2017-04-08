package com.example.toto.aiapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by toto on 17/4/8.
 */

public class CostomActivity extends Activity implements View.OnClickListener {

    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5;
    private Button mConfirm;
    private boolean imageboolean1 = true;
    private boolean imageboolean2 = true;
    private boolean imageboolean3 = true;
    private boolean imageboolean4 = true;
    private boolean imageboolean5 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.costom_layout);
        initView();
    }

    private void initView() {
        imageView1 = (ImageView) findViewById(R.id.imageview_1);
        imageView2 = (ImageView) findViewById(R.id.imageview_2);
        imageView3 = (ImageView) findViewById(R.id.imageview_3);
        imageView4 = (ImageView) findViewById(R.id.imageview_4);
        imageView5 = (ImageView) findViewById(R.id.imageview_5);
        mConfirm = (Button) findViewById(R.id.confirm);
        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);
        imageView4.setOnClickListener(this);
        imageView5.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_1:
                break;
            case R.id.imageview_2:
                imageView2.setVisibility(View.GONE);
                imageboolean2 = false;
                break;
            case R.id.imageview_3:
                imageView3.setVisibility(View.GONE);
                imageboolean3 = false;
                break;
            case R.id.imageview_4:
                imageView4.setVisibility(View.GONE);
                imageboolean4 = false;
                break;
            case R.id.imageview_5:
                imageView5.setVisibility(View.GONE);
                imageboolean5 = false;
                break;
            case R.id.confirm:
                imageView5.setVisibility(View.GONE);
                break;
        }
    }
}
