package hit.zhou.common.bean.test;

public class MyRDF {
    private MyRelation relation;
    private MyEntry head;
    private MyEntry tail;
    public MyRDF(MyEntry head,MyEntry tail,MyRelation relation){
        this.head = head;
        this.tail = tail;
        this.relation = relation;
    }

    public MyEntry getHead() {
        return head;
    }

    public MyEntry getTail() {
        return tail;
    }

    public MyRelation getRelation() {
        return relation;
    }

    public void setHead(MyEntry head) {
        this.head = head;
    }

    public void setRelation(MyRelation relation) {
        this.relation = relation;
    }

    public void setTail(MyEntry tail) {
        this.tail = tail;
    }
}
