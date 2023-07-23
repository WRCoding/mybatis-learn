package longjunwang.com.mybatis.binding;

import longjunwang.com.mybatis.session.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * desc: MapperProxy
 *
 * @author ink
 * date:2023-07-09 17:56
 */
public class MapperProxy<T> implements InvocationHandler {

    /**
     * key:mapper里面的方法
     * value: 方式对应的SQL语句
     * */
    private SqlSession sqlSession;

    /** 要代理的mapper接口 */
    private Class<T> mapperInterface;

    private Map<Method, MapperMethod> methodCache;

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethod> methodCache) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
        this.methodCache = methodCache;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (filterClass(method)) {
            return method.invoke(this, args);
        } else {
            MapperMethod mapperMethod = cachedMapperMethod(method);
            return mapperMethod.execute(sqlSession, args);
        }
    }

    private MapperMethod cachedMapperMethod(Method method){
        MapperMethod mapperMethod = methodCache.get(method);
        if (Objects.isNull(mapperMethod)){
            mapperMethod = new MapperMethod(mapperInterface, method, sqlSession.getConfiguration());
            methodCache.put(method, mapperMethod);
        }
        return methodCache.get(method);
    }

    private static boolean filterClass(Method method) {
        return Object.class.equals(method.getDeclaringClass());
    }
}
