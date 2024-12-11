package com.bodhitech.it.lib_base.lib_base.modules.networking.graphql;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class GraphQLConverter extends Converter.Factory {

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    private GraphQueryProcessor graphProcessor;
    private final Gson mGson;

    private GraphQLConverter(Context context) {
        graphProcessor = new GraphQueryProcessor(context);
        mGson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .setLenient()
                .create();
    }

    public static GraphQLConverter create(Context context) {
        return new GraphQLConverter(context);
    }

    /** Override Converter.Factory Methods **/
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return null;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        if(type == QueryContainerBuilder.class){
            return new GraphRequestConverter(methodAnnotations);
        } else {
            return null;
        }
    }

    /** RequestConverter Class **/
    private class GraphRequestConverter implements Converter<QueryContainerBuilder, RequestBody> {

        private Annotation[] mAnnotations;

        private GraphRequestConverter(Annotation[] annotations) {
            mAnnotations = annotations;
        }

        @Override
        public RequestBody convert(@NonNull QueryContainerBuilder containerBuilder) {
            QueryContainerBuilder.QueryContainer queryContainer = containerBuilder
                    .setQuery(graphProcessor.getQuery(mAnnotations))
                    .build();
            return RequestBody.create(MEDIA_TYPE, mGson.toJson(queryContainer).getBytes());
        }

    }
}
