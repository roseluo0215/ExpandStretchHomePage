package com.lc.expand.stretch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by luocheng on 18/6/1.
 */

public class InterceptNestedScrollView extends NestedScrollView {

  // 滑动底部list新时候，是否拦截联动效果。如果是展开效果就可以联动也就是不拦截，折叠的时候就拦截。让新闻listview自己滑动处理
  private boolean mIsExpanded = true;

  public InterceptNestedScrollView(@NonNull Context context) {
    super(context);
  }

  public InterceptNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public InterceptNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
    return super.onNestedFling(target, velocityX, velocityY, consumed);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    return mIsExpanded && super.onInterceptTouchEvent(ev);// 如果展开就拦截，不展开不拦截 注意super.onInterceptTouchEvent(ev)必须加上否则展开时子类点击事件被拦截
  }

  /**
   * mIsExpanded  true  展开， false 不展开
   * */
  public void setIsExpanded(boolean isExpanded) {
    mIsExpanded = isExpanded;
  }
}
