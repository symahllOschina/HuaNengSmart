package com.huaneng.zhgd.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.huaneng.zhgd.R;

import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

/**
 * 图片九宫格
 */
public class NineGridlayout extends ViewGroup {

    /**
     * 图片之间的间隔
     */
    private int gap = 5;
    private int columns;//
    private int rows;//
    private ArrayList<String> listData;
    private int totalWidth;
    private Activity activity;
    private boolean isOnlyShow;

    private int maxCount = 9;

    public void init(Activity activity) {
        this.init(activity, false);
    }

    public void init(Activity activity, boolean isOnlyShow) {
        this.activity = activity;
        this.isOnlyShow = isOnlyShow;
    }

    public NineGridlayout(Context context) {
        super(context);
    }

    public NineGridlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        ScreenTools screenTools= ScreenTools.instance(getContext());
        totalWidth=screenTools.getScreenWidth()-screenTools.dip2px(80);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
    private void layoutChildrenView(){
        int childrenCount = listData.size();

        int singleWidth = (totalWidth - gap * (3 - 1)) / 3;
        int singleHeight = singleWidth;
        int offset = ((3-columns)*singleWidth)/2;

        //根据子view数量确定高度
        LayoutParams params = getLayoutParams();
        params.height = singleHeight * rows + gap * (rows - 1);
        setLayoutParams(params);

        for (int i = 0; i < childrenCount; i++) {
            CustomImageView childrenView = (CustomImageView) getChildAt(i);
            String url = listData.get(i);
            if (!TextUtils.isEmpty(url)) {
                childrenView.setImageUrl(url);
            } else {
                childrenView.setBackgroundResource(R.drawable.ic_add_img);
            }
            int[] position = findPosition(i);
            int left = (singleWidth + gap) * position[1];
            int top = (singleHeight + gap) * position[0];
            int right = left + singleWidth;
            int bottom = top + singleHeight;

            childrenView.layout(left + offset, top, right + offset, bottom);
        }

    }

    private int[] findPosition(int childNum) {
        int[] position = new int[2];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if ((i * columns + j) == childNum) {
                    position[0] = i;//行
                    position[1] = j;//列
                    break;
                }
            }
        }
        return position;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }


    public void setImagesData(ArrayList<String> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        if (!isOnlyShow && !TextUtils.isEmpty(list.get(0)) && list.size() < maxCount) {
            list.add("");
        }
        if (isOnlyShow) {
            // 远程文件
            listData = UIUtils.decorateUrl(list);
        } else {
            // 本机文件
            listData = list;
        }
        removeAllViews();
        generateChildrenLayout(listData.size());
        int i = 0;
        while (i < listData.size()) {
            CustomImageView iv = generateImageView(i);
            addView(iv,generateDefaultLayoutParams());
            i++;
        }
        layoutChildrenView();
    }


    /**
     * 根据图片个数确定行列数量
     * 对应关系如下
     * num	row	column
     * 1	   1	1
     * 2	   1	2
     * 3	   1	3
     * 4	   2	2
     * 5	   2	3
     * 6	   2	3
     * 7	   3	3
     * 8	   3	3
     * 9	   3	3
     *
     * @param length
     */
    private void generateChildrenLayout(int length) {
        if (length <= 3) {
            rows = 1;
            columns = length;
        } else if (length <= 6) {
            rows = 2;
            columns = 3;
//            if (length == 4) {
//                columns = 2;
//            }
        } else {
            rows = 3;
            columns = 3;
        }
    }

    private CustomImageView generateImageView(int pos) {
        CustomImageView iv = new CustomImageView(getContext());
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomImageView img = (CustomImageView)v;
                int position = Integer.valueOf(v.getTag(R.id.nineGridlayout).toString());
                if (TextUtils.isEmpty(img.getImageUrl())) {
                    PhotoPicker.builder()
                            .setPhotoCount(maxCount)
                            .setShowCamera(true)
                            .setShowGif(true)
                            .setPreviewEnabled(false)
                            .start(activity, PhotoPicker.REQUEST_CODE);
                } else {
                    if (TextUtils.isEmpty(listData.get(listData.size() - 1))) {
                        ArrayList<String> list = new ArrayList<>(listData);
                        list.remove(list.size() - 1);
                        PhotoPreview.builder()
                                .setPhotos(list)
                                .setCurrentItem(position)
                                .setShowDeleteButton(!isOnlyShow)
                                .start(activity, PhotoPicker.REQUEST_CODE);
                    } else {
                        PhotoPreview.builder()
                                .setPhotos(listData)
                                .setCurrentItem(position)
                                .setShowDeleteButton(!isOnlyShow)
                                .start(activity, PhotoPicker.REQUEST_CODE);
                    }
                }
            }
        });
        iv.setTag(R.id.nineGridlayout, pos);
        iv.setBackgroundColor(Color.parseColor("#f5f5f5"));
        return iv;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }
}
