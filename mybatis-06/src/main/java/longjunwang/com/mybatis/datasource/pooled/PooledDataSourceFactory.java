package longjunwang.com.mybatis.datasource.pooled;

import longjunwang.com.mybatis.datasource.unpooled.UnPooledDataSourceFactory;

import javax.sql.DataSource;

/**
 * desc: PooledDataSourceFactory
 *
 * @author ink
 * date:2023-07-23 13:21
 */
public class PooledDataSourceFactory extends UnPooledDataSourceFactory {

    @Override
    public DataSource getDataSource() {
        PooledDataSource pooledDataSource = new PooledDataSource();
        pooledDataSource.setDriver(properties.getProperty("driver"));
        pooledDataSource.setUrl(properties.getProperty("url"));
        pooledDataSource.setUsername(properties.getProperty("username"));
        pooledDataSource.setPassword(properties.getProperty("password"));
        return pooledDataSource;
    }
}
