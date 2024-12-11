package com.bodhitech.it.lib_base.lib_base.ui.views.multi_buttons;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

public class ButtonData implements Cloneable {

    // Defaul Colors
    private static final int DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    // Undefined Int Value
    public static final int INT_UNDEFINED = -0x1;

    private Object mTag;
    private String mText;
    private Drawable mIcon;
    private float mIconPaddingDp;
    private int mBackgroundColor;
    private int mTextColor;
    private boolean mIsIconButton;
    private boolean mHasBorders;

    /** Public Static Factory Methods **/
    public static List<ButtonData> buildIconButtons(Context context, Object[] tags, int[] iconResIds, int bckgrndColorResId,
                                                    float iconPaddingDp, String[] labels, int[] txtColorResId, boolean hasBorders){
        List<ButtonData> bttns = new ArrayList<>();
        for(int i=0x0; i < tags.length; i++){
            bttns.add(buildIconButton(context, tags[i], iconResIds[i], bckgrndColorResId, iconPaddingDp, labels[i], txtColorResId[i], hasBorders));
        }
        return bttns;
    }

    public static List<ButtonData> buildIconButtons(Context context, Object[] tags, int[] iconResIds, int bckgrndColorResId,
                                                    float iconPaddingDp, String[] labels, boolean hasBorders){
        List<ButtonData> bttns = new ArrayList<>();
        for(int i=0x0; i < tags.length; i++){
            bttns.add(buildIconButton(context, tags[i], iconResIds[i], bckgrndColorResId, iconPaddingDp, labels[i], hasBorders));
        }
        return bttns;
    }

    public static ButtonData buildIconButton(Context context, Object tag, int iconResId, int bckgrndColorResId, float iconPaddingDp,
                                             String label, int txtColorResId, boolean hasBorder) {
        return new ButtonData(true)
                .setTag(tag)
                .setIsIconButton(true)
                .setIconResId(context, iconResId)
                .setBackgroundColorId(context, bckgrndColorResId)
                .setIconPaddingDp(iconPaddingDp)
                .setText(label)
                .setTextColor(context, txtColorResId)
                .setHasBorders(hasBorder);
    }

    public static ButtonData buildIconButton(Context context, Object tag, int iconResId, int bckgrndColorResId, float iconPaddingDp,
                                             String label, boolean hasBorder) {
        return new ButtonData(true)
                .setTag(tag)
                .setIsIconButton(true)
                .setIconResId(context, iconResId)
                .setBackgroundColorId(context, bckgrndColorResId)
                .setIconPaddingDp(iconPaddingDp)
                .setText(label)
                .setHasBorders(hasBorder);
    }

    public static ButtonData buildTextButton(String text, Object tag, int textColor, int bckgrndColor) {
        return  new ButtonData(false)
                .setTag(tag)
                .setTextColor(textColor)
                .setIsIconButton(false)
                .setText(text)
                .setBackgroundColor(bckgrndColor);
    }

    /** Private Constructors **/
    private ButtonData(boolean iconButton) {
        mIsIconButton = iconButton;
        mBackgroundColor = DEFAULT_BACKGROUND_COLOR;
        mTextColor = DEFAULT_TEXT_COLOR;
    }

    /** Getter Methods **/
    public Object getTag() {
        return mTag;
    }

    public String getText() {
        return mText;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public float getIconPaddingDp() {
        return mIconPaddingDp;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public int getTextColor(){
        return mTextColor;
    }

    public boolean isIconButton() {
        return mIsIconButton;
    }

    public boolean hasBorders(){
        return mHasBorders;
    }

    /** Setter Methods **/
    public ButtonData setTag(Object tag) {
        mTag = tag;
        return this;
    }

    public ButtonData setText(String text) {
        mText = text;
        return this;
    }

    public ButtonData setText(Context context, int resId){
        mText = context.getString(resId);
        return this;
    }

    public ButtonData setIcon(Drawable icon) {
        mIcon = icon;
        return this;
    }

    public ButtonData setIconResId(Context context, int iconResId) {
        mIcon = context.getResources().getDrawable(iconResId);
        return this;
    }

    public ButtonData setIconPaddingDp(float padding) {
        mIconPaddingDp = padding;
        return this;
    }

    public ButtonData setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        return this;
    }

    public ButtonData setBackgroundColorId(Context context, int backgroundColorId) {
        mBackgroundColor = context.getResources().getColor(backgroundColorId);
        return this;
    }

    public ButtonData setTextColor(int textColor){
        mTextColor = textColor;
        return this;
    }

    public ButtonData setTextColor(Context context, int textColorId){
        mTextColor = context.getResources().getColor(textColorId);
        return this;
    }

    public ButtonData setIsIconButton(boolean isIconButton) {
        mIsIconButton = isIconButton;
        return this;
    }

    public ButtonData setHasBorders(boolean hasBorders){
        mHasBorders = hasBorders;
        return this;
    }

    /** Override Cloneable Methods **/
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return ((ButtonData) super.clone())
                .setTag(mTag)
                .setIsIconButton(mIsIconButton)
                .setIcon(mIcon)
                .setIconPaddingDp(mIconPaddingDp)
                .setText(mText)
                .setBackgroundColor(mBackgroundColor)
                .setTextColor(mTextColor)
                .setHasBorders(mHasBorders);
    }

}
