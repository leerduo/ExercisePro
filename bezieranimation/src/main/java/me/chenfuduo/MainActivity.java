package me.chenfuduo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    BezierLayout bezierLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bezierLayout = (BezierLayout) findViewById(R.id.bezierLayout);
    }

    public void startAnimation(View view) {
        bezierLayout.addNewImgs();
    }

}
