package hit.zhou.common.bean;

import hit.zhou.common.tools.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Dir {
    private String dirPath;
    private List<MyFile> fileList;
    private Dir parent;
    private List<Dir> dirCihldList;
    private String dirName;
    private String fileType;

    private static final String TYPE_FILE_NAME = "type_file.txt";

    private static final String TYPE_FILE_NOT_EXIST = "type file is not exist";
    private static final String DIR_ERROR = "Your path is not exist";
    private static final String FILES_NULL = "Files are null";

    public Dir(Dir parent,String dirPath) throws IOException {
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
                fileList.add(new MyFile(this,dirPath,fileInDir.getName()));
            }
            else if(fileInDir.isDirectory()){
                System.out.println(fileInDir.getPath());
                System.out.println(fileInDir.getName());
                dirCihldList.add(new Dir(this,fileInDir.getPath() + "/"));
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

    public List<Dir> getDirCihldList() {
        return  dirCihldList;
    }

    public String getFileType() {
        return fileType;
    }

    public String getDirName() {
        return dirName;
    }


    public Dir getParent() {
        return parent;
    }
}
