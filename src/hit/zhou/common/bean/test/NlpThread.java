package hit.zhou.common.bean.test;

import hit.zhou.common.tools.LtpBaseOpLocal;

import java.io.IOException;

public class NlpThread implements Runnable {
    private PassageNode passageNode;
    private LtpBaseOpLocal ltpBaseOpLocal;
    private String nlpPassagePath;


    public NlpThread(PassageNode passageNode,LtpBaseOpLocal ltpBaseOpLocal,String nlpPassagePath){
        this.passageNode = passageNode;
        this.ltpBaseOpLocal = ltpBaseOpLocal;
        this.nlpPassagePath = nlpPassagePath;
    }

    @Override
    public void run(){
        try {
            passageNode.nlpExecutor(ltpBaseOpLocal,nlpPassagePath);
        }catch (IOException e){
            e.printStackTrace();
        }
    }


}
