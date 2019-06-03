package hit.zhou.graph.tools.kmeans;

import java.util.Map;

public class Vector<T extends Enum> {
    private String wordString;
    private float[] vectorData;
    private Map<T,Integer> typeMapIndex;

    public Vector(String wordString, float[] vectorData, Map<T,Integer> typeMapIndex){
        this.wordString = wordString;
        this.vectorData = vectorData;
        this.typeMapIndex = typeMapIndex;
    }

    public String getWordString() {
        return wordString;
    }

    public float[] getVectorData() {
        return vectorData;
    }

    public Map<T, Integer> getTypeMapIndex() {
        return typeMapIndex;
    }

    public void setWordString(String wordString) {
        this.wordString = wordString;
    }

    public void setVectorData(float[] vectorData) {
        this.vectorData = vectorData;
    }

    public void setTypeMapIndex(Map<T, Integer> typeMapIndex) {
        this.typeMapIndex = typeMapIndex;
    }

    public void setDataByIndex(float data, int index){
        this.vectorData[index] = data;
    }

    public void setDataByType(float data,T type){
        this.vectorData[typeMapIndex.get(type)] = data;
    }

    public float getDataByIndex(int index){
        return this.vectorData[index];
    }

    public float getDataByType(T type){
        return this.vectorData[typeMapIndex.get(type)];
    }

    public float shadow(float[] baseline){
        return CalculateHelper.calculateInner(vectorData,baseline) / (float) Math.sqrt(CalculateHelper.moudle(baseline));
    }

    public int dimensionSize(){
        return vectorData.length;
    }


}
