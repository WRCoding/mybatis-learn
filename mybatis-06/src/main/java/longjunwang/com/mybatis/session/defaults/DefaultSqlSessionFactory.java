package longjunwang.com.mybatis.session.defaults;

import longjunwang.com.mybatis.binding.MapperRegistry;
import longjunwang.com.mybatis.executor.Executor;
import longjunwang.com.mybatis.mapping.Environment;
import longjunwang.com.mybatis.session.Configuration;
import longjunwang.com.mybatis.session.SqlSession;
import longjunwang.com.mybatis.session.SqlSessionFactory;
import longjunwang.com.mybatis.session.TransactionIsolationLevel;
import longjunwang.com.mybatis.transaction.Transaction;
import longjunwang.com.mybatis.transaction.TransactionFactory;

import java.sql.SQLException;

/**
 * desc:
 *
 * @author ink
 * date:2023-07-09 18:46
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        Transaction tx = null;
        try {
            Environment environment = configuration.getEnvironment();
            TransactionFactory transactionFactory = environment.getTransactionFactory();
            tx = transactionFactory.newTransaction(configuration.getEnvironment().getDataSource(), TransactionIsolationLevel.READ_COMMITTED, false);
            Executor executor = configuration.newExecutor(tx);
            return new DefaultSqlSession(configuration, executor);
        }catch (Exception e){
            try {
                assert tx != null;
                tx.close();
            }catch (SQLException ignore){

            }
            throw new RuntimeException("Error opening session. Cause: " + e);
        }
    }
}
