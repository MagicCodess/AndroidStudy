package cn.edu.pku.zhangchenning.miniweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class SplashActivity extends AppCompatActivity {
    private Context context;
    private boolean first;
    private static final int GO_GUIDE=0;
    private static final int GO_HOME=1;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_GUIDE:
                    Intent intent=new Intent(context,WelcomeActivity.class);
                    startActivity(intent);
                    SharedPreferences preferences=getSharedPreferences("user",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putBoolean("first", false);
                    editor.commit();
                    finish();
                    break;
                case GO_HOME:
                    Intent intent2=new Intent(context,MainActivity.class);
                    startActivity(intent2);
                    finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        context = SplashActivity.this;
        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
        //获取文件中的XX所对应的布尔值，如果没有则默认为true
        first = preferences.getBoolean("first", true);
        //如果该值为真则进入引导页，否则进入主页
        if (first) {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, 10);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, 10);
        }
    }
}
