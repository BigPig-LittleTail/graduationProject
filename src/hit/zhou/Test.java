package hit.zhou;

import hit.zhou.basic.PassageNode;
import hit.zhou.basic.PassageTree;
import hit.zhou.helper.KmeansParamBuildTest;
import hit.zhou.tools.LtpBaseOpLocal;
import hit.zhou.tools.kmeans.ClusterTest;
import hit.zhou.tools.kmeans.KmeansTest;
import hit.zhou.tools.kmeans.VectorTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Test {
    private static final String NLP_FILE_NAME = "nlp_result.txt";
    private static final String RDF_FILE_NAME = "rdf_result.txt";
    private static final String COUNT_ENTRY_FILE_NAME = "count_entry_result.txt";
    private static final String KEY_WORD_FILE_NAME = "key_word_result.txt";

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        LtpBaseOpLocal ltpBaseOpLocal = new LtpBaseOpLocal("C:/Users/zhou/Desktop/3.4.0/ltp_data_v3.4.0/ltp_data_v3.4.0/", "C:\\Users\\zhou\\Desktop\\words");
        PassageTree passageTree = new PassageTree();
        passageTree.buildPassageTree("C:/Users/zhou/Desktop/loyalChange/", false);
        passageTree.nlp(ltpBaseOpLocal, "C:/Users/zhou/Desktop/rdfTest/", NLP_FILE_NAME, false);
        passageTree.rdf("C:/Users/zhou/Desktop/rdfTTTTest/", RDF_FILE_NAME, false);
        passageTree.countEntry("C:/Users/zhou/Desktop/rdfTest/", COUNT_ENTRY_FILE_NAME, false);
        passageTree.buildKeyWordFilePath("C:/Users/zhou/Desktop/rdfTest/", KEY_WORD_FILE_NAME);
        List<PassageNode> passageNodesLevel3 = passageTree.getPassageNodeListByLevel(3);


        Map<String, VectorTest<EntryType>> headVectorsMap = new HashMap<>();
        Map<String, VectorTest<EntryType>> tailVectorsMap = new HashMap<>();
        Map<EntryType,Integer> typeIntegerMap = new HashMap<EntryType, Integer>(){{
                put(EntryType.法律,0);
                put(EntryType.行政法规,1);
            }
        };

        KmeansParamBuildTest.passageNodes2Vectors(passageNodesLevel3,typeIntegerMap,headVectorsMap,tailVectorsMap);

        List<ClusterTest<EntryType>> clustersHead = new ArrayList<>();
        List<ClusterTest<EntryType>> clustersTail = new ArrayList<>();

        float[] center1 = new float[]{
                1,0
        };
        float[] center2 = new float[]{
                0,1
        };
        float[] center3 = new float[]{
                1,1
        };

        List<EntryType> types1 = new ArrayList<EntryType>(){
            {
                add(EntryType.法律);
            }
        };

        List<EntryType> types2 = new ArrayList<EntryType>(){
            {
                add(EntryType.行政法规);
            }
        };

        List<EntryType> types3 = new ArrayList<EntryType>(){
            {
                add(EntryType.法律);
                add(EntryType.行政法规);
            }
        };

        clustersHead.add(new ClusterTest<>(center1,types1));
        clustersHead.add(new ClusterTest<>(center2,types2));
        clustersHead.add(new ClusterTest<>(center3,types3));

        clustersTail.add(new ClusterTest<>(center1,types1));
        clustersTail.add(new ClusterTest<>(center2,types2));
        clustersTail.add(new ClusterTest<>(center3,types3));

        KmeansTest.kmeans(headVectorsMap,clustersHead);
        KmeansTest.kmeans(tailVectorsMap,clustersTail);

        return;

    }

}