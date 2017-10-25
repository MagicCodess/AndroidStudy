package cn.edu.pku.zhangchenning.miniweather;

/**
 * Created by Arthur on 2017/10/11.
 */

public class weather
{
    public enum weatherType{
        baoxue("暴雪"),
        baoyu("暴雨"),
        dabaoyu("大暴雨"),
        daxue("大雪"),
        dayu("大雨"),
        duoyun("多云"),
        leizhenyu("雷阵雨"),
        leizhenyubingbao("雷阵雨冰雹"),
        qing("晴"),
        shachenbao("沙尘暴"),
        tedabaoyu("特大暴雨"),
        wu("雾"),
        xiaoxue("小雪"),
        xiaoyu("小雨"),
        yin("阴"),
        yujiaxue("雨夹雪"),
        zhenxue("阵雪"),
        zhenyu("阵雨"),
        zhongxue("中雪"),
        zhongyu("中雨");

        private final String text;
        weatherType(final String text){
            this.text=text;
        }
        @Override
        public String toString(){
            return text;
        }
    }
}
