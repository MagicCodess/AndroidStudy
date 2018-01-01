package cn.edu.pku.zhangchenning.miniweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Binder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Arthur on 2017/12/6.
 */


/**
 * Created by Arthur on 2017/12/6.
 */

public class MyService extends Service {
    private static final int ONE_Miniute=60;
    private static final int PENDING_REQUEST=0;
    Handler handler;
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new Binder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        //System.out.println("service started");
        handler.post(new Runnable(){
            @Override
            public void run() {
                //System.out.println("service started2");
                // Toast.makeText(getApplicationContext(), "service is running", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(MyService.this, MyReceive.class);
                sendBroadcast(i);
                // onDestroy();
            }
        });
//        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
//        long triggerAtTime= SystemClock.elapsedRealtime()+ONE_Miniute;//从开机到现在的毫秒（手机睡眠(sleep)的时间也包括在内
//        Intent i=new Intent(this, MyReceive.class);
//        //PendingIntent pIntent= PendingIntent.getBroadcast(this,PENDING_REQUEST,i,PENDING_REQUEST);
//
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Welcome Back", Toast.LENGTH_LONG).show();
        System.out.println("onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
    }
}