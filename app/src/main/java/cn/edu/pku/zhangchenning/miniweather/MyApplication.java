package cn.edu.pku.zhangchenning.miniweather;

import android.app.Application;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.zhangchenning.bean.City;
import cn.edu.pku.zhangchenning.db.CityDB;

/**
 * Created by Arthur on 2017/10/18.
 */

public class MyApplication extends Application{
    private  static  final  String TAG="MyAPP";
    private static MyApplication mApplication;
    public CityDB mCityDB;
    private List<City> mCityList;
    public String week="2日 星期二",date="4℃~6℃",climate="晴",wind="风力：2级",type="晴";

    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"MyApplication->OnCreate");
        mApplication=this;
        mCityDB=openCityDB();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        initCityList();
    }
    public static MyApplication getInstance(){
        return mApplication;
    }

    private CityDB openCityDB() {
        String path = "/data"+ Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases"
                + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        Log.d(TAG,path);
        if (!db.exists()) {
            String pathfolder = "/data" + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "databases"
                    + File.separator;
            File dirFirstFolder = new File(pathfolder);
            if(!dirFirstFolder.exists()){
                dirFirstFolder.mkdirs();
                Log.i("MyApp","mkdirs");
            }
            Log.i("MyApp","db is not exists");
            try {
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this, path);
    }

    private void initCityList(){
        mCityList = new ArrayList<City>();
        new Thread(new Runnable() {
            @Override
            public void run() {
// TODO Auto-generated method stub
                prepareCityList();
            }
        }).start();
    }

    private boolean prepareCityList() {
        mCityList = mCityDB.getAllCity();
        int i=0;
        for (City city : mCityList) {
            i++;
            String cityName = city.getCity();
            String cityCode = city.getNumber();
            Log.d(TAG,cityCode+":"+cityName);
        }
        Log.d(TAG,"i="+i);
        return true;
    }
    public List<City> getCityList() {
        return mCityList;
    }
}
