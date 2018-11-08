package com.huaneng.zhgd.site;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaneng.zhgd.utils.UIUtils;
import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.GlideApp;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.modules.Contact;
import com.huaneng.zhgd.utils.GlideCircleTransform;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 2-通讯录：联系人详情
 */
@ContentView(R.layout.activity_contact)
public class ContactActivity extends BaseActivity {

    @ViewInject(R.id.headImg)
    private ImageView headImg;
    @ViewInject(R.id.nameTv)
    private TextView nameTv;
    @ViewInject(R.id.emailTv)
    private TextView emailTv;
    @ViewInject(R.id.departmentTv)
    private TextView departmentTv;
    @ViewInject(R.id.mobileTv)
    private TextView mobileTv;
    @ViewInject(R.id.wechatTv)
    private TextView wechatTv;

    @ViewInject(R.id.companyTv)
    private TextView companyTv;
    @ViewInject(R.id.jobTv)
    private TextView jobTv;
    @ViewInject(R.id.graduateTv)
    private TextView graduateTv;
    @ViewInject(R.id.majorTv)
    private TextView majorTv;
    @ViewInject(R.id.typeworkTv)
    private TextView typeworkTv;
    @ViewInject(R.id.certificateNoTv)
    private TextView certificateNoTv;
    @ViewInject(R.id.bloodTypeTv)
    private TextView bloodTypeTv;

    private Contact user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (Contact)getIntent().getSerializableExtra("contact");
        if (user != null) {
            UIUtils.showText(nameTv, user.name);
            UIUtils.showText(emailTv, user.email);
            UIUtils.showText(companyTv, user.company);
            UIUtils.showText(departmentTv, user.department);
            UIUtils.showText(jobTv, user.job);
            UIUtils.showText(mobileTv, user.mobile);
            UIUtils.showText(wechatTv, user.wechat);

            UIUtils.showText(graduateTv, user.graduate);
            UIUtils.showText(majorTv, user.major);
            UIUtils.showText(typeworkTv, user.typework);
            UIUtils.showText(certificateNoTv, user.certificate_no);
            UIUtils.showText(bloodTypeTv, user.blood_type);

            GlideApp.with(this)
                    .load(user.images)
                    .centerCrop()
                    .placeholder(R.drawable.ic_head)
                    .transform(new GlideCircleTransform(this))
                    .into(headImg);
        }
    }

    public void call(View view) {
        if (user != null && !TextUtils.isEmpty(user.mobile)) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + user.mobile));
            startActivity(intent);
        }
    }
}
