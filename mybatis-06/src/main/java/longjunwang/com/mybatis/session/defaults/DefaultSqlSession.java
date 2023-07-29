package longjunwang.com.mybatis.session.defaults;

import longjunwang.com.mybatis.executor.Executor;
import longjunwang.com.mybatis.mapping.BoundSql;
import longjunwang.com.mybatis.mapping.Environment;
import longjunwang.com.mybatis.mapping.MappedStatement;
import longjunwang.com.mybatis.session.Configuration;
import longjunwang.com.mybatis.session.SqlSession;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * desc:
 *
 * @author ink
 * date:2023-07-09 18:31
 */
public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    private Executor executor;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public <T> T selectOne(String statement) {
        return (T) ("你被代理了！" + "方法：" + statement );
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
        List<T> list = executor.query(mappedStatement, parameter, Executor.NO_RESULT_HANDLER, mappedStatement.getBoundSql());
        return list.get(0);
    }


    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }
    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
}
