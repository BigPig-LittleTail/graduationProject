package hit.zhou.execute;

import hit.zhou.common.bean.Dir;
import hit.zhou.keyword.TFIDF;
import hit.zhou.kmeans.Kmeans;
import hit.zhou.ltp.LtpBaseOpLocal;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Step3 {
    public static void main(String[] args) throws IOException,InterruptedException, ExecutionException {
        LtpBaseOpLocal ltpBaseOpLocal = new LtpBaseOpLocal("C:/Users/zhou/Desktop/3.4.0/ltp_data_v3.4.0/ltp_data_v3.4.0/");
        Dir dir = new Dir("C:/Users/zhou/Desktop/loyal/");
        TFIDF tfidf = new TFIDF(dir,"C:/Users/zhou/Desktop/stop_words.txt");
        tfidf.getAllFileKeyWordList(10,
                tfidf.buildAllFileMap2Rate(ltpBaseOpLocal,"C:/Users/zhou/Desktop/haha/","C:/Users/zhou/Desktop/haha/"),
                "C:/Users/zhou/Desktop/haha/","C:/Users/zhou/Desktop/haha/");

        System.out.println("kmeans start");
        Kmeans kmeans = new Kmeans(dir);
//        kmeans.buildVectors();
        kmeans.buildTestVectors();
        kmeans.kmeans(5);


        return;

    }
}
