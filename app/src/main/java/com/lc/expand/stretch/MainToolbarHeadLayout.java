package com.lc.expand.stretch;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;


/**
 * Created by luocheng on 16/7/26.
 */
public class MainToolbarHeadLayout extends RelativeLayout {

  private ImageView mScan;
  private ImageView mRealTimeBus;
  private ImageView mCardRecharge;
  private ImageView mContactCustomer;
  private ImageView mBackHomePage;

  public MainToolbarHeadLayout(Context context) {
    super(context);
  }

  public MainToolbarHeadLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    mScan = findViewById(R.id.iv_scan);
    mCardRecharge = findViewById(R.id.iv_card_recharge);
    mRealTimeBus = findViewById(R.id.iv_real_time_bus);
    mContactCustomer = findViewById(R.id.iv_contact_customer);
    mBackHomePage = findViewById(R.id.iv_back_home_page);

  }

  public ImageView getScan() {
    return mScan;
  }

  public ImageView getRealTimeBus() {
    return mRealTimeBus;
  }

  public ImageView getCardRecharge() {
    return mCardRecharge;
  }

  public ImageView getContactCustomer() {
    return mContactCustomer;
  }

  public ImageView getBackHomePage() {
    return mBackHomePage;
  }
}
