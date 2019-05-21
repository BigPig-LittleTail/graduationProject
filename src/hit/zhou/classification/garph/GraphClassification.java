package hit.zhou.classification.garph;

import com.alibaba.fastjson.JSON;
import hit.zhou.graph.EntryType;
import hit.zhou.graph.basic.PassageNode;
import hit.zhou.graph.basic.PassageTree;
import hit.zhou.graph.basic.rdf.MyEntry;
import hit.zhou.graph.basic.rdf.MyRDF;
import hit.zhou.graph.helper.graph_build_helper.Neo4jHelper;
import hit.zhou.graph.tools.FileUtil;
import hit.zhou.graph.tools.LtpBaseOpLocal;
import org.neo4j.graphdb.Direction;
import org.thunlp.text.classifiers.BasicTextClassifier;
import org.thunlp.text.classifiers.ClassifyResult;
import org.thunlp.text.classifiers.LinearBigramChineseTextClassifier;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class GraphClassification {
    private static final String TEMP_FILE_PATH= "D:\\graduation\\test\\testGraphNeed";
    private static final String NLP_FILE_NAME = "nlp_result.txt";
    private static final String RDF_FILE_NAME = "rdf_result.txt";
    private static final String KEY_WORD_FILE_NAME = "key_word_result.txt";
    private static final String COUNT_ENTRY_FILE_NAME = "count_entry_result.txt";
    private static final double classification = 0.9;
    private static final double graph = 0.1;

    private static final Map<EntryType,Integer> typeIntegerMap = new HashMap<EntryType, Integer>(){{
        put(EntryType.体育,0);
        put(EntryType.彩票,1);
        put(EntryType.家居,2);
        put(EntryType.娱乐,3);
    }
    };
    private static final List<EntryType> typesLocation = new ArrayList<EntryType>(){{
        add(EntryType.体育);
        add(EntryType.彩票);
        add(EntryType.家居);
        add(EntryType.娱乐);
    }
    };

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
//        LtpBaseOpLocal ltpBaseOpLocal = new LtpBaseOpLocal("C:/Users/zhou/Desktop/3.4.0/ltp_data_v3.4.0/ltp_data_v3.4.0/");
//        PassageTree passageTree = new PassageTree(PassageNodeClassify::new);
//        passageTree.buildPassageTree("C:/Users/zhou/Desktop/newstest/", false);
//        passageTree.nlp(ltpBaseOpLocal, "D:\\newstest\\", NLP_FILE_NAME, false);
//        passageTree.countEntry("D:\\newstest\\classifitest\\", COUNT_ENTRY_CLASSIFY_FILE_NAME, false);
//        passageTree.buildKeyWordFilePath("D:\\newstest\\classifitest\\", KEY_WORD_FILE_NAME);
//        List<PassageNode> passageNodesLevel3 = passageTree.getPassageNodeListByLevel(3);
//
//        KeyWordHelper.keyWord(passageNodesLevel3,passageNodesLevel3,false);
        LtpBaseOpLocal ltpBaseOpLocal = new LtpBaseOpLocal("C:/Users/zhou/Desktop/3.4.0/ltp_data_v3.4.0/ltp_data_v3.4.0/");
        PassageTree passageTree = new PassageTree();
        passageTree.buildPassageTree("D:\\graduation\\test\\testGraph\\", false);
        passageTree.nlp(ltpBaseOpLocal, TEMP_FILE_PATH, NLP_FILE_NAME, false);
        passageTree.rdf(TEMP_FILE_PATH, RDF_FILE_NAME, false);
        passageTree.countEntry(TEMP_FILE_PATH, COUNT_ENTRY_FILE_NAME, false);
        passageTree.buildKeyWordFilePath(TEMP_FILE_PATH, KEY_WORD_FILE_NAME);
        List<PassageNode> passageNodesLevel3 = passageTree.getPassageNodeListByLevel(3);



        BasicTextClassifier classifier = new BasicTextClassifier();

        // 设置分类种类，并读取模型
        classifier.loadCategoryListFromFile("D:\\graduation\\model\\my_model\\category");
        classifier.setTextClassifier(new LinearBigramChineseTextClassifier(classifier.getCategorySize()));
        classifier.getTextClassifier().loadModel("D:\\graduation\\model\\my_model\\");

        int topN = 14;
        Map<EntryType,Integer> typeIntegerMap = new HashMap<>();
        Map<EntryType,Integer> errorMap = new HashMap<>();

        for(PassageNode passageNode:passageNodesLevel3){
            List<Map.Entry<EntryType,Float>> graphResult = score(passageNode);
            ClassifyResult[] result = classifier.classifyFile(passageNode.getPassagePath(),topN);
            EntryType myTest;
            if(graphResult == null){
                myTest = EntryType.valueOf(classifier.getCategoryName(result[0].label));
            }
            else{
                Map<EntryType,Double> score = new HashMap<>();
                for (ClassifyResult classifyResult : result) {
                    String typeString = classifier.getCategoryName(classifyResult.label);
                    EntryType type = EntryType.valueOf(typeString);
                    score.put(type, classifyResult.prob * classification);
                }
                for(Map.Entry<EntryType,Float> entry:graphResult){
                    double old = score.get(entry.getKey());
                    double newData = old + graph * entry.getValue();
                    score.put(entry.getKey(),newData);
                }

                List<Map.Entry<EntryType,Double>> realResult = new ArrayList<>(score.entrySet());
                Comparator<Map.Entry<EntryType,Double>> comparator = (o1, o2) ->{
                    if(o1.getValue() > o2.getValue())
                        return -1;
                    else if(o1.getValue() < o2.getValue())
                        return 1;
                    else
                        return 0;
                };
                realResult.sort(comparator);
                myTest = realResult.get(0).getKey();
            }
            if(typeIntegerMap.containsKey(passageNode.getType())){
                int num = typeIntegerMap.get(passageNode.getType());
                typeIntegerMap.put(passageNode.getType(),num + 1);
            }
            else{
                typeIntegerMap.put(passageNode.getType(),1);
            }

            if(!myTest.equals(passageNode.getType())){
                if(errorMap.containsKey(passageNode.getType())){
                    int num = errorMap.get(passageNode.getType());
                    errorMap.put(passageNode.getType(),num + 1);
                }
                else{
                    errorMap.put(passageNode.getType(),1);
                }
            }

        }

        for(Map.Entry<EntryType,Integer> entry:typeIntegerMap.entrySet()){
            float errorNum = 0;
            if(errorMap.containsKey(entry.getKey())){
                errorNum = errorMap.get(entry.getKey());
            }
            System.err.println(entry.getKey() + "\t" + (errorNum /(float) entry.getValue()) );
        }

    }

    private static List<Map.Entry<EntryType,Float>> score(PassageNode passageNode) throws IOException {
        String rdfJsonArrayString = FileUtil.readString(passageNode.getRdfFilePath());
        List<MyRDF> rdfList = JSON.parseArray(rdfJsonArrayString,MyRDF.class);

        Map<EntryType,Float> score = new HashMap<>();
        for(MyRDF rdf:rdfList){
            MyEntry head = rdf.getHead();
            MyEntry tail = rdf.getTail();
            Map<EntryType,Float> headMap = new HashMap<>();
            Map<EntryType,Float> tailMap = new HashMap<>();
            Neo4jHelper.newInstance().scoreHeadOrTailEntry(head,typeIntegerMap, Direction.OUTGOING,headMap);
            Neo4jHelper.newInstance().scoreHeadOrTailEntry(tail,typeIntegerMap, Direction.INCOMING,tailMap);
            for(Map.Entry<EntryType,Float> entryHead:headMap.entrySet()){
                float sc;
                if(tailMap.containsKey(entryHead.getKey())){
                    sc = tailMap.get(entryHead.getKey()) + entryHead.getValue();

                }
                else{
                    sc = entryHead.getValue();
                }

                if(score.containsKey(entryHead.getKey())){
                    float old = score.get(entryHead.getKey());
                    score.put(entryHead.getKey(),sc+old);
                }
                else{
                    score.put(entryHead.getKey(),sc);
                }

            }

            for(Map.Entry<EntryType,Float> entryTail:tailMap.entrySet()){
                if(headMap.containsKey(entryTail.getKey())){
                    continue;
                }
                else{
                    float sc = entryTail.getValue();
                    if(score.containsKey(entryTail.getKey())){
                        float old = score.get(entryTail.getKey());
                        score.put(entryTail.getKey(),sc+old);
                    }
                    else{
                        score.put(entryTail.getKey(),sc);
                    }
                }
            }
        }
        float total = 0;
        for(Map.Entry<EntryType,Float> entry:score.entrySet()){
            total += entry.getValue();
        }
        if(total == 0){
            return null;
        }
        else{
            for(Map.Entry<EntryType,Float> entry:score.entrySet()){
                score.put(entry.getKey(),entry.getValue() / total);
            }
        }
        List<Map.Entry<EntryType,Float>> result = new ArrayList<>(score.entrySet());
        Comparator<Map.Entry<EntryType,Float>> comparator = (o1, o2) ->{
          if(o1.getValue() > o2.getValue())
              return -1;
          else if(o1.getValue() < o2.getValue())
              return 1;
          else
              return 0;
        };
        result.sort(comparator);
        return result;
    }
}
