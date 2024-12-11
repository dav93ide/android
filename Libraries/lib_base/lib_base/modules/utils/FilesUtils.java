package com.bodhitech.it.lib_base.lib_base.modules.utils;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;

import com.bodhitech.it.lib_base.lib_base.BaseConstants;
import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;
import com.bodhitech.it.lib_base.lib_base.ui.dialogs.PdfViewerDialog;
import com.bodhitech.it.lib_base.R;

import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.MediaType;

import static android.content.Context.DOWNLOAD_SERVICE;

public class FilesUtils {

    private static final String TAG = FilesUtils.class.getSimpleName();
    private static final String TAG_DIALOG = TAG + ".dialog";

    //region [#] Public Methods
    //region [#] Formatter Methods
    public static String formatFilepath(@NonNull String... relativePaths){
        return relativePaths.length > 0x1 ?
                (relativePaths[0x0].charAt(relativePaths[0x0].length() - 0x1) != '/' && relativePaths[0x1].charAt(0x0) != '/' ?
                        String.format(BaseConstants.PATH_FORMAT, relativePaths[0x0], formatFilepath(ArrayUtils.subarray(relativePaths, 0x1, relativePaths.length))) :
                        (relativePaths[0x0].charAt(relativePaths[0x0].length() - 0x1) == '/' && relativePaths[0x1].charAt(0x0) == '/' ?
                                relativePaths[0x0].substring(0x0, relativePaths[0x0].length() - 0x1) :
                                relativePaths[0x0]) + formatFilepath(ArrayUtils.subarray(relativePaths, 0x1, relativePaths.length)))
                : relativePaths[0x0];
    }

    public static String formatFilepathWithExt(@NonNull String ext, @NonNull String... relativePaths){
        return String.format(BaseConstants.PATH_EXT_FORMAT, formatFilepath(relativePaths), ext);
    }
    //endregion

    //region [#] Open Document Activity Methods
    public static String onActivityResultOpenDocument(Context context, Intent data){
        String selectedPath, fileName;
        Uri uri = data.getData();
        String mimeType = context.getContentResolver().getType(uri);
        if (mimeType == null) {
            String path = getPathFromOpenDocumentUri(context, uri);
            if (path == null) {
                fileName = FilenameUtils.getName(uri.toString());
            } else {
                File file = new File(path);
                fileName = file.getName();
            }
        } else {
            Uri returnUri = data.getData();
            Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            fileName = returnCursor.getString(nameIndex);
        }
        String sourcePath = context.getExternalFilesDir(null).toString();
        selectedPath = formatFilepath(sourcePath, fileName);
        File fileSave = new File(selectedPath);
        try {
            copyUriStreamToFile(context, uri, fileSave);
        } catch (Exception e) {
            BaseEnvironment.onExceptionLevelLow(TAG, e);
            Toast.makeText(context, R.string.error_impossibile_recuperare_file, Toast.LENGTH_SHORT).show();
            selectedPath = null;
        }
        return selectedPath;
    }
    //endregion

    //region [#] Bitmap Methods
    public static File storeBitmapFileJPEG(File dest, Bitmap bitmap){
        return storeBitmapFile(dest, bitmap, Bitmap.CompressFormat.JPEG);
    }

    public static File storeBitmapFilePNG(File dest, Bitmap bitmap){
        return storeBitmapFile(dest, bitmap, Bitmap.CompressFormat.PNG);
    }

    public static Bitmap getBitmapFromFile(String path){
        return BitmapFactory.decodeFile(path);
    }
    //endregion

