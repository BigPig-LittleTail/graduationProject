package hit.zhou;

import hit.zhou.hepler.OkHttpUtil;
import hit.zhou.nlp.ltp.LtpBaseOpLocal;
import hit.zhou.nlp.ltp.LtpBaseOpNet;
import hit.zhou.nlp.text.Dir;
import hit.zhou.nlp.text.TFIDF;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class FileTest {
    // webapi接口地址
    private static final String WEBTTS_URL = "http://ltpapi.xfyun.cn/v1/";
    // 应用ID
    private static final String APPID = "5c7d581a";
    // 接口密钥
    private static final String API_KEY = "234c6b50dcea1291420e2d66b3981277";
    // 文本
    private static final String TEXT = "打开页面物联网";

    private static final String TYPE = "dependent";

    public static void main(String[] args) throws IOException,InterruptedException, ExecutionException {
        OkHttpClient okHttpClient =  new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L,TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtil.getInstance().init(okHttpClient,WEBTTS_URL);

        LtpBaseOpNet ltpBaseOpNet = new LtpBaseOpNet(APPID,API_KEY,TYPE);
        LtpBaseOpLocal ltpBaseOpLocal = new LtpBaseOpLocal("C:/Users/zhou/Desktop/3.4.0/ltp_data_v3.4.0/ltp_data_v3.4.0/");

//        byte[] bytes = FileUtil.read("C:/Users/zhou/Desktop/xian.txt");
//        String input = new String(bytes);
//        List<String> intputAllStrings = new ArrayList<>();
//
//        SplitSentence splitSentence = new SplitSentence();
//        splitSentence.splitSentence(input,intputAllStrings);
//        List<String> words1 = new ArrayList<>();
//        String[] strings = new String[1];
//        HashMap<String,Integer> countMap = new HashMap<>();
//
//        for(String s:intputAllStrings){
//            if(s.equals("")){
//                continue;
//            }
////            ltpBaseOpNet.segmentor("text=" + URLEncoder.encode(s, "utf-8"),words1,strings);
//            ltpBaseOpLocal.segmentor(s,words1);
//            strings[0] = words1.toString();
////            FileUtil.save("C:/Users/zhou/Desktop/","tesxXXX.txt", strings[0].getBytes());
//
//            for(String word:words1){
//                if(countMap.containsKey(word)){
//                    int count = countMap.get(word);
//                    countMap.put(word,++count);
//                }
//                else {
//                    countMap.put(word,1);
//                }
//            }
//            words1.clear();
//            System.out.println(strings[0]);
//        }
//
//        Comparator<WordCount<Integer>> comparator = new Comparator<WordCount<Integer>>() {
//            @Override
//            public int compare(WordCount<Integer> o1, WordCount<Integer> o2) {
//                if(o1.getCountNum() > o2.getCountNum())
//                    return 1;
//                else if(o1.getCountNum() < o2.getCountNum())
//                    return -1;
//                else
//                    return 0;
//            }
//        };
//
//        List<WordCount<Integer>> wordCountList = new ArrayList<>();
//
//
//        for(Map.Entry<String,Integer> entry:countMap.entrySet()){
//            System.out.println(entry.getKey() + ","+entry.getValue());
//            wordCountList.add(new WordCount<Integer>(entry.getKey(),entry.getValue()));
//        }
//
//        wordCountList.sort(comparator);
//        for(WordCount wordCount:wordCountList) {
//            System.out.println(wordCount.getWord() + "," + wordCount.getCountNum());
//        }

        Dir dir = new Dir("C:/Users/zhou/Desktop/zhou/");
        TFIDF tfidf = new TFIDF(dir,"C:/Users/zhou/Desktop/stop_words.txt");
//        tfidf.setKeyWordListToMyFile(10,"1.txt");
//        tfidf.setKeyWordListToMyFile(10,"xian.txt");
//        tfidf.setKeyWordListToMyFile(10,"test.docx");


        tfidf.getAllFileKeyWordList(10,
                tfidf.getAllFileMap2Rate(ltpBaseOpLocal,"C:/Users/zhou/Desktop/wawa/","C:/Users/zhou/Desktop/wawa/"),
                "C:/Users/zhou/Desktop/wawa/","C:/Users/zhou/Desktop/wawa/");
//        tfidf.setAllKeyWordListToAllFile(10);
//
////        dir.saveDirFileKeyWordResultToFile("1.txt","C:/Users/zhou/Desktop/haha/");
//        dir.saveDirAllFileKeyWordResult("C:/Users/zhou/Desktop/haha/");
        
    }
}
