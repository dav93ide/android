package com.bodhitech.it.lib_base.lib_base.modules.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;

import com.bodhitech.it.lib_base.lib_base.BaseConstants;
import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;
import com.bodhitech.it.lib_base.R;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import me.echodev.resizer.Resizer;

public class ImageUtils {

    private static final String TAG = ImageUtils.class.getSimpleName();
    // Exceptions
    private static final String EXCEPTION_FILE_NOT_EXIST = "File \'%1$s\' doesn\'t exist.";
    private static final String EXCEPTION_FILE_TO_BITMAP_FAILED = "Fallita la conversione del file in bitmap";

    private static final String[] mAttributes = new String[]
            {
                    ExifInterface.TAG_APERTURE_VALUE,
                    ExifInterface.TAG_DATETIME,
                    ExifInterface.TAG_DATETIME_DIGITIZED,
                    ExifInterface.TAG_EXPOSURE_TIME,
                    ExifInterface.TAG_FLASH,
                    ExifInterface.TAG_FOCAL_LENGTH,
                    ExifInterface.TAG_GPS_ALTITUDE,
                    ExifInterface.TAG_GPS_ALTITUDE_REF,
                    ExifInterface.TAG_GPS_DATESTAMP,
                    ExifInterface.TAG_GPS_LATITUDE,
                    ExifInterface.TAG_GPS_LATITUDE_REF,
                    ExifInterface.TAG_GPS_LONGITUDE,
                    ExifInterface.TAG_GPS_LONGITUDE_REF,
                    ExifInterface.TAG_GPS_PROCESSING_METHOD,
                    ExifInterface.TAG_GPS_TIMESTAMP,
                    ExifInterface.TAG_IMAGE_LENGTH,
                    ExifInterface.TAG_IMAGE_WIDTH,
                    ExifInterface.TAG_RW2_ISO,
                    ExifInterface.TAG_MAKE,
                    ExifInterface.TAG_MODEL,
                    ExifInterface.TAG_SUBSEC_TIME,
                    ExifInterface.TAG_SUBSEC_TIME_DIGITIZED,
                    ExifInterface.TAG_SUBSEC_TIME_ORIGINAL,
                    ExifInterface.TAG_WHITE_BALANCE
            };

