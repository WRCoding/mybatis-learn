package longjunwang.com;

import longjunwang.com.entity.User;
import longjunwang.com.mappers.IUserDao;
import longjunwang.com.mybatis.binding.MapperRegistry;
import longjunwang.com.mybatis.io.Resource;
import longjunwang.com.mybatis.session.SqlSession;
import longjunwang.com.mybatis.session.SqlSessionFactory;
import longjunwang.com.mybatis.session.SqlSessionFactoryBuilder;
import longjunwang.com.mybatis.session.defaults.DefaultSqlSessionFactory;
import org.dom4j.DocumentException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;

/**
 * desc:
 *
 * @author ink
 * date:2023-07-09 18:10
 */
public class ApiTest {

    Logger logger = LoggerFactory.getLogger(ApiTest.class);

    @Test
    public void test_MapperProxyFactory() throws IOException, DocumentException {
        Reader resourceAsReader = Resource.getResourceAsReader("mybatis-config-datasource.xml");
        SqlSessionFactory sqlSessionFactory = SqlSessionFactoryBuilder.build(resourceAsReader);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        User res = userDao.queryUserInfoById(1L);
        logger.info("res:{}", res);
    }
}
