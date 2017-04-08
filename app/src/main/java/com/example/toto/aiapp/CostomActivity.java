package com.example.toto.aiapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by toto on 17/4/8.
 */

public class CostomActivity extends Activity implements View.OnClickListener {

    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5;
    private Button mConfirm;
    private boolean imageboolean2 = true;
    private boolean imageboolean3 = true;
    private boolean imageboolean4 = true;
    private boolean imageboolean5 = true;
    private RequestQueue requestQueue;
    private String mUrl = "http://13.124.90.99:8082/";
    private String mShopType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.costom_layout);
        initView();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mShopType = bundle.getString("shoptype");
        Log.d("tianhao", "shop type is" + mShopType);
        requestQueue = Volley.newRequestQueue(this);
    }

    private void initView() {
        imageView1 = (ImageView) findViewById(R.id.imageview_1);
        imageView2 = (ImageView) findViewById(R.id.imageview_2);
        imageView3 = (ImageView) findViewById(R.id.imageview_3);
        imageView4 = (ImageView) findViewById(R.id.imageview_4);
        imageView5 = (ImageView) findViewById(R.id.imageview_5);
        if (mShopType.equals("book")){
            imageView1.setImageDrawable(getResources().getDrawable(R.drawable.book1));
            imageView2.setImageDrawable(getResources().getDrawable(R.drawable.book2));
            imageView3.setImageDrawable(getResources().getDrawable(R.drawable.book3));
            imageView4.setImageDrawable(getResources().getDrawable(R.drawable.book4));
            imageView5.setImageDrawable(getResources().getDrawable(R.drawable.book5));
        }
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
                requestQueue.add(stringRequest);
                Intent newIntent = new Intent(this, ShowLoadingActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                newIntent.putExtra("shoptype",mShopType);
                startActivity(newIntent);
                break;
        }
    }

    StringRequest stringRequest = new StringRequest(Request.Method.POST, mUrl, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d("tianhao", "定制请求成功");
            Toast.makeText(CostomActivity.this, "定制请求成功", Toast.LENGTH_SHORT).show();
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("tianhao", "定制请求失败");
            Toast.makeText(CostomActivity.this, "定制请求失败", Toast.LENGTH_SHORT).show();
        }
    }) {
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            HashMap<String, String> mMap = new HashMap<>();
            mMap.put("param", "export");
            mMap.put("export1", String.valueOf(parseInt(imageboolean2)));
            mMap.put("export2", String.valueOf(parseInt(imageboolean3)));
            mMap.put("export3", String.valueOf(parseInt(imageboolean4)));
            mMap.put("export4", String.valueOf(parseInt(imageboolean5)));
            return mMap;
        }
    };

    private int parseInt(boolean bool) {
        if (bool) {
            return 0;
        } else {
            return 1;
        }
    }

}
