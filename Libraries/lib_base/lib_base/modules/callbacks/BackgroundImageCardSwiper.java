package com.bodhitech.it.lib_base.lib_base.modules.callbacks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bodhitech.it.lib_base.lib_base.BaseConstants;
import com.bodhitech.it.lib_base.lib_base.modules.utils.DesignUtils;
import com.bodhitech.it.lib_base.R;

public class BackgroundImageCardSwiper extends ItemTouchHelper.Callback {

    public interface IOnItemDismiss{
        void onItemDismiss(int position);
    }

    private static final float SWIPE_THRESHOLD = .7f;
    private static final float SWIPE_VELOCITY_THRESHOLD = 0.01f;
    private static final float SWIPE_ESCAPE_VELOCITY = 0.01f;

    private final IOnItemDismiss mCallback;
    private Context mContext;
    private ItemTouchHelper mTouchHelper;
    private RecyclerView.Adapter mAdapter;
    private Paint mPaint;
    private Bitmap mImage;

    public BackgroundImageCardSwiper(Context context, IOnItemDismiss callback, Bitmap image){
        mContext = context;
        mCallback = callback;
        mPaint = new Paint();
        mImage = image;
    }

    //region [#] Override ItemTouchHelper.Callback Methods
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(0x0, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
        mCallback.onItemDismiss(viewHolder.getBindingAdapterPosition());
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return SWIPE_THRESHOLD;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return SWIPE_VELOCITY_THRESHOLD;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return SWIPE_ESCAPE_VELOCITY;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View itemView = viewHolder.itemView;
            if (dX >= 0x0) {
                mPaint.setColor(mContext.getResources().getColor(R.color.colorAccent));
                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                c.drawRect(background, mPaint);
                c.drawBitmap(mImage, (float) itemView.getLeft() + DesignUtils.dpToPx(recyclerView.getContext(), 0x8),
                        (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - mImage.getHeight())/0x2, mPaint);
            } else {
                mPaint.setColor(mContext.getResources().getColor(R.color.colorAccent));
                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), itemView.getRight(), (float) itemView.getBottom());
                c.drawRect(background, mPaint);
                c.drawBitmap(mImage, (float) itemView.getRight() - DesignUtils.dpToPx(recyclerView.getContext(), 0x8) - mImage.getWidth(),
                        (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - mImage.getHeight())/0x2, mPaint);
            }
            /*
                #############################################################
                # Diminuisce l'alpha (trasparenza) di:                      #
                # [ abs(spostamento) / larghezza totale view ]              #
                #############################################################
                # if dX == view.width => dX / view.width = 1 => alpha = 0   #
                # quindi "alpha == 0" sse "dX == view.width"                #
                # oppure E! y | 1 - y = 0 con y = x / width <=> x = width   #
                #############################################################
            */
            final float alpha = BaseConstants.ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);

        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
    //endregion

}
