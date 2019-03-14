package hit.zhou.nlp.text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Dir {
    private String dirPath;
    private List<MyFile> fileList;

    private static final String DIR_ERROR = "Your path is not exist";
    private static final String FILES_NULL = "Files are null";

    private static final String KEY_WORD_FILE_NAME = "key_word.txt";

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

    public void saveDirFileKeyWordResultToFile(String fileWantToSaveName,String saveToDirPath){
        if(isFileExist(fileWantToSaveName)){
            MyFile myFile = getFileByName(fileWantToSaveName);
            checkAndMakeDir(saveToDirPath);
            String fileRealResultPath = buildFileSaveDirPath(saveToDirPath,myFile.getFileName());
            myFile.saveKeyWordResultToFile(fileRealResultPath,KEY_WORD_FILE_NAME);
        }
    }

    public void saveDirAllFileKeyWordResult(String saveToDirPath){
        for(MyFile myFile:fileList){
            checkAndMakeDir(saveToDirPath);
            String fileRealResultPath = buildFileSaveDirPath(saveToDirPath,myFile.getFileName());
            myFile.saveKeyWordResultToFile(fileRealResultPath,KEY_WORD_FILE_NAME);
        }
    }

    private void checkAndMakeDir(String saveToDirPath) {
        File filedir = new File(saveToDirPath);
        if (!filedir.exists()) {
            filedir.mkdirs();
        }
    }

    private String buildFileSaveDirPath(String topDirPath,String fileName){
        return topDirPath + fileName + "/";
    }

}
