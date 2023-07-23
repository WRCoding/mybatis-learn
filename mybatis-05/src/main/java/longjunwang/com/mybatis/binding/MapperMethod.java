package longjunwang.com.mybatis.binding;

import longjunwang.com.mybatis.mapping.MappedStatement;
import longjunwang.com.mybatis.mapping.SqlCommandType;
import longjunwang.com.mybatis.session.Configuration;
import longjunwang.com.mybatis.session.SqlSession;

import java.lang.reflect.Method;

/**
 * desc: MapperMethod
 *
 * @author ink
 * date:2023-07-16 16:59
 */
public class MapperMethod {

    private final SqlCommand command;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration configuration) {
        this.command = new SqlCommand(configuration, mapperInterface, method);
    }

    public Object execute(SqlSession sqlSession, Object[] args){
        Object result = null;
        switch (command.getType()){
            case SELECT:
                result = sqlSession.selectOne(command.getName(), args);
                break;
            default:
                throw new RuntimeException("Unknown execution method for: " + command.getName());
        }
        return result;
    }

    public static class SqlCommand{
        private final String name;
        private final SqlCommandType type;

        public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
            MappedStatement mappedStatement = getMappedStatement(configuration, mapperInterface, method);
            name = mappedStatement.getId();
            type = mappedStatement.getSqlCommandType();
        }

        private static MappedStatement getMappedStatement(Configuration configuration, Class<?> mapperInterface, Method method) {
            String statementName = mapperInterface.getName() + "." + method.getName();
            return configuration.getMappedStatement(statementName);
        }

        public String getName() {
            return name;
        }

        public SqlCommandType getType() {
            return type;
        }
    }
}
