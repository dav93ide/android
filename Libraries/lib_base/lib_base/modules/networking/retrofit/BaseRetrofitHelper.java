package com.bodhitech.it.lib_base.lib_base.modules.networking.retrofit;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bodhitech.it.lib_base.lib_base.BaseConstants;
import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;
import com.bodhitech.it.lib_base.lib_base.modules.networking.deserializers.BooleanDeserializer;
import com.bodhitech.it.lib_base.lib_base.modules.networking.graphql.GraphQLConverter;
import com.bodhitech.it.lib_base.lib_base.modules.networking.responses.BaseGraphQlResponse;
import com.bodhitech.it.lib_base.lib_base.modules.networking.responses.BaseItemResponse;
import com.bodhitech.it.lib_base.lib_base.modules.networking.responses.BaseItemsResponse;
import com.bodhitech.it.lib_base.lib_base.modules.networking.responses.BaseResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

public abstract class BaseRetrofitHelper {

    /** Callback Interfaces **/
    public interface INetworkResponseCallback<T> {
        void onNetworkResponseSuccess(int requestCode, T answer);
        void onNetworkResponseError(int requestCode, Throwable t);
    }

    public interface IBaseResponseCallback<T extends BaseResponse> {
        void onBaseResponseSuccess(int requestCode, T answer);
        void onBaseResponseError(int requestCode, Throwable t);
    }

    public interface IBaseItemResponseCallback<T> {
        void onBaseItemResponseSuccess(int requestCode, BaseItemResponse<T> answer);
        void onBaseItemResponseError(int requestCode, Throwable t);
    }

    public interface IGetItemBaseResponseCallback<T> {
        void onGetItemBaseResponseSuccess(int requestCode, T answer);
        void onGetItemBaseResponseError(int requestCode, Throwable t);
    }

    public interface IBaseItemsResponseCallback<T> {
        void onBaseItemsResponseSuccess(int requestCode, BaseItemsResponse<T> answer);
        void onBaseItemsResponseError(int requestCode, Throwable t);
    }

    public interface IGetItemsBaseResponseCallback<T> {
        void onGetItemsBaseResponseSuccess(int requestCode, List<T> answer);
        void onGetItemsBaseResponseError(int requestCode, Throwable t);
    }

    public interface IBaseGraphQlResponseCallback<T> {
        void onGraphQlResponseSuccess(int requestCode, BaseGraphQlResponse<T> answer);
        void onGraphQlResponseError(int requestCode, Throwable t);
    }

    // Tags
    private static final String TAG = BaseRetrofitHelper.class.getSimpleName();
    private static final String TAG_HTTP_LOGGER = "HTTP_Logger";
    // Timeouts Headers
    public static final String CONNECT_TIMEOUT = "CONNECT_TIMEOUT";
    public static final String READ_TIMEOUT = "READ_TIMEOUT";
    public static final String WRITE_TIMEOUT = "WRITE_TIMEOUT";
    // Timeout Default Values
    private static final int DEFAULT_CONNECT_TIMEOUT = 20000;
    private static final int DEFAULT_READ_TIMEOUT = 20000;
    private static final int DEFAULT_WRITE_TIMEOUT = 20000;

    private static String mBaseUrl;
    private static Retrofit mRetrofit;
    private HttpLoggingInterceptor.Level mLogLevel;

    protected BaseRetrofitHelper(HttpLoggingInterceptor.Level level){
        mLogLevel = level;
    }

    protected BaseRetrofitHelper(Context context, String baseUrl){
        mBaseUrl = baseUrl;
        mLogLevel = HttpLoggingInterceptor.Level.NONE;
        if(mRetrofit == null){
            initRetrofit(context);
        }
    }

    protected BaseRetrofitHelper(Context context, String baseUrl, HttpLoggingInterceptor.Level level){
        mBaseUrl = baseUrl;
        mLogLevel = level;
        if(mRetrofit == null){
            initRetrofit(context);
        }
    }

    protected BaseRetrofitHelper(HttpLoggingInterceptor.Level level, Retrofit retrofit, String baseUrl){
        mLogLevel = level;
        mRetrofit = retrofit;
        mBaseUrl = baseUrl;
    }

    /** Abstract Methods **/
    public abstract void initRetrofit(@NonNull Context context);

    /** Getter Methods **/
    public HttpLoggingInterceptor.Level getLogLevel() {
        return mLogLevel;
    }

    public static String getBaseUrl() {
        return mBaseUrl;
    }

    protected static Retrofit getRetrofit(){
        return mRetrofit;
    }

    /** Setter Methods **/
    public void setLogLevel(HttpLoggingInterceptor.Level level) {
        mLogLevel = level;
    }

    public void setBaseUrl(String url){
        BaseRetrofitHelper.mBaseUrl = url;
    }

