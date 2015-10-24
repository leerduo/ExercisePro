package me.chenfuduo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    BezierLayout bezierLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bezierLayout = (BezierLayout) findViewById(R.id.bezierLayout);

        Timer timer = new Timer();
        MyTask task = new MyTask();
        timer.scheduleAtFixedRate(task,350,350);

    }

    /*public void startAnimation(View view) {
        bezierLayout.addNewImgs();
    }*/

    class MyTask extends TimerTask{

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bezierLayout.addNewImgs();
                }
            });
        }
    }


}
