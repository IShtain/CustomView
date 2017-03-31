package com.shtainyky.customview;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.shtainyky.customview.views.CustomProgressView;

public class MainActivity extends AppCompatActivity {
    private CustomProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressView = (CustomProgressView) findViewById(R.id.progress);
        progressView.setDistanceBetweenSquares(300);
        progressView.setLengthSquareSide(20);
        progressView.startAnimation();


//        CustomCircleMenu customCircleMenu = (CustomCircleMenu) findViewById(R.id.first);
//        List<Integer> arrayIcons = new ArrayList<>();
//        arrayIcons.add(R.drawable.ic_action_add);
//        arrayIcons.add(R.drawable.ic_action_email);
//        arrayIcons.add(R.drawable.ic_action_location);
//        arrayIcons.add(R.drawable.ic_action_message);
//        arrayIcons.add(R.drawable.ic_action_name);
//        customCircleMenu.setIconsForMenu(arrayIcons);


    }

}
