package com.mdtech.jencterdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Admanager admanager = Admanager.getInstance();
//                admanager.getDataBean();
//
//                Gson gson = new Gson();
//                DataTestBean bean = new DataTestBean();
//                bean.setAge(10);
//                bean.setName("hyw");
//                bean.setSchool("scau");
//                Log.e("hyw", "gson.toJson(bean):" + gson.toJson(bean));
//
//                DataBean bean2 = new DataBean();
//                bean2.setAge(10);
//                bean2.setName("hyw");
//                bean2.setSchool("scau");
//                Log.e("hyw", "gson.toJson(bean2):" + gson.toJson(bean2));
//                admanager.openTestActivity(MainActivity.this);

            }
        });
    }
}