    //region [#] Read & Write Files Methods
    public static String readFromFile(String fullPath, boolean newRows) {
        String ret = null;
        if(checkFileExist(fullPath)) {
            File file = new File(fullPath);
            if(file.isFile() && !file.isDirectory()) {
                StringBuilder text = new StringBuilder();
                try (BufferedReader bR = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = bR.readLine()) != null) {
                        text.append(line);
                        if (newRows) {
                            text.append('\n');
                        }
                    }
                    ret = text.toString();
                } catch (IOException ioE) {
                    BaseEnvironment.onExceptionLevelLow(TAG, ioE);
                }
            }
        }
        return ret;
    }

    public static void writeToFile(String fullPath, String fileData, boolean append) {
        makeDirs(getDirPathFromPath(fullPath));
        File file = new File(fullPath);
        try (PrintWriter pW = new PrintWriter(new BufferedWriter(new FileWriter(file, append)))){
            if(file.exists() || file.createNewFile()) {
                pW.write(fileData);
                pW.flush();
            }
        } catch (IOException ioE) {
            BaseEnvironment.onExceptionLevelLow(TAG, ioE);
        }
    }

    public static boolean storeFile(File source, String fullPath){
        File stored = new File(fullPath);
        if(checkMakeDirs(getDirPathFromPath(fullPath))) {
            try (InputStream is = new BufferedInputStream(new FileInputStream(source));
                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 FileOutputStream fos = new FileOutputStream(stored);
                 BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                if(stored.exists() || stored.createNewFile()) {
                    writeToBufferedOutputStream(is, baos, bos);
                    return true;
                }
            } catch (IOException ioE) {
                BaseEnvironment.onExceptionLevelLow(TAG, ioE);
            }
        }
        return false;
    }

    public static void storeFileFromTempFileUri(Context context, @NonNull Uri uri, String filepath){
        if(uri.getPath() != null) {
            File mFile = new File(uri.getPath());
            if (checkFileExist(filepath)) {
                filepath = changeFilenameOfAbsPath(filepath);
            }
            if (FilesUtils.storeFile(mFile, filepath)) {
                Toast.makeText(context, context.getString(R.string.success_saved_element, context.getString(R.string.label_of_file, getFilenameFromPath(filepath))), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, context.getString(R.string.error_saving_element, context.getString(R.string.label_of_file)), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static File writeBytesToFile(File dest, byte[] data){
        try(FileOutputStream fOS = new FileOutputStream(dest);
            BufferedOutputStream bOS = new BufferedOutputStream(fOS)){
            if(dest.exists() || dest.createNewFile()){
                bOS.write(data);
                bOS.flush();
            }
        } catch(FileNotFoundException fnfE){
            BaseEnvironment.onExceptionLevelLow(TAG, fnfE);
        } catch(IOException ioE){
            BaseEnvironment.onExceptionLevelLow(TAG, ioE);
        }
        return dest;
    }

    public static boolean copyUriStreamToFile(Context context, Uri uriOrig, File fileDest) {
        try (InputStream is = context.getContentResolver().openInputStream(uriOrig);
             OutputStream os = new FileOutputStream(fileDest)) {
            writeToOutputStream(is, os);
            return true;
        } catch (Exception e) {
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
        return false;
    }

    public static boolean writeToFileFromInputStream(InputStream is, String path){
        boolean ret = false;
        makeDirs(getDirPathFromPath(path));
        File file = new File(path);
        try (FileOutputStream fos = new FileOutputStream(file)){
            if(!file.exists()){
                file.createNewFile();
            }
            writeToOutputStream(is, fos);
            is.close();
            ret = true;
        } catch(IOException ioE){
            BaseEnvironment.onExceptionLevelLow(TAG, ioE);
        }
        return ret;
    }
    //endregion

    //region [#] Check, Move & Delete Files Methods
    public static boolean checkFileExist(String fullPath){
        return (new File(fullPath)).exists();
    }

    public static boolean deleteFile(String path){
        try {
            File f = new File(path);
            if(f.exists()){
                return f.delete();
            }
        } catch (Exception e){
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
        return false;
    }

    public static boolean checkMakeDirs(String dirPath){
        try {
            File dir = new File(dirPath);
            return dir.exists() || dir.mkdirs();
        } catch (Exception e) {
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
        return false;
    }

    public static void makeDirs(String dirPath){
        try {
            File dir = new File(dirPath);
            if(!dir.exists()){
                dir.mkdirs();
            }
        } catch (Exception e){
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
    }

    public static boolean copyAndDeleteFile(@NonNull File source, @NonNull File dest){
        boolean ret = copyFile(source, dest);
        source.delete();
        return ret;
    }

    /**
     * Copy a File to a destination. (doesn't work for directories)
     * @param source    file to copy
     * @param dest      destination
     * @return  true if copy success, false otherwise
     */
    public static boolean copyFile(@NonNull File source, @NonNull File dest){
        if(source.exists() && !source.isDirectory()){
            checkMakeDirs(dest.getParent());
            try (FileInputStream fis = new FileInputStream(source);
                 FileOutputStream fos = new FileOutputStream(dest)){
                if(!dest.exists()){
                    dest.createNewFile();
                }
                writeToOutputStream(fis, fos);
                return true;
            } catch (IOException ioE){
                BaseEnvironment.onExceptionLevelLow(TAG, ioE);
            }
        }
        return false;
    }

    public static boolean copyAndDeleteDir(@NonNull File source, @NonNull File dest){
        boolean ret = copyDir(source, dest);
        deleteDirAndContents(source);
        return ret;
    }

    /**
     * Copy a directory, with his contents, to a destination.
     * @param source    dir to copy
     * @param dest      destination
     * @return  true if copy success, false otherwise
     */
    public static boolean copyDir(@NonNull File source, @NonNull File dest){
        if(source.exists() && source.isDirectory()){
            checkMakeDirs(dest.getParent());
            if(source.listFiles() != null && source.listFiles().length > 0x0){
                for(File child : source.listFiles()){
                    if(child.isDirectory()){
                        copyDir(child, new File(dest, child.getName()));
                    } else {
                        copyFile(child, new File(dest, child.getName()));
                    }
                }
            } else {
                new File(dest, source.getName()).mkdir();
            }
            return true;
        }
        return false;
    }

    /**
     * Moves a file by trying using different methods when one of them fails. <p>
     *
     * @param source - source file
     * @param dest - destination file
     * @return true if success, false otherwise.
     */
    public static boolean moveFile(@NonNull File source, @NonNull File dest){
        return renameFileTo(source, dest) ||
                ((source.isDirectory() && copyAndDeleteDir(source, dest)) || copyAndDeleteFile(source, dest));
    }

    public static boolean renameFileTo(@NonNull File source, @NonNull File dest){
        /*
            IMPORTANTE "renameTo":
                Many aspects of the behavior of this method are inherently platform-dependent:
                The rename operation might not be able to move a file from one filesystem to
                another, it might not be atomic, and it might not succeed if a file with the
                destination abstract pathname already exists.
         */
        return source.renameTo(dest);
    }

    public static boolean transferFileTo(@NonNull File source, @NonNull File dest){
        try (FileChannel in = new FileInputStream(source).getChannel();
             FileChannel out = new FileOutputStream(dest).getChannel()){
            in.transferTo(0x0, in.size(), out);
            return true;
        } catch (FileNotFoundException fnfE){
            BaseEnvironment.onExceptionLevelLow(TAG, fnfE);
        } catch (IOException ioE){
            BaseEnvironment.onExceptionLevelLow(TAG, ioE);
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean moveFileTo(@NonNull File source, @NonNull File dest){
        try {
            Files.move(source.toPath(), dest.toPath());
            return true;
        } catch (IOException ioE) {
            BaseEnvironment.onExceptionLevelLow(TAG, ioE);
        }
        return false;
    }

    public static void deleteDirAndContents(@NonNull File dir){
        if (dir.isDirectory() && dir.listFiles() != null && dir.listFiles().length > 0x0) {
            for (File file : dir.listFiles()) {
                deleteDirAndContents(file);
            }
        }
        dir.delete();
    }

    public static void deleteDirContents(@NonNull File dir) {
        if (dir.isDirectory() && dir.listFiles() != null && dir.listFiles().length > 0x0) {
            for (File file : dir.listFiles()) {
                deleteDirAndContents(file);
            }
        }
    }

    public static boolean removeFile(String filePath){
        File file = new File(filePath);
        file.delete();
        return !file.exists();
    }
    //endregion

    //region [#] Download, Store & Open Files Methods
    public static void downloadAndOpenFile(Context context, String url, String token, String path, String relativePath) {
        downloadAndOpenFile(context, url, token, path, relativePath, null);
    }

    public static void downloadAndOpenFile(Context context, String url, String token, String path, String relativePath, String... headers) {
        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        context.registerReceiver(new DownloadAndOpenFileReceiver(downloadManager),
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        if (headers != null && headers.length % 0x2 == 0x0) {
            for(int i=0x0; i < headers.length; i+=0x2){
                request.addRequestHeader(headers[i], headers[i+0x1]);
            }
        }
        File file = new File(path);
        if(!file.exists()) {
            /*
                "setDestinationInExternalFilesDir()" aggiunge come base path la cartella dedicata dell'app al percorso specificato in "relativePath".
                Nello specifico il base path utilizzato da "setDestinationInExternalFilesDir()" e`:
                    /storage/emulated/0/Android/data/[application_package]/files/
            */
            request.setDestinationInExternalFilesDir(context, null, relativePath);
            downloadManager.enqueue(request);
        } else {
            FilesUtils.openStoredFile(context, file);
        }
    }

    public static void storeAndOpenDownloadedFile(Context context, String ext, String attachmentB64, String dialogTag, String title, String filepath){
        switch(ext){
            case BaseConstants.EXT_PDF:
            case BaseConstants.EXT_JPG:
            case BaseConstants.EXT_PNG:
            case BaseConstants.EXT_JPEG:
            case BaseConstants.EXT_ZIP:
            case BaseConstants.EXT_RAR:
            case BaseConstants.EXT_DOC:
            default:
                File file = storeDownloadedFile(context, filepath, attachmentB64);
                if(file.exists()){
                    openStoredFile(context, file, dialogTag, title);
                }
                break;
        }
    }

    public static void openStoredFile(Context context, @NonNull String path){
        File file = new File(path);
        if(file.exists()) {
            openStoredFile(context, file, TAG_DIALOG, context.getString(R.string.label_no_title));
        } else {
            Toast.makeText(context, context.getString(R.string.label_no_element_found_m, context.getString(R.string.label_file)), Toast.LENGTH_SHORT);
        }
    }

    public static void openStoredFile(Context context, File file){
        openStoredFile(context, file, TAG_DIALOG, context.getString(R.string.label_no_title));
    }

    /**
     * openStoredFile - Will open PDF files with internal "PDF Viever".
     * @param context
     * @param file
     * @param tag
     * @param title
     */
    public static void openStoredFile(Context context, File file, String tag, String title){
        if(!startActivityOpenFile(context, file.getPath())){
            String ext = file.getName().replaceAll(BaseConstants.REGEX_EXT_FROM_FILENAME, "$2");
            if(ext.equals(BaseConstants.EXT_PDF)){
                PdfViewerDialog pdfDialog = PdfViewerDialog.initInstance(Uri.fromFile(file), title);
                FragmentManager fM = ((AppCompatActivity) context).getSupportFragmentManager();
                pdfDialog.show(fM, tag);
            }
        }
    }

    /**
     * viewAttachment - Won't open PDF files with internal "PDF Viewer".
     * @param context
     * @param path
     */
    public static void viewAttachment(Context context, String path) {
        startActivityOpenFile(context, path);
    }
    //endregion

    /**
     * Add incremental number between brackets at the end of the file name.
     * @param fullPath
     * @return
     */
    public static String changeFilenameOfAbsPath(String fullPath){                  // TODO: UTILE o INUTILE? se INUTILE toglierlo!
        String relativePath = getDirPathFromPath(fullPath);
        String filename = getFilenameFromPath(fullPath);
        String ext = getExtensionFromFilePathOrName(filename);
        File dir = new File(relativePath);
        if(dir.exists()){
            if(dir.isDirectory()){
                int count = 0x0;
                for(File file : dir.listFiles()){
                    String name = getNotIncrementedFilenameWithExt(file.getName());
                    if (name.equals(filename)) {
                        count++;
                    }
                }
                filename = String.format(BaseConstants.PATH_EXT_FORMAT,
                        getFilePathOrNameWithoutExt(filename).concat(count > 0x0 ? String.format("_(%1$s)", count) : ""), ext);
            }
            fullPath = relativePath + filename;
        }
        return fullPath;
    }

    public static String getFileMimeType(@NonNull URI fileUri){
        return getFileMimeType(fileUri.toString());
    }

    public static String getFileMimeType(@NonNull String filename) {
        String mimeType = "*/*";
        String extension = MimeTypeMap.getFileExtensionFromUrl(filename);
        if (extension != null && !extension.trim().isEmpty()) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        } else {
            extension = FilesUtils.getExtensionFromFilePathOrName(filename);
            if(!extension.trim().isEmpty()){
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
        }
        if(TextUtils.isEmpty(mimeType)){
            mimeType = BaseConstants.CONTENT_TYPE_PLAIN_TEXT;
        }
        return mimeType;
    }

    public static String getNewFilepathWithCurrentTimeAsFilename(@NonNull Context context, String ext) {
        return FilesUtils.formatFilepathWithExt(ext, EnvironmentUtils.getPathApplicationExternalFilesDir(context), DateUtils.getCurrentDateString(BaseConstants.DATETIME_FORMAT_FILE));
    }

    public static String getRandomTempFilename(){
        return String.format(BaseConstants.FILENAME_TEMP_FILE, DateUtils.getCurrentDateString(BaseConstants.DATETIME_FORMAT_FILE), Math.round(Math.random() * 100));
    }

    public static MediaType getFileMediaType(URI fileUri){
        return MediaType.parse(getFileMimeType(fileUri));
    }

    public static String getFormattedFullFilepath(String... relativePaths){
        StringBuilder sBuilder = new StringBuilder();
        int i = 1;
        for(String item : relativePaths){
            sBuilder.append("%").append(i).append("$s");
            if(i < relativePaths.length){
                sBuilder.append("/");
            }
            i++;
        }
        return String.format(sBuilder.toString(), relativePaths);
    }

    @Nullable
    public static String getFilepathFromUri(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)){
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                BaseEnvironment.onExceptionLevelLow(TAG, e);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Genera URI di un file gemerato per salvare dei dati (ad esempio per un'immagine "jpg").
     * @param context
     * @param fileProvider  File provider dell'app per ottenere l'URI del file generato per storare i dati.
     * @param dirType   Tipo di directory dei files dove generare il nuovo file.
     * @param filename  Nome del file utilizzato per salvare dati.
     * @return
     */
    public static Uri getNewFileUri(@NonNull Context context, @NonNull String fileProvider, @NonNull String dirType, @NonNull String filename){
        // dirType = Environment.DIRECTORY_DCIM     =>  La posizione tradizionale per foto e video quando si monta il dispositivo come fotocamera.
        File folder = new File(EnvironmentUtils.getPathApplicationExternalFilesDir(context, dirType));
        folder.mkdirs();

        File file = new File(folder, filename);
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException ioE) {
            BaseEnvironment.onExceptionLevelLow(TAG, ioE);
        }
        return FileProvider.getUriForFile(context, fileProvider, file);
    }
    //endregion

    //region [#] Zip Files Methods
    public static List<String> unzip(String zipFile, String location) {
        List<String> fileInZip = new ArrayList<>();
        try {
            FileInputStream fin = new FileInputStream(zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                checkMakeDirs(location.concat(ze.getName()));
                File file = new File(location.concat(ze.getName().replace(" ", "_")));
                if (!file.exists()) {
                    byte[] buffer = new byte[4096];
                    BufferedOutputStream fout = new BufferedOutputStream(new FileOutputStream(file.getPath()));
                    int count;
                    while((count = zin.read(buffer)) != -1) {
                        fout.write(buffer, 0, count);
                    }
                    fout.close();
                }
                fileInZip.add(file.getPath());
            }
            zin.close();
        } catch (Exception e) {
            BaseEnvironment.onExceptionLevelLow(TAG, e);
            return null;
        }
        return fileInZip;
    }

    public static String getRawAsString(Context context, int resId) {
        final int BUFFER_DIMENSION = 128;
        String result = null;
        // take input stream
        InputStream is = context.getResources().openRawResource(resId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_DIMENSION];
        int numRead = 0;
        try {
            while ((numRead = is.read(buffer)) >= 0) {
                baos.write(buffer, 0, numRead);
            }
            result = new String(baos.toByteArray());
        } catch (IOException ioE) {
            BaseEnvironment.onExceptionLevelLow(TAG, ioE);
        } finally {
            try {
                baos.close();
            } catch (IOException ioE) {
                BaseEnvironment.onExceptionLevelLow(TAG, ioE);
            }
        }
        return result;
    }

    public static String readFileFromAssets(Context context, String filename) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)))){
            return readFromBufferedReader(br);
        } catch (IOException ioE){
            BaseEnvironment.onExceptionLevelLow(TAG, ioE);
        }
        return null;
    }
    //endregion

    //region [#] Path Regexes Methods
    public static String getDirPathFromPath(String path){
        return path.replaceAll(BaseConstants.REGEX_FILENAME_FROM_PATH, "$1");
    }

    public static String getFilenameFromPath(String path){
        return path.replaceAll(BaseConstants.REGEX_FILENAME_FROM_PATH, "$2");
    }

    public static String getFilePathOrNameWithoutExt(String filePathOrName){
        return filePathOrName.replaceAll(BaseConstants.REGEX_EXT_FROM_FILENAME, "$1");
    }

    public static String getExtensionFromFilePathOrName(String filePathOrName){
        return filePathOrName.replaceAll(BaseConstants.REGEX_EXT_FROM_FILENAME, "$2");
    }

    public static String getNotIncrementedFilenameWithExt(String filename){
        return filename.replaceAll(BaseConstants.REGEX_SPLIT_INCREMENTAL_FILENAME, "$1$3");
    }
    //endregion

    //region [#] Uri Methods
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
    //endregion
    //endregion

    //region [#] Private Methods
    //region [#] InputStream & OutputStream Methods
    private static void writeToOutputStream(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        try {
            if (is != null) {
                while ((length = is.read(buffer)) > 0x0) {
                    os.write(buffer, 0x0, length);
                }
            }
            os.flush();
        } catch (Exception e){
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
    }

    private static String readFromBufferedReader(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        String mLine = br.readLine();
        while (mLine != null) {
            sb.append(mLine);
            mLine = br.readLine();
        }
        return sb.toString();
    }

    private static void writeToBufferedOutputStream(InputStream is, ByteArrayOutputStream os, BufferedOutputStream bos) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -0x1) {
            os.write(buffer, 0x0, length);
        }
        bos.write(os.toByteArray());
        bos.flush();
    }
    //endregion

    //region [#] Store & Open Files Methods
    private static File storeDownloadedFile(Context context, String filepath, String b64){
        if(new File(filepath).exists()) {
            filepath = changeFilenameOfAbsPath(filepath);
        }
        Toast.makeText(context, context.getString(R.string.success_saved_element, String.format("del file '%1$s' ", getFilenameFromPath(filepath))), Toast.LENGTH_SHORT).show();
        EncodingUtils.base64ToFile(b64, filepath);
        return new File(filepath);
    }

    private static boolean startActivityOpenFile(Context context, String path){
        Intent start; boolean ret = false;
        String mime = FilesUtils.getFileMimeType(path);
        if(context != null){
            File file = new File(path);
            if (getExtensionFromFilePathOrName(path).equals(BaseConstants.EXT_APK) &&
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                start = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                start.setDataAndType(FileProvider.getUriForFile(context, context.getString(R.string.app_file_provider), file), mime);
                start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                Uri uri;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    uri = FileProvider.getUriForFile(context, context.getString(R.string.app_file_provider), file);
                } else {
                    uri = Uri.fromFile(file);
                }
                start = new Intent(Intent.ACTION_VIEW);
                start.setDataAndType(uri, mime);
                start.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            try {
                context.startActivity(start);
                ret = true;
            } catch (ActivityNotFoundException anfE) {
                BaseEnvironment.onExceptionLevelLow(TAG, anfE);
                Toast.makeText(context, context.getString(R.string.error_activity_not_found, ""), Toast.LENGTH_SHORT).show();
                ret = false;
            }
        }
        return ret;
    }
    //endregion

    //region [#] Bitmap Methods
    private static File storeBitmapFile(File dest, Bitmap bitmap, Bitmap.CompressFormat format){
        try(FileOutputStream fos = new FileOutputStream(dest)){
            if(dest.exists() || dest.createNewFile()) {
                bitmap.compress(format, 100, fos);
            }
        } catch(FileNotFoundException fnfE){
            BaseEnvironment.onExceptionLevelLow(TAG, fnfE);
        } catch (IOException ioE) {
            BaseEnvironment.onExceptionLevelLow(TAG, ioE);
        }
        return dest;
    }
    //endregion

    //region [#] Open Document Activity Methods
    @SuppressLint("NewApi")
    private static String getPathFromOpenDocumentUri(@NonNull Context context, Uri uri) {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0x0];

                if ("primary".equalsIgnoreCase(type)) {
                    return formatFilepath(EnvironmentUtils.getPathApplicationExternalFilesDir(context), split[0x1]);
                }
                // (?) handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getOpenDocumentDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0x0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[0x1]};
                return getOpenDocumentDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getOpenDocumentDataColumn(context, uri, null, null);
        }
        // EMaxFile
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private static String getOpenDocumentDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        final String column = "_data";
        try (Cursor cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        }
        return null;
    }
    //endregion
    //endregion

    //region [#] DownloadAndOpenFile BroadcastReceiver Private Static Class
    private static class DownloadAndOpenFileReceiver extends BroadcastReceiver {

        private final DownloadManager mDownloadManager;

        private DownloadAndOpenFileReceiver(DownloadManager dManager){
            mDownloadManager = dManager;
        }

        //region [#] Override BroadcastReceiver Methods
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                Bundle extras = intent.getExtras();
                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
                Cursor c = mDownloadManager.query(q);
                if (c.moveToFirst()) {
                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        String path = null;
                        File file = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            path = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            file = new File(Uri.parse(path).getPath());
                        } else {
                            path = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                            file = new File(path);
                        }
                        if (file.exists() ) {
                            FilesUtils.openStoredFile(context, file);
                        }
                    }
                }
                c.close();
            }
            Objects.requireNonNull(context).unregisterReceiver(this);
        }
        //endregion
    }
    //endregion

}
