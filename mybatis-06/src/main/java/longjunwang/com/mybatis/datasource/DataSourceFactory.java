package longjunwang.com.mybatis.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * desc:
 *
 * @author ink
 * date:2023-07-22 14:51
 */
public interface DataSourceFactory {

    void setProperties(Properties properties);

    DataSource getDataSource();
}
