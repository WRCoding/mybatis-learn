package longjunwang.com.mybatis.datasource.unpooled;

import longjunwang.com.mybatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * desc: UnpooledDataSourceFactory
 *
 * @author ink
 * date:2023-07-23 13:22
 */
public class UnPooledDataSourceFactory implements DataSourceFactory {

    protected Properties properties;
    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public DataSource getDataSource() {
        UnPooledDataSource unPooledDataSource = new UnPooledDataSource();
        unPooledDataSource.setDriver(properties.getProperty("driver"));
        unPooledDataSource.setUrl(properties.getProperty("url"));
        unPooledDataSource.setUsername(properties.getProperty("username"));
        unPooledDataSource.setPassword(properties.getProperty("password"));
        return unPooledDataSource;
    }
}
