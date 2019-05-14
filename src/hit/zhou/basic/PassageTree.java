package hit.zhou.basic;

import hit.zhou.tools.LtpBaseOpLocal;
import hit.zhou.EntryType;
import hit.zhou.tools.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

public class PassageTree {
    private PassageNode root;
    private boolean isBuild;
    private boolean isNlp;
    private boolean isRdf;
    private boolean isCountEntry;

    private static ExecutorService pool = Executors.newFixedThreadPool(4);
    // 这个type_file.txt 一定要是纯文本txt，在windows下直接创建的txt是带头的，不行
    private static final String TYPE_FILE_NAME = "type_file.txt";

    public PassageTree(){
        root = null;
        isBuild = false;
        isNlp = false;
        isRdf = false;
        isCountEntry = false;
    }

    public void buildPassageTree(String passagePath,boolean isPassageChange) throws IOException{
        PassageNode passageNode = new PassageNode(passagePath);
        if(!passageNode.exists()){
            throw new IllegalArgumentException();
        }
        root = passageNode;
        Queue<PassageNode> queue = new LinkedList<>();
        Queue<EntryType> typeQueue = new LinkedList<>();

        EntryType type;
        if(passageNode.isDirectory()){
            queue.offer(passageNode);
            String typePath = passageNode.getPath() + "\\" + TYPE_FILE_NAME;
            File typeFile = new File(typePath);
            if(!typeFile.exists())
                throw new IllegalArgumentException();
            String typeString = FileUtil.readString(typePath);
            EntryType tempType = EntryType.valueOf(typeString);
            typeQueue.offer(tempType);
        }

        while(!queue.isEmpty()){
            passageNode = queue.poll();
            type = typeQueue.poll();
            File[] files = passageNode.listFiles();
            if(files == null){
                throw new IllegalArgumentException();
            }
            String passageNodeTextName = passageNode.getName() + ".txt";
            String passageNodeTextPath = passageNode.getPath() + "\\" + passageNodeTextName;
            boolean passageNodeTextWrite = true;
            StringBuilder stringBuilder = new StringBuilder();
            for(File file:files){
                if(file.isFile() && file.getName().equals(passageNodeTextName)){
                    if(!isPassageChange){
                        passageNodeTextWrite = false;
                        passageNode.setPassagePath(passageNodeTextPath);
                    }
                    continue;
                }
                if(file.isFile() && file.getName().equals(TYPE_FILE_NAME)){
                    continue;
                }
                PassageNode childPassageNode = new PassageNode(file.getPath());
                childPassageNode.setType(type);
                passageNode.addChildPassageNode(childPassageNode);
                stringBuilder.append(file.getName());
                stringBuilder.append("\r\n");
                if(file.isDirectory()){
                    queue.offer(childPassageNode);
                    String typePath = childPassageNode.getPath() + "\\" + TYPE_FILE_NAME;
                    File typeFile = new File(typePath);
                    if(!typeFile.exists())
                        throw new IllegalArgumentException();
                    String typeString = FileUtil.readString(typePath);
                    EntryType tempType = EntryType.valueOf(typeString);
                    typeQueue.offer(tempType);
                }
            }
            if(passageNodeTextWrite){
                passageNode.setPassagePath(passageNodeTextPath);
                FileUtil.save(passageNodeTextPath,stringBuilder.toString().getBytes(),false);
            }
        }
        isBuild = true;
    }

    public PassageNode getPassageNode(String passagePath){
        if(root == null)
            throw new IllegalArgumentException();
        PassageNode passageNode = root;
        Queue<PassageNode> queue = new LinkedList<>();
        queue.offer(passageNode);
        while(!queue.isEmpty()){
            passageNode = queue.poll();
            if(passageNode.getPassagePath().equals(passagePath)){
                return passageNode;
            }
            List<PassageNode> children = passageNode.getChildren();
            for(PassageNode child:children){
                queue.offer(child);
            }
        }
        return null;
    }

