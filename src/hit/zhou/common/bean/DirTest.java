package hit.zhou.common.bean;

import hit.zhou.common.tools.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DirTest{
    private String dirPath;
    private List<MyFileTest> fileList;
    private DirTest parent;
    private List<DirTest> dirCihldList;
    private List<WordCount<Float>> keyWordList;
    private String dirName;
    private String fileType;

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

    public DirTest(DirTest parent, String dirPath) throws IOException {
        File dirFile = new File(dirPath);
        if(!dirFile.exists()){
            throw new IllegalArgumentException(DIR_ERROR);
        }
        this.dirPath = dirPath;
        this.dirName = dirFile.getName();
        this.fileList = new ArrayList<>();
        this.parent = parent;
        this.dirCihldList = new ArrayList<>();

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
                fileList.add(new MyFileTest(this,dirPath,fileInDir.getName()));
            }
            else if(fileInDir.isDirectory()){
                dirCihldList.add(new DirTest(this,fileInDir.getPath() + "/"));
            }
        }

    }

    public int getFileCount() {
        return fileList.size();
    }

    public List<MyFileTest> getFileList() {
        return fileList;
    }

    public String getDirPath() {
        return dirPath;
    }

    public boolean isFileExist(String fileName){
        for (MyFileTest myFile : fileList){
            if(myFile.getFileName().equals(fileName)){
                return true;
            }
        }
        return false;
    }

    public MyFileTest getFileByName(String fileName){
        for(MyFileTest myFile:fileList){
            if(myFile.getFileName().equals(fileName))
                return myFile;
        }
        return null;
    }

    public List<DirTest> getDirCihldList() {
        return  dirCihldList;
    }

    public String getFileType() {
        return fileType;
    }

    public String getDirName() {
        return dirName;
    }


    public DirTest getParent() {
        return parent;
    }


}
