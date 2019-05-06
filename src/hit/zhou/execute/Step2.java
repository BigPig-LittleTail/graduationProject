package hit.zhou.execute;

import hit.zhou.common.bean.Dir;
import hit.zhou.common.bean.PageForKeyWord;
import hit.zhou.keyword.TFIDF;
import hit.zhou.kmeans.KmeansCos;
import hit.zhou.kmeans.cluster.KmeansClusterCos;
import hit.zhou.common.tools.LtpBaseOpLocal;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Step2 {

    public static void main(String[] args) throws IOException,InterruptedException, ExecutionException {
        LtpBaseOpLocal ltpBaseOpLocal = new LtpBaseOpLocal("C:/Users/zhou/Desktop/3.4.0/ltp_data_v3.4.0/ltp_data_v3.4.0/");

        Dir<PageForKeyWord> dir1 = new Dir<>(null,"C:/Users/zhou/Desktop/loyalFinal/",PageForKeyWord::new);

        TFIDF TFIDF = new TFIDF("C:/Users/zhou/Desktop/stop_words.txt");
        TFIDF.buildMap2Count(dir1,ltpBaseOpLocal,"C:/Users/zhou/Desktop/loyalResult/");
        TFIDF.buildKeyWord(10,false,dir1,"C:/Users/zhou/Desktop/loyalResult/");

        KmeansCos kmeansCos = new KmeansCos(KmeansClusterCos::new);
        kmeansCos.buildVectors(dir1,2);
        kmeansCos.kmeans(10,1000);
        kmeansCos.saveClusters("C:/Users/zhou/Desktop/clusters/");


        return;

    }
}