    public List<PassageNode> getPassageNodeListByLevel(int targetLevel){
        if(targetLevel < 1 || !isBuild){
            throw new IllegalArgumentException();
        }

        List<PassageNode> levelList = new ArrayList<>();
        if(targetLevel == 1){
            levelList.add(root);
            return levelList;
        }

        Deque<PassageNode> queue = new LinkedList<>();
        PassageNode nodeLast = root;
        PassageNode passageNode;
        queue.offer(root);
        int nowLevel = 1;
        while (!queue.isEmpty() && nowLevel < targetLevel){
            do{
                passageNode = queue.peek();
                List<PassageNode> children = passageNode.getChildren();
                for (PassageNode childPassageNode : children) {
                    queue.offer(childPassageNode);
                }
            }while(queue.poll() != nodeLast);
            nodeLast = queue.peekLast();
            nowLevel++;
        }
        levelList.addAll(queue);
        return levelList;
    }

    public void nlp(LtpBaseOpLocal ltpBaseOpLocal,String nlpTopPath,final String fileTypeName,boolean isRebuild) throws InterruptedException, ExecutionException {
        if(root == null)
            throw new IllegalArgumentException();

        File nlpTopDir = new File(nlpTopPath);
        if(!nlpTopDir.exists()){
            nlpTopDir.mkdirs();
        }

        List<Future> futures = new ArrayList<>();

        Queue<PassageNode> queue = new LinkedList<>();
        Queue<String> passageNodeQueue = new LinkedList<>();

        String nlpPassageDirPath = nlpTopPath + "\\" + root.getName() + "\\";
        File nlpPassageDir = new File(nlpPassageDirPath);
        if(!nlpPassageDir.exists())
            nlpPassageDir.mkdirs();
        String nlpPassagePath = nlpPassageDirPath + fileTypeName;
        File nlpPassage = new File(nlpPassagePath);
        root.setNlpFilePath(nlpPassagePath);

        queue.offer(root);
        passageNodeQueue.offer(nlpPassageDirPath);

        if(isRebuild || !nlpPassage.exists()){
            NlpThread nlpThread = new NlpThread(root,ltpBaseOpLocal,nlpPassagePath);
            futures.add(pool.submit(nlpThread));
        }

        while(!queue.isEmpty()){
            PassageNode passageNode = queue.poll();
            nlpTopPath = passageNodeQueue.poll();
            List<PassageNode> childPassageNodeList = passageNode.getChildren();
            for(PassageNode childPassageNode:childPassageNodeList){
                nlpPassageDirPath = nlpTopPath + childPassageNode.getName() + "\\";
                nlpPassageDir = new File(nlpPassageDirPath);
                if(!nlpPassageDir.exists())
                    nlpPassageDir.mkdirs();
                nlpPassagePath = nlpPassageDirPath + fileTypeName;
                nlpPassage = new File(nlpPassagePath);
                childPassageNode.setNlpFilePath(nlpPassagePath);
                queue.offer(childPassageNode);
                passageNodeQueue.offer(nlpPassageDirPath);
                if(isRebuild || !nlpPassage.exists()){
                    NlpThread nlpThread = new NlpThread(childPassageNode,ltpBaseOpLocal,nlpPassagePath);
                    futures.add(pool.submit(nlpThread));
                }
            }
        }

        for(Future future:futures){
            future.get();
        }

        pool.shutdown();
        ltpBaseOpLocal.releaseSegmentor();
        ltpBaseOpLocal.releasePos();
        ltpBaseOpLocal.releaseNer();
        ltpBaseOpLocal.releaseParser();

        isNlp = true;
    }

    public void rdf(String topPath, final String fileTypeName, boolean isReExecute){
        executor(topPath,fileTypeName,isReExecute,Executor::rdf);
        isRdf = true;
    }

    public void countEntry(String topPath, final String fileTypeName, boolean isReExecute){
        executor(topPath,fileTypeName,isReExecute,Executor::countEntry);
        isCountEntry = true;
    }