    public void setRetrofit(Retrofit retrofit){
        BaseRetrofitHelper.mRetrofit = retrofit;
    }

    public <T> T createService(Class<T> service) {
        return mRetrofit.create(service);
    }

    /** Sync Request Methods **/
    public <T> T executeSyncCall(Call<T> call){
        return new ResponseExecutorCallable<>(newSingleThreadExecutor(), call).executeSyncCall();
    }

    public boolean getBaseResponseSuccess(Call<? extends  BaseResponse> call){
        return new BaseResponseExecutorCallable<>(newSingleThreadExecutor(), call).getBaseResponseSuccess();
    }

    /** Check Response Methods **/
    public static boolean checkBaseResponseSuccess(BaseResponse response){
        if(response != null && response.getSuccess()){
            return true;
        } else {
            return false;
        }
    }

    public static String getBaseResponseMessage(BaseResponse response){
        if(response != null){
            return response.getMsg();
        } else {
            return null;
        }
    }

    /** Protected Methods **//** Init Methods **/
    protected void initBaseRetrofit(Context context){
        initBaseRetrofit(context, getBaseGson(), getBaseInterceptor());
    }

    protected void initBaseRetrofit(Context context, Gson gson, Interceptor interceptor){
        mRetrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(GraphQLConverter.create(context))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(getBaseHttpClient(interceptor))
                .build();
    }

    /** Get Base Components Methods **/
    protected Gson getBaseGson(){
        return new GsonBuilder()
                .setDateFormat(BaseConstants.DATETIME_FORMAT_DB)
                .setLenient()
                .registerTypeAdapter(boolean.class, new BooleanDeserializer())
                .create();
    }

    protected Interceptor getBaseInterceptor(){
        return chain -> {
            Request request = chain.request();
            int connectTimeout = DEFAULT_CONNECT_TIMEOUT,
                    readTimeout = DEFAULT_READ_TIMEOUT,
                    writeTimeout = DEFAULT_WRITE_TIMEOUT;
            String connectNew = request.header(CONNECT_TIMEOUT);
            String readNew = request.header(READ_TIMEOUT);
            String writeNew = request.header(WRITE_TIMEOUT);
            if (!TextUtils.isEmpty(connectNew)) {
                connectTimeout = Integer.valueOf(connectNew);
            }
            if (!TextUtils.isEmpty(readNew)) {
                readTimeout = Integer.valueOf(readNew);
            }
            if (!TextUtils.isEmpty(writeNew)) {
                writeTimeout = Integer.valueOf(writeNew);
            }

            Request.Builder builder = request.newBuilder();
            builder.removeHeader(CONNECT_TIMEOUT);
            builder.removeHeader(READ_TIMEOUT);
            builder.removeHeader(WRITE_TIMEOUT);
            return chain
                    .withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                    .withReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
                    .withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                    .proceed(builder.build());
        };
    }

    protected OkHttpClient getBaseHttpClient(Interceptor interceptor){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(interceptor);
        if(BaseEnvironment.isAppDebugMode()){
            HttpLoggingInterceptor.Logger httpLogger =
                    message -> {
                        if(BaseEnvironment.hasLogger()) {
                            BaseEnvironment.net(TAG_HTTP_LOGGER, message);
                        } else if(BaseEnvironment.isAppDebugMode()){
                            Log.d(TAG_HTTP_LOGGER, message);
                        }
                    };
            HttpLoggingInterceptor httpInterceptor = new HttpLoggingInterceptor(httpLogger);
            httpInterceptor.setLevel(mLogLevel);
            httpClient.addInterceptor(httpInterceptor);
        }
        return httpClient.build();
    }

    /** Json Request Methods **/
    String getJsonRequest(LinkedHashMap<String, String> paramsToJson) {
        Gson gson = new Gson();
        return gson.toJson(paramsToJson, LinkedHashMap.class);
    }

    /** Sync Request Methods **/
    protected <T> T executeSyncCall(ExecutorService exe, Call<T> call){
        return new ResponseExecutorCallable<>(exe, call).executeSyncCall();
    }

    protected String getBaseResponseMessage(Call<? extends BaseResponse> call){
        return new BaseResponseExecutorCallable<>(newSingleThreadExecutor(), call).getBaseResponseMessage();
    }

    protected <T> List<T> getBaseResponseItems(Call<BaseItemsResponse<T>> call){
        return new BaseItemsResponseExecutorCallable<T>(newSingleThreadExecutor(), call).getBaseResponseItems();
    }

    protected <T> T getBaseResponseItem(Call<BaseItemResponse<T>> call){
        return new BaseItemResponseExecutorCallable<T>(newSingleThreadExecutor(), call).getBaseResponseItem();
    }

    /** Check Response Methods **/
    protected JsonObject checkGetJsonObject(Response<JsonObject> response){
        JsonObject answer = null;
        if(response != null && response.isSuccessful()){
            answer = response.body();
        }
        return answer;
    }

