package longjunwang.com.mybatis.executor.resultset;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * desc: ResultSetHandler
 *
 * @author ink
 * date:2023-07-29 12:50
 */
public interface ResultSetHandler {

    <E> List<E> handleResultSets(Statement statement) throws SQLException;
}
