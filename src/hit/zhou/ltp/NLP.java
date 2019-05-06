package hit.zhou.ltp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import hit.zhou.common.bean.Dir;
import hit.zhou.common.bean.PageForNlp;
import hit.zhou.common.bean.Sentence;
import hit.zhou.common.tools.FileUtil;
import hit.zhou.common.tools.LtpBaseOpLocal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NLP {
    private static final String PAGE_DEAL_RESULT = "page_deal_result.txt";
    private static ExecutorService pool = Executors.newFixedThreadPool(4);

    public void buildPages(Dir<PageForNlp> dirNode, LtpBaseOpLocal ltpBaseOpLocal, String saveDirPath) throws IOException, InterruptedException, ExecutionException {
        List<Future> futures = new ArrayList<>();
        realBuildPages(dirNode, ltpBaseOpLocal, saveDirPath,futures);
        for(Future f : futures){
            f.get();
        }
        pool.shutdown();
        ltpBaseOpLocal.releaseSegmentor();
        ltpBaseOpLocal.releasePos();
        ltpBaseOpLocal.releaseNer();
        ltpBaseOpLocal.releaseParser();
    }

    private void realBuildPages(Dir<PageForNlp> dirNode, LtpBaseOpLocal ltpBaseOpLocal, String saveDirPath,List<Future> futures) throws IOException {
        File saveDirFile = new File(saveDirPath);
        if(!saveDirFile.exists()){
            saveDirFile.mkdirs();
        }
        // 真正的存储路径顶级是指定savePath加上dir的名字
        String saveTopDirPath = saveDirPath + dirNode.getDirName() + "/";
        File saveTopDirFile = new File(saveTopDirPath);
        if(!saveTopDirFile.exists()){
            saveTopDirFile.mkdirs();
        }

        List<Dir<PageForNlp>> childDirList = dirNode.getDirCihldList();
        for(Dir<PageForNlp> childDir:childDirList){
            realBuildPages(childDir,ltpBaseOpLocal,saveTopDirPath,futures);
        }
        List<PageForNlp> childFileList = dirNode.getFileList();

        for(PageForNlp childFile:childFileList){
            // 每个文件对应的文件夹路径，用来保存文件的map，keyword等结果
            String childFileSaveDirPath = saveTopDirPath + childFile.getPageName() + "/";
            String childFilePageDealResultPath = childFileSaveDirPath + PAGE_DEAL_RESULT;
            File childFilePageDealResultFile = new File(childFilePageDealResultPath);
            if(!childFilePageDealResultFile.exists()){
                // 如果map不存在，则直接多线程分词，写入文件
                File childFileSaveDir = new File(childFileSaveDirPath);
                if(!childFileSaveDir.exists()){
                    childFileSaveDir.mkdirs();
                }
                NLPRunnable r = new NLPRunnable(childFile,ltpBaseOpLocal,childFilePageDealResultPath);
                futures.add(pool.submit(r));
            }
            else {
//                 存在，则读入
                //:todo 读入内存会爆炸，将路径存起来
                childFile.setNlpResultPath(childFilePageDealResultPath);
//                childFile.setSentences(readFromFile(childFilePageDealResultPath));
            }
        }
    }

    private List<Sentence> readFromFile(String filePath) throws IOException{
        String jsonArrayString = FileUtil.readString(filePath);
        JSONArray jsonArray = JSON.parseArray(jsonArrayString);
        return JSON.parseArray(jsonArrayString,Sentence.class);
    }

}
