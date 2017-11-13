package cn.edu.pku.zhangchenning.miniweather;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;


import java.io.File;
import java.io.FileOutputStream;
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
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.zhangchenning.bean.TodayWeather;
import cn.edu.pku.zhangchenning.db.CityDB;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static cn.edu.pku.zhangchenning.miniweather.R.drawable.base_action_bar_action_city;

/**
 * Created by Arthur on 2017/9/22.
 */

public class MainActivity extends Activity implements View.OnClickListener{
    private ImageView mUpdateBtn;

    private ImageView mCitySelect;

    private ImageView mTitleShare;

    private EditText mCityName;

    private String dictationResultStr = "[";

    private MyApplication mApp;
    private TextView cityTv, timeTv, temperatureTv0, humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv, climateTv, windTv, city_name_Tv;
    private TextView dateTv2, temperatureTv2, climateTv2, windTv2, dateTv3, temperatureTv3, climateTv3, windTv3, dateTv4, temperatureTv4, climateTv4, windTv4,
            dateTv5, temperatureTv5, climateTv5, windTv5, dateTv6, temperatureTv6, climateTv6, windTv6, dateTv7, temperatureTv7, climateTv7, windTv7;
    private ImageView weatherImg, pmImg, weatherImg2, weatherImg3, weatherImg4, weatherImg5, weatherImg6, weatherImg7;

    View view1, view2, view3;
    ViewPager viewPager;
    ImageView banner_top;
    ImageView mLocation;
    String city;
    String imagePath;
    ///////////////////////////////////////////////////////////////////////////
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    ////////////////////////////////////////////////////////////////////////////
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateBtn = findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        mCitySelect = findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        cityTv = findViewById(R.id.city);
        cityTv.setOnClickListener(this);

        mCityName = findViewById(R.id.city_name);

        pmImg = findViewById(R.id.pm2_5_img);
        mLocation = findViewById(R.id.title_location);
        mTitleShare=findViewById(R.id.title_share);
/////////////////////////////////////////////////////////////////////////////////////////
        mLocationClient = new LocationClient(MainActivity.this);
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setWifiCacheTimeOut(0);
//可选，是否需要地址信息，默认为不需要，即参数为false
//如果开发者需要获得当前点的地址信息，此处必须为true

        mLocationClient.setLocOption(option);


        initView();

        viewPager = (ViewPager) findViewById(R.id.imaster_viewpager);      //banner位
        banner_top = (ImageView) findViewById(R.id.percentage);    //banner进度
        banner_top.setImageResource(R.drawable.banner_select0);
        mLocation.setOnClickListener(this);
        mTitleShare.setOnClickListener(this);
        ArrayList LayoutList = new ArrayList<>();

        LayoutList.add(view1);
        LayoutList.add(view2);

