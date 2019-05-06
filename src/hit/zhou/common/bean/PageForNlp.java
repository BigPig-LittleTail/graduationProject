package hit.zhou.common.bean;

import java.util.ArrayList;
import java.util.List;

public class PageForNlp extends Page {
    private List<Sentence> sentences;
    private String nlpResultPath;
    public PageForNlp(Dir parent, String dirPath, String pageName) {
        super(parent, dirPath, pageName);
        this.sentences = new ArrayList<>();
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    public String getNlpResultPath() {
        return nlpResultPath;
    }

    public void setNlpResultPath(String nlpResultPath) {
        this.nlpResultPath = nlpResultPath;
    }

    public void addSentence(Sentence sentence){
        synchronized (this){
            sentences.add(sentence);
        }
    }

    public void setSentences(List<Sentence> sentences) {
        this.sentences = sentences;
    }
}
