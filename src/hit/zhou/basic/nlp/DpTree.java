package hit.zhou.basic.nlp;

import hit.zhou.basic.rdf.MyEntry;
import hit.zhou.basic.rdf.MyRDF;
import hit.zhou.basic.rdf.MyRelation;

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
                head.getChildren().add(treeNodes[i]);
                continue;
            }

            if(nowWord.getDp().equals("COO")){
                Word parentWord = wordsList.get(parentIndex);
                nowWord.setDp(parentWord.getDp());
                nowWord.setParent(parentWord.getParent());
                parentIndex = parentWord.getParent() - 1;
            }

            if(parentIndex == -1){
                head.getChildren().add(treeNodes[i]);
                continue;
            }

            if(treeNodes[parentIndex] == null){
                treeNodes[parentIndex] = new DpNode(wordsList.get(parentIndex));
            }
            treeNodes[parentIndex].getChildren().add(treeNodes[i]);
        }
    }

    private String getDpNodeFeature(DpNode dpNode){
        List<DpNode> children = dpNode.getChildren();
        StringBuilder stringBuilder = new StringBuilder();
        for(DpNode childNode:children){
            if(childNode.getWord().getDp().equals("ATT") && !childNode.getWord().getPos().equals("v")){
                stringBuilder.append(getDpNodeFeature(childNode));
                stringBuilder.append(childNode.getWord().getWordString());
            }
        }
        return stringBuilder.toString();
    }

    private String getDpNodeRelationFeature(DpNode dpNode){
        List<DpNode> children = dpNode.getChildren();
        StringBuilder stringBuilder = new StringBuilder();
        for(DpNode childNode:children){
            if(childNode.getWord().getDp().equals("ADV") && childNode.getWord().getPos().equals("d")){
                stringBuilder.append(childNode.getWord().getWordString());
            }
            else if(childNode.getWord().getDp().equals("ADV") && childNode.getWord().getPos().equals("p")){
                List<DpNode> gradchildren = childNode.getChildren();
                if(gradchildren.size() == 1){
                    if(gradchildren.get(0).getWord().getDp().equals("POB") && gradchildren.get(0).getWord().getPos().matches("n.*")){
                        stringBuilder.append(childNode.getWord().getWordString());
                        stringBuilder.append(getDpNodeFeature(gradchildren.get(0)));
                        stringBuilder.append(gradchildren.get(0).getWord().getWordString());
                    }
                }
            }
        }
        return stringBuilder.toString();
    }


    public List<MyRDF> getRelation(){
        if(head == null){
            throw new IllegalArgumentException();
        }
        List<MyRDF> RDFList = new ArrayList<>();
        List<DpNode> rootNodes = head.getChildren();
        List<DpNode> sbvNodes = new ArrayList<>();
        for(DpNode root:rootNodes){
            List<DpNode> vobNodes = new ArrayList<>();
            if(root.getWord().getPos().equals("v")){
                List<DpNode> childrenNodes = root.getChildren();
                for(DpNode childern:childrenNodes){
                    if(childern.getWord().getPos().matches("n.*") && childern.getWord().getDp().equals("SBV")){
                        sbvNodes.add(childern);
                    }
                    else if(childern.getWord().getPos().matches("n.*") && childern.getWord().getDp().equals("VOB")){
                        vobNodes.add(childern);
                    }
                }
            }

            for(DpNode sbvNode:sbvNodes){
                for(DpNode vobNode:vobNodes){
                    MyEntry sbvEntry = new MyEntry(sbvNode.getWord().getWordString());
                    String sbvNodeFeature = getDpNodeFeature(sbvNode);
                    sbvEntry.setFeature(sbvNodeFeature);
                    MyEntry vobEntry = new MyEntry(vobNode.getWord().getWordString());
                    String vobNodeFeature = getDpNodeFeature(vobNode);
                    vobEntry.setFeature(vobNodeFeature);
                    MyRelation relation = new MyRelation(root.getWord().getWordString());
                    String relationFeature = getDpNodeRelationFeature(root);
                    relation.setFeature(relationFeature);
                    MyRDF rdf = new MyRDF(sbvEntry,vobEntry,relation);
                    RDFList.add(rdf);
                }
            }
        }
        return RDFList;
    }

}
