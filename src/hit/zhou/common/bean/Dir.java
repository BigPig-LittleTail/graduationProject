package hit.zhou.common.bean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Dir {
    private String dirPath;
    private List<MyFile> fileList;
    private static final String DIR_ERROR = "Your path is not exist";
    private static final String FILES_NULL = "Files are null";

    public Dir(String dirPath){
        File file = new File(dirPath);
        if(!file.exists()){
            throw new IllegalArgumentException(DIR_ERROR);
        }
        this.dirPath = dirPath;
        this.fileList = new ArrayList<>();
        File[] files = file.listFiles();
        if(files == null){
            throw new IllegalArgumentException(FILES_NULL);
        }
        for(File fileInDir:files){
            if(fileInDir.isFile()){
                fileList.add(new MyFile(dirPath,fileInDir.getName()));
            }
            else {

            }
        }
    }

    public int getFileCount() {
        return fileList.size();
    }

    public List<MyFile> getFileList() {
        return fileList;
    }

    public String getDirPath() {
        return dirPath;
    }

    public boolean isFileExist(String fileName){
        for (MyFile myFile : fileList){
            if(myFile.getFileName().equals(fileName)){
                return true;
            }
        }
        return false;
    }

    public MyFile getFileByName(String fileName){
        for(MyFile myFile:fileList){
            if(myFile.getFileName().equals(fileName))
                return myFile;
        }
        return null;
    }

}
