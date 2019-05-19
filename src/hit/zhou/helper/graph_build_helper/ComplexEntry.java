package hit.zhou.helper.graph_build_helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComplexEntry<T extends Enum>{
    private String wordString;
    private Map<T,Integer> typeIntegerMap;
    private float[] headVector;
    private float[] tailVector;
    private List<T> types;

    public ComplexEntry(String wordString,Map<T,Integer> typeIntegerMap,int dimensionSize){
        this.wordString = wordString;
        this.typeIntegerMap = typeIntegerMap;
        headVector = new float[dimensionSize];
        tailVector = new float[dimensionSize];
        types = new ArrayList<>();
    }

    public String getWordString() {
        return wordString;
    }

    public int getDimensionSize(){
        return headVector.length;
    }

    public void addType(T type){
        this.types.add(type);
    }

    public T getType(int index){
        return types.get(index);
    }

    public boolean containsType(T type){
        return types.contains(type);
    }

    public int typesSize(){
        return types.size();
    }

    public float getHeadDataByIndex(int index){
        return this.headVector[index];
    }

    public float getHeadDataByType(T type){
        return this.headVector[typeIntegerMap.get(type)];
    }

    public float getTailDataByIndex(int index){
        return this.tailVector[index];
    }

    public float getTailDataByType(T type){
        return this.tailVector[typeIntegerMap.get(type)];
    }

    public void setHeadDataByIndex(float data, int index){
        headVector[index] = data;
    }

    public void setHeadDataByType(float data,T type){
        headVector[typeIntegerMap.get(type)] = data;
    }

    public void setTailDataByIndex(float data, int index){
        tailVector[index] = data;
    }

    public void setTailDataByType(float data, T type){
        tailVector[typeIntegerMap.get(type)] = data;
    }

}
