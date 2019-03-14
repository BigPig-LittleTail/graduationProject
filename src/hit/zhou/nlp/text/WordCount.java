package hit.zhou.nlp.text;

public class WordCount<T> {
    private String word;
    private T countOrRate;

    public WordCount(String word,T countOrRate){
        this.word = word;
        this.countOrRate = countOrRate;
    }

    public void setWord(String word){
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setcountOrRate(T countOrRate) {
        this.countOrRate = countOrRate;
    }

    public T getcountOrRate() {
        return countOrRate;
    }

}
