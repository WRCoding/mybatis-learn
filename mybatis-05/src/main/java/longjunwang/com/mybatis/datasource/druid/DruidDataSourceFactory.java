package longjunwang.com.mybatis.datasource.druid;

import com.alibaba.druid.pool.DruidDataSource;
import longjunwang.com.mybatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * desc: DruidDataSourceFactory
 *
 * @author ink
 * date:2023-07-22 14:52
 */
public class DruidDataSourceFactory implements DataSourceFactory {

    private Properties properties;
    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public DataSource getDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(properties.getProperty("driver"));
        dataSource.setUrl(properties.getProperty("url"));
        dataSource.setUsername(properties.getProperty("username"));
        dataSource.setPassword(properties.getProperty("password"));
        return dataSource;
    }
}
