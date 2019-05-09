package hit.zhou.execute;

import hit.zhou.common.bean.*;
import hit.zhou.common.tools.FileUtil;
import hit.zhou.common.tools.LtpBaseOpLocal;
import hit.zhou.keyword.TFIDF;
import hit.zhou.ltp.NLP;
import hit.zhou.relationship.RelationShipGet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Step4 {
//    private static String[] entrys = {
//            "港口",
//            "岸线",
//            "规划",
//            "州",
//            "交通",
//            "总体",
//            "运输",
//            "港区",
//            "部门",
//            "规划区"
//    };

    private static String[] entrys = {
            "检察院",
            "被告人",
            "嫌疑人",
            "侦查",
            "案件",
            "人民法院",
            "辩护人",
            "审判",
            "犯罪"
    };



    public static void main(String[] args) throws IOException,InterruptedException, ExecutionException {
        LtpBaseOpLocal ltpBaseOpLocal = new LtpBaseOpLocal("C:/Users/zhou/Desktop/3.4.0/ltp_data_v3.4.0/ltp_data_v3.4.0/");
        Dir<PageForNlp> dir2 = new Dir<>(null,"C:/Users/zhou/Desktop/loyalFinal/",PageForNlp::new);
        NLP nlp = new NLP();
        nlp.buildPages(dir2,ltpBaseOpLocal,"C:/Users/zhou/Desktop/nlpResult/");
//        PageForNlp pageForNlp = new PageForNlp(null,"C:\\Users\\zhou\\Desktop\\loyalFinal\\地方政府规章","《四川省港口管理条例》实施办法.txt");
//        pageForNlp.setNlpResultPath("C:\\Users\\zhou\\Desktop\\nlpResult\\loyalFinal\\地方政府规章\\《四川省港口管理条例》实施办法.txt\\page_deal_result.txt");
        PageForNlp pageForNlp = new PageForNlp(null,"C:\\Users\\zhou\\Desktop\\loyalFinal\\法律","中华人民共和国刑事诉讼法 .txt");
        pageForNlp.setNlpResultPath("C:\\Users\\zhou\\Desktop\\nlpResult\\loyalFinal\\法律\\中华人民共和国刑事诉讼法 .txt\\page_deal_result.txt");

        RelationShipGet relationShipGet = new RelationShipGet();
//        relationShipGet.relationshipGet("中国","国家",pageForNlp);

        Dir<PageForKeyWord> dir1 = new Dir<>(null,"C:/Users/zhou/Desktop/loyalFinal/",PageForKeyWord::new);
        TFIDF TFIDF = new TFIDF("C:/Users/zhou/Desktop/stop_words.txt");
//        TFIDF.buildMap2Count(dir1,ltpBaseOpLocal,"C:/Users/zhou/Desktop/loyalResult/");
        TFIDF.buildKeyWord(10,false,dir1,"C:/Users/zhou/Desktop/loyalResult/");

        List<Dir<PageForKeyWord>> dir1childDirList = dir1.getDirCihldList();
        List<Dir<PageForNlp>> dir2childDirList = dir2.getDirCihldList();
        int length = dir2childDirList.size() > dir1childDirList.size() ? dir1childDirList.size() : dir2childDirList.size();
        for(int i = 0;i<length;i++) {
            List<PageForKeyWord> fileList1 = dir1childDirList.get(i).getFileList();
            List<PageForNlp> fileList2 = dir2childDirList.get(i).getFileList();
            int length2 = fileList2.size() > fileList1.size() ? fileList1.size() : fileList2.size();
            for (int j = 0; j < length2; j++) {
                List<WordCount<Float>> words = fileList1.get(j).getKeyWord();
                PageForNlp myPage = fileList2.get(j);
                for (int k = 0; k < words.size(); k++) {
                    for (int m = 0; m < words.size(); m++) {
                        if (k == m)
                            continue;
                        String head = words.get(k).getWord();
                        String tail = words.get(m).getWord();
                        relationShipGet.relationshipGet(head,tail,myPage);
                    }

                }
            }
        }
//        for(int i = 0;i<entrys.length;i++){
//            for(int j = 0;j<entrys.length;j++){
//                if(i == j)
//                    continue;
//                String head = entrys[i];
//                String tail = entrys[j];
//                relationShipGet.relationshipGet(head,tail,pageForNlp);
//            }
//        }
        float sumRate = 0;

        for(Map.Entry<Relation,Relation> entry:relationShipGet.getRelationAll().entrySet()){
            Relation relation = entry.getValue();
            sumRate += relation.getTruthRate();
        }

        float totalNum = relationShipGet.getRelationAll().size();
        for(Map.Entry<Relation,Relation> entry:relationShipGet.getRelationAll().entrySet()){
            Relation relation = entry.getValue();
            if(relation.getTruthRate() > (sumRate / totalNum )){
                writeRRR(relation,"C:\\Users\\zhou\\Desktop\\temp\\relation.txt");
            }
        }

        return;
    }

    public static void writeRRR(Relation relation,String filePath){
        StringBuilder relationStr = new StringBuilder();
        relationStr.append(relation.getHead());
        relationStr.append("\t");
        relationStr.append(relation.getRelation());
        relationStr.append("\t");
        relationStr.append(relation.getTail());
        relationStr.append("\t");
        relationStr.append(relation.getTruthRate());
        relationStr.append("\t");
        StringBuilder sentenceBuilder = new StringBuilder();
        for(Sentence sentence:relation.getSentenceList()){
            for(Word word : sentence.getWordsList()){
                sentenceBuilder.append(word.getWordString());
            }
            sentenceBuilder.append("\t");
        }
        relationStr.append(sentenceBuilder.toString());
        relationStr.append("\r\n");
        FileUtil.saveTest(filePath,relationStr.toString().getBytes(),true);
    }
}
