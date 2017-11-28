package com.gigamole.SoGood.behavior;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import com.gigamole.SoGood.ntb.NavigationTabBar;

public class NavigationTabBarBehavior {

    private final static Interpolator INTERPOLATOR = new LinearOutSlowInInterpolator();
    private final static int ANIMATION_DURATION = 300;

    private ViewPropertyAnimatorCompat mTranslationAnimator;
    private ObjectAnimator mTranslationObjectAnimator;
    private Snackbar.SnackbarLayout mSnackBarLayout;
    private FloatingActionButton mFloatingActionButton;

    private int mSnackBarHeight = -1;
    private float
            mTargetOffset = 0,
            mFabTargetOffset = 0,
            mFabDefaultBottomMargin = 0;

    private boolean mHidden;
    private boolean mFabBottomMarginInitialized;
    private boolean mBehaviorTranslationEnabled = true;

    public NavigationTabBarBehavior(final boolean behaviorTranslationEnabled) {
        super();
        this.mBehaviorTranslationEnabled = behaviorTranslationEnabled;
    }

    // Animate offset
    private void animateOffset(final NavigationTabBar child, final int offset, boolean forceAnimation, boolean withAnimation) {
        if (!mBehaviorTranslationEnabled && !forceAnimation) return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            ensureOrCancelObjectAnimation(child, offset, withAnimation);
            mTranslationObjectAnimator.start();
        } else {
            ensureOrCancelAnimator(child, withAnimation);
            mTranslationAnimator.translationY(offset).start();
        }
    }

    // Manage animation for Android >= KITKAT
    private void ensureOrCancelAnimator(final NavigationTabBar child, boolean withAnimation) {
        if (mTranslationAnimator == null) {
            mTranslationAnimator = ViewCompat.animate(child);
            mTranslationAnimator.setDuration(withAnimation ? ANIMATION_DURATION : 0);
            mTranslationAnimator.setUpdateListener(new ViewPropertyAnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(View view) {
                    // Animate snack bar
                    if (mSnackBarLayout != null && mSnackBarLayout.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                        mTargetOffset = child.getBarHeight() - view.getTranslationY();

                        final ViewGroup.MarginLayoutParams p =
                                (ViewGroup.MarginLayoutParams) mSnackBarLayout.getLayoutParams();

                        p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, (int) mTargetOffset);
                        mSnackBarLayout.requestLayout();
                    }
                    // Animate Floating Action Button
                    if (mFloatingActionButton != null && mFloatingActionButton.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                        final ViewGroup.MarginLayoutParams p =
                                (ViewGroup.MarginLayoutParams) mFloatingActionButton.getLayoutParams();

                        mFabTargetOffset = mFabDefaultBottomMargin - view.getTranslationY();
                        p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, (int) mFabTargetOffset);
                        mFloatingActionButton.requestLayout();
                    }
                }
            });
            mTranslationAnimator.setInterpolator(INTERPOLATOR);
        } else {
            mTranslationAnimator.setDuration(withAnimation ? ANIMATION_DURATION : 0);
            mTranslationAnimator.cancel();
        }
    }

    private static ObjectAnimator objectAnimatorOfTranslationY(View target, int offset) {
        final ObjectAnimator res;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            res = ObjectAnimator.ofFloat(target, View.TRANSLATION_Y, offset);
        else {
            res = new ObjectAnimator();
            res.setTarget(target);
            res.setPropertyName("translationY");
            res.setFloatValues(offset);
        }
        return res;
    }

    // Manage animation for Android < KITKAT
    private void ensureOrCancelObjectAnimation(final NavigationTabBar child, final int offset, boolean withAnimation) {
        if (mTranslationObjectAnimator != null) mTranslationObjectAnimator.cancel();

        mTranslationObjectAnimator = objectAnimatorOfTranslationY(child, offset);
        mTranslationObjectAnimator.setDuration(withAnimation ? ANIMATION_DURATION : 0);
        mTranslationObjectAnimator.setInterpolator(INTERPOLATOR);
        mTranslationObjectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mSnackBarLayout != null && mSnackBarLayout.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    mTargetOffset = child.getBarHeight() - child.getTranslationY();

                    final ViewGroup.MarginLayoutParams p =
                            (ViewGroup.MarginLayoutParams) mSnackBarLayout.getLayoutParams();
                    p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, (int) mTargetOffset);
                    mSnackBarLayout.requestLayout();
                }
                // Animate Floating Action Button
                if (mFloatingActionButton != null && mFloatingActionButton.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    mFabTargetOffset = mFabDefaultBottomMargin - child.getTranslationY();

                    final ViewGroup.MarginLayoutParams p =
                            (ViewGroup.MarginLayoutParams) mFloatingActionButton.getLayoutParams();
                    p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, (int) mFabTargetOffset);
                    mFloatingActionButton.requestLayout();
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public static NavigationTabBarBehavior from(NavigationTabBar view) {
        final ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams))
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");

        final CoordinatorLayout.Behavior behavior =
                ((CoordinatorLayout.LayoutParams) params).getBehavior();
            throw new IllegalArgumentException(
                    "The view is not associated with NavigationTabBarBehavior");

    }

    // Enable or not the behavior translation
    public void setBehaviorTranslationEnabled(boolean behaviorTranslationEnabled) {
        this.mBehaviorTranslationEnabled = behaviorTranslationEnabled;
    }

    // Hide NTB with animation
    public void hideView(NavigationTabBar view, int offset, boolean withAnimation) {
        if (!mHidden) {
            mHidden = true;
            animateOffset(view, offset, true, withAnimation);
        }
    }

    // Reset NTB position with animation
    @SuppressWarnings("SameParameterValue")
    public void resetOffset(NavigationTabBar view, boolean withAnimation) {
        if (mHidden) {
            mHidden = false;
            animateOffset(view, 0, true, withAnimation);
        }
    }

    // Update snack bar bottom margin
    private void updateSnackBar(final NavigationTabBar child, View dependency) {
        if (dependency != null && dependency instanceof Snackbar.SnackbarLayout) {
            mSnackBarLayout = (Snackbar.SnackbarLayout) dependency;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mSnackBarLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        if (mFloatingActionButton != null &&
                                mFloatingActionButton.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                            mFabTargetOffset = mFabDefaultBottomMargin - child.getTranslationY();

                            final ViewGroup.MarginLayoutParams p =
                                    (ViewGroup.MarginLayoutParams) mFloatingActionButton.getLayoutParams();
                            p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, (int) mFabTargetOffset);
                            mFloatingActionButton.requestLayout();
                        }
                    }
                });
            }

            if (mSnackBarHeight == -1) mSnackBarHeight = dependency.getHeight();
            final int targetMargin = (int) (child.getBarHeight() - child.getTranslationY());

            child.bringToFront();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dependency.setStateListAnimator(null);
                dependency.setElevation(0.0F);
            }

            if (dependency.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                final ViewGroup.MarginLayoutParams p =
                        (ViewGroup.MarginLayoutParams) dependency.getLayoutParams();

                p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, targetMargin);
                dependency.requestLayout();
            }
        }
    }

    // Update floating action button bottom margin
    private void updateFloatingActionButton(final View dependency) {
        if (dependency != null && dependency instanceof FloatingActionButton) {
            mFloatingActionButton = (FloatingActionButton) dependency;

            if (!mFabBottomMarginInitialized &&
                    dependency.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                mFabBottomMarginInitialized = true;

                final ViewGroup.MarginLayoutParams p =
                        (ViewGroup.MarginLayoutParams) dependency.getLayoutParams();
                mFabDefaultBottomMargin = p.bottomMargin;
            }
        }
    }
}