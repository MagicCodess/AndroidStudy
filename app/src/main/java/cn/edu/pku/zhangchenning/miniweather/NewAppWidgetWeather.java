package cn.edu.pku.zhangchenning.miniweather;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidgetWeather extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget_weather);
        //RemoteViews v=new RemoteViews(context.getPackageName(),R.layout.weather_info);

        System.out.println(appWidgetId+appWidgetId);
        views.setTextViewText(R.id.temperature1, MyApplication.getInstance().date);
        views.setTextViewText(R.id.climate1, MyApplication.getInstance().climate);
        views.setTextViewText(R.id.wind1, "风力:"+MyApplication.getInstance().wind);
        views.setTextViewText(R.id.week_today1, MyApplication.getInstance().week);
        switch (MyApplication.getInstance().type) {
            case "暴雪":
                views.setImageViewResource(R.id.weather_img1, R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                views.setImageViewResource(R.id.weather_img1, R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                views.setImageViewResource(R.id.weather_img1, R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                views.setImageViewResource(R.id.weather_img1, R.drawable.biz_plugin_weather_daxue);
                break;
            case "多云":
                views.setImageViewResource(R.id.weather_img1, R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                views.setImageViewResource(R.id.weather_img1, R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                views.setImageViewResource(R.id.weather_img1, R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "晴":
                views.setImageViewResource(R.id.weather_img1, R.drawable.biz_plugin_weather_qing);
                break;
            case "沙尘暴":
                views.setImageViewResource(R.id.weather_img1, R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                views.setImageViewResource(R.id.weather_img1, R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                views.setImageViewResource(R.id.weather_img1, R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                views.setImageViewResource(R.id.weather_img1, R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "小雨":
                views.setImageViewResource(R.id.weather_img1, R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "阴":
                views.setImageViewResource(R.id.weather_img1, R.drawable.biz_plugin_weather_yin);
                break;
            case "雨夹雪":
                views.setImageViewResource(R.id.weather_img1, R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雨":
                views.setImageViewResource(R.id.weather_img1, R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "中雪":
                views.setImageViewResource(R.id.weather_img1, R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "中雨":
                views.setImageViewResource(R.id.weather_img1, R.drawable.biz_plugin_weather_zhongyu);
                break;
            default:
                break;
        }


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    public void onReceive(Context context,Intent intent){
        Bundle extras = intent.getExtras();
        int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        if (appWidgetIds != null && appWidgetIds.length > 0) {
            this.onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
        }
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget_weather);
        views.setTextViewText(R.id.temperature1, "dt");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

