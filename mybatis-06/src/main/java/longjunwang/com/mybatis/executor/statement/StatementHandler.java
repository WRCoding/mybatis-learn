package longjunwang.com.mybatis.executor.statement;

import longjunwang.com.mybatis.executor.resultset.ResultSetHandler;
import longjunwang.com.mybatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * desc: StatementHandler
 * 语句处理器
 * @author ink
 * date:2023-07-29 12:52
 */
public interface StatementHandler {

    Statement prepare(Connection connection) throws SQLException;

    void parameterize(Statement statement) throws SQLException;

    <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;
}
