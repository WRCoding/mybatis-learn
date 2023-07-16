package longjunwang.com.mybatis.session;

import longjunwang.com.mybatis.builder.xml.XMLConfigBuilder;
import longjunwang.com.mybatis.session.defaults.DefaultSqlSessionFactory;
import org.dom4j.DocumentException;

import java.io.Reader;

/**
 * desc: SqlSessionFactoryBuilder
 *
 * @author ink
 * date:2023-07-15 16:27
 */
public class SqlSessionFactoryBuilder {

    public static SqlSessionFactory build(Reader reader) throws DocumentException {
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        return build(xmlConfigBuilder.parse());
    }

    public static SqlSessionFactory build(Configuration config) {
        return new DefaultSqlSessionFactory(config);
    }

}
