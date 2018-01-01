package cn.edu.pku.zhangchenning.miniweather;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Arthur on 2018/1/1.
 */

class myRunnable implements Runnable{
    private Context context;
    public myRunnable(Context c)
    {
        this.context=c;
    }
    public void run()
    {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        context.startService(new Intent(context,MyService.class));
    }
}

public class MyReceive extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        //System.out.println("service started3");
        //message.getMsg("123");
        NewAppWidgetWeather.updateAppWidget(context, AppWidgetManager.getInstance(context),0);
        new Thread(new myRunnable(context)).start();
    }
}
