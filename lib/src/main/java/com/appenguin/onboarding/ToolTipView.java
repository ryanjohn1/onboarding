/*
 * Copyright 2013 Niek Haarman
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appenguin.onboarding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A ViewGroup to visualize ToolTips. Use
 * ToolTipRelativeLayout.showToolTipForView() to show ToolTips.
 */
public class ToolTipView extends LinearLayout implements ViewTreeObserver.OnPreDrawListener, View.OnClickListener {

    public static final String TRANSLATION_Y_COMPAT = "translationY";
    public static final String TRANSLATION_X_COMPAT = "translationX";
    public static final String SCALE_X_COMPAT = "scaleX";
    public static final String SCALE_Y_COMPAT = "scaleY";
    public static final String ALPHA_COMPAT = "alpha";
    public static final int POINTER_MARGIN_OFFSET = 20;

    private ImageView topPointerView;
    private View topFrame;
    private ViewGroup contentHolder;
    private TextView toolTipTV;
    private View bottomFrame;
    private ImageView bottomPointerView;
    private View shadowView;

    private boolean initialised = false;

    private OnboardingTracker tracker;
    private ToolTip toolTip;
    private View view;

    private boolean dimensionsKnown;
    private int relativeMasterViewY;

    private int relativeMasterViewX;
    private int width;
    private int verticalOffsetDp;
    private int screenMarginDp;

    private OnToolTipViewClickedListener listener;

    public ToolTipView(final Context context) {
        super(context);
        initialiseView();
    }

    public ToolTipView(final Context context, OnboardingTracker tracker) {
        super(context);
        this.tracker = tracker;
        if (tracker.shouldShow()) {
            initialiseView();
        }
    }

    private void initialiseView() {
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.tooltip, this, true);

        topPointerView = (ImageView) findViewById(R.id.tooltip_pointer_up);
        topFrame = findViewById(R.id.tooltip_topframe);
        contentHolder = (ViewGroup) findViewById(R.id.tooltip_contentholder);
        toolTipTV = (TextView) findViewById(R.id.tooltip_contenttv);
        bottomFrame = findViewById(R.id.tooltip_bottomframe);
        bottomPointerView = (ImageView) findViewById(R.id.tooltip_pointer_down);
        shadowView = findViewById(R.id.tooltip_shadow);

