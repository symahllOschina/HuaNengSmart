package com.huaneng.zhgd.study;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.huaneng.zhgd.BaseRefreshFragment;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.adapter.AttachmentAdapter;
import com.huaneng.zhgd.bean.Attachment;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.utils.FileDisplay;
import com.huaneng.zhgd.utils.UIUtils;

import org.xutils.view.annotation.ContentView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 学习资料
 */
@ContentView(R.layout.fragment_study_doc)
public class StudyDocFragment extends BaseRefreshFragment<Attachment> {//

    AttachmentAdapter mAdapter;

    public static final String TYPE_1 = "1";//学习资料
    public static final String TYPE_2 = "2";//网络党课
    public static final String TYPE_3 = "3";//内部资料
    // 1.学习资料2.网络党课3.内部资料
    String type;

    public static StudyDocFragment getInstance(String type) {
        StudyDocFragment fragment = new StudyDocFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        type = getArguments().getString("type");
        mAdapter = new AttachmentAdapter(activity, mList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FileDisplay.displayAttachment(activity, mList.get(i), "schoolfile/read");
            }


        });
        pagerHandler.adapter = mAdapter;
        getList();
    }

    public void getList() {
        if (!activity.checkNetwork()) {
            return;
        }
        HTTP.service.schoolfileList(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Attachment>>(activity){

                    @Override
                    public void onSuccess(Response<List<Attachment>> response) {
                        if (response.data != null && !response.data.isEmpty()) {
                            String imageurl = response.imageurl;
                            for (Attachment attachment: response.data) {
                                attachment.path = UIUtils.decorateUrl(attachment.path, imageurl);
                            }
                        }
                        pagerHandler.requestSuccess(response);
                    }

                    @Override
                    public void onWrong(String msg) {
                        pagerHandler.requestFail();
                    }
                });
    }
}
