package com.test.stream.stream.UI;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import com.test.stream.stream.R;
import com.test.stream.stream.UIFragments.ViewPinFragment;

public class PinDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_detail);
        createAndAddFragment();
    }

    private void createAndAddFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ViewPinFragment pinViewFragment = new ViewPinFragment();
        fragmentTransaction.add(R.id.pin_container, pinViewFragment, "PIN_VIEW_FRAGMENT");
        fragmentTransaction.commit();
    }
}
