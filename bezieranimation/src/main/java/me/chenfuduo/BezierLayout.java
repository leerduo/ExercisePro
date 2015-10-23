package me.chenfuduo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Created by chenfuduo on 2015/10/23.
 */
public class BezierLayout extends RelativeLayout {

    Drawable img1;
    Drawable img2;
    Drawable img3;
    Drawable img4;

    Drawable[] imgs;

    //图片的宽高
    int height;
    int width;

    //容器的宽高
    int measuredWidth;
    int measuredHeight;
    private RelativeLayout.LayoutParams params;

    private Random random = new Random();//0~1

    //估值器
    private Interpolator line = new LinearInterpolator();
    private Interpolator acc = new AccelerateInterpolator();
    private Interpolator dec = new DecelerateInterpolator();
    private Interpolator accdec = new AccelerateDecelerateInterpolator();
    private Interpolator os = new OvershootInterpolator();
    private Interpolator[] interpolators;


    public BezierLayout(Context context) {
        this(context, null);
    }

    public BezierLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        interpolators = new Interpolator[5];
        interpolators[0] = line;
        interpolators[1] = acc;
        interpolators[2] = dec;
        interpolators[3] = accdec;
        interpolators[4] = os;
        imgs = new Drawable[4];
        img1 = getResources().getDrawable(R.mipmap.ic_photo_liked);
        img2 = getResources().getDrawable(R.mipmap.ic_photo_liked);
        img3 = getResources().getDrawable(R.mipmap.ic_photo_liked);
        img4 = getResources().getDrawable(R.mipmap.ic_photo_liked);
        imgs[0] = img1;
        imgs[1] = img2;
        imgs[2] = img3;
        imgs[3] = img4;
        //得到图片的宽高
        height = img1.getIntrinsicHeight();
        width = img1.getIntrinsicWidth();

        params = new RelativeLayout.LayoutParams(width, height);
        //父容器水平居中
        params.addRule(CENTER_HORIZONTAL, TRUE);
        //相对父容器底部
        params.addRule(ALIGN_PARENT_BOTTOM, TRUE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //得到容器的宽高
        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();
    }

    public void addNewImgs() {
        final ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(imgs[random.nextInt(4)]);
        imageView.setLayoutParams(params);
        addView(imageView);
        //属性动画控制坐标
        AnimatorSet set = getAnimator(imageView);
        //开启动画集合
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //结束后,将ImageView移除
                removeView(imageView);
            }
        });
        set.start();
    }

    /**
     * 构造三个动画
     *
     * @param imageView
     * @return
     */
    private AnimatorSet getAnimator(ImageView imageView) {
        //透明度动画
        ObjectAnimator alpha = ObjectAnimator.ofFloat(imageView, "alpha", 0.3f, 1f);
        //缩放动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", 0.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", 0.2f, 1f);
        AnimatorSet enter = new AnimatorSet();
        enter.setDuration(500);
        //三个动画同时执行
        enter.playTogether(alpha, scaleX, scaleY);
        enter.setTarget(imageView);
        //贝塞尔曲线动画(核心是不断修改ImageView的坐标)
        ValueAnimator bezierValueAnimator = getBezierValueAnimator(imageView);
        AnimatorSet bezierSet = new AnimatorSet();
        //先执行与后执行,按顺序执行
        bezierSet.playSequentially(enter, bezierValueAnimator);

        bezierSet.setInterpolator(interpolators[random.nextInt(5)]);

        bezierSet.setTarget(imageView);
        return bezierSet;
    }

    /**
     * 构造一个贝塞尔曲线动画
     *
     * @param imageView
     * @return
     */
    private ValueAnimator getBezierValueAnimator(final ImageView imageView) {
        PointF pointF2 = getPointF(2);
        PointF pointF1 = getPointF(1);
        PointF pointF0 = new PointF((measuredWidth - width) / 2, measuredHeight - height);
        PointF pointF3 = new PointF(random.nextInt(measuredWidth),0);

        //通过插值器来控制View的运动路径
        BezierEvaluator evaluator = new BezierEvaluator(pointF1,pointF2);
        ValueAnimator animator = ValueAnimator.ofObject(evaluator,pointF0,pointF3);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                imageView.setX(pointF.x);
                imageView.setY(pointF.y);
                imageView.setAlpha(1-animation.getAnimatedFraction());
            }
        });
        animator.setTarget(imageView);
        animator.setDuration(2500);
        return animator;
    }

    private PointF getPointF(int i) {
        PointF pointF = new PointF();
        pointF.x = random.nextInt(measuredWidth);
        if (i == 2) {
            pointF.y = random.nextInt(measuredHeight / 2);
        } else {
            pointF.y = random.nextInt(measuredHeight / 2) + measuredHeight / 2;
        }
        return pointF;
    }

}
