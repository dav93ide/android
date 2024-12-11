package com.bodhitech.it.lib_base.lib_base.ui.views.multi_buttons;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.core.content.ContextCompat;

import com.bodhitech.it.lib_base.R;
import com.bodhitech.it.lib_base.lib_base.modules.utils.DesignUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("AppCompatCustomView")
public class MultiButtons extends View implements ValueAnimator.AnimatorUpdateListener {

    public interface IOnButtonClicked {
        void onButtonClicked(View view, Object tag);
    }

    public interface IOnButtonExpanded {
        void onButtonExpanded(View view);
    }

    // Undefined Values
    private static final int UNDEFINED = -0x1;
    // Buttons Positions
    private static final int POS_MAIN_BUTTON = 0x0;
    // Pixels Buttons Size
    private static final int PX_BUTTONS_BORDER = 0x3;
    // Buttons Labels Alignment
    public static final int LABEL_ALIGN_LEFT = -0x1;
    public static final int LABEL_ALIGN_CENTER = 0x0;
    public static final int LABEL_ALIGN_RIGHT = 0x1;
    // View Types
    public static final int VIEW_TYPE_CIRCLE = 0x0;
    public static final int VIEW_TYPE_MULTI_CIRCLE = 0x1;
    public static final int VIEW_TYPE_LINE_TOP = 0x2;
    public static final int VIEW_TYPE_LINE_BOTTOM = 0x3;
    public static final int VIEW_TYPE_LINE_RIGHT = 0x4;
    public static final int VIEW_TYPE_LINE_LEFT = 0x5;
    public static final int VIEW_TYPE_DIAG_TOP_LEFT = 0x6;
    public static final int VIEW_TYPE_DIAG_TOP_RIGHT = 0x7;
    public static final int VIEW_TYPE_DIAG_BOTTOM_RIGHT = 0x8;
    public static final int VIEW_TYPE_DIAG_BOTTOM_LEFT = 0x9;
    public static final int VIEW_TYPE_CROSS_VERTICAL = 0xA;
    public static final int VIEW_TYPE_CROSS_DIAGONAL = 0xB;
    // View Data HashMap Keys
    private static final int KEY_VIEW_TYPE = 0x0;
    private static final int KEY_LABELS_ALIGN = 0x1;
    private static final int KEY_LABELS_SIZE = 0x2;
    private static final int KEY_ROTATE_DEGREES = 0x3;
    private static final int KEY_START_ANGLE_DEGREES = 0x4;
    private static final int KEY_END_ANGLE_DEGREES = 0x5;
    private static final int KEY_NUM_VIEWS_FOR_LAYER = 0x6;
    private static final int KEY_BTTNS_DISTANCE = 0x7;
    private static final int KEY_DIAGONAL_DEGREES = 0x8;
    private static final int KEY_ANIMATION_DURATION = 0x9;
    // Cross View Num Views For Layer
    private static final int NUM_VIEWS_CROSS = 0x4;
    // Cross View Types Angles Degrees
    private static final int CROSS_VERTICAL_START_ANGLE_DEGREES = 0;
    private static final int CROSS_VERTICAL_END_ANGLE_DEGREES = 360;
    private static final int CROSS_DIAGONAL_START_ANGLE_DEGREES = 45;
    private static final int CROSS_DIAGONAL_END_ANGLE_DEGREES = 405;
    // Default Values
    private static final int DEFAULT_ROTATE_DEGREES = 360;
    private static final int DEFAULT_BTTNS_SIZE = 75;
    private static final int DEFAULT_BTTNS_MARGIN = 0x5;
    private static final int DEFAULT_LABELS_ALIGN = LABEL_ALIGN_CENTER;
    private static final int DEFAULT_LABELS_SIZE = UNDEFINED;
    private static final int DEFAULT_VIEW_TYPE = VIEW_TYPE_CIRCLE;
    private static final int DEFAULT_START_ANGLE_DEGREES = 0;
    private static final int DEFAULT_END_ANGLE_DEGREES = 360;
    private static final int DEFAULT_NUM_VIEWS_FOR_LAYER = 0x6;
    private static final int DEFAULT_CIRCLE_RADIUS = UNDEFINED;
    private static final int DEFAULT_DIAGIONAL_DEGREES = 45;
    private static final long DEFAULT_ANIMATION_DURATION = 500L;

    private IOnButtonClicked mClickCallback;
    private IOnButtonExpanded mExpandCallback;

