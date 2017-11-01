package cn.edu.pku.zhangchenning.miniweather;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.lang.String;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.HttpURLConnection;
import java.util.List;

import cn.edu.pku.zhangchenning.bean.TodayWeather;
import cn.edu.pku.zhangchenning.db.CityDB;

import static cn.edu.pku.zhangchenning.miniweather.R.drawable.base_action_bar_action_city;

/**
 * Created by Arthur on 2017/9/22.
 */

public class MainActivity extends Activity implements View.OnClickListener{
    private ImageView mUpdateBtn;

    private ImageView mCitySelect;

    private EditText mCityName;

    private String dictationResultStr = "[";

    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv,pmQualityTv,temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    private static final int UPDATE_TODAY_WEATHER = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateBtn= findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        mCitySelect=findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        cityTv=findViewById(R.id.city);
        cityTv.setOnClickListener(this);

        mCityName=findViewById(R.id.city_name);

        networkStateTest();

        initView();

    }

    void networkStateTest() {
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view){
        if(view.getId()==R.id.title_city_manager){
            Intent i=new Intent(this,SelectCity.class);
            startActivityForResult(i,1);
        }

        if(view.getId()==R.id.city){
            dictationResultStr = "[";
            // 语音配置对象初始化
            SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID + "=59f97bec");

            // 1.创建SpeechRecognizer对象，第2个参数：本地听写时传InitListener
            SpeechRecognizer mIat = SpeechRecognizer.createRecognizer(MainActivity.this, null);
            // 交互动画
            RecognizerDialog iatDialog = new RecognizerDialog(MainActivity.this, null);
            // 2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
            mIat.setParameter(SpeechConstant.DOMAIN, "iat"); // domain:域名
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            mIat.setParameter(SpeechConstant.ACCENT, "mandarin"); // mandarin:普通话

            //3.开始听写
            iatDialog.setListener(new RecognizerDialogListener() {

                @Override
                public void onResult(RecognizerResult results, boolean isLast) {
                    // TODO 自动生成的方法存根
                    Log.d("Result", results.getResultString());
                    //contentTv.setText(results.getResultString());
                    if (!isLast) {
                        dictationResultStr += results.getResultString() + ",";
                    } else {
                        dictationResultStr += results.getResultString() + "]";
                    }
                    if (isLast) {
                        // 解析Json列表字符串
                        Gson gson = new Gson();
                        List<DictationResult> dictationResultList = gson.fromJson(dictationResultStr,new TypeToken<List<DictationResult>>() {}.getType());
                        String finalResult = "";
                        for (int i = 0; i < dictationResultList.size() - 1; i++) {
                            finalResult += dictationResultList.get(i).toString();
                        }
                        mCityName.setText(finalResult);

                        //获取焦点
                        mCityName.requestFocus();
                        System.out.println(mCityName.getText().toString());
                        MyApplication mApp=MyApplication.getInstance();
                        String cityNumber;
                        if ((cityNumber=mApp.mCityDB.getCityNumber(mCityName.getText().toString()))!=null){
                            queryWeatherCode(cityNumber);
                        }else{
                            Toast.makeText(MainActivity.this, "Please Try Again！", Toast.LENGTH_LONG).show();
                        }
                        System.out.println(mApp.mCityDB.getCityNumber(mCityName.getText().toString()));
                        //将光标定位到文字最后，以便修改
                        mCityName.setSelection(finalResult.length());

                        Log.d("From reall phone", finalResult);
                    }
                }

                @Override
                public void onError(SpeechError error) {
                    // TODO 自动生成的方法存根
                    error.getPlainDescription(true);
                }
            });
            // 开始听写
            //Log.d("DB","hahahah");
            iatDialog.show();
            //MyApplication mApp=MyApplication.getInstance();

            //queryWeatherCode(mApp.mCityDB.getCityNumber(cityTv.getText().toString()));
            //Toast.makeText(MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show();
        }

        if(view.getId()==R.id.title_update_btn){
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code","101010100");
            //String cityCode = sharedPreferences.getString("main_city_code","101160101");
            Log.d("myWeather",cityCode);
            saveState(cityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);
                Toast.makeText(MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show();
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode==1&&resultCode==RESULT_OK){
            String newCityCode = data.getStringExtra("cityCode");
            //String cityCode = sharedPreferences.getString("main_city_code","101160101");
            Log.d("myWeather",newCityCode);
            saveState(newCityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(newCityCode);
                Toast.makeText(MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show();
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void queryWeatherCode(String cityCode){
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;
        Log.d("myWeather", address);
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        HttpURLConnection con = null;
                        TodayWeather todayWeather;
                        try{
                            URL url = new URL(address);
                            con = (HttpURLConnection)url.openConnection();
                            con.setRequestMethod("GET");
                            con.setConnectTimeout(8000);
                            con.setReadTimeout(8000);
                            InputStream in = con.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            StringBuilder response = new StringBuilder();
                            String str;
                            while ((str=reader.readLine())!=null){
                                response.append(str);
                                Log.d("myWeather",str);
                            }
                            Log.d("myWeather", address);
                            String responseStr=response.toString();
                            Log.d("myWeather", responseStr);
                            Log.d("myWeather", address);
                            todayWeather = parseXML(responseStr);
                            if (todayWeather!=null){
                                Log.d("myWeather", todayWeather.toString());

                                Message msg =new Message();
                                msg.what = UPDATE_TODAY_WEATHER;
                                msg.obj=todayWeather;
                                mHandler.sendMessage(msg);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            if(con!=null){
                                con.disconnect();
                            }
                        }
                    }
                }
        ).start();
    }

    private TodayWeather parseXML(String xmldata) {
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
// 判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")){
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather!=null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                                Log.d("myWeather", "city: " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                                //Log.d("myWeather", "updatetime: " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                                //Log.d("myWeather", "shidu: " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                                //Log.d("myWeather", "wendu: " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                                //Log.d("myWeather", "pm25: " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                                //Log.d("myWeather", "quality: " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                //Log.d("myWeather", "fengxiang: " + xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                //Log.d("myWeather", "fengli: " + xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                //Log.d("myWeather", "date: " + xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                //Log.d("myWeather", "high: " + xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                //Log.d("myWeather", "low: " + xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                //Log.d("myWeather", "type: " + xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        break;
// 判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
// 进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

    void initView(){
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        loadState();
    }

    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+ "发布");
        humidityTv.setText("湿度："+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:"+todayWeather.getFengli());
        //weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
        setWeatherImg(todayWeather.getType());
        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
    }


    void setWeatherImg(String type){
        switch (type){
            case "暴雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "多云":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "晴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "沙尘暴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "小雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "阴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雨夹雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "中雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "中雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
            default:
                break;
        }
    }

    void saveState(String cityCode){
        SharedPreferences preferences=getSharedPreferences("user",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("cityCode", cityCode);
        editor.commit();
    }

    void loadState(){
        SharedPreferences preferences=getSharedPreferences("user", MODE_PRIVATE);
        String cityCode=preferences.getString("cityCode", "101010100");
        queryWeatherCode(cityCode);
    }
}


