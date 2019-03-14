package hit.zhou.nlp.ltp;

import com.alibaba.fastjson.JSONObject;
import edu.hit.ir.ltp4j.Pair;
import hit.zhou.hepler.JsonUtil;
import hit.zhou.hepler.OkHttpUtil;
import okhttp3.Headers;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LtpBaseOpNet {
    private Headers headers;
    private String appId;
    private String apiKey;
    private String type;

    public LtpBaseOpNet(String appId,String apiKey,String type) throws IOException{
        this.appId = appId;
        this.apiKey = apiKey;
        this.type = type;
        headers = buildHeaders();
    }

    public int segmentor (String text, List<String> words,String[] resultString) throws IOException {
        final String actionStr = "cws";
        String dealResult = httpRequestAndResponse(text, words, actionStr);
        resultString[0] = dealResult;
        return resultCodeFromString2Int(dealResult);
    }

    private int resultCodeFromString2Int(String dealResult) {
        if(dealResult.equals("failure"))
            return 0;
        else if(dealResult.equals("listNull"))
            return -1;
        else
            return 1;
    }

    public int postagger(String text,List<String> posttag,String[] resultString) throws IOException{
        final String actionStr = "pos";
        String dealResult = httpRequestAndResponse(text, posttag, actionStr);
        resultString[0] = dealResult;
        return resultCodeFromString2Int(dealResult);
    }

    public int ner(String text,List<String> ners,String[] resultString) throws IOException{
        final String actionStr = "ner";
        String dealResult = httpRequestAndResponse(text, ners, actionStr);
        resultString[0] = dealResult;
        return resultCodeFromString2Int(dealResult);
    }

    public int parser(String text,List<Integer> heads,List<String> deprels,String[] resultString) throws IOException {
        final String actionStr = "dp";
        List<Pair<Integer,String>> list = new ArrayList<>();
        String resultCode = httpRequestAndResponse(text,list, actionStr);
        if(!resultCode.equals("failure") && !resultCode.equals("listNull")){
            for(Pair pair : list){
                heads.add((Integer) pair.first);
                deprels.add((String) pair.second);
            }
            resultString[0] = resultCode;
        }
        return  resultCodeFromString2Int(resultCode);
    }

    public int srl(String text,List<Pair<Integer, List<Pair<String,Pair<Integer, Integer>>>>> srls,String[] resultString) throws IOException{
        final String actionStr = "srl";
        String dealResult = httpRequestAndResponse(text,srls,actionStr);
        resultString[0] = dealResult;
        return resultCodeFromString2Int(dealResult);
    }

    /**
     * 用来发送Http请求和处理Response
     * @param text 文本
     * @param resultList 结果List
     * @param actionStr ltp的操作类型
     * @param <T> 结果List中元素的类型
     * @return 结果码 listNull:请求成功，但返回结果为空 failure：请求失败 其他:成功并有结果
     * @throws IOException  ioc错误
     */
    private <T> String httpRequestAndResponse(String text, List<T> resultList, String actionStr) throws IOException {
        Response response = OkHttpUtil.getInstance().post(headers, text, actionStr);
        if (response.isSuccessful()) {
            String responseJson = response.body().string();
            System.out.println(responseJson);
            String[] strings = new String[1];
            if(dealWithResponseJson(responseJson, actionStr,resultList,strings) == -1){
                return "listNull";
            }
            return strings[0];
        }
        return "failure";
    }

    /**
     * 初步处理Response。code == 0，交给JsonUtil进一步处理
     * @param responseJson responseJson
     * @param actionStr ltp的操作类型
     * @param resultList 结果List(为了方便和LtpBaseOpLocal结果比较)
     * @param resultString 结果String(为了方便存入文件)
     * @param <T> 结果List中元素类型
     * @return 操作码 1:成功 -1:失败
     * @throws IOException IO错误
     */
    private <T> int dealWithResponseJson(String responseJson,String actionStr,List<T> resultList,String[] resultString) throws IOException{
        JSONObject jsonObject = JSONObject.parseObject(responseJson);
        if(jsonObject.getString("code").equals("0")){
            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
            resultString[0] = jsonObject.getString("data");
            List<?> list = JsonUtil.dealWithDataJsonObject(actionStr, jsonObject1);
            resultList.addAll((List<T>) list);
            return 1;
        }
        return -1;
    }




    private Headers buildHeaders() throws IOException{
        String curTime = System.currentTimeMillis() / 1000L + "";
        String param = "{\"type\":\"" + type +"\"}";
        String paramBase64 = new String(Base64.encodeBase64(param.getBytes("UTF-8")));
        String checkSum = DigestUtils.md5Hex(apiKey + curTime + paramBase64);
        Headers headers = Headers.of(
                "X-Param", paramBase64,
                "X-CurTime", curTime,
                "X-CheckSum", checkSum,
                "X-Appid", appId
        );
        return headers;
    }
}
