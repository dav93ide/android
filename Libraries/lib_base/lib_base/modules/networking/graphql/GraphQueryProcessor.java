package com.bodhitech.it.lib_base.lib_base.modules.networking.graphql;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.WeakHashMap;

class GraphQueryProcessor {

    private static final String TAG = GraphQueryProcessor.class.getSimpleName();
    // GraphQl Constants
    private static final String EXT_GRAPHQL = ".graphql";
    private static final String ROOT_FOLDER_GRAPHQL = "graphql";

    private final Map<String, String> mGraphQueries;
    private Context mContext;

    GraphQueryProcessor(Context context) {
        mGraphQueries = new WeakHashMap<>();
        mContext = context;
    }

    /** Package-Private Methods **/
    String getQuery(Annotation[] annotations) {
        GraphQuery graphQuery = null;
        for (Annotation annotation : annotations) {
            if (annotation instanceof GraphQuery) {
                graphQuery = (GraphQuery) annotation;
                break;
            }
        }

        if (graphQuery != null) {
            String fileName = String.format("%s%s", graphQuery.value(), EXT_GRAPHQL);
            return getGraphQuery(ROOT_FOLDER_GRAPHQL, fileName);
        }
        return null;
    }

    /** Private Methods **/
    private String getGraphQuery(@NonNull String path, @NonNull String filename) {
        try {
            String query = null;
            String[] paths = mContext.getAssets().list(path);
            if (paths != null && paths.length > 0x0) {
                for (String item : paths) {
                    String absolute = path + "/" + item;
                    if (!item.endsWith(EXT_GRAPHQL)) {
                        query = getGraphQuery(absolute, filename);
                    } else if(filename.equals(item)){
                        query = getFileContents(mContext.getAssets().open(absolute));
                    }
                    if(!TextUtils.isEmpty(query)){
                        return query;
                    }
                }
            }
        } catch (IOException ioE) {
            BaseEnvironment.onExceptionLevelLow(TAG, ioE);
        }
        return null;
    }

    private String getFileContents(InputStream inputStream) {
        StringBuilder queryBuffer = new StringBuilder();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            for (String line; (line = bufferedReader.readLine()) != null; )
                queryBuffer.append(line);
            inputStreamReader.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return queryBuffer.toString();
    }

}
