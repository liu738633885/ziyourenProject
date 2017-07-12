package me.yokeyword.indexablelistview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by AINANA-RD-X on 2016/7/5.
 */
public class IndexHeadGridView extends GridView {
    private boolean haveScrollbar = true;
    public IndexHeadGridView(Context context) {
        super(context);
    }
    public IndexHeadGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public IndexHeadGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    /**
     * 设置是否有ScrollBar，当要在ScollView中显示时，应当设置为false。 默认为 true
     *
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        if (haveScrollbar == false) {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
//        } else {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        }
    }
}
