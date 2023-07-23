package longjunwang.com.mybatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * desc: Transaction
 *
 * @author ink
 * date:2023-07-22 14:55
 */
public interface Transaction {
    Connection getConnection() throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;

    void close() throws SQLException;
}
