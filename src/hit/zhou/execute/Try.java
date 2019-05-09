package hit.zhou.execute;

import hit.zhou.common.bean.test.PassageNode;
import hit.zhou.common.bean.test.PassageTree;
import hit.zhou.common.bean.test.TFIDF;
import hit.zhou.common.tools.LtpBaseOpLocal;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Try {
    private static final String NLP_FILE_NAME = "nlp_result.txt";
    private static final String RDF_FILE_NAME = "rdf_result.txt";
    private static final String COUNT_ENTRY_FILE_NAME = "count_entry_result.txt";
    private static final String KEY_WORD_FILE_NAME = "key_word_result.txt";

    public static void main(String[] args) throws IOException,ExecutionException, InterruptedException{
        LtpBaseOpLocal ltpBaseOpLocal = new LtpBaseOpLocal("C:/Users/zhou/Desktop/3.4.0/ltp_data_v3.4.0/ltp_data_v3.4.0/");
        PassageTree passageTree = new PassageTree();
        passageTree.buildPassageTree("C:/Users/zhou/Desktop/loyalChange/",false);
        passageTree.nlp(ltpBaseOpLocal,"C:/Users/zhou/Desktop/nlpTest/",NLP_FILE_NAME,false);
        passageTree.rdf("C:/Users/zhou/Desktop/rdfTest/",RDF_FILE_NAME, false);
        passageTree.countEntry("C:/Users/zhou/Desktop/rdfTest/",COUNT_ENTRY_FILE_NAME,false);
        passageTree.buildKeyWordFilePath("C:/Users/zhou/Desktop/rdfTest/",KEY_WORD_FILE_NAME);

        List<PassageNode> passageNodes = passageTree.getPassageNodeListByLevel(3);
        TFIDF.keyWord(passageNodes,passageNodes);


        return;
    }
}
