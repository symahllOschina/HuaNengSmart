package com.huaneng.zhgd.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.Question;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 单选题--考试
 */
public class SingleQuestionView extends LinearLayout {

    @ViewInject(R.id.titleTv)
    private TextView titleTv;
    @ViewInject(R.id.radioGroup)
    private RadioGroup radioGroup;

    public SingleQuestionView(Context context) {
        this(context, null);
    }

    public SingleQuestionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleQuestionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SingleQuestionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_single_question, this);
        x.view().inject(this);
    }

    public void setData(int index, Question question) {
        titleTv.setText(index + "、" + question.getTitleAndScore());
        addOption(question.answer_a, 0);
        addOption(question.answer_b, 1);
        addOption(question.answer_c, 2);
        addOption(question.answer_d, 3);
    }

    private void addOption(String answer, int i) {
        if (!TextUtils.isEmpty(answer)) {
            ((RadioButton)radioGroup.getChildAt(i)).setText(answer);
        }
    }

    public String getAnswer() {
        RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        if (radioButton != null) {
            return radioButton.getTag().toString();
        }
        return null;
    }

}
