package longjunwang.com.mybatis.transaction;

import longjunwang.com.mybatis.session.TransactionIsolationLevel;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * desc: TransactionFactory
 *
 * @author ink
 * date:2023-07-22 14:55
 */
public interface TransactionFactory {

    Transaction newTransaction(Connection conn);

    Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit);
}
