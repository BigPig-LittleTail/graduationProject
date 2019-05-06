package hit.zhou.common.tools;

import edu.hit.ir.ltp4j.*;

import java.util.List;

public class LtpBaseOpLocal{
    //分句
    private SplitSentence baseSplitSentenceImp;
    // 分词
    private boolean isCreateSegmentor;
    private Segmentor baseSegmentorImp;
    // 词性标注
    private boolean isCreatePostaggerImp;
    private Postagger basePostaggerImp;
    // 命名实体识别
    private boolean isCreateNer;
    private NER baseNerImp;
    // 依存句法分析
    private boolean isCreateParser;
    private Parser baseParserImp;
    // model路径
    private final String modelPath;


    public LtpBaseOpLocal(final String modelPath){
        this.modelPath = modelPath;
        baseSplitSentenceImp = new SplitSentence();
        baseSegmentorImp = new Segmentor();
        isCreateSegmentor = false;
        basePostaggerImp = new Postagger();
        isCreatePostaggerImp = false;
        baseNerImp = new NER();
        isCreateNer = false;
        baseParserImp = new Parser();
        isCreateParser = false;
    }

    /**
     * 分句
     * @param pager 文章String
     * @param stringList 结果返回列表
     */
    public void splitSentence(String pager,List<String> stringList){
        baseSplitSentenceImp.splitSentence(pager,stringList);
    }

    public int segmentor(String sentence, List<String> words){
        if(!isCreateSegmentor){
            synchronized (this){
                if(!isCreateSegmentor){
                    baseSegmentorImp.create(modelPath + "cws.model");
                    isCreateSegmentor = true;
                }
            }
        }
        baseSegmentorImp.segment(sentence,words);
        return words.size();
    }

    public void releaseSegmentor(){
        if(isCreateSegmentor){
            synchronized (this){
                if(isCreateSegmentor){
                    baseSegmentorImp.release();
                    isCreateSegmentor = false;
                }
            }
        }
    }

    public int postagger(List<String> words,List<String> posttag){
        if(!isCreatePostaggerImp){
            synchronized (this){
                if(!isCreatePostaggerImp){
                    basePostaggerImp.create(modelPath + "pos.model");
                    isCreatePostaggerImp = true;
                }
            }
        }
        basePostaggerImp.postag(words,posttag);
        return posttag.size();
    }

    public void releasePos(){
        if(isCreatePostaggerImp){
            synchronized (this){
                if(isCreatePostaggerImp){
                    basePostaggerImp.release();
                    isCreatePostaggerImp = false;
                }
            }
        }
    }

    public int ner(List<String> words,List<String> posttag,List<String> ner){
        if(!isCreateNer){
            synchronized (this){
                if(!isCreateNer){
                    baseNerImp.create(modelPath + "ner.model");
                    isCreateNer = true;
                }

            }
        }
        baseNerImp.recognize(words,posttag,ner);
        return  ner.size();
    }

    public void releaseNer(){
        if(isCreateNer){
            synchronized (this){
                if(isCreateNer){
                    baseNerImp.release();
                    isCreateNer = false;
                }
            }
        }
    }

    public int parser(List<String> words,List<String> posttag,List<Integer> heads,List<String> deprels){
        if(!isCreateParser){
            synchronized (this){
                if(!isCreateParser){
                    baseParserImp.create(modelPath + "parser.model");
                    isCreateParser = true;
                }
            }

        }
        baseParserImp.parse(words,posttag,heads,deprels);
        return heads.size();
    }

    public void releaseParser(){
        if(isCreateParser){
            synchronized (this){
                if(isCreateParser){
                    baseParserImp.release();
                    isCreateParser = false;
                }
            }
        }
    }

}
