package com.xk.freedomview.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.xk.freedomview.R;


/**
 * 小圆点
 *
 * @author xuekai1
 * @date 2018/8/24
 */
public class SuspensionView extends android.support.v7.widget.AppCompatImageView {
    private Listener listener;
    /**
     * 上次ontouchevent的x值
     */
    float lastX = 0;
    /**
     * 上次ontouchevent的y值
     */
    float lastY = 0;

    /**
     * 上次手指抬起的时间，用来处理按钮双击的判断
     */
    long lastUp = 0;

    public SuspensionView(Context context) {
        this(context, null);
    }

    public SuspensionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public SuspensionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setImageResource(R.drawable.point);
    }

    //双击之后，这个置为true，使该view消费事件，却不做任何处理。 直到手指抬起。达到双击之后不触发其他效果的目的。
    boolean isDoubleClick = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isDoubleClick) {
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                isDoubleClick = false;
            }
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (System.currentTimeMillis() - lastUp < 100) {
                    if (listener != null) {
                        listener.doubleClick();
                        isDoubleClick = true;
                    }
                    return true;
                }
                //lastup使用完毕，置零
                lastUp = 0;
                if (listener != null) {
                    listener.onDown();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getRawX() - lastX;
                float dy = event.getRawY() - lastY;
                if (listener != null) {
                    listener.onMovie(dx, dy);
                }
                setX(getX() + dx);
                setY(getY() + dy);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDoubleClick = false;
                lastUp = System.currentTimeMillis();
                if (listener != null) {
                    listener.onUpOrCancel();
                }
                break;
            default:
                break;
        }
        lastX = event.getRawX();
        lastY = event.getRawY();
        return true;
    }


    public void setListener(Listener listener) {
        this.listener = listener;
    }


    public interface Listener {
        /**
         * 按下回调
         */
        void onDown();

        /**
         * 手指抬起或cancel
         */
        void onUpOrCancel();

        /**
         * 双击回调
         */
        void doubleClick();

        /**
         * 移动回调
         *
         * @param dx 增量
         * @param dy 增量
         */
        void onMovie(float dx, float dy);

    }
}
