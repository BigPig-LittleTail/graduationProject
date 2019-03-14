package hit.zhou.nlp.ltp;

import edu.hit.ir.ltp4j.*;

import java.util.List;

public class LtpBaseOpLocal{
    //分句
    private SplitSentence baseSplitSentenceImp;
    // 分词
    private Segmentor baseSegmentorImp;
    // 词性标注
    private Postagger basePostaggerImp;
    // 命名实体识别
    private NER baseNerImp;
    // 依存句法分析
    private Parser baseParserImp;
    // model路径
    private final String modelPath;


    public LtpBaseOpLocal(final String modelPath){
        this.modelPath = modelPath;
        baseSplitSentenceImp = new SplitSentence();
        baseSegmentorImp = new Segmentor();
        basePostaggerImp = new Postagger();
        baseNerImp = new NER();
        baseParserImp = new Parser();
    }

    public void splitSentence(String pager,List<String> stringList){
        baseSplitSentenceImp.splitSentence(pager,stringList);
    }

    public int segmentor(String sentence, List<String> words){
        baseSegmentorImp.create(modelPath + "cws.model");
        baseSegmentorImp.segment(sentence,words);
        baseSegmentorImp.release();
        return words.size();
    }

    public int postagger(List<String> words,List<String> posttag){
        basePostaggerImp.create(modelPath + "pos.model");
        basePostaggerImp.postag(words,posttag);
        basePostaggerImp.release();
        return posttag.size();
    }

    public int ner(List<String> words,List<String> posttag,List<String> ner){
        baseNerImp.create(modelPath + "ner.model");
        baseNerImp.recognize(words,posttag,ner);
        baseNerImp.release();
        return  ner.size();
    }

    public int parser(List<String> words,List<String> posttag,List<Integer> heads,List<String> deprels){
        baseParserImp.create(modelPath + "parser.model");
        baseParserImp.parse(words,posttag,heads,deprels);
        baseParserImp.release();
        return heads.size();
    }

}