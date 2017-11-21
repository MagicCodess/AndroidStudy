package cn.edu.pku.zhangchenning.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.pku.zhangchenning.bean.City;

public class SelectCity extends Activity implements View.OnClickListener{

    private ImageView mBackBtn;
    private ListView mlistView;
    private TextView mTitleName;
    private ClearEditText mClearEditText;
    private String code;
    private SideBar sideBar;

    final List<Map<String, Object>> filterDataList = new ArrayList<Map<String, Object>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.select_city);
        sideBar=findViewById(R.id.select_sidebar);

        initViews();

        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                filterData2(s);
            }
        });

    }
    private void filterData2(String filterStr){
        //filterDataList=new ArrayList<City>();

        Log.d("Filter",filterStr);
        List<City> data=MyApplication.getInstance().getCityList();
        final List<Map<String, Object>> cityLists = new ArrayList<Map<String, Object>>();
        if (TextUtils.isEmpty(filterStr)){
            filterDataList.clear();
            for (City city :data ) {
                Map<String, Object> cityList = new HashMap<String, Object>();
                String cityName = city.getCity();
                cityList.put("city",cityName);
                String cityCode=city.getNumber();
                cityList.put("code",cityCode);
                filterDataList.add(cityList);
            }
        }else{
            filterDataList.clear();
            for (City city :data ) {
                Map<String, Object> cityList = new HashMap<String, Object>();
                if (city.getFirstPY().indexOf(filterStr.toString())!=-1){
                    String cityName = city.getCity();
                    cityList.put("city",cityName);
                    String cityCode=city.getNumber();
                    cityList.put("code",cityCode);
                    filterDataList.add(cityList);
                }
            }
        }
        SimpleAdapter cityList = new SimpleAdapter(this, filterDataList,R.layout.item, new String[] { "city", "code"},new int[] {R.id.city,R.id.code});
        mlistView.setAdapter(cityList);
    }
    private void filterData(String filterStr){
        //filterDataList=new ArrayList<City>();

        Log.d("Filter",filterStr);
        List<City> data=MyApplication.getInstance().getCityList();
        final List<Map<String, Object>> cityLists = new ArrayList<Map<String, Object>>();
        if (TextUtils.isEmpty(filterStr)){
            filterDataList.clear();
            for (City city :data ) {
                Map<String, Object> cityList = new HashMap<String, Object>();
                String cityName = city.getCity();
                cityList.put("city",cityName);
                String cityCode=city.getNumber();
                cityList.put("code",cityCode);
                filterDataList.add(cityList);
            }
        }else{
            filterDataList.clear();
            for (City city :data ) {
                Map<String, Object> cityList = new HashMap<String, Object>();
                if (city.getCity().indexOf(filterStr.toString())!=-1){
                    String cityName = city.getCity();
                    cityList.put("city",cityName);
                    String cityCode=city.getNumber();
                    cityList.put("code",cityCode);
                    filterDataList.add(cityList);
                }
            }
        }
        SimpleAdapter cityList = new SimpleAdapter(this, filterDataList,R.layout.item, new String[] { "city", "code"},new int[] {R.id.city,R.id.code});
        mlistView.setAdapter(cityList);
    }
    private void initViews(){
        mBackBtn=findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        mTitleName=findViewById(R.id.title_name);
        Intent intent=getIntent();
        mTitleName.setText("当前城市："+intent.getStringExtra("city"));
        code=intent.getStringExtra("code");

        mClearEditText=(ClearEditText)findViewById(R.id.search_city);

        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                filterData(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //mClearEditText=(ClearEditText)findViewById(R.id.s)
        List<City> data=MyApplication.getInstance().getCityList();
        //final List<Map<String, Object>> cityLists = new ArrayList<Map<String, Object>>();
        for (City city :data ) {
            Map<String, Object> cityList = new HashMap<String, Object>();
            String cityName = city.getCity();
            cityList.put("city",cityName);
            String cityCode=city.getNumber();
            cityList.put("code",cityCode);
            filterDataList.add(cityList);
        }

        mlistView = (ListView)findViewById(R.id.title_list);
//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.item,data);
//        mlistView.setAdapter(adapter);
        SimpleAdapter cityList = new SimpleAdapter(this, filterDataList,R.layout.item, new String[] { "city", "code"},new int[] {R.id.city,R.id.code});

        mlistView.setAdapter(cityList);

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SelectCity.this, "Choosing:"+filterDataList.get(i).get("city")+", Please Wait...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("cityCode",filterDataList.get(i).get("code").toString());

                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                Intent i = new Intent();
                i.putExtra("cityCode",code);
                setResult(RESULT_OK,i);
                finish();
                break;
            default:
                break;
        }
    }

}