        setOnClickListener(this);
        getViewTreeObserver().addOnPreDrawListener(this);
        initialised = true;
    }

    public boolean shouldShow() {
        return (tracker == null || tracker.shouldShow());
    }

    @Override
    public boolean onPreDraw() {
        getViewTreeObserver().removeOnPreDrawListener(this);
        dimensionsKnown = true;

        width = contentHolder.getWidth();

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        layoutParams.width = width;
        setLayoutParams(layoutParams);

        if (toolTip != null) {
            applyToolTipPosition(toolTip.getPosition());
        }
        return true;
    }

    public void setToolTip(final ToolTip toolTip, final View view) {
        this.toolTip = toolTip;
        this.view = view;
        if (initialised) {
            initialiseToolTip();
        }
    }

    private void initialiseToolTip() {
        if (toolTip.getText() != null) {
            toolTipTV.setText(toolTip.getText());
        } else if (toolTip.getTextResId() != 0) {
            toolTipTV.setText(toolTip.getTextResId());
        }

        if (toolTip.getTypeface() != null) {
            toolTipTV.setTypeface(toolTip.getTypeface());
        }

        if (toolTip.getTextColor() != 0) {
            toolTipTV.setTextColor(toolTip.getTextColor());
        }

        if (toolTip.getColor() != 0) {
            setColor(toolTip.getColor());
        }

        if (toolTip.getContentView() != null) {
            setContentView(toolTip.getContentView());
        }

        if (!toolTip.shouldShowShadow()) {
            shadowView.setVisibility(View.GONE);
        }

        verticalOffsetDp = toolTip.getVerticalOffset();
        screenMarginDp = toolTip.getScreenMargin();

        if (dimensionsKnown) {
            applyToolTipPosition(toolTip.getPosition());
        }
    }

    private void applyToolTipPosition(ToolTip.Position position) {
        final int offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, verticalOffsetDp, getResources().getDisplayMetrics());
        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, screenMarginDp, getResources().getDisplayMetrics());
        final int pointerMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, screenMarginDp + POINTER_MARGIN_OFFSET, getResources().getDisplayMetrics());
        final int[] masterViewScreenPosition = new int[2];
        view.getLocationOnScreen(masterViewScreenPosition);

        final Rect viewDisplayFrame = new Rect();
        view.getWindowVisibleDisplayFrame(viewDisplayFrame);

        final int[] parentViewScreenPosition = new int[2];
        ((View) getParent()).getLocationOnScreen(parentViewScreenPosition);

        final int masterViewWidth = view.getWidth();
        final int masterViewHeight = view.getHeight();

        relativeMasterViewX = masterViewScreenPosition[0] - parentViewScreenPosition[0];
        relativeMasterViewY = masterViewScreenPosition[1] - parentViewScreenPosition[1];
        final int relativeMasterViewCenterX = determineCenterXFromDesiredPosition(position, masterViewWidth);

        int toolTipViewAboveY = relativeMasterViewY - getHeight() - offset;//
        int toolTipViewBelowY = Math.max(0, relativeMasterViewY + masterViewHeight + offset);

        int toolTipViewX = Math.max(margin, relativeMasterViewCenterX - width / 2);
        if (toolTipViewX + width + margin > viewDisplayFrame.right) {
            toolTipViewX = viewDisplayFrame.right - width - margin;
        }

        int pointerCenterX = Math.max(pointerMargin, relativeMasterViewCenterX);
        if (pointerCenterX + pointerMargin >= viewDisplayFrame.right) {
            pointerCenterX = pointerCenterX - pointerMargin;
        }

        setX(toolTipViewX);
        setPointerCenterX(pointerCenterX);

        final boolean showBelow = toolTip.getShowBelow() || toolTipViewAboveY < 0;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            ViewHelper.setAlpha(topPointerView, showBelow ? 1 : 0);
            ViewHelper.setAlpha(bottomPointerView, showBelow ? 0 : 1);
        } else {
            topPointerView.setVisibility(showBelow ? VISIBLE : GONE);
            bottomPointerView.setVisibility(showBelow ? GONE : VISIBLE);
        }

        int toolTipViewY;
        if (showBelow) {
            toolTipViewY = toolTipViewBelowY;
        } else {
            toolTipViewY = toolTipViewAboveY;
        }

        if (toolTip.getAnimationType() == ToolTip.AnimationType.NONE) {
            ViewHelper.setTranslationY(this, toolTipViewY);
            ViewHelper.setTranslationX(this, toolTipViewX);
        } else {
            Collection<Animator> animators = new ArrayList<>(5);

            if (toolTip.getAnimationType() == ToolTip.AnimationType.FROM_MASTER_VIEW) {
                animators.add(ObjectAnimator.ofFloat(this, TRANSLATION_Y_COMPAT, relativeMasterViewY + view.getHeight() / 2 - getHeight() / 2, toolTipViewY));
                animators.add(ObjectAnimator.ofFloat(this, TRANSLATION_X_COMPAT, relativeMasterViewX + view.getWidth() / 2 - width / 2, toolTipViewX));
            } else if (toolTip.getAnimationType() == ToolTip.AnimationType.FROM_TOP) {
                animators.add(ObjectAnimator.ofFloat(this, TRANSLATION_Y_COMPAT, 0, toolTipViewY));
            }

            animators.add(ObjectAnimator.ofFloat(this, SCALE_X_COMPAT, 0, 1));
            animators.add(ObjectAnimator.ofFloat(this, SCALE_Y_COMPAT, 0, 1));

            animators.add(ObjectAnimator.ofFloat(this, ALPHA_COMPAT, 0, 1));

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animators);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                animatorSet.addListener(new AppearanceAnimatorListener(toolTipViewX, toolTipViewY));
            }

            animatorSet.start();
        }
    }

    private int determineCenterXFromDesiredPosition(ToolTip.Position position, int masterViewWidth) {
        int centerX;
        switch (position) {
            case LEFT:
                centerX = (int) (relativeMasterViewX + getResources().getDimension(R.dimen.tooltip_point_offset));
                break;
            case CENTER:
                centerX = relativeMasterViewX + masterViewWidth / 2;
                break;
            case RIGHT:
                centerX = (int) (relativeMasterViewX + masterViewWidth - getResources().getDimension(R.dimen.tooltip_point_offset));
                break;
            default:
                centerX = relativeMasterViewX + masterViewWidth / 2;
                break;
        }
        return centerX;
    }

    public void setPointerCenterX(final int pointerCenterX) {
        int pointerWidth = Math.max(topPointerView.getMeasuredWidth(), bottomPointerView.getMeasuredWidth());

        ViewHelper.setX(topPointerView, pointerCenterX - pointerWidth / 2 - (int) getX());
        ViewHelper.setX(bottomPointerView, pointerCenterX - pointerWidth / 2 - (int) getX());
    }

    public void setOnToolTipViewClickedListener(final OnToolTipViewClickedListener listener) {
        this.listener = listener;
    }

    public void setColor(final int color) {
        topPointerView.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        topFrame.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        bottomPointerView.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        bottomFrame.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        contentHolder.setBackgroundColor(color);
    }

    private void setContentView(final View view) {
        contentHolder.removeAllViews();
        contentHolder.addView(view);
    }

    public void remove() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
            setX(params.leftMargin);
            setY(params.topMargin);
            params.leftMargin = 0;
            params.topMargin = 0;
            setLayoutParams(params);
        }

        if (toolTip.getAnimationType() == ToolTip.AnimationType.NONE) {
            if (getParent() != null) {
                ((ViewManager) getParent()).removeView(this);
            }
        } else {
            Collection<Animator> animators = new ArrayList<>(5);
            if (toolTip.getAnimationType() == ToolTip.AnimationType.FROM_MASTER_VIEW) {
                animators.add(ObjectAnimator.ofFloat(this, TRANSLATION_Y_COMPAT, (int) getY(), relativeMasterViewY + view.getHeight() / 2 - getHeight() / 2));
                animators.add(ObjectAnimator.ofFloat(this, TRANSLATION_X_COMPAT, (int) getX(), relativeMasterViewX + view.getWidth() / 2 - width / 2));
            } else {
                animators.add(ObjectAnimator.ofFloat(this, TRANSLATION_Y_COMPAT, getY(), 0));
            }

            animators.add(ObjectAnimator.ofFloat(this, SCALE_X_COMPAT, 1, 0));
            animators.add(ObjectAnimator.ofFloat(this, SCALE_Y_COMPAT, 1, 0));

            animators.add(ObjectAnimator.ofFloat(this, ALPHA_COMPAT, 1, 0));

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animators);
            animatorSet.addListener(new DisappearanceAnimatorListener());
            animatorSet.start();
        }
    }

    @Override
    public void onClick(final View view) {
        remove();
        if (tracker != null) {
            tracker.setDismissedPref(true);
        }

        if (listener != null) {
            listener.onToolTipViewClicked(this);
        }
    }

    /**
     * Convenience method for getting X.
     */
    @SuppressLint("NewApi")
    @Override
    public float getX() {
        float result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            result = super.getX();
        } else {
            result = ViewHelper.getX(this);
        }
        return result;
    }

    /**
     * Convenience method for setting X.
     */
    @SuppressLint("NewApi")
    @Override
    public void setX(final float x) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.setX(x);
        } else {
            ViewHelper.setX(this, x);
        }
    }

    /**
     * Convenience method for getting Y.
     */
    @SuppressLint("NewApi")
    @Override
    public float getY() {
        float result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            result = super.getY();
        } else {
            result = ViewHelper.getY(this);
        }
        return result;
    }

    /**
     * Convenience method for setting Y.
     */
    @SuppressLint("NewApi")
    @Override
    public void setY(final float y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.setY(y);
        } else {
            ViewHelper.setY(this, y);
        }
    }

    public boolean isInitialised() {
        return initialised;
    }

    public ToolTip getToolTip() {
        return toolTip;
    }

    public OnboardingTracker getTracker() {
        return tracker;
    }

    public interface OnToolTipViewClickedListener {
        void onToolTipViewClicked(ToolTipView toolTipView);
    }

    private class AppearanceAnimatorListener extends AnimatorListenerAdapter {

        private final float toolTipViewX;
        private final float toolTipViewY;

        AppearanceAnimatorListener(final float fToolTipViewX, final float fToolTipViewY) {
            toolTipViewX = fToolTipViewX;
            toolTipViewY = fToolTipViewY;
        }

        @Override
        public void onAnimationStart(final Animator animation) {
        }

        @Override
        @SuppressLint("NewApi")
        public void onAnimationEnd(final Animator animation) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
            params.leftMargin = (int) toolTipViewX;
            params.topMargin = (int) toolTipViewY;
            setX(0);
            setY(0);
            setLayoutParams(params);
        }

        @Override
        public void onAnimationCancel(final Animator animation) {
        }

        @Override
        public void onAnimationRepeat(final Animator animation) {
        }
    }

    private class DisappearanceAnimatorListener extends AnimatorListenerAdapter {

        @Override
        public void onAnimationStart(final Animator animation) {
        }

        @Override
        public void onAnimationEnd(final Animator animation) {
            if (getParent() != null) {
                ((ViewManager) getParent()).removeView(ToolTipView.this);
            }
        }

        @Override
        public void onAnimationCancel(final Animator animation) {
        }

        @Override
        public void onAnimationRepeat(final Animator animation) {
        }
    }
}
