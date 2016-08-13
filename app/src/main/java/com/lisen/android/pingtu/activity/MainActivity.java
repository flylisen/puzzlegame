package com.lisen.android.pingtu.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lisen.android.pingtu.R;
import com.lisen.android.pingtu.view.PinTuContainer;

public class MainActivity extends AppCompatActivity {

    private PinTuContainer mPinTuView;
    private TextView mTVLevel;
    private TextView mTVTime;
    private boolean mCancelTiemRestriction = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPinTuView = (PinTuContainer) findViewById(R.id.pintu_main_activty);
        mTVLevel = (TextView) findViewById(R.id.tv_level_main_activity);
        mTVTime = (TextView) findViewById(R.id.tv_time_main_activity);
        mTVLevel.setText("1");
        mTVTime.setText("120");
        //开启倒计时限制
        mPinTuView.setmTimeEnable(true);
        mPinTuView.setPinTuListener(new PinTuContainer.PinTuListener() {
            @Override
            public void next() {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("成功过关！！")
                        .setPositiveButton("下一关", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPinTuView.nextLevel();
                                mTVLevel.setText(mPinTuView.getLevel() + "");

                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }

            @Override
            public void gameOver() {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("游戏结束！！")
                        .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setPositiveButton("重来", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPinTuView.restart();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }

            @Override
            public void timeChanged(int currentTime) {
                mTVTime.setText(currentTime + "");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPinTuView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPinTuView.resume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel_restriction_main_activity:
                if (!mCancelTiemRestriction) {
                    //关闭时间限制
                    mPinTuView.setmTimeEnable(false);
                    mPinTuView.cancelTimeRestriction();
                    mTVTime.setVisibility(View.INVISIBLE);
                    item.setTitle("开启时间限制");
                } else {
                    //开启时间限制
                    mPinTuView.setmTimeEnable(true);
                    mPinTuView.restart();
                    mTVTime.setVisibility(View.VISIBLE);
                    item.setTitle("关闭时间限制");
                }

                mCancelTiemRestriction = !mCancelTiemRestriction;
                break;
            default:
                break;
        }
        return true;
    }
}