        BannerPagerAdapter BPA = new BannerPagerAdapter(LayoutList);
        viewPager.setAdapter(BPA);
        viewPager.setCurrentItem(0);
        //mHandler.sendEmptyMessageDelayed(0, 5000);               //当前延时5秒，最大600张

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int tmp = position % 2;
                switch (tmp) {
                    case 0:
                        banner_top.setImageResource(R.drawable.banner_select0);
                        break;
                    case 1:
                        banner_top.setImageResource(R.drawable.banner_select2);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void screenshot() {
        // 获取屏幕
        View dView = getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bmp = dView.getDrawingCache();
        if (bmp != null)
        {
            try {
                // 获取内置SD卡路径
                String sdCardPath = Environment.getExternalStorageDirectory().getPath();
                // 图片文件路径
                imagePath = sdCardPath + File.separator + "screenshot.png";
                File file = new File(imagePath);
                FileOutputStream os = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            System.out.println(city);
        }
    }

    void networkStateTest() {
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            //Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_city_manager) {
            Intent i = new Intent(this, SelectCity.class);
            i.putExtra("city", cityTv.getText().toString());
            i.putExtra("code", MyApplication.getInstance().mCityDB.getCityNumber(cityTv.getText().toString()));
            startActivityForResult(i, 1);
        }

        if (view.getId() == R.id.city) {
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
                        List<DictationResult> dictationResultList = gson.fromJson(dictationResultStr, new TypeToken<List<DictationResult>>() {
                        }.getType());
                        String finalResult = "";
                        for (int i = 0; i < dictationResultList.size() - 1; i++) {
                            finalResult += dictationResultList.get(i).toString();
                        }
                        mCityName.setText(finalResult);

                        //获取焦点
                        mCityName.requestFocus();
                        System.out.println(mCityName.getText().toString());
                        mApp = MyApplication.getInstance();
                        String cityNumber;
                        if ((cityNumber = mApp.mCityDB.getCityNumber(mCityName.getText().toString())) != null) {
                            queryWeatherCode(cityNumber);
                        } else {
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

        if (view.getId() == R.id.title_update_btn) {
            SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
            String cityCode = preferences.getString("cityCode", "101010100");
            //String cityCode = sharedPreferences.getString("main_city_code","101160101");
            Log.d("myWeather", cityCode);
            saveState(cityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                //Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);
                //Toast.makeText(MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show();
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
        if (view.getId() == R.id.title_location) {
            Toast.makeText(MainActivity.this, "Locating...", Toast.LENGTH_LONG).show();
            new Thread() {
                String cityNumber;
                public void run() {
                    try {
                        mLocationClient.restart();
                        mLocationClient.startIndoorMode();
                        mApp = MyApplication.getInstance();
                        Thread.sleep(3000);

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    System.out.println(city);
                    if ((cityNumber = mApp.mCityDB.getCityNumber(city.replace("市",""))) != null) {
                        queryWeatherCode(cityNumber);
                        saveState(cityNumber);
                    } else {
                        Toast.makeText(MainActivity.this, "Please Try Again！", Toast.LENGTH_LONG).show();
                    }
                }

            }.start();
            mLocationClient.stop();
        }
        if (view.getId()==R.id.title_share){
            screenshot();
            if (imagePath != null){
                Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送的属性
                File file = new File(imagePath);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));// 分享的内容
                //intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this.getApplicationContext(), "你的应用名.fileprovider", file));// 分享的内容
                intent.setType("image/*");// 分享发送的数据类型
                Intent chooser = Intent.createChooser(intent, "Share screen shot");
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivity(chooser);
                }
            } else {
                Toast.makeText(this, "先截屏，再分享", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode==1&&resultCode==RESULT_OK){
            viewPager.setCurrentItem(0);
            String newCityCode = data.getStringExtra("cityCode");
            //String cityCode = sharedPreferences.getString("main_city_code","101160101");
            Log.d("myWeather",newCityCode);
            saveState(newCityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                //Log.d("myWeather", "网络OK");
                queryWeatherCode(newCityCode);
                //Toast.makeText(MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show();
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
                            }else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                                //Log.d("myWeather", "wendu: " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                                //Log.d("myWeather", "shidu: " + xmlPullParser.getText());
                            }  else if (xmlPullParser.getName().equals("pm25")) {
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
                            } else if (xmlPullParser.getName().equals("fengli")) {
                                eventType = xmlPullParser.next();
                                if (fengliCount==0){
                                    todayWeather.setFengli(xmlPullParser.getText());
                                }else if (fengliCount==1)
                                {
                                    todayWeather.setFengli2(xmlPullParser.getText());
                                }else if (fengliCount==2)
                                {
                                    todayWeather.setFengli3(xmlPullParser.getText());
                                }else if (fengliCount==3)
                                {
                                    todayWeather.setFengli4(xmlPullParser.getText());
                                }else if (fengliCount==4)
                                {
                                    todayWeather.setFengli5(xmlPullParser.getText());
                                }else if (fengliCount==5)
                                {
                                    todayWeather.setFengli6(xmlPullParser.getText());
                                }else if (fengliCount==6)
                                {
                                    todayWeather.setFengli7(xmlPullParser.getText());
                                }
                                //Log.d("myWeather", "fengli: " + xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date")) {
                                eventType = xmlPullParser.next();
                                if (dateCount==0){
                                    todayWeather.setDate(xmlPullParser.getText());
                                }else if (dateCount==1)
                                {
                                    todayWeather.setDate2(xmlPullParser.getText());
                                }else if (dateCount==2)
                                {
                                    todayWeather.setDate3(xmlPullParser.getText());
                                }else if (dateCount==3)
                                {
                                    todayWeather.setDate4(xmlPullParser.getText());
                                }else if (dateCount==4)
                                {
                                    todayWeather.setDate5(xmlPullParser.getText());
                                }else if (dateCount==5)
                                {
                                    todayWeather.setDate6(xmlPullParser.getText());
                                }else if (dateCount==6)
                                {
                                    todayWeather.setDate7(xmlPullParser.getText());
                                }
                                //Log.d("myWeather", "date: " + xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high")) {
                                eventType = xmlPullParser.next();
                                if (highCount==0){
                                    todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                }else if (highCount==1)
                                {
                                    todayWeather.setHigh2(xmlPullParser.getText().substring(2).trim());
                                }
                                else if (highCount==2)
                                {
                                    todayWeather.setHigh3(xmlPullParser.getText().substring(2).trim());
                                }
                                else if (highCount==3)
                                {
                                    todayWeather.setHigh4(xmlPullParser.getText().substring(2).trim());
                                }
                                else if (highCount==4)
                                {
                                    todayWeather.setHigh5(xmlPullParser.getText().substring(2).trim());
                                }
                                else if (highCount==5)
                                {
                                    todayWeather.setHigh6(xmlPullParser.getText().substring(2).trim());
                                }
                                else if (highCount==6)
                                {
                                    todayWeather.setHigh7(xmlPullParser.getText().substring(2).trim());
                                }
                                //Log.d("myWeather", "high: " + xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") ) {
                                eventType = xmlPullParser.next();
                                if (lowCount==0){
                                    todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                }else if (lowCount==1)
                                {
                                    todayWeather.setLow2(xmlPullParser.getText().substring(2).trim());
                                }
                                else if (lowCount==2)
                                {
                                    todayWeather.setLow3(xmlPullParser.getText().substring(2).trim());
                                }
                                else if (lowCount==3)
                                {
                                    todayWeather.setLow4(xmlPullParser.getText().substring(2).trim());
                                }
                                else if (lowCount==4)
                                {
                                    todayWeather.setLow5(xmlPullParser.getText().substring(2).trim());
                                }
                                else if (lowCount==5)
                                {
                                    todayWeather.setLow6(xmlPullParser.getText().substring(2).trim());
                                }
                                else if (lowCount==6)
                                {
                                    todayWeather.setLow7(xmlPullParser.getText().substring(2).trim());
                                }
                                //Log.d("myWeather", "low: " + xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type")) {
                                eventType = xmlPullParser.next();
                                if (typeCount==0){
                                    todayWeather.setType(xmlPullParser.getText());
                                }else if (typeCount==1)
                                {
                                    todayWeather.setType2(xmlPullParser.getText());
                                }
                                else if (typeCount==2)
                                {
                                    todayWeather.setType3(xmlPullParser.getText());
                                }
                                else if (typeCount==3)
                                {
                                    todayWeather.setType4(xmlPullParser.getText());
                                }
                                else if (typeCount==4)
                                {
                                    todayWeather.setType5(xmlPullParser.getText());
                                }
                                else if (typeCount==5)
                                {
                                    todayWeather.setType6(xmlPullParser.getText());
                                }
                                else if (typeCount==6)
                                {
                                    todayWeather.setType7(xmlPullParser.getText());
                                }
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
        LayoutInflater layoutInflater2 = LayoutInflater.from(this);
        view1 = layoutInflater2.inflate(R.layout.viewpager, null);
        view2 = layoutInflater2.inflate(R.layout.viewpager2, null);
        view3 = layoutInflater2.inflate(R.layout.viewpager3, null);
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv0=(TextView)findViewById(R.id.temperature_now);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        dateTv2=view1.findViewById(R.id.weather11);
        dateTv3=view1.findViewById(R.id.weather21);
        dateTv4=view1.findViewById(R.id.weather31);
        dateTv5=view2.findViewById(R.id.weather41);
        dateTv6=view2.findViewById(R.id.weather51);
        dateTv7=view2.findViewById(R.id.weather61);
        weatherImg2=view1.findViewById(R.id.weather12);
        weatherImg3=view1.findViewById(R.id.weather22);
        weatherImg4=view1.findViewById(R.id.weather32);
        weatherImg5=view2.findViewById(R.id.weather42);
        weatherImg6=view2.findViewById(R.id.weather52);
        weatherImg7=view2.findViewById(R.id.weather62);
        temperatureTv2=view1.findViewById(R.id.weather13);
        temperatureTv3=view1.findViewById(R.id.weather23);
        temperatureTv4=view1.findViewById(R.id.weather33);
        temperatureTv5=view2.findViewById(R.id.weather43);
        temperatureTv6=view2.findViewById(R.id.weather53);
        temperatureTv7=view2.findViewById(R.id.weather63);
        climateTv2=view1.findViewById(R.id.weather14);
        climateTv3=view1.findViewById(R.id.weather24);
        climateTv4=view1.findViewById(R.id.weather34);
        climateTv5=view2.findViewById(R.id.weather44);
        climateTv6=view2.findViewById(R.id.weather54);
        climateTv7=view2.findViewById(R.id.weather64);
        windTv2=view1.findViewById(R.id.weather15);
        windTv3=view1.findViewById(R.id.weather25);
        windTv4=view1.findViewById(R.id.weather35);
        windTv5=view2.findViewById(R.id.weather45);
        windTv6=view2.findViewById(R.id.weather55);
        windTv7=view2.findViewById(R.id.weather65);
        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        temperatureTv0.setText("N/A");
        climateTv.setText("N/A");
        dateTv2.setText("N/A");
        dateTv3.setText("N/A");
        dateTv4.setText("N/A");
        dateTv5.setText("N/A");
        dateTv6.setText("N/A");
        dateTv7.setText("N/A");
        temperatureTv2.setText("N/A");
        temperatureTv3.setText("N/A");
        temperatureTv4.setText("N/A");
        temperatureTv5.setText("N/A");
        temperatureTv6.setText("N/A");
        temperatureTv7.setText("N/A");
        climateTv2.setText("N/A");
        climateTv3.setText("N/A");
        climateTv4.setText("N/A");
        climateTv5.setText("N/A");
        climateTv6.setText("N/A");
        climateTv7.setText("N/A");
        windTv2.setText("N/A");
        windTv3.setText("N/A");
        windTv4.setText("N/A");
        windTv5.setText("N/A");
        windTv6.setText("N/A");
        windTv7.setText("N/A");
        loadState();
    }

    void updateTodayWeather(TodayWeather todayWeather){
        if (todayWeather.getHigh()!=null) {
            city_name_Tv.setText(todayWeather.getCity() + "天气");
            cityTv.setText(todayWeather.getCity());
            timeTv.setText(todayWeather.getUpdatetime() + "发布");
            humidityTv.setText("湿度：" + todayWeather.getShidu());
            pmDataTv.setText(todayWeather.getPm25());
            pmQualityTv.setText(todayWeather.getQuality());
            weekTv.setText(todayWeather.getDate());
            temperatureTv.setText(todayWeather.getHigh() + "~" + todayWeather.getLow());
            temperatureTv0.setText("温度："+todayWeather.getWendu()+"℃");
            climateTv.setText(todayWeather.getType());
            windTv.setText("风力:" + todayWeather.getFengli());
            dateTv2.setText(todayWeather.getDate2());
            dateTv3.setText(todayWeather.getDate3());
            dateTv4.setText(todayWeather.getDate4());
            dateTv5.setText(todayWeather.getDate5());
            dateTv6.setText("No Data");
            dateTv7.setText("No Data");
            temperatureTv2.setText(todayWeather.getHigh2() + "~" + todayWeather.getLow2());
            temperatureTv3.setText(todayWeather.getHigh3() + "~" + todayWeather.getLow3());
            temperatureTv4.setText(todayWeather.getHigh4() + "~" + todayWeather.getLow4());
            temperatureTv5.setText(todayWeather.getHigh5() + "~" + todayWeather.getLow5());
            temperatureTv6.setText("No Data");
            temperatureTv7.setText("No Data");
            climateTv2.setText(todayWeather.getType2());
            climateTv3.setText(todayWeather.getType3());
            climateTv4.setText(todayWeather.getType4());
            climateTv5.setText(todayWeather.getType5());
            climateTv6.setText("No Data");
            climateTv7.setText("No Data");
            windTv2.setText("风力:" + todayWeather.getFengli2());
            windTv3.setText("风力:" + todayWeather.getFengli3());
            windTv4.setText("风力:" + todayWeather.getFengli4());
            windTv5.setText("风力:" + todayWeather.getFengli5());
            windTv6.setText("No Data");
            windTv7.setText("No Data");
            //weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
            setWeatherImg(weatherImg,todayWeather.getType());
            if (todayWeather.getType2()!=null){
                setWeatherImg(weatherImg2,todayWeather.getType2());
            }
            if (todayWeather.getType3()!=null){
                setWeatherImg(weatherImg3,todayWeather.getType3());
            }
            if (todayWeather.getType4()!=null){
                setWeatherImg(weatherImg4,todayWeather.getType4());
            }
            if (todayWeather.getType5()!=null){
                setWeatherImg(weatherImg5,todayWeather.getType5());
            }
            if(todayWeather.getPm25()!=null)
            {
                int pms=Integer.parseInt(todayWeather.getPm25());
                if(pms<=50)
                    pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
                else if(pms<=100)
                    pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
                else if(pms<=150)
                    pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
                else if(pms<=200)
                    pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
                else if(pms<=300)
                    pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
                else
                    pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
            }
            Toast.makeText(MainActivity.this, "Update Successfully！", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this, "No Weather Info of Selected City, Back to Beijing！", Toast.LENGTH_SHORT).show();
            queryWeatherCode("101010100");
        }
    }


    void setWeatherImg(ImageView Img,String type){
        switch (type){
            case "暴雪":
                Img.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                Img.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                Img.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                Img.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "多云":
                Img.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                Img.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                Img.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "晴":
                Img.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "沙尘暴":
                Img.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                Img.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                Img.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                Img.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "小雨":
                Img.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "阴":
                Img.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雨夹雪":
                Img.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雨":
                Img.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "中雪":
                Img.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "中雨":
                Img.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
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


