package paeqw.app.helpers;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class AnimationUtils {

    public static void expandView(final View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        final int targetHeight = view.getMeasuredHeight();

        view.getLayoutParams().height = 1;
        view.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, targetHeight);
        valueAnimator.addUpdateListener(animation -> {
            view.getLayoutParams().height = (int) animation.getAnimatedValue();
            view.requestLayout();
        });
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(300);
        valueAnimator.start();
    }

    public static void collapseView(final View view) {
        final int initialHeight = view.getMeasuredHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialHeight, 1);
        valueAnimator.addUpdateListener(animation -> {
            view.getLayoutParams().height = (int) animation.getAnimatedValue();
            view.requestLayout();
            if ((int) animation.getAnimatedValue() == 1) {
                view.setVisibility(View.GONE);
            }
        });
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(300);
        valueAnimator.start();
    }

}
