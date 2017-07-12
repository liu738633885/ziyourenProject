package com.huaqie.wubingjie.task;

import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.fragment.BaseFragment;
import com.huaqie.wubingjie.model.Comment;

import java.util.ArrayList;

/**
 * Created by lewis on 2016/10/10.
 */

public class CommentAdapter extends BaseQuickAdapter<Comment> {
    public CommentAdapter() {
        super(R.layout.item_comment, new ArrayList<Comment>());
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, Comment comment) {
        baseViewHolder.setText(R.id.tv_comment_nickname, comment.getMember_nickname());
        baseViewHolder.setText(R.id.tv_comment_content, comment.getComment_content());
    }

    /**
     * Created by lewis on 2016/10/10.
     */

    public static class OrderInfoListFragment extends BaseFragment {
        @Override
        protected void initView(View view, Bundle savedInstanceState) {

        }

        @Override
        protected int getLayoutId() {
            return 0;
        }
    }
}
