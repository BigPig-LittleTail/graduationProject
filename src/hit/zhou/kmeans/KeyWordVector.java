package hit.zhou.kmeans;

public class KeyWordVector {
    private String keyWord;
    private float[] vector;

    public KeyWordVector(String keyWord, int dimension){
        this.keyWord = keyWord;
        this.vector = new float[dimension];
    }

    public void setVectorData(int dimensionNum, float weight){
        vector[dimensionNum] = weight;
    }

    public float getVectorData(int index){
        return this.vector[index];
    }

    public float[] getVector() {
        return vector;
    }

    public String getKeyWord() {
        return keyWord;
    }
}
