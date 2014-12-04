package movil.palermo.com.py.controlstockregreso.custom;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by cromero on 04/12/2014.
 */
public class ResizeAnimation extends Animation {
    private View mView;
    private float mToHeight;
    private float mFromHeight;



    public ResizeAnimation(View v,  float fromHeight,  float toHeight) {
        mToHeight = toHeight;

        mFromHeight = fromHeight;

        mView = v;
        setDuration(1000);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float height =
                (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
        ViewGroup.LayoutParams p = mView.getLayoutParams();
        p.height = (int) height;
        mView.requestLayout();
    }
}