    /** Public Methods **/
    public static void setGreyScale(ImageView imageView) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(cf);
        imageView.setImageAlpha(128);   // 128 = 0.5
    }

    public static void resetFilter(ImageView imageView) {
        imageView.setColorFilter(null);
        imageView.setImageAlpha(255);
    }

    public static Bitmap fileToBitmap(String filePath) {
        File image = new File(filePath);
        if (!image.exists())
            return null;
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(image));
             DataInputStream dis = new DataInputStream(bis)){
            byte[] content = new byte[(int) image.length()];
            dis.readFully(content);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            return BitmapFactory.decodeByteArray(content, 0, content.length, options);
        } catch (Exception e) {
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
        return null;
    }

    public static Bitmap getBitmapFromDrawable (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmapFromResource(Context context, int idRes){
        return BitmapFactory.decodeResource(context.getResources(), idRes);
    }

    public static File bitmapToFile(Bitmap bitmap, String filePath) {
        return FilesUtils.storeBitmapFileJPEG(new File(filePath), bitmap);
    }

    public static File storeTempBitmapJPEG(Context context, String filename, Bitmap bitmap){
        File file = null;
        try{
            file = FilesUtils.storeBitmapFileJPEG(File.createTempFile(filename, "." + BaseConstants.EXT_JPEG, context.getCacheDir()), bitmap);
        } catch (IOException ioE) {
            BaseEnvironment.onExceptionLevelLow(TAG, ioE);
        }
        return file;
    }

    public static Bitmap cutBitmap(Bitmap originalBitmap, int srcLeft, int srcTop, int srcRight, int srcBottom){
        return cutBitmap(originalBitmap, new Rect(srcLeft, srcTop, srcRight, srcBottom));
    }

    public static Bitmap cutBitmap(Bitmap originalBitmap, Rect srcRect){
        Bitmap cutted = Bitmap.createBitmap(Math.abs(srcRect.left - srcRect.right), Math.abs(srcRect.top - srcRect.bottom), originalBitmap.getConfig());
        Canvas cutCanvas = new Canvas(cutted);
        Rect destRect = new Rect(0x0, 0x0, cutted.getWidth(), cutted.getHeight());
        cutCanvas.drawBitmap(originalBitmap, srcRect, destRect, null);
        return cutted;
    }

    /**
     *
     * @param context
     * @param sourcePath
     * @param destDirPath
     * @return Rotated & Resized Image or source
     */
    public static @NonNull File resizeImageAndRotateByExifInterfaceOrientation(Context context, @NonNull String sourcePath, @NonNull String destDirPath) {
        File rotateImage = null;
        try {
            rotateImage = rotateImageByExifInterfaceOrientation(sourcePath);
        } catch (Exception e) {
            BaseEnvironment.onExceptionLevelLow(TAG, e);
            Toast.makeText(context, R.string.error_rotate_foto, Toast.LENGTH_LONG).show();
        } catch (OutOfMemoryError e) {
            BaseEnvironment.onExceptionLevelLow(TAG, new Exception(e));
            Toast.makeText(context, R.string.error_out_of_memory_rotate_foto, Toast.LENGTH_LONG).show();
        }
        if (rotateImage == null) {
            rotateImage = new File(sourcePath);
        }
        return resizeImage(context, rotateImage, destDirPath, BaseConstants.EXT_JPEG.toUpperCase(), 1080, 100);
    }

    /**
     *
     * @param context
     * @param source
     * @param destDirPath
     * @param outFormat
     * @param targetLength
     * @param quality
     * @return Resized image or source.
     */
    @NonNull
    public static File resizeImage(@NonNull Context context, @NonNull File source, @NonNull String destDirPath, @NonNull String outFormat, int targetLength, int quality){
        try {
            File resizedImage = new Resizer(context)
                    .setTargetLength(targetLength)
                    .setQuality(quality)
                    .setOutputFormat(outFormat)
                    .setOutputDirPath(destDirPath)
                    .setSourceImage(source)
                    .getResizedFile();
            if (resizedImage != null && resizedImage.exists()) {
                return resizedImage;
            }
        } catch (Exception e) {
            BaseEnvironment.onExceptionLevelLow(TAG, e);
            Toast.makeText(context, R.string.error_resize_foto, Toast.LENGTH_LONG).show();
        } catch (OutOfMemoryError oomE) {
            BaseEnvironment.onExceptionLevelLow(TAG, new Exception(oomE));
            Toast.makeText(context, R.string.error_out_of_memory_resize_foto, Toast.LENGTH_LONG).show();
        }
        return source;
    }

    @NonNull
    public static File resizeJpegImage1080(@NonNull Context context, @NonNull File source, @NonNull String destDirPath){
        return resizeImage(context, source, destDirPath, BaseConstants.EXT_JPEG.toUpperCase(), 1080, 100);
    }

    public static File rotateImageByExifInterfaceOrientation(String filePath) throws IOException {
        ExifInterface ei = new ExifInterface(filePath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Bitmap bitmap = ImageUtils.fileToBitmap(filePath);
        Bitmap rotatedBitmap = null;
        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }
        File delete = new File(filePath);
        delete.delete();
        File res = ImageUtils.bitmapToFile(rotatedBitmap, filePath);
        if(bitmap != null) {
            bitmap.recycle();
        }
        if(rotatedBitmap != null) {
            rotatedBitmap.recycle();
        }
        return res;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0x0, 0x0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static String resizeImageGetPath(Context context, @NonNull String sourcePath, @NonNull String destDirPath) {
        File f = resizeImageAndRotateByExifInterfaceOrientation(context, destDirPath, sourcePath);
        return f.getPath();
    }

    public static Bitmap setImageToScale(Bitmap image, int width, int height) {
        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();
        int newWidth = 0;
        int newHeight = 0;
        if (imgWidth > imgHeight) {
            double perc = (double) width / (double) imgWidth;
            newHeight = Double.valueOf(Math.floor((double) imgHeight * perc)).intValue();
            newWidth = width;
        } else {
            double perc = (double) height / (double) imgHeight;
            newWidth = Double.valueOf(Math.floor((double) imgWidth * perc)).intValue();
            newHeight = height;
        }
        return Bitmap.createScaledBitmap(image, newWidth, newHeight, false);
    }

    public static boolean convertToPdf(Context context, String jpgFilePath, String outputPdfPath) {
        try {
            File inputFile = new File(jpgFilePath);
            if (!inputFile.exists()) {
                throw new Exception(String.format(EXCEPTION_FILE_NOT_EXIST, jpgFilePath));
            }
            Bitmap image = fileToBitmap(jpgFilePath);
            if (image == null) {
                throw new Exception(EXCEPTION_FILE_TO_BITMAP_FAILED);
            }
            File outputFile = new File(outputPdfPath);
            if (!outputFile.exists()){
                outputFile.createNewFile();
            }
            OutputStream os = new FileOutputStream(outputFile);
            PrintAttributes attributes = new PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build();
            PrintedPdfDocument doc = new PrintedPdfDocument(context, attributes);
            PdfDocument.Page page = doc.startPage(0x0);
            Bitmap newImage = setImageToScale(image, page.getCanvas().getWidth(), page.getCanvas().getHeight());
            image.recycle();
            page.getCanvas().drawBitmap(newImage, 0x0, 0x0, null);
            doc.finishPage(page);
            doc.writeTo(os);
            doc.close();
            newImage.recycle();
            return true;
        } catch (Exception e) {
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
        return false;
    }

    public static void copyExif(String oldPath, String newPath) throws IOException {
        setExif(newPath, getExif(oldPath));
    }


    public static ExifInterface getExif(String path) throws IOException {
        return new ExifInterface(path);
    }

    public static void setExif(String path, ExifInterface exifInterface) throws IOException {
        ExifInterface newExifInterface = new ExifInterface(path);
        for (int i = 0; i < mAttributes.length; i++) {
            String value = exifInterface.getAttribute(mAttributes[i]);
            if (value != null)
                newExifInterface.setAttribute(mAttributes[i], value);
        }
        newExifInterface.saveAttributes();
    }

}