    protected ResponseBody checkResponseBody(Response<ResponseBody> response){
        ResponseBody answer = null;
        if(response != null && response.isSuccessful()){
            answer = response.body();
        }
        return answer;
    }

    protected <T> BaseGraphQlResponse<T> checkGraphQlResponse(Response<BaseGraphQlResponse<T>> response){
        BaseGraphQlResponse<T> answer = null;
        if(response != null && response.isSuccessful()){
            answer = response.body();
        }
        return answer;
    }

    protected <T> T checkGetGraphQlResponseContent(Response<BaseGraphQlResponse<T>> response){
        BaseGraphQlResponse<T> answer = checkGraphQlResponse(response);
        return answer != null ? answer.getData() : null;
    }

    protected <T extends BaseResponse> T checkBaseResponse(Response<T> response){
        T answer = null;
        if(response != null && response.isSuccessful()){
            answer = response.body();
        }
        return answer;
    }

    protected <T extends BaseResponse> boolean checkGetBaseResponseSuccess(Response<T> response){
        T res = checkBaseResponse(response);
        if(res != null){
            return res.getSuccess();
        } else {
            return false;
        }
    }

    protected <G, T extends BaseItemResponse<G>> T checkBaseItemResponse(Response<T> response){
        return checkBaseResponse(response);
    }

    protected <K, T extends BaseItemResponse<K>> K checkGetBaseResponseItem(Response<T> response){
        K item = null;
        if(response != null && response.isSuccessful()){
            T answer = response.body();
            if(answer != null && answer.getSuccess()){
                item = answer.getItem();
            }
        }
        return item;
    }

    protected <G, T extends BaseItemsResponse<G>> T checkBaseItemsResponse(Response<T> response){
        return checkBaseResponse(response);
    }

    protected <K, T extends BaseItemsResponse<K>> List<K> checkGetBaseResponseItems(Response<T> response){
        List<K> items = null;
        if(response != null && response.isSuccessful()){
            T answer = response.body();
            if(answer != null && answer.getSuccess()){
                items = answer.getItems();
            }
        }
        return items;
    }

    /** Private Classes **/
    private class ResponseExecutorCallable<T> implements Callable<T> {

        private ExecutorService mExecutorService;
        private Call<T> mCall;

        ResponseExecutorCallable(ExecutorService exe, Call<T> call){
            mExecutorService = exe;
            mCall = call;
        }

        /** Override Callable Methods **/
        @Override
        public T call() {
            T ret = null;
            try {
                Response<T> response = mCall.execute();
                if (response.isSuccessful()) {
                    ret = response.body();
                }
            } catch(IOException ioE){
                BaseEnvironment.onNetworkException(TAG, ioE);
            }
            return ret;
        }

        /** Public Methods **/
        T executeSyncCall(){
            T ret = null;
            try{
                ret = mExecutorService.submit(this).get();
            } catch(ExecutionException | InterruptedException eiE){
                BaseEnvironment.onNetworkException(TAG, eiE);
            }
            return ret;
        }

    }

    private class BaseResponseExecutorCallable<T extends BaseResponse> extends ResponseExecutorCallable<T> {

        BaseResponseExecutorCallable(ExecutorService exe, Call<T> call){
            super(exe, call);
        }

        /** Public Methods **/
        boolean getBaseResponseSuccess(){
            boolean ret = false;
            T res = super.executeSyncCall();
            if(res != null){
                ret = res.getSuccess();
            }
            return ret;
        }

        String getBaseResponseMessage(){
            String ret = null;
            T res = super.executeSyncCall();
            if(res != null && res.getSuccess()){
                ret = res.getMsg();
            }
            return ret;
        }

    }

    private class BaseItemResponseExecutorCallable<T> extends BaseResponseExecutorCallable<BaseItemResponse<T>>{

        BaseItemResponseExecutorCallable(ExecutorService exe, Call<BaseItemResponse<T>> call){
            super(exe, call);
        }

        /** Public Methods **/
        <T> T getBaseResponseItem(){
            T ret = null;
            BaseItemResponse<T> res = (BaseItemResponse<T>) super.executeSyncCall();
            if(res != null && res.getSuccess()){
                ret = res.getItem();
            }
            return ret;
        }

    }

    private class BaseItemsResponseExecutorCallable<T> extends BaseResponseExecutorCallable<BaseItemsResponse<T>>{

        BaseItemsResponseExecutorCallable(ExecutorService exe, Call<BaseItemsResponse<T>> call){
            super(exe, call);
        }

        /** Public Methods **/
        <T> List<T> getBaseResponseItems(){
            List ret = null;
            BaseItemsResponse<T> res = (BaseItemsResponse<T>) super.executeSyncCall();
            if(res != null && res.getSuccess()){
                ret = res.getItems();
            }
            return ret;
        }

    }

}
