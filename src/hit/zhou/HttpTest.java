package hit.zhou;

import edu.hit.ir.ltp4j.Pair;
import hit.zhou.common.tools.FileUtil;
import hit.zhou.common.tools.OkHttpUtil;
import hit.zhou.ltp.LtpBaseOpNet;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HttpTest {

    // webapi接口地址
    private static final String WEBTTS_URL = "http://ltpapi.xfyun.cn/v1/";
    // 应用ID
    private static final String APPID = "5c7d581a";
    // 接口密钥
    private static final String API_KEY = "234c6b50dcea1291420e2d66b3981277";
    // 文本
    private static final String TEXT = "打开页面物联网";


    private static final String TYPE = "dependent";

    public static void main(String[] args) throws IOException {
        OkHttpClient okHttpClient =  new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L,TimeUnit.MILLISECONDS)
                .build();
//        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        OkHttpUtil.getInstance().init(okHttpClient,WEBTTS_URL);

        LtpBaseOpNet ltpBaseOpNet = new LtpBaseOpNet(APPID,API_KEY,TYPE);


        byte[] bytes = FileUtil.read("C:/Users/zhou/Desktop/x.txt");
        String[] strings = new String[1];
        String out = new String(bytes);
        System.out.println(out);
        List<String> words = new ArrayList<>();
        ltpBaseOpNet.segmentor("text=" + URLEncoder.encode(out, "utf-8"),words,strings);
        System.out.println(words.size());

        List<Integer> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        ltpBaseOpNet.parser("text=" + URLEncoder.encode(out, "utf-8"),list1,list2,strings);
        System.out.println(list1.size());
        System.out.println(list2);

        List<Pair<Integer, List<Pair<String, Pair<Integer, Integer>>>>> srls = new ArrayList<>();
        ltpBaseOpNet.srl("text=" + URLEncoder.encode(TEXT, "utf-8"),srls,strings);
        for (int i = 0; i < srls.size(); ++i) {
            System.out.println(srls.get(i).first + ":");
            for (int j = 0; j < srls.get(i).second.size(); ++j) {
                System.out.println("   tpye = " + srls.get(i).second.get(j).first + " beg = "
                        + srls.get(i).second.get(j).second.first + " end = " + srls.get(i).second.get(j).second.second);
            }
        }

    }
}
