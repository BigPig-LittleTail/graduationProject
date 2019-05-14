package hit.zhou.tools.rdf_build;

import hit.zhou.EntryType;
import hit.zhou.tools.kmeans.ClusterTest;
import hit.zhou.tools.kmeans.VectorTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RDF_Build {
    public static void MergeHeadAndTailClusters(Map<EntryType,Integer> entryTypeIntegerMap,List<ClusterTest<EntryType>> headClusters, List<ClusterTest<EntryType>> tailClusters){
        Map<String,ComplexEntry> wordStringMapComlpexEntry = new HashMap<>();
        for(ClusterTest<EntryType> cluster:headClusters){
            int typeSize = cluster.getTypesSize();
            int vectorsSize = cluster.getVectorsSize();
            for(int i = 0;i < vectorsSize;i++){
                VectorTest<EntryType> vector = cluster.get(i);
                if(wordStringMapComlpexEntry.containsKey(vector.getWordString())){
                    ComplexEntry complexEntry = new ComplexEntry(entryTypeIntegerMap,vector.dimensionSize());

                }
                else {
                    System.out.println("为什么会有重复的实体出现");
                }
            }
        }

    }
}
