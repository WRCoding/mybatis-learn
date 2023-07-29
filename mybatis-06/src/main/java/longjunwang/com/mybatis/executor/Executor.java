package longjunwang.com.mybatis.executor;

import longjunwang.com.mybatis.mapping.BoundSql;
import longjunwang.com.mybatis.mapping.MappedStatement;
import longjunwang.com.mybatis.session.ResultHandler;
import longjunwang.com.mybatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * desc: Executor
 *
 * @author ink
 * date:2023-07-29 12:52
 */
public interface Executor {
    ResultHandler NO_RESULT_HANDLER = null;

    <E> List<E> query(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql);

    Transaction getTransaction();

    void commit(boolean required) throws SQLException;

    void rollback(boolean required) throws SQLException;

    void close(boolean forceRollback);

}
