package com.emax.blesensors_testing_2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileUtils {

    private static final String LOGCAT = FileUtils.class.getSimpleName();
    private static final String STAMPS_DIR_CHECK = "STAMPS";

    public static void writeToFile(Context context, String fileName, String dirPath, String fileData, Boolean append) {
        checkMakeDirs(context, dirPath);
        File dir = new File(context.getFilesDir(), dirPath);
        File file = new File(dir, fileName);
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, append)));
            out.write(fileData.toString());
            out.flush();
            out.close();
        } catch (IOException ioe) {
            Log.e(LOGCAT, "writeToFile - IOException: " + ioe.toString());
            ioe.getStackTrace();
        }
    }

    public static String readFromFile(Context context, String fileName, String dirPath, Boolean newRows) {
        String ret = "";
        if(checkFileAndDirExists(context, fileName, dirPath)) {
            File path = new File(context.getFilesDir(), dirPath);
            File file = new File(path, fileName);
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    if (newRows) {
                        text.append('\n');
                    }
                }
                br.close();
                ret = text.toString();
            } catch (IOException ioe) {
                ioe.getStackTrace();
                Log.e(LOGCAT, "readFromFile - IOException: " + ioe.toString());
            }
        }
        return ret;
    }

    public static void checkMakeDirs(Context context, String dirPath) {
        String fullPath = "";
        String[] dirs = dirPath.split("/");
        for (String path : dirs) {
            fullPath += '/' + path;
            File dir = new File(context.getFilesDir(), fullPath);
            if (!dir.exists()) {
                dir.mkdir();
            }
        }
    }

    public static Boolean checkFileAndDirExists(Context context, String fileName, String dirPath){
        Boolean exists = false;
        File dir = new File(context.getFilesDir(), dirPath);
        if(dir.exists()){
            File file = new File(dir, fileName);
            if(file.exists()){
                exists = true;
            }
        }
        return exists;
    }

    public static String getStringListOfFiles(Context context, String dirPath){
        File[] files = getFilesList(context, dirPath);
        String list = "Files: " + files.length + "\n";
        for(File file : files){
            if(file.isDirectory()){
                list += "Dir: " + file.getName() + "\n";
            } else {
                list += "File: " + file.getName() + "\n";
            }
        }
        return list;
    }

    public static File[] getFilesList(Context context, String dirPath){
        File dir = new File(context.getFilesDir(), dirPath);
        File[] files = dir.listFiles();
        return files;
    }

    private static Integer getIntFromMonthFileName(String monthFileName){
        return Integer.parseInt(monthFileName.replaceAll(".*.(?:_)+([0-9]+)+(?:.month)", "$1"));
    }

    public static ArrayList<ArrayList<?>> getUsersManagerList(Context context, String dirPath){
        File[] files = getFilesList(context, dirPath);
        ArrayList<ArrayList<?>> arrUsers = new ArrayList<>();
        ArrayList<String> usernames = new ArrayList<>();
        ArrayList<Long> sizes = new ArrayList<>();
        ArrayList<String> paths = new ArrayList<>();
        Long totLength = 0l;
        for(File file : files){
            if(file.isDirectory()){
                usernames.add(file.getName());
                paths.add(file.getAbsolutePath());
                sizes.add(getFileSizeRecursive(totLength, file));
            }
        }
        arrUsers.add(usernames);
        arrUsers.add(sizes);
        arrUsers.add(paths);
        return arrUsers;
    }

    public static Long getFileSizeRecursive(Long size, File file){
        Long ret = 0l;
        if(file.isDirectory()){
            File[] children = file.listFiles();
            for(File child : children){
                ret += getFileSizeRecursive(size, child);
            }
        } else {
            return file.length();
        }
        return ret;
    }

    public static void removeFiles(Context context, String path){
        File file = new File(path);
        if( file.isDirectory() ){
            File[] files = file.listFiles();
            if(files != null) {
                if (files.length >= 1) {
                    for (File f : files) {
                        removeFiles(context, f.getAbsolutePath());
                    }
                }
                file.delete();
            }
        } else {
            file.delete();
        }
        String dirPath = getParentDir(path);
        if(!checkDirHasSons(dirPath) && checkCanDeleteDir(path)){
            removeFiles(context, dirPath);
        }
    }

    public static String getParentDir(String path){
        return path.replaceAll("(\\/.*.\\/)+(?:.*.)", "$1");
    }

    public static Boolean checkCanDeleteDir(String path){
        Boolean ret = true;
        String dirName = path.replaceAll(".*.+(?:\\/)+(.*.)+(?:\\/)", "$1");
        if( dirName.equalsIgnoreCase(STAMPS_DIR_CHECK) ){
            ret = false;
        }
        return ret;
    }

    public static Boolean checkDirExist(String dirPath){
        Boolean ret = false;
        File file = new File(dirPath);
        if(file != null){
            ret = true;
        }
        return ret;
    }

    public static Boolean checkDirHasSons(String dirPath){
        Boolean ret = false;
        if(checkDirExist(dirPath)) {
            File dir = new File(dirPath);
            File[] files = dir.listFiles();
            if( files != null && files.length >= 1){
                ret = true;
            }
        }
        return ret;
    }

    public static Boolean checkDirHasSons(Context context, String dirPath){
        Boolean ret = false;
        if(checkDirExist(dirPath)) {
            File dir = new File(context.getFilesDir(), dirPath);
            File[] files = dir.listFiles();
            if( files != null && files.length >= 1){
                ret = true;
            }
        }
        return ret;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
