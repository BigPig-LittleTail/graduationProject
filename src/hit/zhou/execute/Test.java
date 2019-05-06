package hit.zhou.execute;

import hit.zhou.common.tools.FileUtil;
import hit.zhou.common.tools.LtpBaseOpLocal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) throws IOException {
        String s = "n";
        System.out.println(s.matches("n.*"));

        LtpBaseOpLocal ltpBaseOpLocal = new LtpBaseOpLocal("C:/Users/zhou/Desktop/3.4.0/ltp_data_v3.4.0/ltp_data_v3.4.0/");
        String path = "C:/Users/zhou/Desktop/loyalFinal/地方政府规章/上海市人民政府关于中国（上海）自由贸易试验区管理委员会集中行使本市有关行政审批权和行政处罚权的决定.txt";
        String string = FileUtil.readString(path);
        List<String> stringList = new ArrayList<>();
        ltpBaseOpLocal.splitSentence(string, stringList);
        for (String sentenceInFile : stringList) {
            if (sentenceInFile.equals(""))
                continue;
            List<String> wordList = new ArrayList<>();
            List<String> posList = new ArrayList<>();
            List<Integer> headList = new ArrayList<>();
            List<String> dpList = new ArrayList<>();
            ltpBaseOpLocal.segmentor(sentenceInFile, wordList);
            System.out.println(wordList);
            System.out.println(wordList.size());
            ltpBaseOpLocal.postagger(wordList, posList);
            System.out.println(posList);
            ltpBaseOpLocal.parser(wordList, posList, headList, dpList);
            System.out.println(headList);
            System.out.println(dpList);
        }
    }

}
