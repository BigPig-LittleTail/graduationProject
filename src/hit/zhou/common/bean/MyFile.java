package hit.zhou.common.bean;

import java.util.List;

public class MyFile {
    private String dirPath;
    private String fileName;
    private List<WordCount<Float>> keyWord;

    public MyFile(String dirPath,String fileName){
        this.dirPath = dirPath;
        this.fileName = fileName;
    }


    public void setKeyWord(List<WordCount<Float>> keyWord) {
        this.keyWord = keyWord;
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

    public List<WordCount<Float>> getKeyWord() {
        return keyWord;
    }

    public int getKeyWordListSize(){
        return keyWord.size();
    }
}
