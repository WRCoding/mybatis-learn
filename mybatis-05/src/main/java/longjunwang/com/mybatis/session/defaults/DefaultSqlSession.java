package longjunwang.com.mybatis.session.defaults;

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

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T selectOne(String statement) {
        return (T) ("你被代理了！" + "方法：" + statement );
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
        Environment environment = configuration.getEnvironment();
        try {
            Connection connection = environment.getDataSource().getConnection();
            BoundSql boundSql = mappedStatement.getBoundSql();
            PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());
            preparedStatement.setLong(1, Long.parseLong(((Object[]) parameter)[0].toString()));
            ResultSet resultSet = preparedStatement.executeQuery();
            List<T> objList = resultSet2Obj(resultSet, Class.forName(boundSql.getResultType()));
            return objList.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private <T> List<T> resultSet2Obj(ResultSet resultSet, Class<?> clazz) {
        List<T> list = new ArrayList<>();
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                T obj = (T)clazz.newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    Object value = resultSet.getObject(i);
                    String columnName = metaData.getColumnName(i);
                    String methodName = initMethodName(columnName);
                    convert2Obj(obj, value, methodName, clazz);
                }
                list.add(obj);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private <T> void convert2Obj(T obj, Object value, String methodName, Class<?> clazz) throws Exception {
        Method method;
        if (value instanceof Timestamp){
            method = clazz.getMethod(methodName, Date.class);
        }else{
            method = clazz.getMethod(methodName, value.getClass());
        }
        method.invoke(obj, value);
    }

    private String initMethodName(String columnName) {
        return "set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
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
