package com.lc.expand.stretch;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by luocheng on 18/4/9.
 * <p>
 * 1.在构造器中初始化view并注册监听事件。
 * 2.在listView触摸拦截事件中记录手指按下和抬起的坐标。
 * 3.当慢慢向上滑动并抬起手指的时候会走onScroll方法最后走拦截事件的case MotionEvent.ACTION_UP:
 * 当快速向上滑动抬起手指的时候会走onScroll 》》拦截事件case MotionEvent.ACTION_UP: 》》 onScroll方法。
 * 4.当上拉的时候判断是否可以加载更多的条件是listView到了最底部最后一个item, listview不在加载中, 且为上拉操作.
 */

public class SwipePullDownRefresh extends SwipeRefreshLayout
        implements
        AbsListView.OnScrollListener,
        SwipeRefreshLayout.OnRefreshListener {

  public static final int PULL_DOWN_TO_REFRESH_MODE = 0;// 下拉模式

  public static final int PULL_UP_TO_REFRESH_MODE = 1;// 上拉模式

  public static final int BOTH_MODE = 2;// 两者都可以

  public static final int DISABLED_MODE = 4;//禁止上拉下拉模式

  private int mCurrentMode = 2;// 默认是可以上拉和下拉

  private ListView mListView;
  /**
   * 它获得的是触发移动事件的最短距离，如果小于这个距离就不触发移动控件，如viewpager就是用这个距离来判断用户是否翻页
   */
  private int mTouchSlop;
  /**
   * 加载更多的footview
   */
  private View mListViewFooter;
  /**
   * 按下时的y坐标
   */
  private int mYDown;
  /**
   * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
   * 这是初始化一个10000像素，为了解决onScroll和拦截事件ACTION_UP的冲突问题。假如手机的竖屏分辨率为1980，
   * 如果mYLast初始化小于或等于假如手机的竖屏分辨率为1980的时候容易造成当慢慢向上滑动手指还没有抬起的时候就已经加载数据了。
   * 事件被onScroll给截获加载了。我们需要的是当慢慢滑动向上滑动的时候手指抬起走dispatchTouchEvent的UP事件，快速滑动的时候走onScroll。
   */
  private int mYLast = 10000;
  /**
   * 是否正在加载
   */
  private boolean mIsLoading = false;
  /**
   * 上拉下拉监听器
   */
  private IRefreshListener mOnRefreshListener;
  /**
   * 用于标记手指弹起的标记，
   */
  private boolean mIsActionUp = false;
  /**
   * 用于标记已经添加了footerview，避免action_up的时候重复添加
   */
  private boolean mIsAdd = false;

  public SwipePullDownRefresh(@NonNull Context context) {
    super(context);
  }

  public SwipePullDownRefresh(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    setOnRefreshListener(this);// 注册SwipeRefreshLayout的下拉监听事件
    mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    mListView = new ListView(context);
    mListView.setOnScrollListener(this);// ListView的滚动事件的注册
    // inflate一个footerView
    mListViewFooter = LayoutInflater.from(context).inflate(
            R.layout.swipe_pull_down_refresh_footer_view, null, false);
    addView(mListView);
  }

  public void setListViewDivider(Drawable drawable){
    mListView.setDivider(drawable);
  }

  //如果不需要上拉或下拉效果可以通过这个方法设置
  public void setMode(int mode) {
    mCurrentMode = mode;
    if (mode == PULL_UP_TO_REFRESH_MODE || mode == DISABLED_MODE) {// 禁止下拉模式
      setEnabled(false);
    }
  }

  /**
   * 用于判断是否可以上拉。
   */
  private boolean isCanPullUp() {
    return mCurrentMode == PULL_UP_TO_REFRESH_MODE || mCurrentMode ==BOTH_MODE;
  }

  /**
   * set ListView的Adapter
   */
  public void setAdapter(ListAdapter adapter) {
    mListView.setAdapter(adapter);
  }

  /**
   * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
   */
  private void loadDownData() {
    if (mOnRefreshListener != null) {
      // 设置状态
      setLoading(true);
      //
      mOnRefreshListener.onPullUpToRefresh();

    }
  }

  /**
   * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
   *
   * @return
   */
  private boolean isCanLoad() {
    return isBottom() && !mIsLoading && isPullUp();
  }

  /**
   * 根据判断最后一条item是否可见来判断是否已经拉倒底部了。
   *
   * @return
   */
  private boolean isBottom() {
    return mListView.getAdapter() != null
            && mListView.getLastVisiblePosition() == mListView.getAdapter().getCount() - 1;
  }

  /**
   * 是否是上拉操作 用按下是的坐标减去抬起时的坐标 如果这个值大于或等于触发移动事件的最短距离时候则认为是上拉动作，当然这个mTouchSlop也可以相应的增加
   *
   * @return
   */
  private boolean isPullUp() {
    return (mYDown - mYLast) >= mTouchSlop;
  }


  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) {
  }

  /**
   * listView滚动事件
   */
  @Override
  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                       int totalItemCount) {
    if (!isCanPullUp()) {//禁止下拉模式
      return;
    }
    // 滚动时到了最底部加载更多
    if (isCanLoad() && mIsActionUp) {
      loadDownData();
    }
  }

  /**
   * listView触摸拦截事件。
   */
  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    if (!isCanPullUp()) {//禁止下拉模式
      return super.dispatchTouchEvent(ev);
    }
    final int action = ev.getAction();
    switch (action) {
      case MotionEvent.ACTION_DOWN:// 手指按下将之前的手指抬起的坐标初始化并记录按下的坐标
        mIsActionUp = false;
        mYLast = 10000;
        mYDown = (int) ev.getRawY();
        break;
      case MotionEvent.ACTION_MOVE:
        mYLast = (int) ev.getRawY();
        if (isCanLoad() && !mIsAdd) {
          addFooterView();
        }

        break;
      case MotionEvent.ACTION_UP:// 手指抬起时候的坐标
        mIsActionUp = true;
        mYLast = (int) ev.getRawY();
        if (isCanLoad()) {
          loadDownData();
        }
        break;
    }

    return super.dispatchTouchEvent(ev);
  }

  private void addFooterView() {
    mIsAdd = true;
    mListView.addFooterView(mListViewFooter);
  }

  @Override
  public void onRefresh() {
    if (mOnRefreshListener != null) {// 监听到下拉动作时调用接口方法
      mOnRefreshListener.onPullDownToRefresh();
    }
  }

  /**
   * 传入自己自定义的footerview
   */
  public void setFooterView(View footerView) {
    if (footerView != null) {
      mListViewFooter = footerView;
    }
  }

  /**
   * 设置当前状态，如果isLoading是true说明正在加载中，将footerView显示出来，否则移除并将记录的坐标清除。
   */
  public void setLoading(boolean isLoading) {
    mIsLoading = isLoading;
    if (isLoading) {
      if (!mIsAdd) {// 如果没有添加footerview就添加否则不添加
        addFooterView();
      }
      // 避免listview刷新的时候直接滑动到顶部
      mListView.setSelection(mListView.getAdapter().getCount() - 1);
    } else {
      if (mIsAdd) {
        mIsAdd = false;
        mListView.removeFooterView(mListViewFooter);
      }
      mYLast = 0;
      mYDown = 0;
    }
  }

  /**
   * 设置上拉和下拉的监听器
   */
  public void setRefreshListener(IRefreshListener refreshListener) {
    mOnRefreshListener = refreshListener;
  }

  /**
   * 刷新和加载更多的监听器
   */
  public interface IRefreshListener {

    void onPullDownToRefresh();// 下拉刷新

    void onPullUpToRefresh();// 上拉加载

  }

}
