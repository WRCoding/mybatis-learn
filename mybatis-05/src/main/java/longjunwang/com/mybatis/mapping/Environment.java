package longjunwang.com.mybatis.mapping;

import longjunwang.com.mybatis.transaction.TransactionFactory;

import javax.sql.DataSource;

/**
 * desc: Environment
 *
 * @author ink
 * date:2023-07-22 14:52
 */
public class Environment {
    private final String id;
    private final TransactionFactory transactionFactory;
    private final DataSource dataSource;

    private Environment(String id, TransactionFactory transactionFactory, DataSource dataSource) {
        this.id = id;
        this.transactionFactory = transactionFactory;
        this.dataSource = dataSource;
    }

    public static class Builder{
        private String id;
        private TransactionFactory transactionFactory;
        private DataSource dataSource;

        public Builder (String id){
            this.id = id;
        }

        public Builder transactionFactory(TransactionFactory transactionFactory){
            this.transactionFactory = transactionFactory;
            return this;
        }

        public Builder dataSource(DataSource dataSource){
            this.dataSource = dataSource;
            return this;
        }

        public Environment build(){
            return new Environment(id, transactionFactory, dataSource);
        }
    }

    public String getId() {
        return id;
    }

    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
