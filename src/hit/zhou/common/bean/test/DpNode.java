package hit.zhou.common.bean.test;

import hit.zhou.common.bean.Word;

import java.util.ArrayList;
import java.util.List;

public class DpNode {
    Word word;
    List<DpNode> children;
    public DpNode(){
        children = new ArrayList<>();
    }

    public DpNode(Word word){
        this.word = word;
        children = new ArrayList<>();
    }

}
