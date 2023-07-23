package longjunwang.com.mybatis.transaction.jdbc;

import longjunwang.com.mybatis.session.TransactionIsolationLevel;
import longjunwang.com.mybatis.transaction.Transaction;
import longjunwang.com.mybatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * desc: JdbcTransactionFactory
 *
 * @author ink
 * date:2023-07-22 14:54
 */
public class JdbcTransactionFactory implements TransactionFactory {
    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource, level, autoCommit);
    }
}
