package hit.zhou.tools.kmeans;

public class Dimension<T extends Enum> {
    private T type;
    private float value;

    public Dimension(){

    }

    public Dimension(T type){
        this.type = type;
    }

    public Dimension(T type,float value){
        this.type = type;
        this.value = value;
    }

    public T getType() {
        return type;
    }

    public float getValue() {
        return value;
    }

    public void setType(T type) {
        this.type = type;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
