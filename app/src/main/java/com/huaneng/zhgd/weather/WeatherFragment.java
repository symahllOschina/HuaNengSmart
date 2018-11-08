package com.huaneng.zhgd.weather;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.huaneng.zhgd.BaseFragment;
import com.huaneng.zhgd.Constants;
import com.huaneng.zhgd.GlideApp;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.adapter.DailyForecastAdapter;
import com.huaneng.zhgd.adapter.LifeStyleAdapter;
import com.huaneng.zhgd.bean.AirNowCity;
import com.huaneng.zhgd.bean.HNLocation;
import com.huaneng.zhgd.bean.HeAir;
import com.huaneng.zhgd.bean.HeAir6;
import com.huaneng.zhgd.bean.HeWeather;
import com.huaneng.zhgd.bean.HeWeather6;
import com.huaneng.zhgd.bean.Now;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.WeatherSubscriber;
import com.huaneng.zhgd.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 天气预报
 */
@ContentView(R.layout.fragment_weather)
public class WeatherFragment extends BaseFragment {

    @ViewInject(R.id.pm25)
    private TextView pm25;
    @ViewInject(R.id.pm10)
    private TextView pm10;
    @ViewInject(R.id.no2)
    private TextView no2;
    @ViewInject(R.id.so2)
    private TextView so2;
    @ViewInject(R.id.co)
    private TextView co;
    @ViewInject(R.id.o3)
    private TextView o3;

    @ViewInject(R.id.city)
    private TextView cityTv;
    @ViewInject(R.id.cond_txt)
    private TextView cond_txt;
    @ViewInject(R.id.cond_img)
    private ImageView cond_img;
    @ViewInject(R.id.tmp)
    private TextView tmp;
    @ViewInject(R.id.dailyListView)
    private ListView dailyListView;
    @ViewInject(R.id.lifestyleListView)
    private ListView lifestyleListView;

    private String city;
//    private String longLat;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        city = SharedPreferencesUtils.create(activity).get(Constants.SPR_LOC_CITY);
//        longLat = SharedPreferencesUtils.create(activity).get(Constants.SPR_LOC_LONGLAT);
        query();
    }

    private void query() {
        if (TextUtils.isEmpty(city)) {
            city = "西安市";
        }
        if (cityTv == null) {
            return;
        }
        HTTP.weatherService.weather(Constants.HEWEATHER_KEY, city)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new WeatherSubscriber<HeWeather>(activity){

                    @Override
                    public void onNext(HeWeather response) {
                        if (activity.isFinishing() || activity.isDestroyed()) {
                            return;
                        }
                        if (response.HeWeather6 != null && !response.HeWeather6.isEmpty()) {
                            HeWeather6 weather6 = response.HeWeather6.get(0);
                            if (weather6 != null && weather6.success()) {
                                Now now = weather6.now;
                                cityTv.setText(weather6.basic.getCity());
                                cond_txt.setText(now.cond_txt);
                                tmp.setText(now.tmp + "°\t" + now.wind_dir);

                                GlideApp.with(activity)
                                        .load(HeWeather.weatherIcons.get(now.cond_code))
                                        .into(cond_img);

                                dailyListView.setAdapter(new DailyForecastAdapter(activity, weather6.daily_forecast));
                                lifestyleListView.setAdapter(new LifeStyleAdapter(activity, weather6.lifestyle));
                            } else {
                                activity.snackError("天气预报数据获取失败.");
                            }
                        } else {
                            activity.snackError("天气预报数据获取失败.");
                        }
                    }
                });
        HTTP.weatherService.air(Constants.HEWEATHER_KEY, city)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new WeatherSubscriber<HeAir>(activity){

                    @Override
                    public void onNext(HeAir response) {
                        HeAir6 heAir6 = response.HeWeather6.get(0);
                        if (heAir6 != null && heAir6.success()) {
                            AirNowCity air = heAir6.air_now_city;
                            pm25.setText(air.pm25);
                            pm10.setText(air.pm10);
                            no2.setText(air.no2);
                            so2.setText(air.so2);
                            co.setText(air.co);
                            o3.setText(air.o3);
                        }
                    }
                });
    }

    @Override
    protected boolean isEnableEventBus() {
        return true;
    }

    @Subscribe
    public void onLocationSuccess(final HNLocation location) {
        city = location.city;
//        longLat = location.longLat;
        query();
    }
}
