package knaps.hacker.school.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by lisaneigut on 15 Sep 2013.
 */
public class DmSiloTextView extends TextView {

    private static final int NEW_RELIC_MEDIUM = 4;

    private int mStyleType;

    public DmSiloTextView(Context context) {
        super(context);
    }

    public DmSiloTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DmSiloTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        if (!isInEditMode()) {
            super.setTypeface(TypefaceCache.getInstance().getTypeface(getContext(), "dm-silo.ttf"));
        }
    }
}

