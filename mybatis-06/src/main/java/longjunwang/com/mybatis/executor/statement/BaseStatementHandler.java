package longjunwang.com.mybatis.executor.statement;

import longjunwang.com.mybatis.executor.Executor;
import longjunwang.com.mybatis.executor.resultset.ResultSetHandler;
import longjunwang.com.mybatis.mapping.BoundSql;
import longjunwang.com.mybatis.mapping.MappedStatement;
import longjunwang.com.mybatis.session.Configuration;
import longjunwang.com.mybatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * desc: BaseStatementHandler
 *
 * @author ink
 * date:2023-07-29 12:51
 */
public abstract class BaseStatementHandler implements StatementHandler {

    protected final Configuration configuration;

    protected final Executor executor;

    protected final MappedStatement mappedStatement;

    protected final Object parameterObject;

    protected final ResultSetHandler resultSetHandler;

    protected BoundSql boundSql;

    public BaseStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, ResultHandler resultHandler, BoundSql boundSql) {
        this.configuration = mappedStatement.getConfiguration();
        this.executor = executor;
        this.mappedStatement = mappedStatement;
        this.boundSql = boundSql;
        this.parameterObject = parameterObject;
        this.resultSetHandler = configuration.newResultSetHandler(executor, mappedStatement, boundSql);
    }
    @Override
    public Statement prepare(Connection connection) throws SQLException {
        Statement statement;
        try {
            // 实例化 Statement
            statement = instantiateStatement(connection);
            // 参数设置，可以被抽取，提供配置
            statement.setQueryTimeout(350);
            statement.setFetchSize(10000);
            return statement;
        } catch (Exception e) {
            throw new RuntimeException("Error preparing statement.  Cause: " + e, e);
        }
    }

    protected abstract Statement instantiateStatement(Connection connection) throws SQLException;

    public abstract <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;
}
