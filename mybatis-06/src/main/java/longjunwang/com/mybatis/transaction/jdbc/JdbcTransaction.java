package longjunwang.com.mybatis.transaction.jdbc;

import longjunwang.com.mybatis.session.TransactionIsolationLevel;
import longjunwang.com.mybatis.transaction.Transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * desc: JdbcTransaction
 *
 * @author ink
 * date:2023-07-22 14:54
 */
public class JdbcTransaction implements Transaction {

    protected Connection connection;
    protected DataSource dataSource;

    protected TransactionIsolationLevel level = TransactionIsolationLevel.NONE;

    protected boolean autoCommit;

    public JdbcTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        this.dataSource = dataSource;
        this.level = level;
        this.autoCommit = autoCommit;
    }

    public JdbcTransaction(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() throws SQLException {
        connection = dataSource.getConnection();
        connection.setTransactionIsolation(level.getLevel());
        connection.setAutoCommit(autoCommit);
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        if (noAutoCommit()){
            connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (noAutoCommit()){
            connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (noAutoCommit()){
            connection.close();
        }
    }

    private boolean noAutoCommit() throws SQLException {
        return Objects.nonNull(connection) && !connection.getAutoCommit();
    }
}
