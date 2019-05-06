package hit.zhou.common.bean;

import java.util.ArrayList;
import java.util.List;

public class MyEntry {
    private String entryName;
    private List<String> typeList;

    public MyEntry(String entryName){
        this.entryName = entryName;
        typeList = new ArrayList<>();
    }

    public String getEntryName() {
        return entryName;
    }

    public List<String> getTypeList() {
        return typeList;
    }

    public void addType(String type){
        typeList.add(type);
    }


}
