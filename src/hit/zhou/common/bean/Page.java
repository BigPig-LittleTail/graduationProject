package hit.zhou.common.bean;

public class Page {
    private String dirPath;
    private String pageName;
    private Dir parent;

    public Page(Dir parent, String dirPath, String pageName) {
        this.parent = parent;
        this.dirPath = dirPath;
        this.pageName = pageName;
    }

    public Dir getParent() {
        return parent;
    }

    public String getDirPath() {
        return dirPath;
    }

    public String getPageName() {
        return pageName;
    }

    public String getPath(){
        return dirPath + pageName;
    }

    public void setParent(Dir parent) {
        this.parent = parent;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }


}
