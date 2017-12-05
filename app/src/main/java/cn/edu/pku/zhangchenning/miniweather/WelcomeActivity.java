package cn.edu.pku.zhangchenning.miniweather;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import static cn.edu.pku.zhangchenning.miniweather.R.layout.page3;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener{
    private ViewPagerAdapter vpAdapter;

    private ViewPager vp;
    private List<View> views;
    private Button enter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.introduction);
        initViews();
    }
    private void initViews(){
        LayoutInflater inflater=LayoutInflater.from(this);
        views=new ArrayList<>();
        views.add(inflater.inflate(R.layout.page1,null));
        views.add(inflater.inflate(R.layout.page2,null));
        View p3 = inflater.inflate(R.layout.page3,null);
        enter= (Button) p3.findViewById(R.id.enter);
        enter.setOnClickListener(this);
        views.add(p3);
        vpAdapter=new ViewPagerAdapter(views,this);
        vp= (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.enter){
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
