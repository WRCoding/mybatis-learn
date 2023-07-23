package longjunwang.com.mybatis.datasource.pooled;

import lombok.extern.slf4j.Slf4j;
import longjunwang.com.mybatis.datasource.unpooled.UnPooledDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Logger;

/**
 * desc: PooledDataSource
 *
 * @author ink
 * date:2023-07-23 13:21
 */
@Slf4j
public class PooledDataSource implements DataSource {

    private final PoolState state = new PoolState(this);
    private final UnPooledDataSource dataSource;

    // 活跃连接数
    protected int poolMaximumActiveConnections = 10;
    // 空闲连接数
    protected int poolMaximumIdleConnections = 5;
    // 在被强制返回之前,池中连接被检查的时间
    protected int poolMaximumCheckoutTime = 20000;
    // 这是给连接池一个打印日志状态机会的低层次设置,还有重新尝试获得连接, 这些情况下往往需要很长时间 为了避免连接池没有配置时静默失败)。
    protected int poolTimeToWait = 20000;
    // 发送到数据的侦测查询,用来验证连接是否正常工作,并且准备 接受请求。默认是“NO PING QUERY SET” ,这会引起许多数据库驱动连接由一 个错误信息而导致失败
    protected String poolPingQuery = "NO PING QUERY SET";
    // 开启或禁用侦测查询
    protected boolean poolPingEnabled = false;
    // 用来配置 poolPingQuery 多次时间被用一次
    protected int poolPingConnectionsNotUsedFor = 0;

    private int expectedConnectionTypeCode;

    public PooledDataSource() {
        this.dataSource = new UnPooledDataSource();
    }

    protected void pushConnection(PooledConnection connection) {
        synchronized (state) {
            state.activeConnections.remove(connection);
            if (connection.is)
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    public boolean pingConnection(PooledConnection connection) {
        boolean result;

        try {
            //真实链接是否关闭
            result = !connection.getRealConnection().isClosed();
        } catch (SQLException e) {
            log.info("Connection " + connection.getRealHashCode() + " is BAD: " + e.getMessage());
            result = false;
        }
        if (result) {
            if (poolPingEnabled) {
                if (poolPingConnectionsNotUsedFor >= 0 && connection.getTimeElapsedSinceLastUse() > poolPingConnectionsNotUsedFor) {
                    log.info("Testing connection " + connection.getRealHashCode() + " ...");
                    Connection realConnection = connection.getRealConnection();
                    try {
                        Statement statement = realConnection.createStatement();
                        ResultSet resultSet = statement.executeQuery(poolPingQuery);
                        resultSet.close();
                        if (!realConnection.getAutoCommit()) {
                            realConnection.rollback();
                        }
                        log.info("Connection " + connection.getRealHashCode() + "is GOOD!");
                    } catch (SQLException e) {
                        log.info("Execution of ping query '" + poolPingQuery + "' failed: " + e.getMessage());
                        try {
                            connection.getRealConnection().close();
                        } catch (SQLException ignore) {
                        }
                        result = false;
                        log.info("Connection " + connection.getRealHashCode() + " is BAD: " + e.getMessage());
                    }
                }
            }
        }
        return result;
    }
}
