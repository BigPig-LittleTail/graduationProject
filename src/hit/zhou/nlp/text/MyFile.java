package hit.zhou.nlp.text;

import hit.zhou.hepler.FileUtil;

import java.util.List;
import java.util.Map;

public class MyFile {
    private String dirPath;
    private String fileName;
    private Map<String,Float> word2Rate;
    private List<WordCount<Float>> keyWord;

    public MyFile(String dirPath,String fileName){
        this.dirPath = dirPath;
        this.fileName = fileName;
    }

    public void setWord2Rate(Map<String, Float> word2Rate) {
        this.word2Rate = word2Rate;
    }

    public void setKeyWord(List<WordCount<Float>> keyWord) {
        this.keyWord = keyWord;
    }

    public Map<String, Float> getWord2Rate() {
        return word2Rate;
    }

    public String getDirPath() {
        return dirPath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPath(){
        return dirPath + fileName;
    }

    public void saveKeyWordResultToFile(String dirPath,String fileName){
        String writeString = "";
        for(WordCount<Float> word2KeyWordRate:keyWord){
            writeString = writeString + word2KeyWordRate.getWord() + "," + word2KeyWordRate.getcountOrRate() + "\r\n";
        }
        System.out.println(writeString);
        FileUtil.save(dirPath,fileName,writeString.getBytes(),false);
    }

}
