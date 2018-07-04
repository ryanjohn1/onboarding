/*
 * Copyright 2013 Niek Haarman
 *
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

import android.graphics.Typeface;
import android.view.View;

public class ToolTip {



    public enum AnimationType {
        FROM_MASTER_VIEW,
        FROM_TOP,
        NONE
    }


    public enum Position {
        LEFT,
        CENTER,
        RIGHT
    }
    private CharSequence mText;

    private int textResId;

    private int color;
    private int textColor;
    private View contentView;
    private AnimationType animationType;
    private Position position;
    private boolean showBelow;
    private boolean shouldShowShadow;
    private Typeface typeface;
    private int delayInMilliseconds;
    private int verticalOffset;
    private int screenMargin;
    /**
     * Creates a new ToolTip without any values.
     */
    public ToolTip() {
        mText = null;
        typeface = null;
        textResId = 0;
        color = 0;
        contentView = null;
        showBelow = false;
        animationType = AnimationType.FROM_MASTER_VIEW;
        position = Position.CENTER;
        delayInMilliseconds = 0;
        verticalOffset = 0;
        screenMargin = 8;
    }

    /**
     * Set the text to show. Has no effect when a content View is set using setContentView().
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withText(final CharSequence text) {
        mText = text;
        textResId = 0;
        return this;
    }

    /**
     * Set the text resource id to show. Has no effect when a content View is set using setContentView().
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withText(final int resId) {
        textResId = resId;
        mText = null;
        return this;
    }

    /**
     * Set the text resource id to show and the custom typeface for that view. Has no effect when a content View is set using setContentView().
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withText(final int resId, final Typeface tf) {
        textResId = resId;
        mText = null;
        withTypeface(tf);
        return this;
    }

    /**
     * Set the color of the ToolTip. Default is white.
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withColor(final int color) {
        this.color = color;
        return this;
    }

    /**
     * Set the text color of the ToolTip. Default is white.
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withTextColor(final int color) {
        textColor = color;
        return this;
    }

    /**
     * attempt to place the tooltip below the target.
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withShowBelow() {
        showBelow = true;
        return this;
    }

    /**
     * Set a custom content View for the ToolTip. This will cause any text that has been set to be ignored.
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withContentView(final View view) {
        contentView = view;
        return this;
    }

    /**
     * Set the animation type for the ToolTip. Defaults to {@link AnimationType#FROM_MASTER_VIEW}.
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withAnimationType(final AnimationType animationType) {
        this.animationType = animationType;
        return this;
    }

    /**
     * Set to show a shadow below the ToolTip.
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withShadow() {
        shouldShowShadow = true;
        return this;
    }

    /**
     * Set to NOT show a shadow below the ToolTip.
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withoutShadow() {
        shouldShowShadow = false;
        return this;
    }

    /**
     * Set a delay in milliseconds before ToolTip is shown.
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withDelay(int delayInMilliseconds) {
        this.delayInMilliseconds = delayInMilliseconds;
        return this;
    }

    /**
     * Set vertical offset for the whole tooltip away from the target view (in dp).
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withVerticalOffset(int dpOffset) {
        this.verticalOffset = dpOffset;
        return this;
    }

    /**
     * Set margin of tooltip away from it's parent view, if not set, defaults to 8 (in dp).
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withScreenMargin(int dpMargin) {
        this.screenMargin = dpMargin;
        return this;
    }

    /**
     * @param typeface the typeface to set
     */
    public void withTypeface(final Typeface typeface) {
        this.typeface = typeface;
    }

    /**
     * Position of the ToolTip relative to the target view.
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withPosition(Position position) {
        this.position = position;
        return this;
    }

    public CharSequence getText() {
        return mText;
    }

    public int getTextResId() {
        return textResId;
    }

    public int getColor() {
        return color;
    }

    public int getTextColor() {
        return textColor;
    }

    public View getContentView() {
        return contentView;
    }

    public AnimationType getAnimationType() {
        return animationType;
    }

    public boolean shouldShowShadow() {
        return shouldShowShadow;
    }

    /**
     * @return the typeface
     */
    public Typeface getTypeface() {
        return typeface;
    }

    public int getDelayInMilliseconds() {
        return delayInMilliseconds;
    }

    public int getVerticalOffset() {
        return verticalOffset;
    }

    public int getScreenMargin() {
        return screenMargin;
    }

    public Position getPosition() {
        return position;
    }

    public boolean getShowBelow() {
      return showBelow;
    }
}
