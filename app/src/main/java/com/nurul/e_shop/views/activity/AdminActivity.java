package com.nurul.e_shop.views.activity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nurul.e_shop.R;
import com.nurul.e_shop.views.adminfragments.AddCartFragment;
import com.nurul.e_shop.views.adminfragments.AllProductFragement;
import com.nurul.e_shop.views.adminfragments.RequestOrder;
import com.nurul.e_shop.views.adminfragments.SubmitedOrder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.widget.TextView;

public class AdminActivity extends AppCompatActivity {
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.add_product:
                    getSupportFragmentManager().beginTransaction().replace(R.id.adminContainer, new AddCartFragment()).commit();
                    return true;
                case R.id.request_order:
                    getSupportFragmentManager().beginTransaction().replace(R.id.adminContainer, new RequestOrder()).commit();
                    return true;
                case R.id.submited_order:
                    getSupportFragmentManager().beginTransaction().replace(R.id.adminContainer, new SubmitedOrder()).commit();
                    return true;
                case R.id.all_product:
                    getSupportFragmentManager().beginTransaction().replace(R.id.adminContainer, new AllProductFragement()).commit();
                    return true;
                default:
                    getSupportFragmentManager().beginTransaction().replace(R.id.adminContainer, new RequestOrder()).commit();
                    return true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.adminContainer, new AddCartFragment()).commit();
    }

}
