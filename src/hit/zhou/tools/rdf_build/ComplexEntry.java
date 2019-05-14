package hit.zhou.tools.rdf_build;

import hit.zhou.EntryType;

import java.util.Map;

public class ComplexEntry{
    private Map<EntryType,Integer> typeIntegerMap;
    private float[] headVector;
    private float[] tailVector;

    public ComplexEntry(Map<EntryType,Integer> typeIntegerMap,int dimensionSize){
        this.typeIntegerMap = typeIntegerMap;
        headVector = new float[dimensionSize];
        tailVector = new float[dimensionSize];
    }
    

}
