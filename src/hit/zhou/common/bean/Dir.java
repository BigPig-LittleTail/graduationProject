package hit.zhou.common.bean;

import hit.zhou.common.tools.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Dir<T extends Page>{
    private String dirPath;
    private List<T> fileList;
    private Dir<T> parent;
    private List<Dir<T>> dirCihldList;
    private List<WordCount<Float>> keyWordList;
    private String dirName;
    private String fileType;
    private PageCreater<T> builder;

    private int totalNum;
    private Map<String,Integer> word2Count;


    public List<WordCount<Float>> getKeyWordList() {
        return keyWordList;
    }

    public void setKeyWordList(List<WordCount<Float>> keyWordList) {
        this.keyWordList = keyWordList;
    }

    public Map<String, Integer> getWord2Count() {
        return word2Count;
    }

    public void setWord2Count(Map<String, Integer> word2Count) {
        this.word2Count = word2Count;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    private static final String TYPE_FILE_NAME = "type_file.txt";
    private static final String TYPE_FILE_NOT_EXIST = "type file is not exist";
    private static final String DIR_ERROR = "Your path is not exist";
    private static final String FILES_NULL = "Files are null";

    public Dir(Dir parent, String dirPath,PageCreater<T> builder) throws IOException {
        File dirFile = new File(dirPath);
        if(!dirFile.exists()){
            throw new IllegalArgumentException(DIR_ERROR);
        }
        this.dirPath = dirPath;
        this.dirName = dirFile.getName();
        this.fileList = new ArrayList<>();
        this.parent = parent;
        this.dirCihldList = new ArrayList<>();
        this.builder = builder;

        String typeFilePath = dirPath + TYPE_FILE_NAME;
        File typeFile = new File(typeFilePath);

        if(!typeFile.exists()){
            throw new IllegalArgumentException(TYPE_FILE_NOT_EXIST);
        }

        this.fileType = FileUtil.readString(typeFilePath);

        File[] files = dirFile.listFiles();
        if(files == null){
            throw new IllegalArgumentException(FILES_NULL);
        }
        for(File fileInDir:files){
            if(fileInDir.isFile() && !fileInDir.getName().equals(TYPE_FILE_NAME)){
                fileList.add(builder.create(this,dirPath,fileInDir.getName()));
            }
            else if(fileInDir.isDirectory()){
                dirCihldList.add(new Dir<>(this,fileInDir.getPath() + "/",builder));
            }
        }

    }

    public int getFileCount() {
        return fileList.size();
    }

    public List<T> getFileList() {
        return fileList;
    }

    public String getDirPath() {
        return dirPath;
    }

    

    public List<Dir<T>> getDirCihldList() {
        return  dirCihldList;
    }

    public String getFileType() {
        return fileType;
    }

    public String getDirName() {
        return dirName;
    }


    public Dir<T> getParent() {
        return parent;
    }


}
