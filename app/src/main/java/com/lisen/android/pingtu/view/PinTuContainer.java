package com.lisen.android.pingtu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lisen.android.pingtu.R;
import com.lisen.android.pingtu.bean.BitmapPieces;
import com.lisen.android.pingtu.util.SplitBitmapUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2016/8/10.
 */
public class PinTuContainer extends RelativeLayout implements View.OnClickListener {

    /**
     * 列数及行数
     */
    private int mColumns = 3;

    /**
     * 容器的内边距
     */
    private int mPadding;
    /**
     * 游戏面板长度
     */
    private int mValue;

    /**
     * 图片item间的间距
     */
    private int mMargin = 3;

    /**
     * 图片item数组
     */
    private ImageView[] mImageItems;

    private int mItemWidth;

    private boolean mOnce = false;
    private List<BitmapPieces> mPiecesList;

    private final static int NEXT_LEVEL = 0x110;
    private final static int TIME_CHANGED = 0x111;
    private int mLevel = 1;

    /**
     * 设置是否开启计时
     *
     * @param mTimeEnable
     */
    public void setmTimeEnable(boolean mTimeEnable) {
        this.mTimeEnable = mTimeEnable;
    }

    public int getLevel() {
        return mLevel;
    }
    private boolean mTimeEnable = false;
    private int mTime;

    private boolean mGameSuccess = false;
    private boolean mGameOver = false;
    private boolean mPause = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NEXT_LEVEL:
                    mLevel++;
                    if (mListener != null) {
                        mListener.next();
                    } else {
                        nextLevel();
                    }
                    break;
                case TIME_CHANGED:
                    if (mGameSuccess || mGameOver || mPause) {
                        return;
                    }
                    if (mListener != null) {
                        mListener.timeChanged(mTime);
                    }
                    if (mTime == 0) {
                        mGameOver = true;
                        if (mListener != null) {
                            mListener.gameOver();

                        }
                        return;
                    }
                    mTime--;
                    mHandler.sendEmptyMessageDelayed(TIME_CHANGED, 1000);
                    break;

