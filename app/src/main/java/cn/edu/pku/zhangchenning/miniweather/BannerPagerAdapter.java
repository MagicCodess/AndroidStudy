package cn.edu.pku.zhangchenning.miniweather;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;
/**
 * Created by Arthur on 2017/11/9.
 */

public class BannerPagerAdapter extends PagerAdapter {

    private List<View> banners;
    /**
     * 返回viewPager元素数量
     * 仅banner位使用
     */

    public BannerPagerAdapter(List<View> list)
    {
        banners = list;
    }
    @Override
    public int getCount() {
        return banners.size();
    }

    /**
     * 实例化一个元素
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v=banners.get(position%banners.size());
        ViewGroup parent = (ViewGroup) v.getParent();
        //Log.i("ViewPaperAdapter", parent.toString());
        if (parent != null) {
            parent.removeView(v);
        }
        container.addView(v);
        return v;
    }

    /**
     * 判断是否相等
     */
    //  @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO 自动生成的方法存根
        return arg0 == (View)arg1;
    }

    /**
     * 销毁元素
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO 自动生成的方法存根
        //ImageView imageView = banners.get(position%banners.size());
        //imageView.setImageDrawable(null);

        container.removeView((View) object);
    }
}
