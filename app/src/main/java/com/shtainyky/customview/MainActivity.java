package com.shtainyky.customview;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.shtainyky.customview.views.CustomCircleMenu;
import com.shtainyky.customview.views.CustomProgressView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private CustomProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
//        progressView = (CustomProgressView) findViewById(R.id.progress);
//        progressView.setDistanceBetweenSquares(300);
//        progressView.setLengthSquareSide(20);
//        progressView.startAnimation();

        final ImageButton imageView = (ImageButton) findViewById(R.id.iv);
        CustomCircleMenu customCircleMenu = (CustomCircleMenu) findViewById(R.id.first);
        List<Integer> arrayIcons = new ArrayList<>();
        arrayIcons.add(R.drawable.ic_action_add);
        arrayIcons.add(R.drawable.ic_action_email);
        arrayIcons.add(R.drawable.ic_action_location);
        arrayIcons.add(R.drawable.ic_action_message);
        arrayIcons.add(R.drawable.ic_action_name);
        arrayIcons.add(R.drawable.ic_action_audio);
        customCircleMenu.setIconsForMenu(arrayIcons);
        customCircleMenu.setOnMenuItemClickListener(new CustomCircleMenu.OnMenuItemClickListener() {
            @Override
            public void onIconClick(int drawableId) {
                switch (drawableId){
                    case R.drawable.ic_action_add:
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_add));
                        break;
                    case R.drawable.ic_action_email:
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_email));
                        break;
                    case R.drawable.ic_action_location:
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_location));
                        break;
                    case R.drawable.ic_action_message:
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_message));
                        break;
                    case R.drawable.ic_action_name:
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_name));
                        break;
                    case R.drawable.ic_action_audio:
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_audio));
                        break;

                }
            }
        });


    }

}
