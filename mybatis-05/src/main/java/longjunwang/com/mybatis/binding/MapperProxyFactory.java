package longjunwang.com.mybatis.binding;

import longjunwang.com.mybatis.session.SqlSession;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * desc: MapperProxyFactory
 *
 * @author ink
 * date:2023-07-09 18:03
 */
public class MapperProxyFactory<T> {
    private Class<T> mapperInterface;

    private Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<>();

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public T newInstance(SqlSession sqlSession){
        MapperProxy mapperProxy = new MapperProxy(sqlSession, mapperInterface, methodCache);
        return (T) Proxy.newProxyInstance(mapperProxy.getClass().getClassLoader(),
                new Class[]{mapperInterface},
                mapperProxy);
    }

}
