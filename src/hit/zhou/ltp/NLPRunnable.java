package hit.zhou.ltp;

import com.alibaba.fastjson.JSON;
import hit.zhou.common.bean.PageForNlp;
import hit.zhou.common.bean.Sentence;
import hit.zhou.common.tools.FileUtil;
import hit.zhou.common.tools.LtpBaseOpLocal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NLPRunnable implements Runnable {
    private PageForNlp pageForNlp;
    private LtpBaseOpLocal ltpBaseOpLocal;
    private String savePath;

    public NLPRunnable(PageForNlp pageForNlp, LtpBaseOpLocal ltpBaseOpLocal, String savePath){
        this.pageForNlp = pageForNlp;
        this.ltpBaseOpLocal = ltpBaseOpLocal;
        this.savePath = savePath;
    }

    @Override
    public void run() {
        try {
            String fileString = FileUtil.readString(pageForNlp.getPath());
            List<Sentence> list = buildSentencesToPage(fileString);
            savePage(list);

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void savePage(List<Sentence> sentenceList){
        String jsonArrayString = JSON.toJSONString(sentenceList);
        System.out.println(jsonArrayString);
        FileUtil.saveTest(savePath,jsonArrayString.getBytes(),false);
    }


    private List<Sentence> buildSentencesToPage(String fileString) {
        List<String> stringList = new ArrayList<>();
        ltpBaseOpLocal.splitSentence(fileString, stringList);
        List<Sentence> sentenceList = new ArrayList<>();
        for(String sentenceInFile:stringList){
            if(sentenceInFile.equals(""))
                continue;
            List<String> wordList = new ArrayList<>();
            List<String> posList = new ArrayList<>();
            List<Integer> headList = new ArrayList<>();
            List<String> dpList = new ArrayList<>();
            ltpBaseOpLocal.segmentor(sentenceInFile,wordList);
            if(wordList.size() > 100){
                continue;
            }

            ltpBaseOpLocal.postagger(wordList,posList);
            ltpBaseOpLocal.parser(wordList,posList,headList,dpList);
            Sentence sentence = new Sentence();
            sentence.buildWordList(wordList,posList,headList,dpList);

            sentenceList.add(sentence);
//            pageForNlp.addSentence(sentence);
        }
        return sentenceList;
    }
}
