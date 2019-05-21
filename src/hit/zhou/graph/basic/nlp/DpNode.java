package hit.zhou.graph.basic.nlp;

import java.util.ArrayList;
import java.util.List;

public class DpNode {
    private Word word;
    private List<DpNode> children;
    public DpNode(){
        children = new ArrayList<>();
    }

    public DpNode(Word word){
        this.word = word;
        children = new ArrayList<>();
    }

    public List<DpNode> getChildren() {
        return children;
    }

    public Word getWord() {
        return word;
    }
}
