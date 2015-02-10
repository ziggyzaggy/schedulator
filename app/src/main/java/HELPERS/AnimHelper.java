package HELPERS;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.View;

/**
 * Created by ziggyzaggy on 10/02/2015.
 */
public class AnimHelper {

    int duration, fromColor, toColor;
    View v;

    public AnimHelper(int duration, int fromColor, int toColor, View v) {
        this.duration = duration;
        this.fromColor = fromColor;
        this.toColor = toColor;
        this.v = v;
    }
/*
    public void animteColorFromTo(){
        ValueAnimator anim = ValueAnimator.ofInt(fromColor, toColor);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.setBackgroundColor((int)animation.getAnimatedValue());
            }
        });
        anim.start();
    }*/

    public void changeViewColor() {
        // Load initial and final colors.
        final int initialColor = fromColor;
        final int finalColor = toColor;

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Use animation position to blend colors.
                float position = animation.getAnimatedFraction();
                int blended = blendColors(initialColor, finalColor, position);

                // Apply blended color to the view.
                v.setBackgroundColor(blended);
            }
        });

        anim.setDuration(500).start();
    }

    private int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }

}
