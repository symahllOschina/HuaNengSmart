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
import com.huaneng.zhgd.bean.Answer;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 单选题--考试结果
 */
public class QuestionResultView extends LinearLayout {

    @ViewInject(R.id.titleTv)
    private TextView titleTv;
    @ViewInject(R.id.radioGroup)
    private RadioGroup radioGroup;
    @ViewInject(R.id.choiceTv)
    private TextView choiceTv;
    @ViewInject(R.id.correctTv)
    private TextView correctTv;

    public QuestionResultView(Context context) {
        this(context, null);
    }

    public QuestionResultView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuestionResultView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public QuestionResultView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_question_result, this);
        x.view().inject(this);
    }

    public void setData(int index, Answer answer) {
        titleTv.setText(index + "、" + answer.getTitleAndScore());
        addOption(answer.answer_a, 0);
        addOption(answer.answer_b, 1);
        addOption(answer.answer_c, 2);
        addOption(answer.answer_d, 3);
        UIUtils.showText(choiceTv, "选择答案：", answer.choice);
        UIUtils.showText(correctTv, "正确答案：", answer.answer);

        ((RadioButton)radioGroup.getChildAt(answer.getChoiceIndex())).setTextColor(getResources().getColor(R.color.red));
    }

    private void addOption(String answer, int i) {
        if (!TextUtils.isEmpty(answer)) {
            ((RadioButton)radioGroup.getChildAt(i)).setText(answer);
        }
    }
}