    private List<ButtonData> mButtonsData;
    private MaskView mMask;
    private OvershootInterpolator mOvershootInterpolator;
    private AnticipateInterpolator mAnticipateInterpolator;
    private ValueAnimator mExpandValAnim;
    private ValueAnimator mCollapseValAnim;
    private ValueAnimator mRotateValAnim;
    private AngleCalculator mAngleCalculator;
    private RectF mMainRectF;
    private Paint mPaint;
    private float mExpandProgress;
    private float mRotateProgress;
    private boolean mIsExpanded;
    private boolean mIsMaskAttached;

    private HashMap<Integer, Object> mViewData;
    private float mBttnsSize;
    private float mBttnsMargin;

    public MultiButtons(Context context){
        super(context);
        init();
    }

    public MultiButtons(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
        init(attrs);
    }

    /** Override View Methods **/
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        float[] temp = new float[]{mMainRectF.left, mMainRectF.top, mMainRectF.right, mMainRectF.bottom};
        mMainRectF.set(mBttnsMargin,  mBttnsMargin, mBttnsSize + mBttnsMargin, mBttnsSize + mBttnsMargin);
        drawButton(canvas, mPaint, POS_MAIN_BUTTON);
        mMainRectF.set(temp[0x0], temp[0x1], temp[0x2], temp[0x3]);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) (mBttnsSize + PX_BUTTONS_BORDER + (mBttnsMargin * 0x2)),
                (int) (mBttnsSize + PX_BUTTONS_BORDER + (mBttnsMargin * 0x2)));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initButtonInfo();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(!mIsExpanded){
                expand();
            } else {
                collapse();
            }
        }
        return super.onTouchEvent(event);
    }

    /** Override ValueAnimator.AnimatorUpdateListener Methods **/
    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        if(valueAnimator == mExpandValAnim || valueAnimator == mCollapseValAnim) {
            mExpandProgress = (float) valueAnimator.getAnimatedValue();
        }
        if(valueAnimator == mRotateValAnim){
            mRotateProgress = (float) valueAnimator.getAnimatedValue();
        }
        if(mIsMaskAttached && mExpandProgress > 0f && mExpandProgress < 1f && mRotateProgress > 0f && mRotateProgress < 1f){
            mMask.updateButtons();
            mMask.invalidate();
        }
    }

    /** Builder Methods **/
    public MultiButtons setViewTag(Object tag){
        setTag(tag);
        return this;
    }

    public MultiButtons setViewTag(int key, Object tag){
        setTag(key, tag);
        return this;
    }

    public MultiButtons setClickCallback(IOnButtonClicked callback){
        mClickCallback = callback;
        return this;
    }

    public MultiButtons setExpandCallback(IOnButtonExpanded callback){
        mExpandCallback = callback;
        return this;
    }

    public MultiButtons setButtonsData(List<ButtonData> buttonsData){
        mButtonsData = new ArrayList<>();
        mButtonsData.addAll(buttonsData);
        switch(getViewDataViewType()){
            case VIEW_TYPE_CIRCLE:
                mAngleCalculator = new AngleCalculator(getViewDataStartAngleDegrees(), getViewDataEndAngleDegrees(), buttonsData.size() - 0x1);
                break;
            case VIEW_TYPE_MULTI_CIRCLE:
                mAngleCalculator = new AngleCalculator(getViewDataStartAngleDegrees(), getViewDataEndAngleDegrees(), getViewDataNumViewsForLayer());
                break;
            case VIEW_TYPE_CROSS_VERTICAL:
                mViewData.put(KEY_NUM_VIEWS_FOR_LAYER, NUM_VIEWS_CROSS);
                mAngleCalculator = new AngleCalculator(CROSS_VERTICAL_START_ANGLE_DEGREES, CROSS_VERTICAL_END_ANGLE_DEGREES, getViewDataNumViewsForLayer());
                break;
            case VIEW_TYPE_CROSS_DIAGONAL:
                mViewData.put(KEY_NUM_VIEWS_FOR_LAYER, NUM_VIEWS_CROSS);
                mAngleCalculator = new AngleCalculator(CROSS_DIAGONAL_START_ANGLE_DEGREES, CROSS_DIAGONAL_END_ANGLE_DEGREES, getViewDataNumViewsForLayer());
                break;
            default:
                mAngleCalculator = null;
                break;
        }
        return this;
    }

    public MultiButtons setBttnsSize(int resId){
        mBttnsSize = getContext().getResources().getDimensionPixelSize(resId);
        return this;
    }

    public MultiButtons setBttnsMargin(int resId){
        mBttnsMargin = getContext().getResources().getDimensionPixelSize(resId);
        return this;
    }

    public MultiButtons setBttnsDistance(int resId){
        mViewData.put(KEY_BTTNS_DISTANCE, getContext().getResources().getDimensionPixelSize(resId));
        return this;
    }

    public MultiButtons setViewType(int viewType){
        mViewData.put(KEY_VIEW_TYPE, viewType);
        return this;
    }

    public MultiButtons setLabelsAlign(int align){
        mViewData.put(KEY_LABELS_ALIGN, align);
        return this;
    }

    public MultiButtons setLabelsSize(int resId){
        mViewData.put(KEY_LABELS_SIZE, getContext().getResources().getDimensionPixelSize(resId));
        return this;
    }

    public MultiButtons setStartAngleDegrees(int degrees){
        mViewData.put(KEY_START_ANGLE_DEGREES, degrees);
        return this;
    }

    public MultiButtons setEndAngleDegrees(int degrees){
        mViewData.put(KEY_END_ANGLE_DEGREES, degrees);
        return this;
    }

    public MultiButtons setNumViewsForLayer(int numViews){
        mViewData.put(KEY_NUM_VIEWS_FOR_LAYER, numViews);
        return this;
    }

    public MultiButtons setRotateDegrees(int degrees){
        mViewData.put(KEY_ROTATE_DEGREES, degrees);
        return this;
    }

    public MultiButtons setDiagonalDegrees(int degrees){
        mViewData.put(KEY_DIAGONAL_DEGREES, degrees);
        return this;
    }

    public MultiButtons setAnimationDuration(long duration){
        mViewData.put(KEY_ANIMATION_DURATION, duration);
        return this;
    }

    /** Getter Methods **/
    public List<ButtonData> getButtonsData(){
        return mButtonsData;
    }

    public boolean isExpanded(){
        return mIsExpanded;
    }

    /** Public Methods **//** Moving & Collapse Methods **/
    public void collapseButtons(){
        collapse();
    }

    /** Useful Inside RecyclerView Methods **/
    public void collapseDetachingMask(){
        detachMask();
        mIsExpanded = false;
    }

    // Invalidate & Re-Draw the view moving it of an offset, the buttons view get closed when it touch a border of the "bounds" rect.
    public void setMoving(Rect bounds, int dx, int dy){
        invalidate();
        mMask.invalidate();
        mMainRectF.top -= dy;
        mMainRectF.bottom -= dy;
        mMainRectF.left -= dx;
        mMainRectF.right -= dx;
        if(mMask.mExpandCoordinates != null && mMask.mExpandCoordinates.size() > 0x0){
            RectF rBttns;
            for(int i = 0x0; i < mMask.mExpandCoordinates.size(); i++){
                MaskView.Coordinates coords = mMask.mExpandCoordinates.get(i);
                if(coords != null){
                    rBttns = new RectF(mMainRectF);
                    rBttns.top += coords.mY;
                    rBttns.bottom += coords.mY;
                    rBttns.left += coords.mX;
                    rBttns.right += coords.mX;
                    if(rBttns.top <= bounds.top || rBttns.bottom >= bounds.bottom || rBttns.left <= bounds.left || rBttns.right >= bounds.right){
                        collapseDetachingMask();
                    }
                }
            }
        }
    }

    /** Private Methods **//** Init Methods **/
    private void init(){
        mIsMaskAttached = false;
        mIsExpanded = false;
        mViewData = new HashMap<>();
        mMainRectF = new RectF();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        initViewTreeObserver();
        initAnimators();
    }

    private void init(AttributeSet attrs){
        TypedArray ta = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.MultiButtons, 0x0, 0x0);
        try{
            mBttnsSize = ta.getDimensionPixelSize(R.styleable.MultiButtons_bttns_size, DEFAULT_BTTNS_SIZE);
            mBttnsMargin = ta.getDimensionPixelSize(R.styleable.MultiButtons_bttns_margin, DEFAULT_BTTNS_MARGIN);

            int attr = ta.getDimensionPixelSize(R.styleable.MultiButtons_bttns_distance, UNDEFINED);
            if(attr != UNDEFINED){
                mViewData.put(KEY_BTTNS_DISTANCE, attr);
            }
            attr = ta.getInt(R.styleable.MultiButtons_labels_align, UNDEFINED);
            if(attr != UNDEFINED) {
                mViewData.put(KEY_LABELS_ALIGN, attr);
            }
            attr = ta.getDimensionPixelSize(R.styleable.MultiButtons_labels_size, UNDEFINED);
            if(attr != UNDEFINED){
                mViewData.put(KEY_LABELS_SIZE, attr);
            }
            attr = ta.getInt(R.styleable.MultiButtons_rotate_degrees, UNDEFINED);
            if(attr != UNDEFINED){
                mViewData.put(KEY_ROTATE_DEGREES, attr);
            }
            attr = ta.getInt(R.styleable.MultiButtons_view_type, UNDEFINED);
            if(attr != UNDEFINED){
                mViewData.put(KEY_VIEW_TYPE, attr);
                switch (attr){
                    case VIEW_TYPE_MULTI_CIRCLE:
                        attr = ta.getInt(R.styleable.MultiButtons_num_views_for_layer, UNDEFINED);
                        if(attr != UNDEFINED){
                            mViewData.put(KEY_NUM_VIEWS_FOR_LAYER, attr);
                        }
                    case VIEW_TYPE_CIRCLE:
                        attr = ta.getInt(R.styleable.MultiButtons_start_angle_degrees, UNDEFINED);
                        if(attr != UNDEFINED){
                            mViewData.put(KEY_START_ANGLE_DEGREES, attr);
                        }
                        attr = ta.getInt(R.styleable.MultiButtons_end_angle_degrees, UNDEFINED);
                        if(attr != UNDEFINED){
                            mViewData.put(KEY_END_ANGLE_DEGREES, attr);
                        }
                        break;
                    case VIEW_TYPE_DIAG_TOP_LEFT:
                    case VIEW_TYPE_DIAG_TOP_RIGHT:
                    case VIEW_TYPE_DIAG_BOTTOM_RIGHT:
                    case VIEW_TYPE_DIAG_BOTTOM_LEFT:
                        attr = ta.getInt(R.styleable.MultiButtons_diagonal_degrees, UNDEFINED);
                        if(attr != UNDEFINED){
                            mViewData.put(KEY_DIAGONAL_DEGREES, attr);
                        }
                        break;
                }
            }
            attr = ta.getInt(R.styleable.MultiButtons_animation_duration, UNDEFINED);
            if(attr != UNDEFINED){
                mViewData.put(KEY_ANIMATION_DURATION, attr);
            }

        } finally {
            ta.recycle();
        }
    }

    private void initViewTreeObserver(){
        getViewTreeObserver().addOnGlobalLayoutListener(
                () -> {
                    Rect r = new Rect();
                    getGlobalVisibleRect(r);
                    mMainRectF.set(r);
                }
        );
    }

    private void initAnimators(){
        final MultiButtons _this = this;
        mOvershootInterpolator = new OvershootInterpolator();
        mAnticipateInterpolator = new AnticipateInterpolator();
        long duration = getViewDataAnimationDuration();
        mExpandValAnim = ValueAnimator.ofFloat(0f, 1f);
        mExpandValAnim.setDuration(duration);
        mExpandValAnim.setInterpolator(mOvershootInterpolator);
        mExpandValAnim.addUpdateListener(this);
        mExpandValAnim.addListener(new SimpleAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                attachMask();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                mIsExpanded = true;
                if(mExpandCallback != null){
                    mExpandCallback.onButtonExpanded(_this);
                }
            }
        });

        mCollapseValAnim = ValueAnimator.ofFloat(1f, 0f);
        mCollapseValAnim.setDuration(duration);
        mCollapseValAnim.setInterpolator(mAnticipateInterpolator);
        mCollapseValAnim.addUpdateListener(this);
        mCollapseValAnim.addListener(new SimpleAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                mIsExpanded = false;
                detachMask();
                if(mExpandCallback != null){
                    mExpandCallback.onButtonExpanded(_this);
                }
            }
        });

        mRotateValAnim = ValueAnimator.ofFloat(0f, 1f);
        mRotateValAnim.setDuration(duration);
        mRotateValAnim.addUpdateListener(this);
    }

    private void initButtonInfo(){
        Rect r = new Rect();
        getGlobalVisibleRect(r);
        mMainRectF.set(r);
    }

    /** Expand/Collapse Methods **/
    private void expand(){
        if(!mIsExpanded && !mExpandValAnim.isRunning()){
            mExpandValAnim.start();
            startRotateAnimation(true);
        }
    }

    private void collapse(){
        if(mIsExpanded && !mCollapseValAnim.isRunning()){
            mCollapseValAnim.start();
            startRotateAnimation(false);
        }
    }

    /** Mask Methods **/
    private void attachMask(){
        if(mMask == null){
            mMask = new MaskView(getContext(), this);
        }
        if(!mIsMaskAttached){
            ViewGroup root = ((ViewGroup)getRootView());
            if(root.getChildCount() != 0x0){
                root.removeView(mMask);
            }
            root.addView(mMask);
            mIsMaskAttached = true;
        }
    }

    private void detachMask(){
        if(mIsMaskAttached){
            mIsMaskAttached = false;
            ((ViewGroup)getRootView()).removeView(mMask);
        }
    }

    /** Animations Methods **/
    private void startRotateAnimation(boolean expand){
        if(mRotateValAnim != null){
            if(mRotateValAnim.isRunning()){
                mRotateValAnim.cancel();
            }
            if(expand){
                mRotateValAnim.setInterpolator(mOvershootInterpolator);
                mRotateValAnim.setFloatValues(0f, 1f);
            } else {
                mRotateValAnim.setInterpolator(mAnticipateInterpolator);
                mRotateValAnim.setFloatValues(1f, 0f);
            }
            mRotateValAnim.start();
        }
    }

    /** Draw Buttons Methods **/
    private void drawButton(Canvas canvas, Paint paint, int index){
        ButtonData bData = mButtonsData.get(index);
        if(bData.hasBorders()){
            drawButtonBorder(canvas, paint);
        }
        drawButtonContent(canvas, paint, index);
        if(bData.isIconButton() && !TextUtils.isEmpty(bData.getText())){
            drawButtonText(canvas, paint, index);
        }
    }

    private void drawButtonBorder(Canvas canvas, Paint paint){
        paint.setColor(ContextCompat.getColor(getContext(), R.color.grey_900));
        RectF rect = new RectF(mMainRectF);
        checkFixSize(rect);
        rect.left -= PX_BUTTONS_BORDER;
        rect.right += PX_BUTTONS_BORDER;
        rect.bottom += PX_BUTTONS_BORDER;
        rect.top -= PX_BUTTONS_BORDER;
        canvas.drawOval(rect, paint);
    }

    private void drawButtonContent(Canvas canvas, Paint paint, int index){
        ButtonData bData = mButtonsData.get(index);
        paint.setColor(bData.getBackgroundColor());
        RectF rect = new RectF(mMainRectF);
        checkFixSize(rect);
        canvas.drawOval(rect, paint);
        if(bData.isIconButton()){
            Drawable drawable = bData.getIcon();
            drawable.setBounds((int) (rect.left + bData.getIconPaddingDp()), (int) (rect.top + bData.getIconPaddingDp()),
                    (int) (rect.right - bData.getIconPaddingDp()), (int) (rect.bottom - bData.getIconPaddingDp()));
            drawable.draw(canvas);
        } else {
            paint.setColor(bData.getTextColor());
            float size = bData.getText().length() > 0x2 ?
                    DesignUtils.calculateTextSize(bData.getText(), (int)(rect.width() - (mBttnsMargin * 0x2))) :
                    DesignUtils.calculateTextSize(bData.getText(), (int)(rect.height() - (mBttnsMargin * 0x2)), (int)(rect.width() - (PX_BUTTONS_BORDER * 0x2) - (mBttnsMargin * 0x2)));
            paint.setTextSize(size);
            float y = rect.centerY() + (size / 0x2) - (mBttnsMargin / 0x2);
            float x = rect.centerX() - (paint.measureText(bData.getText()) / 0x2);
            canvas.drawText(bData.getText(), x, y, paint);
        }
    }

    private void drawButtonText(Canvas canvas, Paint paint, int index){
        ButtonData bData = mButtonsData.get(index);
        paint.setColor(bData.getTextColor());
        float size = getViewDataLabelsSize() != UNDEFINED ? getViewDataLabelsSize() : mBttnsSize / 0x4;
        paint.setTextSize(size);
        RectF rect = new RectF(mMainRectF);
        checkFixSize(rect);
        String text = bData.getText();
        float temp = rect.right;
        rect.right = rect.left - size;
        rect.left = temp + size;
        temp = rect.top;
        rect.top = rect.bottom + size;
        rect.bottom = temp - size;
        Path path = new Path();
        path.addOval(rect, Path.Direction.CCW);

        float hOffset = 0x0;
        switch(getViewDataLabelAlign()){
            case LABEL_ALIGN_LEFT:
                paint.setTextAlign(Paint.Align.LEFT);
                break;
            case LABEL_ALIGN_CENTER:
                float tSize = paint.measureText(text);
                float circle = (float) (2f * Math.PI * (mBttnsSize / 0x2));
                if(tSize < circle / 0x2){
                    hOffset = (circle / 0x2 - tSize / 0x2) / 0x2;
                }
                break;
            case LABEL_ALIGN_RIGHT:
                paint.setTextAlign(Paint.Align.RIGHT);
                break;
        }
        canvas.drawTextOnPath(text, path, hOffset, 0x0, paint);
    }

    private void checkFixSize(RectF rect){
        float height = Math.abs(rect.top - rect.bottom);
        if(height < mBttnsSize){
            float diff = Math.abs(height - mBttnsSize);
            rect.top -= diff/0x2;
            rect.bottom += diff/0x2;
        }
    }

    /** Get View Data Methods **/
    private int getViewDataViewType(){
        return mViewData != null && mViewData.containsKey(KEY_VIEW_TYPE) ?
                (Integer) mViewData.get(KEY_VIEW_TYPE) : DEFAULT_VIEW_TYPE;
    }

    private int getViewDataLabelAlign(){
        return mViewData != null && mViewData.containsKey(KEY_LABELS_ALIGN) ?
                (Integer) mViewData.get(KEY_LABELS_ALIGN) : DEFAULT_LABELS_ALIGN;
    }

    private int getViewDataLabelsSize(){
        return mViewData != null && mViewData.containsKey(KEY_LABELS_SIZE) ?
                (Integer) mViewData.get(KEY_LABELS_SIZE) : DEFAULT_LABELS_SIZE;
    }

    private int getViewDataRotateDegrees(){
        return mViewData != null && mViewData.containsKey(KEY_ROTATE_DEGREES) ?
                (Integer) mViewData.get(KEY_ROTATE_DEGREES) : DEFAULT_ROTATE_DEGREES;
    }

    private int getViewDataStartAngleDegrees(){
        return mViewData != null && mViewData.containsKey(KEY_START_ANGLE_DEGREES) ?
                (Integer) mViewData.get(KEY_START_ANGLE_DEGREES) : DEFAULT_START_ANGLE_DEGREES;
    }

    private int getViewDataEndAngleDegrees(){
        return mViewData != null && mViewData.containsKey(KEY_END_ANGLE_DEGREES) ?
                (Integer) mViewData.get(KEY_END_ANGLE_DEGREES) : DEFAULT_END_ANGLE_DEGREES;
    }

    private int getViewDataNumViewsForLayer(){
        return mViewData != null && mViewData.containsKey(KEY_NUM_VIEWS_FOR_LAYER) ?
                (Integer) mViewData.get(KEY_NUM_VIEWS_FOR_LAYER) : DEFAULT_NUM_VIEWS_FOR_LAYER;
    }

    private int getViewDataBttnsDistance(){
        return mViewData != null && mViewData.containsKey(KEY_BTTNS_DISTANCE) ?
                (Integer) mViewData.get(KEY_BTTNS_DISTANCE) : DEFAULT_CIRCLE_RADIUS;
    }

    private int getViewDataDiagonalDegrees(){
        return mViewData != null && mViewData.containsKey(KEY_DIAGONAL_DEGREES) ?
                (Integer) mViewData.get(KEY_DIAGONAL_DEGREES) : DEFAULT_DIAGIONAL_DEGREES;
    }

    private long getViewDataAnimationDuration(){
        return mViewData != null && mViewData.containsKey(KEY_ANIMATION_DURATION) ?
                Long.valueOf((Integer) mViewData.get(KEY_ANIMATION_DURATION)) : DEFAULT_ANIMATION_DURATION;
    }

    /** Private Classes **/
    private static class MaskView extends View {

        private MultiButtons mMainButton;
        private Matrix[] mArrMatrix;
        private Map<Integer, Coordinates> mExpandCoordinates;
        private Paint mPaint;

        public MaskView(Context context, MultiButtons main) {
            super(context);
            mMainButton = main;
            init();
        }

        /** Override View Methods **/
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawButtons(canvas);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            View root = getRootView();
            setMeasuredDimension(root.getWidth(), root.getHeight());
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                for(int i=0x1; i < mMainButton.mButtonsData.size(); i++){
                    ButtonData bd = mMainButton.mButtonsData.get(i);
                    RectF rect = new RectF(mMainButton.mMainRectF);
                    Coordinates coords = mExpandCoordinates.get(i);
                    if(coords != null){
                        rect.left += coords.mX;
                        rect.right += coords.mX;
                        rect.top += coords.mY;
                        rect.bottom += coords.mY;
                        if(mMainButton.mClickCallback != null && bd != null &&
                                event.getX() >= rect.left && event.getX() <= rect.right && event.getY() >= rect.top && event.getY() <= rect.bottom){
                            mMainButton.mClickCallback.onButtonClicked(mMainButton, bd.getTag());
                        }
                    }
                }
            }
            return super.onTouchEvent(event);
        }

        /** Private Methods **//** Init Methods **/
        private void init(){
            mArrMatrix = new Matrix[mMainButton.mButtonsData.size()];
            for(int i=0x0; i<mMainButton.mButtonsData.size(); i++){
                mArrMatrix[i] = new Matrix();
            }
            mExpandCoordinates = new HashMap<>(mMainButton.mButtonsData.size());
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setAntiAlias(true);
        }

        /** Buttons Methods **/
        private void drawButtons(Canvas canvas){
            for(int i = mMainButton.mButtonsData.size() - 0x1; i >= 0x0 ; i--){
                canvas.save();
                canvas.concat(mArrMatrix[i]);
                mMainButton.drawButton(canvas, mPaint, i);
                canvas.restore();
            }
        }

        private void updateButtons(){
            Matrix matrix = mArrMatrix[POS_MAIN_BUTTON];
            matrix.reset();
            matrix.postRotate(mMainButton.getViewDataRotateDegrees() * mMainButton.mRotateProgress, mMainButton.mMainRectF.centerX(), mMainButton.mMainRectF.centerY());
            for(int i=0x1; i < mMainButton.mButtonsData.size(); i++) {
                matrix = mArrMatrix[i];
                matrix.reset();
                if(mMainButton.mIsExpanded){
                    Coordinates coords = mExpandCoordinates.get(i);
                    if(coords != null){
                        matrix.postTranslate(mMainButton.mExpandProgress * coords.mX, mMainButton.mExpandProgress * coords.mY);
                    }
                } else {
                    Coordinates coords = mExpandCoordinates.get(i);
                    if(coords == null) {
                        switch (mMainButton.getViewDataViewType()) {
                            case VIEW_TYPE_CIRCLE:
                                coords = getCircleCoords(i);
                                break;
                            case VIEW_TYPE_MULTI_CIRCLE:
                            case VIEW_TYPE_CROSS_VERTICAL:
                            case VIEW_TYPE_CROSS_DIAGONAL:
                                coords = getMultiCircleCoords(i);
                                break;
                            case VIEW_TYPE_LINE_TOP:
                                coords = getLineTopCoords(i);
                                break;
                            case VIEW_TYPE_LINE_BOTTOM:
                                coords = getLineBottomCoords(i);
                                break;
                            case VIEW_TYPE_LINE_RIGHT:
                                coords = getLineRightCoords(i);
                                break;
                            case VIEW_TYPE_LINE_LEFT:
                                coords = getLineLeftCoords(i);
                                break;
                            case VIEW_TYPE_DIAG_TOP_LEFT:
                                coords = getDiagTopLeftCoords(i);
                                break;
                            case VIEW_TYPE_DIAG_TOP_RIGHT:
                                coords = getDiagTopRightCoords(i);
                                break;
                            case VIEW_TYPE_DIAG_BOTTOM_RIGHT:
                                coords = getDiagBottomRightCoords(i);
                                break;
                            case VIEW_TYPE_DIAG_BOTTOM_LEFT:
                                coords = getDiagBottomLeftCoords(i);
                                break;
                        }
                        mExpandCoordinates.put(i, coords);
                    }
                    matrix.postTranslate(mMainButton.mExpandProgress * coords.mX, mMainButton.mExpandProgress * coords.mY);
                }
            }
        }

        /** Coordinates Methods **/
        private Coordinates getCircleCoords(int i){
            int radius = mMainButton.getViewDataBttnsDistance() != UNDEFINED ?
                    mMainButton.getViewDataBttnsDistance() :
                    (int) ((mMainButton.mBttnsSize / 0x2) + mMainButton.mBttnsSize);
            return new Coordinates(mMainButton.mAngleCalculator.getMoveX(radius, i), mMainButton.mAngleCalculator.getMoveY(radius, i));
        }

        private Coordinates getMultiCircleCoords(int i){
            int radius = mMainButton.getViewDataBttnsDistance() != UNDEFINED ?
                    mMainButton.getViewDataBttnsDistance() :
                    (int) ((mMainButton.mBttnsSize / 0x2) + mMainButton.mBttnsSize);
            radius += radius * Math.round(i / (mMainButton.getViewDataNumViewsForLayer() + 0x1));
            return new Coordinates(mMainButton.mAngleCalculator.getMoveX(radius, i), mMainButton.mAngleCalculator.getMoveY(radius, i));
        }

        private Coordinates getLineTopCoords(int i){
            int distance = mMainButton.getViewDataBttnsDistance() != UNDEFINED ?
                    mMainButton.getViewDataBttnsDistance() * i :
                    (int) (((mMainButton.mBttnsSize / 0x2) * i) + (mMainButton.mBttnsSize * i));
            return new Coordinates(0x0, -distance);
        }

        private Coordinates getLineBottomCoords(int i){
            int distance = mMainButton.getViewDataBttnsDistance() != UNDEFINED ?
                    mMainButton.getViewDataBttnsDistance() * i :
                    (int) (((mMainButton.mBttnsSize / 0x2) * i) + (mMainButton.mBttnsSize * i));
            return new Coordinates(0x0, distance);
        }

        private Coordinates getLineRightCoords(int i){
            int distance = mMainButton.getViewDataBttnsDistance() != UNDEFINED ?
                    mMainButton.getViewDataBttnsDistance() * i :
                    (int) (((mMainButton.mBttnsSize / 0x2) * i) + (mMainButton.mBttnsSize * i));
            return new Coordinates(distance, 0x0);
        }

        private Coordinates getLineLeftCoords(int i){
            int distance = mMainButton.getViewDataBttnsDistance() != UNDEFINED ?
                    mMainButton.getViewDataBttnsDistance() * i :
                    (int) (((mMainButton.mBttnsSize / 0x2) * i) + (mMainButton.mBttnsSize * i));
            return new Coordinates(-distance, 0x0);
        }

        private Coordinates getDiagTopLeftCoords(int i){
            int distance = mMainButton.getViewDataBttnsDistance() != UNDEFINED ?
                    mMainButton.getViewDataBttnsDistance() * i :
                    (int) (((mMainButton.mBttnsSize / 0x4) * i) + (mMainButton.mBttnsSize * i));
            int degrees = mMainButton.getViewDataDiagonalDegrees();
            float x = (float) (distance * Math.cos(degrees));
            float y = (float) (distance * Math.sin(degrees));
            return new Coordinates(x, -y);
        }

        private Coordinates getDiagTopRightCoords(int i){
            int distance = mMainButton.getViewDataBttnsDistance() != UNDEFINED ?
                    mMainButton.getViewDataBttnsDistance() * i :
                    (int) (((mMainButton.mBttnsSize / 0x4) * i) + (mMainButton.mBttnsSize * i));
            int degrees = mMainButton.getViewDataDiagonalDegrees();
            float x = (float) (distance * Math.cos(degrees));
            float y = (float) (distance * Math.sin(degrees));
            return new Coordinates(x, y);
        }

        private Coordinates getDiagBottomRightCoords(int i){
            int distance = mMainButton.getViewDataBttnsDistance() != UNDEFINED ?
                    mMainButton.getViewDataBttnsDistance() * i :
                    (int) (((mMainButton.mBttnsSize / 0x4) * i) + (mMainButton.mBttnsSize * i));
            int degrees = mMainButton.getViewDataDiagonalDegrees();
            float x = (float) (distance * Math.cos(degrees));
            float y = (float) (distance * Math.sin(degrees));
            return new Coordinates(-x, y);
        }

        private Coordinates getDiagBottomLeftCoords(int i){
            int distance = mMainButton.getViewDataBttnsDistance() != UNDEFINED ?
                    mMainButton.getViewDataBttnsDistance() * i :
                    (int) (((mMainButton.mBttnsSize / 0x4) * i) + (mMainButton.mBttnsSize * i));
            int degrees = mMainButton.getViewDataDiagonalDegrees();
            float x = (float) (distance * Math.cos(degrees));
            float y = (float) (distance * Math.sin(degrees));
            return new Coordinates(-x, -y);
        }

        /** MaskView Private Classes **/
        private static class Coordinates {
            private float mX;
            private float mY;

            private Coordinates(float x, float y){
                mX = x;
                mY = y;
            }
        }
    }

    private static class SimpleAnimatorListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {

        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }

}
