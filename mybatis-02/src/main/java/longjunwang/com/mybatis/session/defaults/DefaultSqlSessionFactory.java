package longjunwang.com.mybatis.session.defaults;

import longjunwang.com.mybatis.binding.MapperRegistry;
import longjunwang.com.mybatis.session.SqlSession;
import longjunwang.com.mybatis.session.SqlSessionFactory;

/**
 * desc:
 *
 * @author ink
 * date:2023-07-09 18:46
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private MapperRegistry mapperRegistry;

    public DefaultSqlSessionFactory(MapperRegistry mapperRegistry) {
        this.mapperRegistry = mapperRegistry;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(mapperRegistry);
    }
}
