package com.caobo.bottombar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;

import com.caobo.bottombar.BottomBar;
import com.caobo.bottombar.fragment.Fragment1;
import com.caobo.bottombar.fragment.Fragment2;
import com.caobo.bottombar.fragment.Fragment3;
import com.caobo.bottombar.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private SparseArray<Fragment> fragments;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        fragments = new SparseArray<>();

        BottomBar bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setTitleBeforeAndAfterColor("#999999", "#ff5d5e")
                .setTitleSize(8) //设置按钮文字大小
                .setNumberSize(10) //设置角标文字大小
                .addItem("首页",
                        R.drawable.item1_before,
                        R.drawable.item1_after, () -> {openFragment(1);})
                .addItem("订单",
                        R.drawable.item2_before,
                        R.drawable.item2_after, () -> {openFragment(2);})
                .addItem("我的",
                        R.drawable.item3_before,
                        R.drawable.item3_after, () -> {openFragment(3);})
                .build(false, 0);

        bottomBar.setNumber(0, 5);
        bottomBar.setNumber(1, 6);
        bottomBar.setNumber(2, 7);
    }

    private void openFragment(int tab) {

        Fragment fragment = fragments.get(tab);
        boolean bNew = false;

        if (fragment == null) {
            switch (tab) {
                case 2:
                {
                    fragment = new Fragment2();
                    bNew = true;
                }
                break;
                case 3:
                {
                    fragment = new Fragment3();
                    bNew = true;
                }
                break;
                case 1:
                default: {
                    fragment = new Fragment1();
                    bNew = true;
                }
                break;
            }
            fragments.put(tab, fragment);
        }
        hideOthersFragment(fragment, bNew);
    }

    private void hideOthersFragment(Fragment showFragment, boolean add) {
        FragmentTransaction transition = fragmentManager.beginTransaction();
        if (add) {
            transition.add(R.id.fl_container, showFragment);
        }

        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.valueAt(i);
            if (showFragment.equals(fragment)) {
                transition.show(fragment);
            } else {
                transition.hide(fragment);
            }
        }
        transition.commit();
    }
}
