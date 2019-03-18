package hit.zhou.common.tools;

import okhttp3.*;

import java.io.IOException;

public class OkHttpUtil {
    private static final MediaType TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    private static final String ERROR_INIT_CLIENT_WITH_NULL = "Your param client is null";
    private static final String ERROR_INIT_URL_WITH_NULL = "Your param url is null";

    private static OkHttpUtil singleOkHttpUtil;
    private OkHttpClient httpClient;
    private String serverUrl;

    public static OkHttpUtil getInstance(){
        if(singleOkHttpUtil == null){
            synchronized (OkHttpUtil.class){
                if(singleOkHttpUtil == null)
                    singleOkHttpUtil = new OkHttpUtil();
            }
        }
        return singleOkHttpUtil;
    }

    private OkHttpUtil(){

    }

    public synchronized void init(OkHttpClient InitHttpClient,String url){
        if(InitHttpClient == null){
            throw new IllegalArgumentException(ERROR_INIT_CLIENT_WITH_NULL);
        }
        if(this.httpClient == null){
            this.httpClient = InitHttpClient;
        }

        if(url == null){
            throw new IllegalArgumentException(ERROR_INIT_URL_WITH_NULL);
        }
        if(this.serverUrl == null)
        {
            this.serverUrl = url;
        }
    }

    public Response post(Headers header,String text, String action) throws IOException{
        checkClient();
        String url = serverUrl;
        if(!action.equals("")){
            url = url + action;
        }
        RequestBody requestBody = RequestBody.create(TYPE,text);
        Request request = new Request.Builder()
                .headers(header)
                .url(url)
                .post(requestBody)
                .build();

        Call call = httpClient.newCall(request);
        return call.execute();

    }

    private void checkClient(){
        if(this.httpClient == null){
            throw new IllegalArgumentException(ERROR_INIT_CLIENT_WITH_NULL);
        }
    }
}
