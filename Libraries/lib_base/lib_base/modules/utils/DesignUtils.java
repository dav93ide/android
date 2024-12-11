package com.bodhitech.it.lib_base.lib_base.modules.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bodhitech.it.lib_base.lib_base.BaseConstants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;
import com.bodhitech.it.lib_base.R;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DesignUtils {

    public interface ISimpleConfirmMaterialDialogCallback {
        void scmdOnConfirm(int requestCode);
        void scmdOnAbort(int requestCode);
    }

    private static final String TAG = DesignUtils.class.getSimpleName();
    // Resources
    private static final String RES_DRAWABLE = "drawable";
    // Notification Data
    private static final int MAX_LEN_NOTIFICATION_LINE = 45;
    // Constants Values
    private static final int INT_UNDEFINED = -0x1;

    //region [#] Public Static Methods
    //region [#] Dimensions Methods
    public static Integer dpToPx(@NonNull Context context, int dp){
        DisplayMetrics dM = context.getResources().getDisplayMetrics();
        return Math.round((float) dp * dM.density);
    }

    public static float calculateTextSize(@NonNull String text, float maxWidth){
        float size = 0x1;
        Paint paint = new Paint();
        while(paint.measureText(text) < maxWidth){
            paint.setTextSize(size++);
        }
        return --size;
    }

    public static float calculateTextSize(@NonNull String text, float maxHeight, float maxWidth){
        float size = 0x1;
        Paint paint = new Paint();
        Rect bounds = new Rect(0x0, 0x0, 0x0, 0x0);
        while(bounds.width() < maxWidth && bounds.height() < maxHeight){
            paint.setTextSize(size++);
            paint.getTextBounds(text, 0x0, text.length(), bounds);
        }
        return --size;
    }

    @Nullable
    public static Integer getActionBarSize(@NonNull Context context){
        return DataUtils.getAttributeDimensionPixelSize(context, android.R.attr.actionBarSize);
    }

    public static int getStatusBarHeight(@NonNull Activity activity){
        Rect rect = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }
    //endregion

    //region [#] Drawable Methods
    public static int getIdDrawableFromResourceName(Context context, String drawable){
        return context.getResources().getIdentifier(drawable, RES_DRAWABLE, context.getPackageName());
    }

    public static String getResourceNameDrawableFromId(Context context, @DrawableRes int id){
        return context.getResources().getResourceName(id);
    }
    //endregion

    //region [#] Snackbar Methods
    public static void showSnackbarTopBelowActionBar(@NonNull Activity activity, View view, String msg, int duration){
        Integer margin = DesignUtils.getActionBarSize(activity);
        if(margin == null){
            margin = 0x0;
        }
        margin += DesignUtils.getStatusBarHeight(activity);
        showSnackbar(view, msg, duration, Gravity.TOP, margin);
    }

    public static void showSnackbar(View view, String msg, int duration){
        Snackbar.make(view, msg, duration).show();
    }

    public static void showSnackbar(View view, String msg, int duration, int gravity){
        @SuppressLint("WrongConstant") Snackbar snack = Snackbar.make(view, msg, duration);
        View v = snack.getView();
        CoordinatorLayout.LayoutParams lp = v.getLayoutParams() != null ?
                new CoordinatorLayout.LayoutParams(v.getLayoutParams()) :
                new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = gravity;
        v.setLayoutParams(lp);
        snack.show();
    }

    public static void showSnackbar(View view, String msg, int duration, int gravity, int margin){
        @SuppressLint("WrongConstant") Snackbar snack = Snackbar.make(view, msg, duration);
        View v = snack.getView();
        CoordinatorLayout.LayoutParams lp = v.getLayoutParams() != null ?
                new CoordinatorLayout.LayoutParams(v.getLayoutParams()) :
                new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = gravity;
        switch (gravity){
            case Gravity.TOP:
                lp.topMargin = margin;
                break;
            default:
            case Gravity.CENTER:
                lp.setMargins(margin, margin, margin, margin);
                break;
            case Gravity.BOTTOM:
                lp.bottomMargin = margin;
                break;
        }
        v.setLayoutParams(lp);
        snack.show();
    }
    //endregion

    //region [#] Animations Methods
    public static void shakeView(@NonNull Context context, @NonNull View view){
        final Animation animation = AnimationUtils.loadAnimation(context, R.anim.shake);
        view.startAnimation(animation);
    }
    //endregion

    //region [#] LinearLayout Methods
    public static LinearLayout getLayoutDownloadItem(@NonNull Context context, @NonNull String text, @DrawableRes int drawable, @DimenRes int bttnSize, @DimenRes int txtSize, @ColorRes int resColor, @Nullable View.OnClickListener listener){
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ImageButton button = new ImageButton(context);
        button.setBackground(null);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                Math.round(context.getResources().getDimensionPixelSize(bttnSize)),
                Math.round(context.getResources().getDimensionPixelSize(bttnSize)));
        button.setLayoutParams(lp);
        button.setBackground(context.getDrawable(drawable));
        button.setFocusable(false);
        button.setClickable(false);
        ll.addView(button);
        TextView tv = new TextView(context);
        tv.setText(text);
        tv.setTextColor(ContextCompat.getColor(context, resColor));
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextSize(context.getResources().getDimension(txtSize));
        tv.setGravity(Gravity.CENTER_VERTICAL);
        ll.addView(tv);
        if(listener != null){
            ll.setOnClickListener(listener);
        }
        return ll;
    }
    //endregion

    //region [#] TextViews Methods
    public static View makeSimpleTextView(@NonNull Context context, @NonNull String text){
        TextView tv = new TextView(context);
        tv.setText(text);
        return tv;
    }

    public static View makeSpannedTextView(@NonNull Context context, @NonNull Spanned text){
        TextView tv = new TextView(context);
        tv.setText(text);
        return tv;
    }

    public static View makeHtmlTextView(@NonNull Context context, String html){
        return makeSpannedTextView(context, getHtmlText(html));
    }

    public static TextView getFormattedHtmlTextView(@NonNull Context context, int resString, Object... args){
        TextView tv = new TextView(context);
        tv.setText(getFormattedHtmlText(context, resString, args));
        return tv;
    }

    public static TextView getFormattedHtmlTextView(@NonNull Context context, String text, Object... args){
        TextView tv = new TextView(context);
        tv.setText(getFormattedHtmlText(text, args));
        return tv;
    }

    public static TextView getFormattedHtmlTextViewWithPadding(@NonNull Context context, int resString, @Px int[] padding, Object... args){
        TextView tv = getFormattedHtmlTextView(context, resString, args);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setPadding(padding[0x0], padding[0x1], padding[0x2], padding[0x3]);
        return tv;
    }
    //endregion

    //region [#] Text Methods
    public static Spanned getFormattedHtmlText(@NonNull Context context, int resString, Object... args){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                Html.fromHtml(context.getString(resString, args), Html.FROM_HTML_MODE_LEGACY) :
                Html.fromHtml(context.getString(resString, args));
    }

    public static Spanned getFormattedHtmlText(String formatString, Object... args){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                Html.fromHtml(String.format(formatString, args), Html.FROM_HTML_MODE_LEGACY) :
                Html.fromHtml(String.format(formatString, args));
    }

    public static Spanned getHtmlText(String html){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY) :
                Html.fromHtml(html);
    }
    //endregion

    //region [#] ListViews Methods
    public static ListView getStringsListViewWithHeader(@NonNull Context context, String txtHeader, @ColorRes int colorHeader, @DimenRes int sizeTxtHeader, @LayoutRes int idResLayoutAdapter, @NonNull List<String> items){
        TextView tvListHeader = new TextView(context);
        tvListHeader.setText(txtHeader);
        if(colorHeader != 0x0){
            tvListHeader.setTextColor(ContextCompat.getColor(context, colorHeader));
        }
        if(sizeTxtHeader != 0x0){
            tvListHeader.setTextSize(context.getResources().getDimensionPixelSize(sizeTxtHeader));
        }
        tvListHeader.setTypeface(null, Typeface.BOLD);
        ListView lvList = getStringsListView(context, idResLayoutAdapter, (List<String>) items);
        lvList.addHeaderView(tvListHeader);
        return lvList;
    }

    public static ListView getStringsListView(@NonNull Context context, @LayoutRes int idResLayoutAdapter, @NonNull List<String> itemsList){
        ListView lvList = new ListView(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, idResLayoutAdapter);
        lvList.setAdapter(adapter);
        adapter.addAll(itemsList);
        adapter.notifyDataSetChanged();
        return lvList;
    }
    //endregion

    //region [#] ImageViews Methods
    public static void initImageViewGlideStoredOrDefault(@NonNull ImageView iv, String url, String path, int defaultResID, boolean downloadWithGlide){
        if(downloadWithGlide && !TextUtils.isEmpty(url) && EnvironmentUtils.isConnected(iv.getContext())){
            initImageViewGlide(iv, url, path, defaultResID);
        } else if(!TextUtils.isEmpty(path) && new File(path).exists()) {
            iv.setImageBitmap(BitmapFactory.decodeFile(path));
        } else if(defaultResID != INT_UNDEFINED){
            iv.setImageResource(defaultResID);
        }
    }

    private static void initImageViewGlide(@NonNull ImageView iv, @NonNull String url, @NonNull String path, int defaultResID){
        Glide.with(iv.getContext())
                .asBitmap()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        if(!TextUtils.isEmpty(path) && new File(path).exists()) {
                            iv.setImageBitmap(BitmapFactory.decodeFile(path));
                        } else if(defaultResID != INT_UNDEFINED){
                            iv.setImageResource(defaultResID);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        if(dataSource.equals(DataSource.REMOTE) && !TextUtils.isEmpty(path)){
                            FilesUtils.checkMakeDirs(path);
                            FilesUtils.storeBitmapFilePNG(new File(path), resource);
                        }
                        return false;
                    }
                }).into(iv);
    }
    //endregion

    //region [#] Dialogs Methods
    public static <T extends Object> void makeSimpleConfirmMaterialDialog(@NonNull Context context, @NonNull String title, @NonNull String text, final int requestCode, @NonNull final ISimpleConfirmMaterialDialogCallback callback){
        View dialogTextView = makeSimpleTextView(context, text);
        new MaterialDialog.Builder(context)
                .title(title)
                .customView(dialogTextView, true)
                .cancelable(false)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive(
                        (dialog, which) -> {
                            callback.scmdOnConfirm(requestCode);
                            dialog.dismiss();
                        }
                )
                .onNegative(
                        (dialog, which) -> {
                            callback.scmdOnAbort(requestCode);
                            dialog.dismiss();
                        }
                )
                .autoDismiss(false)
                .build()
                .show();
    }

    public static ProgressDialog makeSimpleProgressDialog(@NonNull Context context, @NonNull String title, @NonNull String text, boolean cancelable){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(text);
        progressDialog.setCancelable(cancelable);
        return progressDialog;
    }
    //endregion

    //region [#] Notifications Methods
    public static List<String> getLinesNotificationMessage(@NonNull String msg){
        ArrayList<String> lines = new ArrayList<>();
        String[] splitted = msg.split("[ ]");
        if(splitted.length > 0x0){
            String line = "";
            for(String split : splitted){
                line = line + " " + split;
                if(line.length() > MAX_LEN_NOTIFICATION_LINE){
                    String[] temp = line.split("[ ]");
                    line = temp[temp.length - 0x1];
                    String lastLine = TextUtils.join(" ", ArrayUtils.remove(temp, (temp.length - 0x1)));
                    lines.add(lastLine);
                } else if(line.length() == MAX_LEN_NOTIFICATION_LINE){
                    lines.add(line);
                    line = "";
                }
            }
            if(line.length() > 0x0){
                lines.add(line);
            }
        }
        return lines;
    }

    public static List<String> getLinesNotificationHtmlMessage(@NonNull String msg){
        ArrayList<String> lines = new ArrayList<>();
        String[] openedTags = new String[]{};
        String[] splitted = msg.trim().split("[ ]");
        StringBuilder head = new StringBuilder();
        StringBuilder tail;
        if(splitted.length > 0x0){
            StringBuilder line = new StringBuilder(); String original = "";
            for(String split : splitted){
                original = original + " " + split;
                String replaced = split.replaceAll(BaseConstants.REGEX_REMOVE_HTML_TAGS, "");
                if(!TextUtils.isEmpty(replaced)) {
                    line.append(" ").append(replaced);
                    if(line.length() >= MAX_LEN_NOTIFICATION_LINE) {
                        String[][] tags = findHtmlTags(original.replaceFirst(head.toString(), ""));
                        String[] opened = tags[0x0];
                        String[] closed = tags[0x1];
                        if(closed.length > 0x0){
                            for (String s : closed) {
                                String oTag = s.replace("/", "");
                                if (ArrayUtils.contains(openedTags, oTag)) {
                                    openedTags = ArrayUtils.removeElement(openedTags, oTag);
                                }
                            }
                        }
                        openedTags = ArrayUtils.addAll(openedTags, opened);
                        head = new StringBuilder(); tail = new StringBuilder();
                        if(openedTags.length > 0x0){
                            for(String op : openedTags){
                                head.append(op);
                                tail.append(String.format("%1$c/%2$s", op.charAt(0x0), op.substring(0x1)));
                            }
                        }
                        if(line.length() > MAX_LEN_NOTIFICATION_LINE) {
                            String[] oTemp = original.split("[ ]");
                            String[] rTemp = line.toString().split("[ ]");
                            line = new StringBuilder(rTemp[rTemp.length - 0x1]);
                            int i = 0x1; original = "";
                            while (TextUtils.isEmpty(original.replaceAll(BaseConstants.REGEX_REMOVE_HTML_TAGS, ""))) {
                                original = oTemp[oTemp.length - i] + original;
                                oTemp = ArrayUtils.remove(oTemp, (oTemp.length - i));
                                i++;
                            }
                            String[][] checkTags = findHtmlTags(original);
                            String[] checkOpened = checkTags[0x0];
                            String[] checkClosed = checkTags[0x1];
                            if(checkOpened.length > 0x0){
                                for (String s : checkOpened) {
                                    head = new StringBuilder(head.toString().replaceFirst(s, ""));
                                    tail = new StringBuilder(tail.toString().replaceFirst(String.format("%1$c/%2$s", s.charAt(0x0), s.substring(0x1)), ""));
                                }
                            }
                            if(checkClosed.length > 0x0){
                                for (String s : checkClosed) {
                                    head.append(s.replace("/", ""));
                                    tail.append(s);
                                }
                            }
                            original = head + original;
                            lines.add(TextUtils.join(" ", oTemp) + tail);
                        } else {
                            lines.add(original + tail);
                            line = new StringBuilder(); original = head.toString();
                        }
                    }
                }
            }
            if(original.length() > 0x0){
                lines.add(original);
            }
        }
        return lines;
    }

    public static NotificationCompat.Style getMultiLineNotificationStyle(@Nullable String title, @Nullable String summary, @Nullable String... lines){
        NotificationCompat.InboxStyle mStyle = new NotificationCompat.InboxStyle();
        if(title != null) {
            mStyle.setBigContentTitle(title);
        }
        if(summary != null){
            mStyle.setSummaryText(summary);
        }
        if(lines != null && lines.length > 0x0){
            for(String s : lines){
                mStyle.addLine(Html.fromHtml(s));
            }
        }
        return mStyle;
    }
    //endregion

    //region [#] Keyboard Methods
    public static void hideKeyboardFromView(@NonNull Context context, @NonNull View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0x0);
        }
    }
    //endregion

    //region [#] Toast Methods
    public static void showCustomToast(@NonNull Activity activity, @LayoutRes int resLayout, @NonNull String msg, int duration){
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(resLayout, activity.findViewById(android.R.id.content), false);

        TextView text = layout.findViewById(R.id.text_view);
        text.setText(msg);

        Toast toast = new Toast(activity.getApplicationContext());
        toast.setGravity(Gravity.BOTTOM | Gravity.LEFT | Gravity.FILL_HORIZONTAL, 0x0, 0x0);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }

    public static void showCustomToast(@NonNull Activity activity, @LayoutRes int resLayout, @NonNull String msg, int duration, int gravity){
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(resLayout, activity.findViewById(android.R.id.content), false);

        TextView text = layout.findViewById(R.id.text_view);
        text.setText(msg);

        Toast toast = new Toast(activity.getApplicationContext());
        toast.setGravity(gravity, 0x0, 0x0);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }
    //endregion
    //endregion

    //region [#] Private Static Methods
    private static String[][] findHtmlTags(@NonNull String text){
        String[][] found = new String[0x2][0x0];
        for(int i = 0x0; i < text.toCharArray().length; i++){
            char c = text.toCharArray()[i];
            if(c == '<'){
                String sub = text.substring(i);
                int index = findIndexClosingTag(sub);
                if(index != INT_UNDEFINED) {
                    index += i;
                    String tag = text.substring(i, (index + 0x1));
                    i = index;
                    if (!tag.contains("/")) {
                        found[0x0] = ArrayUtils.add(found[0x0], tag);
                    } else {
                        tag = tag.replace("/", "");
                        if (ArrayUtils.contains(found[0x0], tag)) {
                            found[0x0] = ArrayUtils.removeElement(found[0x0], tag);
                        } else {
                            found[0x1] = ArrayUtils.add(found[0x1], tag);
                        }
                    }
                } else {
                    break;
                }
            }
        }
        return found;
    }

    private static int findIndexClosingTag(@NonNull String text){
        for(int i = 0x0; i < text.toCharArray().length; i++){
            char c = text.toCharArray()[i];
            if(c == '>'){
                return i;
            }
        }
        return INT_UNDEFINED;
    }
    //endregion

}
