package longjunwang.com.mybatis.session;

/**
 * desc: SqlSessionFactory
 *
 * @author ink
 * date:2023-07-09 18:45
 */
public interface SqlSessionFactory {
    SqlSession openSession();
}
