package hit.zhou.common.bean;

public interface PageCreater<T> {
    T create(Dir parent, String dirPath, String pageName);
}