                default:
                    break;
            }
        }
    };
    private PinTuListener mListener;

    public void setPinTuListener(PinTuListener l) {
        mListener = l;
    }

    /**
     * 接口
     */
    public interface PinTuListener {
        void next();

        void gameOver();

        void timeChanged(int currentTime);
    }

    /**
     * 重新开始
     */
    public void restart(){
        mGameOver = false;
        mColumns--;
        nextLevel();
    }

    /**
     * 进行下一关
     */
    public void nextLevel() {
        this.removeAllViews();
        mAnimiContainer = null;
        mColumns++;
        mGameSuccess = false;
        checkTimeEnable();
        initBitmapList();
        initImageItem();
    }

    /**
     * 暂停
     */
    public void pause() {
        mPause = true;
        mHandler.removeMessages(TIME_CHANGED);
    }

    /**
     * 恢复
     */
    public void resume() {
        if (mPause) {
            mPause = false;
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }

    }
    public PinTuContainer(Context context) {
        this(context, null);
    }

    public PinTuContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinTuContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mMargin, getResources().getDisplayMetrics());
        mPadding = min(getPaddingLeft(), getPaddingBottom(), getPaddingRight(), getPaddingTop());

    }

    /**
     * 返回最小的padding
     *
     * @param params
     * @return
     */
    private int min(int... params) {
        int padding = params[0];
        for (int i = 1; i < params.length; i++) {
            if (padding > params[i]) {
                padding = params[i];
            }
        }
        return padding;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mValue = Math.min(getMeasuredHeight(), getMeasuredWidth());

        if (!mOnce) {
            initBitmapList();
            initImageItem();
            //判断是否开启时间
            checkTimeEnable();
            mOnce = true;
        }

        setMeasuredDimension(mValue, mValue);
    }

    /**
     * 判断是否开启计时限制
     */
    private void checkTimeEnable() {
        if (mTimeEnable) {
            countdownTime();
        }
    }

    public void cancelTimeRestriction() {
        if (!mTimeEnable) {
            mHandler.removeMessages(TIME_CHANGED);
        }
    }

    /**
     * 倒计时
     */
    private void countdownTime() {
        //根据当前等级设置倒计时时长
        mTime = (int) (Math.pow(2, mLevel) * 60);
        //开启倒计时
        mHandler.sendEmptyMessage(TIME_CHANGED);
    }

    /**
     * 得到切割后的小图集合
     */
    private void initBitmapList() {


        Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);
        mPiecesList = SplitBitmapUtil.splitBitmap(sourceBitmap, mColumns);
        //乱序
        Collections.sort(mPiecesList, new Comparator<BitmapPieces>() {
            @Override
            public int compare(BitmapPieces lhs, BitmapPieces rhs) {
                return Math.random() > 0.5 ? 1 : -1;
            }
        });

    }

    /**
     * 将小图设置到imageView上
     */
    private void initImageItem() {

        mImageItems = new ImageView[mColumns * mColumns];
        mItemWidth = (mValue - 2 * mPadding - (mColumns - 1) * mMargin) / mColumns;
        for (int i = 0; i < mImageItems.length; i++) {
            mImageItems[i] = new ImageView(getContext());
            mImageItems[i].setOnClickListener(this);
            mImageItems[i].setId(i + 1);
            mImageItems[i].setTag(i + "_" + mPiecesList.get(i).getIndex());
            mImageItems[i].setImageBitmap(mPiecesList.get(i).getBitmap());
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(mItemWidth, mItemWidth);
            //不是最后一列，添加右margin
            if ((i + 1) % mColumns != 0) {
                p.rightMargin = mMargin;
            }

            //不是第一列，设置左边图片为上一张
            if (i % mColumns != 0) {
                p.addRule(RelativeLayout.RIGHT_OF, mImageItems[i - 1].getId());
            }

            //不是第一行，添加上margin
            if (i + 1 > mColumns) {
                p.topMargin = mMargin;
                p.addRule(RelativeLayout.BELOW, mImageItems[i - mColumns].getId());
            }

            addView(mImageItems[i], p);
        }
    }

    private ImageView mFirstClickView;
    private ImageView mSecondClickView;

    @Override
    public void onClick(View v) {
        if (mIsAnimiing) {
            return;
        }
        //点击的是同一个imageView,不动作
        if (mFirstClickView == v) {
            //取消高亮
            mFirstClickView.setColorFilter(null);
            mFirstClickView = null;
            return;
        }

        if (mFirstClickView == null) {
            mFirstClickView = (ImageView) v;
            //高亮被点击中的view
            mFirstClickView.setColorFilter(Color.parseColor("#55ff0000"));
        } else {
            mSecondClickView = (ImageView) v;
            exchangeView();
        }
    }

    private RelativeLayout mAnimiContainer;

    /**
     * 在动画过程总不响应用户的点击
     */
    private boolean mIsAnimiing;

    /**
     * 交换图片
     */
    private void exchangeView() {
        mFirstClickView.setColorFilter(null);
        //构建动画层
        setupAnimiContainer();
        //进行交换
        final String firstTag = (String) mFirstClickView.getTag();
        final String secondTag = (String) mSecondClickView.getTag();
        String[] firstSplit = firstTag.split("_");
        String[] secondSplit = secondTag.split("_");
        final Bitmap firstBitmap = mPiecesList.get(Integer.valueOf(firstSplit[0])).getBitmap();
        final Bitmap secondBitmap = mPiecesList.get(Integer.valueOf(secondSplit[0])).getBitmap();


        //动画层中的元素,其元素的位置与被点击的item的位置一模一样
        ImageView firstImageView = new ImageView(getContext());
        firstImageView.setImageBitmap(firstBitmap);
        RelativeLayout.LayoutParams lpFirst = new RelativeLayout.LayoutParams(mItemWidth, mItemWidth);
        lpFirst.leftMargin = mFirstClickView.getLeft() - mPadding;
        lpFirst.topMargin = mFirstClickView.getTop() - mPadding;
        firstImageView.setLayoutParams(lpFirst);
        mAnimiContainer.addView(firstImageView);

        ImageView secondImageView = new ImageView(getContext());
        secondImageView.setImageBitmap(secondBitmap);
        RelativeLayout.LayoutParams lpSecond = new RelativeLayout.LayoutParams(mItemWidth, mItemWidth);
        lpSecond.leftMargin = mSecondClickView.getLeft() - mPadding;
        lpSecond.topMargin = mSecondClickView.getTop() - mPadding;
        secondImageView.setLayoutParams(lpSecond);
        mAnimiContainer.addView(secondImageView);

        //构建动画
        TranslateAnimation firstAnimation = new TranslateAnimation(0, mSecondClickView.getLeft() - mFirstClickView.getLeft(),
                0, mSecondClickView.getTop() - mFirstClickView.getTop());
        firstAnimation.setDuration(300);
        firstAnimation.setFillAfter(true);
        firstImageView.setAnimation(firstAnimation);

        TranslateAnimation secondAnimation = new TranslateAnimation(0, mFirstClickView.getLeft() - mSecondClickView.getLeft(),
                0, mFirstClickView.getTop() - mSecondClickView.getTop());
        secondAnimation.setDuration(300);
        secondAnimation.setFillAfter(true);
        secondImageView.setAnimation(secondAnimation);
        //动画监听
        firstAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFirstClickView.setVisibility(INVISIBLE);
                mSecondClickView.setVisibility(INVISIBLE);
                mIsAnimiing = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFirstClickView.setImageBitmap(secondBitmap);
                mSecondClickView.setImageBitmap(firstBitmap);
                //重新设置tag,避免图片错乱
                mFirstClickView.setTag(secondTag);
                mSecondClickView.setTag(firstTag);

                mFirstClickView.setVisibility(VISIBLE);
                mSecondClickView.setVisibility(VISIBLE);

                mAnimiContainer.removeAllViews();

                mFirstClickView = mSecondClickView = null;
                mIsAnimiing = false;

                checkSuccess();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    /**
     * 判断游戏是否过关
     */
    private void checkSuccess() {
        boolean success = true;
        for (int i = 0; i < mImageItems.length; i++) {
            if (getImageItemIndexByTag((String) mImageItems[i].getTag()) != i) {
                success = false;
                break;
            }
            success = true;
        }

        if (success) {
            mGameSuccess = true;
            Toast.makeText(getContext(), "成功过关", Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessage(NEXT_LEVEL);
        }
    }

    /**
     * 根据tag找到index
     *
     * @param tag
     * @return
     */
    private int getImageItemIndexByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[1]);
    }

    /**
     * 设置容器
     */
    private void setupAnimiContainer() {
        if (mAnimiContainer == null) {
            mAnimiContainer = new RelativeLayout(getContext());
            addView(mAnimiContainer);
        }
    }


}
