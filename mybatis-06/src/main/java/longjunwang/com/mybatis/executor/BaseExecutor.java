package longjunwang.com.mybatis.executor;

import lombok.extern.slf4j.Slf4j;
import longjunwang.com.mybatis.mapping.BoundSql;
import longjunwang.com.mybatis.mapping.MappedStatement;
import longjunwang.com.mybatis.session.Configuration;
import longjunwang.com.mybatis.session.ResultHandler;
import longjunwang.com.mybatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * desc: BaseExecutor
 *
 * @author ink
 * date:2023-07-29 12:52
 */
@Slf4j
public abstract class BaseExecutor implements Executor{

    protected Configuration configuration;
    protected Transaction transaction;
    protected Executor wrapper;
    private boolean closed;

    public BaseExecutor(Configuration configuration, Transaction transaction) {
        this.configuration = configuration;
        this.transaction = transaction;
        this.wrapper = this;
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        if (closed){
            throw new RuntimeException("Executor was closed");
        }
        return doQuery(ms, parameter, resultHandler, boundSql);
    }

    protected abstract  <E> List<E> doQuery(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql);

    @Override
    public Transaction getTransaction() {
        if (closed) {
            throw new RuntimeException("Executor was closed.");
        }
        return transaction;
    }
    @Override
    public void commit(boolean required) throws SQLException {
        if (closed) {
            throw new RuntimeException("Cannot commit, transaction is already closed");
        }
        if (required) {
            transaction.commit();
        }
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        if (!closed) {
            if (required) {
                transaction.rollback();
            }
        }
    }

    @Override
    public void close(boolean forceRollback) {
        try {
            try {
                rollback(forceRollback);
            } finally {
                transaction.close();
            }
        } catch (SQLException e) {
            log.warn("Unexpected exception on closing transaction.  Cause: " + e);
        } finally {
            transaction = null;
            closed = true;
        }
    }
}
