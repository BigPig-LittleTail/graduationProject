package hit.zhou.common.bean.test;

import hit.zhou.common.bean.Sentence;
import hit.zhou.common.bean.Word;

import java.util.ArrayList;
import java.util.List;

public class DpTree {
    private DpNode head;
    public DpTree(){
        head = null;
    }
    
    public void buildDpTree(Sentence sentence){
        List<Word> wordsList = sentence.getWordsList();
        DpNode[] treeNodes = new DpNode[wordsList.size()];
        head = new DpNode();
        for(int i = 0;i<wordsList.size();i++){
            Word nowWord = wordsList.get(i);
            if(treeNodes[i] == null){
                treeNodes[i] = new DpNode(nowWord);
            }
            int parentIndex = nowWord.getParent() - 1;

            if(parentIndex == -1){
                head.children.add(treeNodes[i]);
                continue;
            }

            if(nowWord.getDp().equals("COO")){
                Word parentWord = wordsList.get(parentIndex);
                nowWord.setDp(parentWord.getDp());
                nowWord.setParent(parentWord.getParent());
                parentIndex = parentWord.getParent() - 1;
            }

            if(parentIndex == -1){
                head.children.add(treeNodes[i]);
                continue;
            }

            if(treeNodes[parentIndex] == null){
                treeNodes[parentIndex] = new DpNode(wordsList.get(parentIndex));
            }
            treeNodes[parentIndex].children.add(treeNodes[i]);
        }
    }

    public List<MyRDF> getRelation(){
        if(head == null){
            throw new IllegalArgumentException();
        }
        List<MyRDF> RDFList = new ArrayList<>();
        List<DpNode> rootNodes = head.children;
        for(DpNode root:rootNodes){
            List<DpNode> sbvNodes = new ArrayList<>();
            List<DpNode> vobNodes = new ArrayList<>();
            if(root.word.getPos().equals("v")){
                List<DpNode> childrenNodes = root.children;
                for(DpNode childern:childrenNodes){
                    if(childern.word.getPos().matches("n.*") && childern.word.getDp().equals("SBV")){
                        sbvNodes.add(childern);
                    }
                    else if(childern.word.getPos().matches("n.*") && childern.word.getDp().equals("VOB")){
                        vobNodes.add(childern);
                    }
                }
            }

            for(DpNode sbvNode:sbvNodes){
                for(DpNode vobNode:vobNodes){
                    MyEntry sbvEntry = new MyEntry(sbvNode.word.getWordString());
                    MyEntry vobEntry = new MyEntry(vobNode.word.getWordString());
                    MyRelation relation = new MyRelation(root.word.getWordString());
                    MyRDF rdf = new MyRDF(sbvEntry,vobEntry,relation);
                    RDFList.add(rdf);
                }
            }
        }
        return RDFList;
    }

}
