package longjunwang.com.mybatis.binding;

import longjunwang.com.mybatis.session.SqlSession;

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
    private SqlSession sqlSession;

    /** 要代理的mapper接口 */
    private Class<T> mapperInterface;

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (filterClass(method)) {
            return method.invoke(this, args);
        } else {
            return sqlSession.selectOne(mapperInterface.getName()+ "." +method.getName(), args);
        }
    }

    private static boolean filterClass(Method method) {
        return Object.class.equals(method.getDeclaringClass());
    }
}
