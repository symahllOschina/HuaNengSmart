package com.huaneng.zhgd.exam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.huaneng.zhgd.BaseRefreshActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.adapter.QuestionAdapter;
import com.huaneng.zhgd.bean.TestQuestion;
import com.huaneng.zhgd.bean.User;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.utils.SQLiteHelper;
import com.huaneng.zhgd.utils.UserUtils;

import org.xutils.view.annotation.ContentView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 考试中心
 */
@ContentView(R.layout.activity_exam_center)
public class ExamCenterActivity extends BaseRefreshActivity<TestQuestion> {//

    QuestionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("试题库");
        mAdapter = new QuestionAdapter(this, mList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ctx, ExamActivity.class);
                intent.putExtra("question", mList.get(i));
                startActivity(intent);
            }
        });
        pagerHandler.adapter = mAdapter;
    }

    @Override
    protected void onResume() {
        mPager.reset();
        getList();
        super.onResume();
    }

    public void getList() {
        if (!checkNetwork()) {
            return;
        }
        User user = UserUtils.getUser();
        HTTP.service.getSchoolexamList(user.markid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<TestQuestion>>(ctx) {

                    @Override
                    public void onSuccess(Response<List<TestQuestion>> response) {
                        pagerHandler.requestSuccess(response);
                    }

                    @Override
                    public void onWrong(String msg) {
                        super.onWrong(msg);
                        pagerHandler.requestFail();
                    }
                });
    }
}
