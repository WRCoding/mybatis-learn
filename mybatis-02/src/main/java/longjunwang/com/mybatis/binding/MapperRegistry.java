package longjunwang.com.mybatis.binding;

import cn.hutool.core.lang.ClassScanner;
import longjunwang.com.mybatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * desc: MapperRegistry
 *
 * @author ink
 * date:2023-07-09 18:24
 */
public class MapperRegistry {

    private Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    public <T> T getMapper(Class<T> type, SqlSession sqlSession){
        MapperProxyFactory<?> mapperProxyFactory = knownMappers.get(type);
        if (Objects.isNull(mapperProxyFactory)){
            throw new RuntimeException("Type " + type + " is not known to the MapperRegistry.");
        }
        try {
            return (T) mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new RuntimeException("Error getting mapper instance. Cause: " + e, e);
        }
    }
    public <T> void addMapper(Class<T> mapperInterface){
        if (mapperInterface.isInterface()){
            if(hasMapper(mapperInterface)){
                throw new RuntimeException("Type " + mapperInterface + " is already known to the MapperRegistry.");
            }
            knownMappers.put(mapperInterface,new MapperProxyFactory<>(mapperInterface));
        }
    }

    private <T> boolean hasMapper(Class<T> mapperInterface) {
        return knownMappers.containsKey(mapperInterface);
    }

    public void addMappers(String packageName) {
        Set<Class<?>> mapperSet = ClassScanner.scanPackage(packageName);
        for (Class<?> mapperClass : mapperSet) {
            addMapper(mapperClass);
        }
    }
}