    public void buildKeyWordFilePath(String topPath, final String fileTypeName){
        executor(topPath,fileTypeName,true,Executor::buildKeyWordFilePath);
    }

    private void executor(String topPath, final String fileTypeName, boolean isReExecute, Executor.ExecutorInterface<PassageNode,String,Boolean> method) {
        if(root == null)
            throw new IllegalArgumentException();
        File topDir = new File(topPath);
        if(!topDir.exists()){
            topDir.mkdirs();
        }

        Queue<PassageNode> queue = new LinkedList<>();
        Queue<String> pathQueue = new LinkedList<>();

        String fileDirPath = topPath + "\\" + root.getName() + "\\";
        File fileDir = new File(fileDirPath);
        if(!fileDir.exists())
            fileDir.mkdirs();

        queue.offer(root);
        pathQueue.offer(fileDirPath);

        String filePath = fileDirPath + fileTypeName;
        method.run(root,filePath,isReExecute);

        while (!queue.isEmpty()){
            PassageNode passageNode = queue.poll();
            topPath = pathQueue.poll();
            List<PassageNode> chilren = passageNode.getChildren();
            for(PassageNode childPassageNode:chilren){
                fileDirPath = topPath + childPassageNode.getName() + "\\";
                fileDir = new File(fileDirPath);
                if(!fileDir.exists()){
                    fileDir.mkdirs();
                }

                queue.offer(childPassageNode);
                pathQueue.offer(fileDirPath);

                filePath = fileDirPath + fileTypeName;
                method.run(childPassageNode,filePath,isReExecute);
            }
        }
    }

    public void deleteNlp(){
        if(!isNlp){
            throw new IllegalArgumentException();
        }
        deleteExecutor(Executor::getNlpFilePath);
    }

    public void deleteRdf(){
        if(!isRdf){
            throw new IllegalArgumentException();
        }
        deleteExecutor(Executor::getRdfFilePath);
    }


    public void deleteEntryCount(){
        if(!isCountEntry){
            throw new IllegalArgumentException();
        }
        deleteExecutor(Executor::getEntryCountFilePath);
    }


    private void deleteExecutor(Function<PassageNode,String> function){
        PassageNode passageNode = root;
        Queue<PassageNode> queue = new LinkedList<>();
        queue.offer(passageNode);
        File file = new File(function.apply(passageNode));
        if(file.exists()){
            file.delete();
        }
        while(!queue.isEmpty()){
            passageNode = queue.poll();
            List<PassageNode> children = passageNode.getChildren();
            for(PassageNode childPassageNode:children){
                queue.offer(childPassageNode);
                file = new File(function.apply(childPassageNode));
                if(file.exists()){
                    file.delete();
                }
            }
        }
    }


    private static class Executor{
        public interface ExecutorInterface<T,U,R>{
            void run(T t,U u,R r);
        }

        private static void rdf(PassageNode passageNode,String rdfFilePath,boolean isReRdf) {
            try {
                passageNode.rdf(rdfFilePath,isReRdf);
                passageNode.setRdfFilePath(rdfFilePath);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        private static void countEntry(PassageNode passageNode,String countEntryFilePath,boolean isReCountEntry){
            try {
                passageNode.countEntry(countEntryFilePath,isReCountEntry);
                passageNode.setEntryCountFilePath(countEntryFilePath);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        private static void buildKeyWordFilePath(PassageNode passageNode,String keyWordFilePath,boolean isReCountEntry){
            passageNode.setKeyWordFilePath(keyWordFilePath);
        }

        private static String getNlpFilePath(PassageNode passageNode){
            return  passageNode.getNlpFilePath();
        }

        private static String getEntryCountFilePath(PassageNode passageNode){
            return passageNode.getEntryCountFilePath();
        }

        private static String getRdfFilePath(PassageNode passageNode){
            return passageNode.getRdfFilePath();
        }

    }
}
