package com.bodhitech.it.lib_base.lib_base.modules.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Base64OutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bodhitech.it.lib_base.lib_base.BaseConstants;
import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class EncodingUtils {

    private static final String TAG = EncodingUtils.class.getSimpleName();
    // Hex Chars
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    //region [#] Public Static Methods
    //region [#] Bytes Methods
    public static long convertByteArrayToLong(byte[] byteArray){
        long total = 0x0L; int len = byteArray.length;
        for(int index = 0x0, i = (len - 0x1); index < len && i >= 0x0; index++, i--){
            long value = index > 0x0 ?
                    DataUtils.toUnsignedLong(byteArray[index]) :
                    byteArray[index];
            total += value * Math.pow(0x2, (i * 8));
        }
        return total;
    }
    //endregion

    //region [#] Hexadecimal Methods
    public static String fileToHex(String path) {
        File f = new File(path);
        if(!f.exists())
            return null;
        try(InputStream inputStream = new FileInputStream(f)){
            byte[] buffer = new byte[(int)f.length()];
            int value = 0;
            do{
                value = inputStream.read(buffer);
            }while(value != -1);
            String content = new String(buffer);
            return stringToHex(content);
        } catch (Exception e) {
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
        return null;
    }

    public static String stringToHex(String input){
        return asHex(input.getBytes());
    }

    public static String hexToString(String txtInHex){
        byte [] txtInByte = new byte [txtInHex.length() / 2];
        int j = 0;
        for (int i = 0; i < txtInHex.length(); i += 2){
            txtInByte[j++] = (byte) (Integer.parseInt(txtInHex.substring(i, i + 2), 16) & 0xff);
        }
        return new String(txtInByte);
    }
    //endregion

    //region [#] Base64 Encoding Utils
    public static boolean checkIsBase64(String base64){
        return TextUtils.isEmpty(DataUtils.applyRegexMultiline(base64, BaseConstants.REGEX_CHECK_BASE64_ENCODED, ""));
    }

    @Nullable
    public static Bitmap base64ToBitmap(String base64) {
        if (TextUtils.isEmpty(base64)) {
            return null;
        }
        try {
            byte[] base64converted = Base64.decode(base64, Base64.DEFAULT);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(base64converted, 0x0, base64converted.length, options);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeByteArray(base64converted, 0x0, base64converted.length, options);
        } catch (Exception e){
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
        return null;
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Boolean base64ToFile(String base64, String path, String fileName) {
        String pathComplete = '/' == new StringBuilder(path).reverse().toString().charAt(0) ? path + fileName : path + "/" + fileName;
        return base64ToFile(base64, pathComplete);
    }

    public static boolean base64ToFile(String base64, String pathComplete) {
        FilesUtils.checkMakeDirs(FilesUtils.getDirPathFromPath(pathComplete));
        boolean ret = false;
        if (!TextUtils.isEmpty(base64)) {
            File file = new File(pathComplete);
            byte[] content = Base64.decode(base64, 0x0);
            FilesUtils.writeBytesToFile(file, content);
            if(file.exists()){
                ret = true;
            }
        }
        return ret;
    }

    public static String fileToBase64(@NonNull String filePath) {
        String ret = null;
        byte[] buffer = new byte[8192];
        int bytesRead;
        try(ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
            Base64OutputStream b64OS = new Base64OutputStream(bAOS, Base64.DEFAULT);
            InputStream iS = new FileInputStream(filePath)){
            while ((bytesRead = iS.read(buffer)) != -0x1) {
                b64OS.write(buffer, 0x0, bytesRead);
            }
            b64OS.flush();
            ret = bAOS.toString();
        } catch (IOException ioE) {
            BaseEnvironment.onExceptionLevelLow(TAG, ioE);
        }
        return ret;
    }

    public static File base64ToTempFile(Context context, final String b64Data, String filename, String ext) {
        File ret = null;
        byte[] fileData = Base64.decode(b64Data, Base64.DEFAULT);
        try {
            ret = FilesUtils.writeBytesToFile(File.createTempFile(filename, ext, context.getCacheDir()), fileData);
        } catch (IOException ioE) {
            BaseEnvironment.onExceptionLevelLow(TAG, ioE);
        }
        return ret;
    }

    public static String base64EncodeFromString(String data){
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static String base64DecodeFromString(String b64){
        byte[] data = Base64.decode(b64, Base64.DEFAULT);
        return new String(data, StandardCharsets.UTF_8);
    }

    public static String base64EncodeFromBytes(byte[] bytes){
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static byte[] base64DecodeToBytes(String b64){
        return Base64.decode(b64, Base64.DEFAULT);
    }
    //endregion

    //region [#] Json Encoding Methods
    public static <T extends Object> Map<String, T> getMapFromJson(String json) {
        return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
    }

    public static <T extends Object> String getJsonFromMap(Map<String, T> map){
        return new GsonBuilder()
                .setLenient()
                .disableHtmlEscaping()
                .create()
                .toJson(map, Map.class);
    }

    public static <T extends Object> String getJsonWithNullsFromMap(Map<String, T> map){
        return new GsonBuilder()
                .setLenient()
                .serializeNulls()
                .disableHtmlEscaping()
                .create()
                .toJson(map, Map.class);
    }

    public static <T extends Object> String getJsonFromObject(T obj){
        return new Gson().toJson(obj);
    }
    //endregion

    //region [#] Serialization Methods
    public static <T> void serializeAndB64EncodeObjectToFile(@NonNull String fullPath, @NonNull T obj){
        String serialized = serializeAndB64EncodeObject(obj);
        if(!TextUtils.isEmpty(serialized)){
            FilesUtils.writeToFile(fullPath, serialized, false);
        }
    }

    public static <T> String serializeAndB64EncodeObject(@NonNull T obj){
        byte[] serialized = serializeObject(obj);
        return serialized.length > 0x0 ? EncodingUtils.base64EncodeFromBytes(serialized) : "";
    }

    public static <T> byte[] serializeObject(@NonNull T obj) {
        try (ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
             ObjectOutputStream oOS = new ObjectOutputStream(bAOS)){
            oOS.writeObject(obj);
            oOS.flush();
            return bAOS.toByteArray();
        } catch (IOException ioE){
            BaseEnvironment.onExceptionLevelLow(TAG, ioE);
        }
        return new byte[]{};
    }

    @Nullable
    public static Object deserializeB64EncodedObjectFromFile(@NonNull String fullPath){
        return FilesUtils.checkFileExist(fullPath) ? deserializeB64EncodedObject(FilesUtils.readFromFile(fullPath, false)) : null;
    }

    @Nullable
    public static Object deserializeB64EncodedObject(@NonNull String b64){
        return deserializeObject(EncodingUtils.base64DecodeToBytes(b64));
    }

    @Nullable
    public static Object deserializeObject(@NonNull byte[] obj){
        try (ObjectInputStream oIS = new ObjectInputStream(new ByteArrayInputStream(obj))){
            return oIS.readObject();
        } catch (IOException | ClassNotFoundException e) {
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
        return null;
    }
    //endregion
    //endregion

    //region [#] Private Static Methods
    //region [#] Hexadecimal Encoding Methods
    private static String asHex(byte[] buf){
        try{
            char[] chars = new char[0x2 * buf.length];
            for (int i = 0x0; i < buf.length; ++i)
            {
                chars[0x2 * i] = HEX_CHARS[(buf[i] >>> 0x4) & 0x0F];
                chars[0x2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
            }
            return new String(chars);
        } catch (Exception e){
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
        return null;
    }
    //endregion
    //endregion

}
