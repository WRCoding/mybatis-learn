package longjunwang.com.mybatis.binding;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * desc: MapperProxyFactory
 *
 * @author ink
 * date:2023-07-09 18:03
 */
public class MapperProxyFactory<T> {
    private Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public T newInstance(Map<String, String> sqlSession){
        MapperProxy mapperProxy = new MapperProxy(sqlSession, mapperInterface);
        return (T) Proxy.newProxyInstance(mapperProxy.getClass().getClassLoader(),
                new Class[]{mapperInterface},
                mapperProxy);
    }

}
