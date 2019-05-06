package hit.zhou.common.bean;

import java.util.ArrayList;
import java.util.List;

public class Relation {
    private int relationCount;
    private int totalDistance;
    private String head;
    private String tail;
    private String relation;
    private List<Sentence> sentenceList;

    public Relation(String head, String tail, String relation,Sentence sentence,int distance){
        this.head = head;
        this.tail = tail;
        this.relation = relation;
        this.relationCount = 1;
        this.totalDistance = distance;
        this.sentenceList = new ArrayList<>();
        this.sentenceList.add(sentence);
    }

    public List<Sentence> getSentenceList() {
        return sentenceList;
    }

    public int getRelationCount() {
        return relationCount;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public float getTruthRate() {
        return ((float) 1 / (((float) totalDistance )/((float) relationCount) - 1));
    }

    public String getHead() {
        return head;
    }

    public String getTail() {
        return tail;
    }

    public String getRelation() {
        return relation;
    }

    public boolean addSameNameRelation(Relation relation) {
        if (this.equals(relation)) {
            this.totalDistance += relation.getTotalDistance();
            this.relationCount += relation.getRelationCount();
            this.sentenceList.addAll(relation.getSentenceList());
            return true;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return this.head.hashCode() + this.tail.hashCode() + this.relation.hashCode();
    }

    @Override
    public boolean equals(Object object){
        if(!(object instanceof Relation)){
            return false;
        }
        Relation relation = (Relation)object;
        return this.head.equals(relation.getHead()) && this.tail.equals(relation.getTail()) && this.relation.equals(relation.getRelation());
    }

}
