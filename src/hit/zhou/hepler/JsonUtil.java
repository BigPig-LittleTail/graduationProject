package hit.zhou.hepler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import edu.hit.ir.ltp4j.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonUtil {
    /**
     * @param actionStr ltp的操作类型
     * @param dataJsonObject data的JsonObject(http的response已经拆了几层)
     * @return
     */
    public static List<?> dealWithDataJsonObject(String actionStr, JSONObject dataJsonObject) {
        List<?> list;
        switch (actionStr){
            case "cws":
                list = JSON.parseArray(dataJsonObject.getString("word"),String.class);
                break;
            case "pos":
            case "ner":
                list = JSON.parseArray(dataJsonObject.getString(actionStr),String.class);
                break;
            case "dp": {
                List<Pair<Integer, String>> tempList = new ArrayList<>();
                JSONArray jsonArray = dataJsonObject.getJSONArray(actionStr);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject tempJsonObject = JSON.parseObject(jsonArray.getString(i));
                    Pair<Integer, String> pair = new Pair<>(tempJsonObject.getInteger("parent"), tempJsonObject.getString("relate"));
                    tempList.add(pair);
                }
                list = tempList;
                break;
            }
            case "srl":{
                List<Pair<Integer, List<Pair<String,Pair<Integer, Integer>>>>> tempList = new ArrayList<>();
                JSONArray jsonArray = dataJsonObject.getJSONArray(actionStr);
                HashMap<Integer,Integer> map = new HashMap<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject tempJsonObject = JSON.parseObject(jsonArray.getString(i));
                    int id = tempJsonObject.getInteger("id");
                    String type = tempJsonObject.getString("type");
                    int beg = tempJsonObject.getInteger("beg");
                    int end = tempJsonObject.getInteger("end");
                    if(!map.containsKey(id)){
                        map.put(id,tempList.size());
                        Pair<String,Pair<Integer,Integer>> bottomPair = new Pair<>(type,new Pair<>(beg,end));
                        List<Pair<String,Pair<Integer,Integer>>> pairList = new ArrayList<>();
                        pairList.add(bottomPair);
                        Pair<Integer, List<Pair<String,Pair<Integer, Integer>>>> topPair =
                                new Pair<>(id,pairList);
                        tempList.add(topPair);
                    }
                    else {
                        int index = map.get(id);
                        Pair<Integer, List<Pair<String,Pair<Integer, Integer>>>> topPair = tempList.get(index);
                        topPair.second.add(new Pair<>(type,new Pair<>(beg,end)));
                        tempList.set(index,topPair);
                    }
                }
                list = tempList;
                break;
            }
            default:
                list = new ArrayList<>();
                break;
        }
        return list;
    }
}
