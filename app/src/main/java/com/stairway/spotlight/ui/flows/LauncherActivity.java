package com.stairway.spotlight.ui.flows;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.stairway.spotlight.R;
import com.stairway.spotlight.ui.flows.home.HomeActivity;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        startActivity(HomeActivity.callingIntent(this));
    }
}
