package hit.zhou.common.bean.test;

import com.alibaba.fastjson.JSON;
import hit.zhou.common.bean.Sentence;
import hit.zhou.common.tools.FileUtil;
import hit.zhou.common.tools.LtpBaseOpLocal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PassageNode extends File {
    private String passagePath;
    private String nlpFilePath;
    private String rdfFilePath;
    private String entryCountFilePath;
    private String keyWordFilePath;
    private List<PassageNode> children;

    // 用来正则匹配句子开始的诸如"第二条"，以便提升NLP预处理的效果。
    private static final String RE_PATTERN = "^\\u7b2c([\\u4e00|\\u4e8c|\\u4e09|\\u56db|\\u4e94|\\u516d|\\u4e03|\\u516b|\\u4e5d|\\u96f6|\\u5341|\\u767e]{1,4})\\u6761";

    public static final String HEAD_STRING = "head_map";
    public static final String TAIL_STRING = "tail_map";

    public PassageNode(String path) {
        super(path);
        this.children = new ArrayList<>();
        this.passagePath = path;
    }

    public void setPassagePath(String passagePath) {
        this.passagePath = passagePath;
    }

    public void setNlpFilePath(String nlpFilePath) {
        this.nlpFilePath = nlpFilePath;
    }

    public void setRdfFilePath(String rdfFilePath) {
        this.rdfFilePath = rdfFilePath;
    }

    public void setEntryCountFilePath(String entryCountFilePath) {
        this.entryCountFilePath = entryCountFilePath;
    }

    public void setKeyWordFilePath(String keyWordFilePath) {
        this.keyWordFilePath = keyWordFilePath;
    }

    public String getPassagePath() {
        return passagePath;
    }


    public String getNlpFilePath() {
        return nlpFilePath;
    }

    public String getRdfFilePath() {
        return rdfFilePath;
    }

    public String getEntryCountFilePath() {
        return entryCountFilePath;
    }

    public String getKeyWordFilePath() {
        return keyWordFilePath;
    }


    public void addChildPassageNode(PassageNode childPassageNode){
        children.add(childPassageNode);
    }

    public int getChildPassageNodeNumber(){
        return children.size();
    }

    public List<PassageNode> getChildren() {
        return children;
    }

    public void nlp(LtpBaseOpLocal ltpBaseOpLocal, String nlpFilePath, boolean reNlp) throws IOException {
        File nlpFile = new File(nlpFilePath);
        if(reNlp || !nlpFile.exists()){
            nlpExecutor(ltpBaseOpLocal, nlpFilePath);
            setNlpFilePath(nlpFilePath);
        }
    }

    protected void nlpExecutor(LtpBaseOpLocal ltpBaseOpLocal, String nlpFilePath) throws IOException {
        String passageStr = FileUtil.readString(passagePath);
        List<String> sentenceStringList = new ArrayList<>();
        ltpBaseOpLocal.splitSentence(passageStr,sentenceStringList);
        List<Sentence> sentences = new ArrayList<>();
        for(String sentenceString:sentenceStringList){
            if(sentenceString.equals(""))
                continue;
            sentenceString = sentenceString.replaceFirst(RE_PATTERN,"");
            sentences.add(transSentence(ltpBaseOpLocal, sentenceString));
        }
        String jsonArrayString = JSON.toJSONString(sentences);
        FileUtil.saveTest(nlpFilePath,jsonArrayString.getBytes(),false);
    }

    private Sentence transSentence(LtpBaseOpLocal ltpBaseOpLocal, String sentenceString) {
        List<String> wordList = new ArrayList<>();
        List<String> posList = new ArrayList<>();
        List<Integer> headList = new ArrayList<>();
        List<String> dpList = new ArrayList<>();
        ltpBaseOpLocal.segmentor(sentenceString,wordList);
        ltpBaseOpLocal.postagger(wordList,posList);
        ltpBaseOpLocal.parser(wordList,posList,headList,dpList);
        Sentence sentence = new Sentence();
        sentence.buildWordList(wordList,posList,headList,dpList);
        return sentence;
    }

    public void rdf(String rdfFilePath,boolean isReRdf) throws IOException{
        File file = new File(rdfFilePath);
        if(isReRdf || !file.exists()){
            rdfExecutor(rdfFilePath);
            setRdfFilePath(rdfFilePath);
        }
    }

    protected void rdfExecutor(String rdfFilePath) throws IOException {
        if(nlpFilePath == null){
            throw new IllegalArgumentException();
        }
        String nlpJsonArrayString = FileUtil.readString(nlpFilePath);
        List<Sentence> sentenceList = JSON.parseArray(nlpJsonArrayString,Sentence.class);

        List<MyRDF> totalRDFs = new ArrayList<>();
        for(Sentence sentence:sentenceList){
            DpTree dpTree = new DpTree();
            dpTree.buildDpTree(sentence);
            totalRDFs.addAll(dpTree.getRelation());
        }

        String RDFJsonArrayString = JSON.toJSONString(totalRDFs);
        FileUtil.saveTest(rdfFilePath,RDFJsonArrayString.getBytes(),false);
    }

    public void countEntry(String countEntryPath,boolean isReCountEntry) throws IOException{
        File file = new File(countEntryPath);
        if(isReCountEntry || !file.exists() ){
            countEntryExecutor(countEntryPath);
            setEntryCountFilePath(countEntryPath);
        }
    }

    protected void countEntryExecutor(String countEntryPath) throws IOException {
        if(rdfFilePath == null){
            throw new IllegalArgumentException();
        }
        String rdfJsonArrayString = FileUtil.readString(rdfFilePath);
        List<MyRDF> rdfList = JSON.parseArray(rdfJsonArrayString,MyRDF.class);

        Map<String,Float> head2Count = new HashMap<>();
        Map<String,Float> tail2Count = new HashMap<>();

        for(MyRDF rdf:rdfList){
            MyEntry head = rdf.getHead();
            MyEntry tail = rdf.getTail();

            if(head2Count.containsKey(head.getWordString())){
                float count = head2Count.get(head.getWordString());
                count++;
                head2Count.put(head.getWordString(),count);
            }
            else{
                head2Count.put(head.getWordString(),1.0f);
            }

            if(tail2Count.containsKey(tail.getWordString())){
                float count = tail2Count.get(tail.getWordString());
                count++;
                tail2Count.put(tail.getWordString(),count);
            }
            else{
                tail2Count.put(tail.getWordString(),1.0f);
            }
        }

        Map<String,Map<String,Float>> countEntryMap = new HashMap<>();
        countEntryMap.put(HEAD_STRING,head2Count);
        countEntryMap.put(TAIL_STRING,tail2Count);
        String entryCountJsonMapString = JSON.toJSONString(countEntryMap);

        FileUtil.saveTest(countEntryPath,entryCountJsonMapString.getBytes(),false);
    }



}
