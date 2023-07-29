package longjunwang.com.mybatis.session;

import longjunwang.com.mybatis.binding.MapperRegistry;
import longjunwang.com.mybatis.datasource.druid.DruidDataSourceFactory;
import longjunwang.com.mybatis.datasource.pooled.PooledDataSourceFactory;
import longjunwang.com.mybatis.datasource.unpooled.UnPooledDataSourceFactory;
import longjunwang.com.mybatis.executor.Executor;
import longjunwang.com.mybatis.executor.SimpleExecutor;
import longjunwang.com.mybatis.executor.resultset.DefaultResultSetHandler;
import longjunwang.com.mybatis.executor.resultset.ResultSetHandler;
import longjunwang.com.mybatis.executor.statement.PreparedStatementHandler;
import longjunwang.com.mybatis.executor.statement.StatementHandler;
import longjunwang.com.mybatis.mapping.BoundSql;
import longjunwang.com.mybatis.mapping.Environment;
import longjunwang.com.mybatis.mapping.MappedStatement;
import longjunwang.com.mybatis.transaction.Transaction;
import longjunwang.com.mybatis.transaction.jdbc.JdbcTransactionFactory;
import longjunwang.com.mybatis.type.TypeAliasRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * desc: Configuration
 *
 * @author ink
 * date:2023-07-15 16:27
 */
public class

Configuration {

    protected Environment environment;
    protected MapperRegistry mapperRegistry = new MapperRegistry(this);

    protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();


    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    public Configuration(){
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);

        typeAliasRegistry.registerAlias("UNPOOLED", UnPooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
    }
    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, BoundSql boundSql){
        return new DefaultResultSetHandler(executor, mappedStatement, boundSql);
    }

    public Executor newExecutor(Transaction transaction){
        return new SimpleExecutor(this, transaction);
    }

    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, ResultHandler resultHandler,
                                                BoundSql boundSql){
        return new PreparedStatementHandler(executor, mappedStatement, parameter, resultHandler, boundSql);
    }
}
