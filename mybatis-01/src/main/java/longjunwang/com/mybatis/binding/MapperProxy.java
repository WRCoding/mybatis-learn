package longjunwang.com.mybatis.binding;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

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
    private Map<String, String> sqlSession;

    /** 要代理的mapper接口 */
    private Class<T> mapperInterface;

    public MapperProxy(Map<String, String> sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (filterClass(method)) {
            return method.invoke(this, args);
        } else {
            return "你的被代理了！" + sqlSession.get(mapperInterface.getName() + "." + method.getName());
        }
    }

    private static boolean filterClass(Method method) {
        return Object.class.equals(method.getDeclaringClass());
    }
}
