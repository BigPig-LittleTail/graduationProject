package hit.zhou.classification;

import hit.zhou.graph.EntryType;
import hit.zhou.graph.basic.PassageNode;
import hit.zhou.graph.basic.PassageTree;
import hit.zhou.graph.tools.LtpBaseOpLocal;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class RealClassification {
    private static final String TEMP_FILE_PATH= "D:\\graduation\\testMajor\\testMidResult\\";
//    private static final String TEMP_FILE_PATH= "D:\\graduation\\test\\testGraphNeed\\";


    private static final String NLP_FILE_NAME = "nlp_result.txt";
    private static final String RDF_FILE_NAME = "rdf_result.txt";

    private static final Map<EntryType,Integer> typeIntegerMap = new HashMap<EntryType, Integer>(){{
        put(EntryType.体育,0);
        put(EntryType.娱乐,1);
        put(EntryType.家居,2);
        put(EntryType.彩票,3);
        put(EntryType.房产,4);
        put(EntryType.教育,5);
        put(EntryType.时尚,6);
        put(EntryType.时政,7);
        put(EntryType.星座,8);
        put(EntryType.游戏,9);
        put(EntryType.社会,10);
        put(EntryType.科技,11);
        put(EntryType.股票,12);
        put(EntryType.财经,13);
    }
    };
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        LtpBaseOpLocal ltpBaseOpLocal = new LtpBaseOpLocal("C:/Users/zhou/Desktop/3.4.0/ltp_data_v3.4.0/ltp_data_v3.4.0/");
        PassageTree passageTree = new PassageTree();
        passageTree.buildPassageTree("D:\\graduation\\testMajor\\testSourse\\", false);
        passageTree.nlp(ltpBaseOpLocal, TEMP_FILE_PATH, NLP_FILE_NAME, false);
        passageTree.rdf(TEMP_FILE_PATH, RDF_FILE_NAME, false);
        List<PassageNode> passageNodesLevel3 = passageTree.getPassageNodeListByLevel(3);
        Map<EntryType,Integer> countMap = countMap(passageNodesLevel3);
        Map<PassageNode,Map<EntryType,Double>> classificationScore = ClassifyHelper.buildPassageNodeScoreMapByClassify(passageNodesLevel3,"D:\\graduation\\model\\my_model\\",14);
        Map<PassageNode,Map<EntryType,Float>> graphScore = GraphScoreHelper.buildPassageNodeScoreMapByGraph(passageNodesLevel3,typeIntegerMap);

        errorClassificationAndGraph(passageNodesLevel3,countMap,classificationScore,graphScore,0.5,0.5);
        errorClassificationAndGraph(passageNodesLevel3,countMap,classificationScore,graphScore,0.6,0.4);
        errorClassificationAndGraph(passageNodesLevel3,countMap,classificationScore,graphScore,0.7,0.3);
        errorClassificationAndGraph(passageNodesLevel3,countMap,classificationScore,graphScore,0.8,0.2);
        errorClassificationAndGraph(passageNodesLevel3,countMap,classificationScore,graphScore,0.9,0.1);



        errorClassificationAndGraph(passageNodesLevel3,countMap,classificationScore,graphScore,1.0,0.0);
    }

    public static Map<EntryType,Integer> countMap(List<PassageNode> passageNodes){
        Map<EntryType,Integer> countMap = new HashMap<>();
        for(PassageNode passageNode:passageNodes){
            if(countMap.containsKey(passageNode.getType())){
                int old = countMap.get(passageNode.getType());
                countMap.put(passageNode.getType(),old + 1);
            }
            else {
                countMap.put(passageNode.getType(),1);
            }
        }
        return countMap;
    }

    public static void errorClassificationAndGraph(List<PassageNode> passageNodes,Map<EntryType,Integer> countMap,
                                                   Map<PassageNode,Map<EntryType,Double>> classificationScore,Map<PassageNode,Map<EntryType,Float>> graphScore,
                                                   double cliassifiactionQ,double graphQ){
        Map<EntryType,Integer> trueMap = new HashMap<>();
        Map<EntryType,Integer> ACMap = new HashMap<>();

        for(PassageNode passageNode:passageNodes){
            EntryType trueType = passageNode.getType();


            Map<EntryType,Double> combineMap;
            if (graphScore.containsKey(passageNode)){
                combineMap = combineClassificationAndGraph(classificationScore.get(passageNode),graphScore.get(passageNode),
                        cliassifiactionQ,graphQ);
            }
            else {
                combineMap = classificationScore.get(passageNode);
            }

            List<Map.Entry<EntryType,Double>> combineList = new ArrayList<>(combineMap.entrySet());
            Comparator<Map.Entry<EntryType,Double>> comparator = (o1, o2) ->{
                if(o1.getValue() > o2.getValue())
                    return -1;
                else if(o1.getValue() < o2.getValue())
                    return 1;
                else
                    return 0;
            };
            combineList.sort(comparator);

            EntryType myType = combineList.get(0).getKey();

            if(ACMap.containsKey(myType)){
                int num = ACMap.get(myType);
                ACMap.put(myType,num + 1);
            }
            else {
                ACMap.put(myType,1);
            }

            if(trueType.equals(myType)){
                if(trueMap.containsKey(trueType)){
                    int num = trueMap.get(trueType);
                    trueMap.put(trueType,num + 1);
                }
                else{
                    trueMap.put(trueType,1);
                }
            }
        }

        System.err.println(cliassifiactionQ + ":" + graphQ);

        for(Map.Entry<EntryType,Integer> entry:trueMap.entrySet()){
            double trueNum = entry.getValue();
            double ABNum = 0;
            double ACNum = 0;
            if(countMap.containsKey(entry.getKey())){
                ABNum = countMap.get(entry.getKey());
            }
            if(ACMap.containsKey(entry.getKey())){
                ACNum = ACMap.get(entry.getKey());
            }
//            System.err.println(entry.getKey() + "\t" +"召回率:"+(trueNum /ABNum) +"\t"+"准确率:"+(trueNum/ACNum));
            System.err.println(entry.getKey() + "\t" +(trueNum /ABNum) +"\t"+(trueNum/ACNum));
        }

    }

    private static Map<EntryType,Double> combineClassificationAndGraph(Map<EntryType,Double> cliassificationMap,Map<EntryType,Float> graphMap,
                                                                       double cliassifiactionQ,double graphQ){
        Map<EntryType,Double> combineMap = new HashMap<>();
        for (Map.Entry<EntryType,Double> entryC:cliassificationMap.entrySet()) {
            combineMap.put(entryC.getKey(), entryC.getValue() * cliassifiactionQ);
        }
        for(Map.Entry<EntryType,Float> entryG:graphMap.entrySet()){
            double old = combineMap.get(entryG.getKey());
            double newData = old + graphQ * entryG.getValue();
            combineMap.put(entryG.getKey(),newData);
        }
        return combineMap;
    }



}
