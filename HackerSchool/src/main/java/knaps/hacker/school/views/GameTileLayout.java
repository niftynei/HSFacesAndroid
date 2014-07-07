package knaps.hacker.school.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import knaps.hacker.school.R;

/**
 * Created by lisaneigut on 16 Mar 2014.
 */
public class GameTileLayout extends FrameLayout {

    private static final int ANIMATION_DURATION = 400;
    private ImageView mImageView;
    private ViewGroup mResultView;
    private TextView mMessageText;
    private ImageView mResultStatusView;

    public GameTileLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setupViews();
    }

    public GameTileLayout(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        setupViews();
    }

    public GameTileLayout(final Context context) {
        super(context);
        setupViews();
    }

    private void setupViews() {
        View baseView = LayoutInflater.from(getContext()).inflate(R.layout.game_tile, null);
        mImageView = (ImageView) baseView.findViewById(R.id.hs_image);
        mResultView = (ViewGroup) baseView.findViewById(R.id.layout_result);
        mMessageText = (TextView) mResultView.findViewById(R.id.text_result);
        mResultStatusView = (ImageView) mResultView.findViewById(R.id.image_result);
        addView(baseView);
    }

    public void showSuccess(String successMessage, final GameTileCallback callback) {
        mResultView.setBackgroundColor(getResources().getColor(R.color.success_color));
        mMessageText.setText(successMessage);
        mResultStatusView.setImageResource(R.drawable.ic_email);
        showMessage(callback);
    }

    public void showFail(String failMessage, final GameTileCallback callback) {
        mResultView.setBackgroundColor(getResources().getColor(R.color.fail_color));
        mMessageText.setText(failMessage);
        mResultStatusView.setImageResource(R.drawable.ic_twitter);
        showMessage(callback);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void showMessage(final GameTileCallback callback) {
        ValueAnimator animator = ObjectAnimator.ofFloat(mResultView, "translationY", -mResultView.getHeight(), 0);
        animator.setDuration(ANIMATION_DURATION);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            public int mLayerType;

            @Override
            public void onAnimationStart(final Animator animation) {
                mLayerType = GameTileLayout.this.getLayerType();
                GameTileLayout.this.setLayerType(LAYER_TYPE_HARDWARE, null);
                mResultView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                GameTileLayout.this.setLayerType(mLayerType, null);
                if (callback != null) callback.onAnimationEnd();
            }

            @Override
            public void onAnimationCancel(final Animator animation) {
            }

            @Override
            public void onAnimationRepeat(final Animator animation) {

            }
        });
        animator.start();
    }

    public void showImage() {
        mResultView.setVisibility(GONE);
    }

    public void clearHSPicture() {
        mImageView.setImageBitmap(null);
    }

    public void setHSPicture(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
    }

    public void setHSPicture(Drawable drawable) {
        mImageView.setImageDrawable(drawable);
    }

    public void setHSPicture(int resId) {
        mImageView.setImageResource(resId);
    }

}
