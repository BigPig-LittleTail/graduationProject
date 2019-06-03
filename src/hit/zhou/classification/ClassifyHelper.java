package hit.zhou.classification;

import hit.zhou.graph.EntryType;
import hit.zhou.graph.basic.PassageNode;
import org.thunlp.text.classifiers.BasicTextClassifier;
import org.thunlp.text.classifiers.ClassifyResult;
import org.thunlp.text.classifiers.LinearBigramChineseTextClassifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassifyHelper {

    public static void main(String[] args){
        train("D:\\graduation\\classificationMajor\\",5000,"D:\\graduation\\model\\my_model\\");
    }


    public static void train(String trainPath,int featureNum,String savePath){
        // 新建分类器对象
        BasicTextClassifier classifier = new BasicTextClassifier();

        // 设置参数
        String defaultArguments = ""
                + "-train " + trainPath  // 设置您的训练路径，这里的路径只是给出样例
                //	+ "-l C:\\Users\\do\\workspace\\TestJar\\my_novel_model "
                //	+ "-cdir E:\\Corpus\\书库_cleared "
                //	+ "-n 1 "
                // + "-classify E:\\Corpus\\书库_cleared\\言情小说 "  // 设置您的测试路径。一般可以设置为与训练路径相同，即把所有文档放在一起。
                + "-d1 1 "  // 前70%用于训练
                + "-f "+ featureNum // 设置保留特征数，可以自行调节以优化性能
                +  "-s " + savePath // 将训练好的模型保存在硬盘上，便于以后测试或部署时直接读取模型，无需训练
                ;

        // 初始化
        classifier.Init(defaultArguments.split(" "));

        // 运行
        classifier.runAsBigramChineseTextClassifier();


    }



    public static Map<PassageNode,Map<EntryType,Double>> buildPassageNodeScoreMapByClassify(List<PassageNode> passageNodes,String modelPath,int topN) {
        BasicTextClassifier classifier = new BasicTextClassifier();
        classifier.loadCategoryListFromFile(modelPath + "category");
        classifier.setTextClassifier(new LinearBigramChineseTextClassifier(classifier.getCategorySize()));
        classifier.getTextClassifier().loadModel(modelPath);

        Map<PassageNode,Map<EntryType,Double>> resultMap = new HashMap<>();
        for(PassageNode passageNode:passageNodes) {
            ClassifyResult[] result = classifier.classifyFile(passageNode.getPassagePath(), topN);
            Map<EntryType,Double> scoreMap = new HashMap<>();
            for(int i = 0;i < result.length;i++){
                String typeString = classifier.getCategoryName(result[i].label);
                EntryType type = EntryType.valueOf(typeString);
                scoreMap.put(type,result[i].prob);
            }
            resultMap.put(passageNode,scoreMap);
        }
        return resultMap;
    }

}
