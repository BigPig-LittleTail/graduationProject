package hit.zhou.execute;

import hit.zhou.common.bean.Dir;
import hit.zhou.common.bean.PageForNlp;
import hit.zhou.common.tools.LtpBaseOpLocal;
import hit.zhou.ltp.NLP;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Step3 {
    public static void main(String[] args) throws IOException,InterruptedException, ExecutionException {
        LtpBaseOpLocal ltpBaseOpLocal = new LtpBaseOpLocal("C:/Users/zhou/Desktop/3.4.0/ltp_data_v3.4.0/ltp_data_v3.4.0/");
        Dir<PageForNlp> dir2 = new Dir<>(null,"C:/Users/zhou/Desktop/loyalFinal/",PageForNlp::new);
        NLP nlp = new NLP();
        nlp.buildPages(dir2,ltpBaseOpLocal,"C:/Users/zhou/Desktop/nlpResult/");

        return;

    }
}
