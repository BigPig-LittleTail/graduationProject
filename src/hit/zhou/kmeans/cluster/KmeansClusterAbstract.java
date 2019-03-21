package hit.zhou.kmeans.cluster;

import hit.zhou.common.tools.FileUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class KmeansClusterAbstract{
    private List<KeyWordVector> keyWordVectorList;
    private float[] centerMass;

    protected abstract float calculateVector2VectorDistance(float[] vector1,float[] vector2);

    public KmeansClusterAbstract(KeyWordVector initCenterMass) {
        this.keyWordVectorList = new ArrayList<>();
        this.centerMass = initCenterMass.getVector().clone();
        keyWordVectorList.add(initCenterMass);
    }

    public void saveCenterMass(String savePath){
       String writeString = buildVectorString(centerMass);
       FileUtil.saveTest(savePath,writeString.getBytes(),false);
    }


    private String buildVectorString(float[] p){
        String writeString = "[";
        if(p.length > 0){
            for(int i = 0;i < p.length - 1;i++){
                writeString = writeString + p[i] + ",";
            }
            writeString = writeString + p[p.length - 1];
        }
        writeString = writeString + "]\r\n";
        return writeString;
    }

    public void saveVectors(String savePath){
        String totalString = "";
        for(int i = 0;i < keyWordVectorList.size();i++){
            KeyWordVector keyWordVector = keyWordVectorList.get(i);
            String wirteString = keyWordVector.getKeyWord() + "\t";
            wirteString = wirteString + buildVectorString(keyWordVector.getVector());
            totalString = totalString + wirteString;
        }
        FileUtil.saveTest(savePath,totalString.getBytes(),false);
    }


    public float calculateVector2CenterMass(float[] vector){
        return calculateVector2VectorDistance(vector,centerMass);
    }


    public float reCalculateCenterMass(){
        int ponintNum = keyWordVectorList.size();
        float[] oldCenterMass = centerMass.clone();
        for(int i = 0;i < centerMass.length; i++){
            centerMass[i] = 0;
        }
        for(KeyWordVector keyWordVector:keyWordVectorList){
            float[] vector = keyWordVector.getVector();
            for(int i = 0;i < vector.length;i++){
                centerMass[i] += vector[i];
            }
        }

        for(int i = 0;i < centerMass.length; i++){
            centerMass[i] = centerMass[i] / ponintNum;
        }

        return calculateVector2VectorDistance(oldCenterMass,centerMass);
    }

    public List<KeyWordVector> getKeyWordVectorList() {
        return keyWordVectorList;
    }

    public boolean contains(KeyWordVector keyWordVector){
        return keyWordVectorList.contains(keyWordVector);
    }

    public void add(KeyWordVector keyWordVector){
        keyWordVectorList.add(keyWordVector);
    }

    public void clear(){
        keyWordVectorList.clear();
    }

    public void sortVectors(){

    }

    protected void sortVectors(Comparator<KeyWordVector> comparator){
        keyWordVectorList.sort(comparator);
    }

    protected float[] getCenterMass() {
        return centerMass;
    }
}
