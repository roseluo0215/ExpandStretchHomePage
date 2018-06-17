package com.lc.expand.stretch;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private AppBarLayout mAppBarLayout;
  private LinearLayout mHomePage;
  private Toolbar mToolbar;
  private MainToolbarHeadLayout mHeadLayout;
  private InterceptNestedScrollView mNestedScrollView;
  private boolean mIsExpanded = true;
  private FrameLayout mHomeBg;
  private TextView mCardRecharge1;
  private TextView mCardRecharge2;
  private TextView mCardRecharge3;
  private TextView mCardRecharge4;
  private SwipePullDownRefresh mNewsPageList;
  private NewsListAdapter mAdapter;
  private List<ListData> mData;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mAppBarLayout = findViewById(R.id.app_barLayout);
    mHomePage = findViewById(R.id.include_home_page);
    mToolbar = findViewById(R.id.tb_tool_bar);
    mHeadLayout = findViewById(R.id.include_toolbar_head);
    mNestedScrollView = findViewById(R.id.ns_view);
    mHomeBg = findViewById(R.id.rl_home_bg);
    mCardRecharge1 = findViewById(R.id.tv_card_recharge1);
    mCardRecharge2 = findViewById(R.id.tv_card_recharge2);
    mCardRecharge3 = findViewById(R.id.tv_card_recharge3);
    mCardRecharge4 = findViewById(R.id.tv_card_recharge4);
    mNewsPageList = findViewById(R.id.news_page_list);
    mAdapter = new NewsListAdapter(this);
    setListener();
    ArrayList<ListData> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      ListData data = new ListData();
      data.setTitle("数据" + i);
      list.add(data);
    }
    mData = list;
    mAdapter.setData(mData);
    mNewsPageList.setAdapter(mAdapter);

  }


  private void setListener() {
    mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      @Override
      public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0) {
          mIsExpanded = true;

          // 完全展开
          mHeadLayout.setVisibility(View.GONE);
          mToolbar.setVisibility(View.GONE);
          setHomePageViewAlpha(255);
          mHeadLayout.getBackground().setAlpha(255);

        } else if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
          mIsExpanded = false;
          // 完全折叠
          mHeadLayout.setVisibility(View.VISIBLE);
          mToolbar.setVisibility(View.VISIBLE);
          setHomePageViewAlpha(255);
          mHeadLayout.getBackground().setAlpha(255);
        } else {
          mIsExpanded = true;
          if (mHomeBg.getVisibility() == View.VISIBLE) {// 完全展开
            int total = Math.abs(appBarLayout.getTotalScrollRange());
            int rate = total / 255;// 获取比例，0 － 255代表透明度
            int temp = (total - Math.abs(verticalOffset)) / rate;
            int alpha = temp < 255 ? temp : 255;
            setHomePageViewAlpha(alpha);// 拖动时让界面的透明度发生变化
            mHeadLayout.setVisibility(View.VISIBLE);
            mToolbar.setVisibility(View.VISIBLE);
            mHeadLayout.getBackground().setAlpha(255 - alpha);

          }
        }
        mNestedScrollView.setIsExpanded(mIsExpanded);
      }
    });

    mHeadLayout.getBackHomePage().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mAppBarLayout.setExpanded(true);// 向下展开
      }
    });


    mNewsPageList.setRefreshListener(new SwipePullDownRefresh.IRefreshListener() {
      @Override
      public void onPullDownToRefresh() {
        mNewsPageList.setRefreshing(false);
      }

      @Override
      public void onPullUpToRefresh() {
        final ArrayList<ListData> listData = new ArrayList<>();
        for (int i = 10; i < 20; i++) {
          ListData data = new ListData();

          data.setTitle("加载更多" + i);
          listData.add(data);
        }
        mData.addAll(listData);
        new Handler().postDelayed(new Runnable() {// 模拟网络请求延迟两秒
          @Override
          public void run() {
            mAdapter.setData(mData);
            mAdapter.notifyDataSetChanged();

            mNewsPageList.setLoading(false);
          }
        }, 2000);
      }
    });
  }

  private void setHomePageViewAlpha(int alpha) {
    mHomeBg.getBackground().setAlpha(alpha);
    float i = alpha / 255f;
    mCardRecharge1.setAlpha(i);
    mCardRecharge2.setAlpha(i);
    mCardRecharge3.setAlpha(i);
    mCardRecharge4.setAlpha(i);

  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (event.getAction() == KeyEvent.ACTION_DOWN) {
      if (keyCode == KeyEvent.KEYCODE_BACK) { // 表示按返回键 时的操作
        // 监听到返回按钮点击事件
        // 后退
        if (mIsExpanded) {// 如果是展开的就关闭界面
          finish();
        } else {// 如果不是展开的就打开
          mAppBarLayout.setExpanded(true);
        }
        return true; // 已处理
      }
    }
    return super.onKeyDown(keyCode, event);
  }
}
