package hit.zhou.classification;


import hit.zhou.graph.EntryType;
import hit.zhou.graph.basic.PassageNode;
import hit.zhou.graph.basic.PassageTree;
import org.thunlp.text.classifiers.BasicTextClassifier;
import org.thunlp.text.classifiers.ClassifyResult;
import org.thunlp.text.classifiers.LinearBigramChineseTextClassifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Classifacation {

    public static void main(String[] args) throws IOException
    {
        PassageTree passageTree = new PassageTree();
        passageTree.buildPassageTree("D:\\graduation\\test\\testGraph\\", false);
        List<PassageNode> passageNodes = passageTree.getPassageNodeListByLevel(3);
        runLoadModelAndUse(passageNodes);
//        train();
    }


    public static void train(){
        // 新建分类器对象
        BasicTextClassifier classifier = new BasicTextClassifier();

        // 设置参数
        String defaultArguments = ""
                + "-train D:\\graduation\\classificationMajor\\ "  // 设置您的训练路径，这里的路径只是给出样例
                //	+ "-l C:\\Users\\do\\workspace\\TestJar\\my_novel_model "
                //	+ "-cdir E:\\Corpus\\书库_cleared "
                //	+ "-n 1 "
                // + "-classify E:\\Corpus\\书库_cleared\\言情小说 "  // 设置您的测试路径。一般可以设置为与训练路径相同，即把所有文档放在一起。
                + "-d1 1 "  // 前70%用于训练
                + "-f 5000 " // 设置保留特征数，可以自行调节以优化性能
                +  "-s D:\\graduation\\model\\my_model"  // 将训练好的模型保存在硬盘上，便于以后测试或部署时直接读取模型，无需训练
                ;

        // 初始化
        classifier.Init(defaultArguments.split(" "));

        // 运行
        classifier.runAsBigramChineseTextClassifier();


    }

    public static void runLoadModelAndUse(List<PassageNode> passageNodes) {
        // 新建分类器对象
        BasicTextClassifier classifier = new BasicTextClassifier();

        // 设置分类种类，并读取模型
        classifier.loadCategoryListFromFile("D:\\graduation\\model\\my_model\\category");
        classifier.setTextClassifier(new LinearBigramChineseTextClassifier(classifier.getCategorySize()));
        classifier.getTextClassifier().loadModel("D:\\graduation\\model\\my_model\\");

        int topN = 4;
        Map<EntryType,Integer> typeIntegerMap = new HashMap<>();
        Map<EntryType,Integer> errorMap = new HashMap<>();
        for(PassageNode passageNode:passageNodes){
            ClassifyResult[] result = classifier.classifyFile(passageNode.getPassagePath(),topN);
            String typeString = classifier.getCategoryName(result[0].label);
            EntryType type = EntryType.valueOf(typeString);


            if(typeIntegerMap.containsKey(passageNode.getType())){
                int num = typeIntegerMap.get(passageNode.getType());
                typeIntegerMap.put(passageNode.getType(),num + 1);
            }
            else{
                typeIntegerMap.put(passageNode.getType(),1);
            }

            if(!type.equals(passageNode.getType())){
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

}
