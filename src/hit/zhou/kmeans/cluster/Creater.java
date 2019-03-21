package hit.zhou.kmeans.cluster;

public interface Creater<T> {
    T create(KeyWordVector keyWordVector);
}
