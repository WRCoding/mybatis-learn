package longjunwang.com.mybatis.executor.resultset;

import longjunwang.com.mybatis.executor.Executor;
import longjunwang.com.mybatis.mapping.BoundSql;
import longjunwang.com.mybatis.mapping.MappedStatement;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * desc: DefaultResultSetHandler
 *
 * @author ink
 * date:2023-07-29 12:50
 */
public class DefaultResultSetHandler implements ResultSetHandler{

    private final BoundSql boundSql;

    public DefaultResultSetHandler(Executor executor, MappedStatement mappedStatement, BoundSql boundSql) {
        this.boundSql = boundSql;
    }

    @Override
    public <E> List<E> handleResultSets(Statement statement) throws SQLException {
        ResultSet resultSet = statement.getResultSet();
        try {
            return resultSet2Obj(resultSet, Class.forName(boundSql.getResultType()));
        } catch (ClassNotFoundException e) {
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
}
