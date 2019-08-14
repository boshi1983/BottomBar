package com.caobo.bottombar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.caobo.bottombar.BottomBar;
import com.caobo.bottombar.fragment.Fragment1;
import com.caobo.bottombar.fragment.Fragment2;
import com.caobo.bottombar.fragment.Fragment3;
import com.caobo.bottombar.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomBar bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setTitleBeforeAndAfterColor("#999999", "#ff5d5e")
                .addItem("首页",
                        R.drawable.item1_before,
                        R.drawable.item1_after, () -> {})
                .addItem("订单",
                        R.drawable.item2_before,
                        R.drawable.item2_after, () -> {})
                .addItem("我的",
                        R.drawable.item3_before,
                        R.drawable.item3_after, () -> {})
                .build(false, 0);
    }
}
