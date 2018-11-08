package com.huaneng.zhgd.network;

import com.huaneng.zhgd.BaseActivity;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/10.
 */

public class WeatherSubscriber<T> implements Observer<T> {

    private BaseActivity act;

    public WeatherSubscriber() {
    }

    public WeatherSubscriber(BaseActivity act) {
        this.act = act;
    }

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(T response) {
    }

    @Override
    public void onError(Throwable e) {
        if (act != null) {
            act.snackError("获取天气失败.");
            act.hideWaitDialog();
        }
    }

    @Override
    public void onComplete() {
        if (act != null) {
            act.hideWaitDialog();
        }
    }
}
