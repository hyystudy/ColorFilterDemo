package com.abclauncher.colorfilterdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final int REPEATE_ANIM = 1;
    private ImageView mBottomImageView, mTopImageView;
    private CircleProgress mCircleProgress;
    private float mTranslateY;
    private int count;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case REPEATE_ANIM:
                    count++;
                    startAnim(false, getResources().getDrawable(R.mipmap.image));
                    break;
            }
            return false;
        }
    });
    private AnimatorSet animatorSet;
    private float mImageSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBottomImageView = (ImageView) findViewById(R.id.bottom_image);
        mTopImageView = (ImageView) findViewById(R.id.top_image);
        mCircleProgress = (CircleProgress) findViewById(R.id.circle_progress);
        mCircleProgress.setReverse(true);

        mTranslateY = getResources().getDimension(R.dimen.image_translateY);
        mImageSize = getResources().getDimension(R.dimen.image_size);
    }


    @Override
    protected void onResume() {
        super.onResume();
      /*  mTopImageView.setImageDrawable(getResources().getDrawable(R.mipmap.image));
        mTopImageView.setVisibility(View.VISIBLE);
        mTopImageView.setScaleX(0.8f);
        mTopImageView.setScaleY(0.8f);*/
        //changeImageViewSaturation(0);
        startAnim(true, getResources().getDrawable(R.mipmap.ic_launcher));
    }

    //start image saturation anim
    private void startAnim(boolean isFirst, final Drawable drawable) {
        if (animatorSet != null && animatorSet.isRunning()){
            animatorSet.end();
            animatorSet = null;
        }

        //scale up to center
        //tips that setAlpha method parameter should be float
        ObjectAnimator step1 = ObjectAnimator.ofPropertyValuesHolder(mBottomImageView,
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0, -mTranslateY),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 1.5f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 1.5f),
                PropertyValuesHolder.ofFloat(View.ALPHA, 0, 1f));
        step1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = mBottomImageView.getAlpha();
                Log.d(TAG, "onAnimationUpdate: " + (mBottomImageView.getVisibility() == View.VISIBLE));
            }
        });
        step1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mBottomImageView.setVisibility(View.GONE);
                mBottomImageView.setScaleX(1);
                mBottomImageView.setScaleY(1);
                mBottomImageView.setAlpha(0f);
                mBottomImageView.setTranslationY(0);

                Drawable drawable = mBottomImageView.getDrawable();
                mTopImageView.setVisibility(View.VISIBLE);
                mTopImageView.setImageDrawable(drawable);
                mTopImageView.setAlpha(1f);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                Log.d(TAG, "onAnimationStart: step1");
                super.onAnimationStart(animation);
                mBottomImageView.setVisibility(View.VISIBLE);
                drawable.setBounds(0, 0, (int)mImageSize, (int)mImageSize);
                mBottomImageView.setImageDrawable(drawable);
                mBottomImageView.setScaleX(1);
                mBottomImageView.setScaleY(1);

                mBottomImageView.setAlpha(0f);
                mBottomImageView.setTranslationY(0);


            }
        });

        step1.setInterpolator(new AccelerateInterpolator());
        step1.setDuration(400);

        //translate up to disappear
        ObjectAnimator step2 = null;
        if (!isFirst){
            step2 = ObjectAnimator.ofPropertyValuesHolder(mTopImageView,
                    PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0, -mTranslateY),
                    PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0));
            step2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mTopImageView.setTranslationY(0);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    Log.d(TAG, "onAnimationStart: step2");
                    mTopImageView.setTranslationY(0);
                    mTopImageView.setAlpha(1f);
                }
            });
            step2.setDuration(400);
            step2.setInterpolator(new AccelerateInterpolator(0.5f));
        }

        Animator step3 = getSaturationAnim();

        //if (animatorSet == null){
            animatorSet = new AnimatorSet();
      //  }

        if (step2 != null){
            animatorSet.play(step1).with(step2).before(step3);
        }else {
            animatorSet.play(step1).before(step3);
            animatorSet.setStartDelay(2000);
        }

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (count < 6){
                    mHandler.sendEmptyMessageDelayed(REPEATE_ANIM, 2000);
                }
               // startAnim(false, getResources().getDrawable(R.mipmap.ic_launcher));

            }
        });
        animatorSet.start();
    }

    private Animator getSaturationAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedFraction = animation.getAnimatedFraction();
                changeImageViewSaturation(1- animatedFraction);
                mCircleProgress.setProgress((int)(100 * animatedFraction));
                mTopImageView.setScaleX(1 - 0.2f * animatedFraction);
                mTopImageView.setScaleY(1 - 0.2f * animatedFraction);
            }
        });

        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        return valueAnimator;
    }

    //change imageView saturation(饱和度)
    private void changeImageViewSaturation(float animatedFraction) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(animatedFraction);
        mTopImageView.clearColorFilter();
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
        mTopImageView.setColorFilter(colorMatrixColorFilter);
    }


}
