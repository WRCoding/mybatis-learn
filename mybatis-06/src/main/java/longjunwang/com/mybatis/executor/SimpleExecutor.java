package longjunwang.com.mybatis.executor;

import longjunwang.com.mybatis.executor.statement.StatementHandler;
import longjunwang.com.mybatis.mapping.BoundSql;
import longjunwang.com.mybatis.mapping.MappedStatement;
import longjunwang.com.mybatis.session.Configuration;
import longjunwang.com.mybatis.session.ResultHandler;
import longjunwang.com.mybatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * desc: SimpleExecutor
 *
 * @author ink
 * date:2023-07-29 12:52
 */
public class SimpleExecutor extends BaseExecutor{

    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    protected <E> List<E> doQuery(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        try {
            Configuration configuration = ms.getConfiguration();
            StatementHandler statementHandler = configuration.newStatementHandler(this, ms, parameter, resultHandler, boundSql);
            Connection connection = transaction.getConnection();
            Statement statement = statementHandler.prepare(connection);
            statementHandler.parameterize(statement);
            return statementHandler.query(statement, resultHandler);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
