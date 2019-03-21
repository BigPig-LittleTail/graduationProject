package hit.zhou.execute;

import hit.zhou.common.bean.DirTest;
import hit.zhou.keyword.TFIDFTEST;
import hit.zhou.kmeans.KmeansCos;
import hit.zhou.kmeans.cluster.KmeansClusterCos;
import hit.zhou.ltp.LtpBaseOpLocal;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Step2 {

    public static void main(String[] args) throws IOException,InterruptedException, ExecutionException {
        LtpBaseOpLocal ltpBaseOpLocal = new LtpBaseOpLocal("C:/Users/zhou/Desktop/3.4.0/ltp_data_v3.4.0/ltp_data_v3.4.0/");

        DirTest dir1 = new DirTest(null,"C:/Users/zhou/Desktop/loyalFinal/");

        TFIDFTEST tfidftest = new TFIDFTEST("C:/Users/zhou/Desktop/stop_words.txt");
        tfidftest.buildMap2Count(dir1,ltpBaseOpLocal,"C:/Users/zhou/Desktop/loyalTestResult/");
        tfidftest.buildKeyWord(10,dir1,"C:/Users/zhou/Desktop/loyalTestResult/");

        KmeansCos kmeansCos = new KmeansCos(KmeansClusterCos::new);
        kmeansCos.buildVectors(dir1,2);
        kmeansCos.kmeans(10,1000);
        kmeansCos.saveClusters("C:/Users/zhou/Desktop/clusters/");


        return;

    }
}
