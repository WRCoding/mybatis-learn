package longjunwang.com.mybatis.datasource.pooled;

import lombok.extern.slf4j.Slf4j;
import longjunwang.com.mybatis.datasource.unpooled.UnPooledDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Objects;
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
    protected int poolMaximumCheckoutTime = 10000;
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

    protected void pushConnection(PooledConnection connection) throws SQLException {
        synchronized (state) {
            state.activeConnections.remove(connection);
            if (connection.isValid()) {
                if (state.idleConnections.size() < poolMaximumIdleConnections &&
                        connection.getConnectionTypeCode() == expectedConnectionTypeCode) {
                    state.accumulatedCheckoutTime += connection.getCheckOutTime();
                    if (!connection.getRealConnection().getAutoCommit()) {
                        connection.getRealConnection().rollback();
                    }
                    //将原先的连接换一个壳子,再加入到idle列表
                    PooledConnection newConnection = new PooledConnection(connection.getRealConnection(), this);
                    state.idleConnections.add(newConnection);
                    newConnection.setCreatedTimestamp(connection.getCreatedTimestamp());
                    newConnection.setLastUsedTimestamp(connection.getLastUsedTimestamp());
                    connection.invalidate();
                    log.info("Return connection " + newConnection.getRealHashCode() + " to idle");
                    state.notify();
                    ;
                } else {
                    state.accumulatedCheckoutTime += connection.getCheckOutTime();
                    if (!connection.getRealConnection().getAutoCommit()) {
                        connection.getRealConnection().rollback();
                    }
                    connection.getRealConnection().close();
                    log.info("Close connection " + connection.getRealHashCode() + ".");
                }
            } else {
                log.info("A bad connection (" + connection.getRealHashCode() + ") attempted to return to the pool, discarding connection.");
                state.badConnectionCount++;
            }
        }
    }

    protected PooledConnection popConnection(String username, String password) throws SQLException {
        boolean countedWait = false;
        PooledConnection connection = null;
        long t = System.currentTimeMillis();
        int localBadConnectionCount = 0;
        while (Objects.isNull(connection)) {
            synchronized (state) {
                if (!state.idleConnections.isEmpty()) {
                    connection = state.idleConnections.remove(0);
                    log.info("Checked Out Connection " + connection.getRealHashCode() + " from idle");
                } else {
                    //如果没有空闲的连接，但是活跃连接数小于最大值
                    if (state.activeConnections.size() < poolMaximumActiveConnections) {
                        connection = new PooledConnection(dataSource.getConnection(), this);
                        log.info("Created Connection " + connection.getRealHashCode() + ".");
                    } else {
                        PooledConnection oldestActiveConnection = state.activeConnections.get(0);
                        long longestCheckoutTime = oldestActiveConnection.getCheckOutTime();
                        if (longestCheckoutTime > poolMaximumCheckoutTime){
                            //下面两个属性好像没啥作用
                            state.claimedOverdueConnectionCount++;
                            state.accumulatedCheckoutTimeOfOverdueConnections += longestCheckoutTime;
                            state.activeConnections.remove(oldestActiveConnection);
                            if (!oldestActiveConnection.getRealConnection().getAutoCommit()){
                                oldestActiveConnection.getRealConnection().rollback();
                            }
                            //将最老未使用的连接套个新壳给新的用，老的失效
                            connection = new PooledConnection(oldestActiveConnection.getRealConnection(), this);
                            oldestActiveConnection.invalidate();
                            log.info("Claimed overdue connection " + connection.getRealHashCode() + ".");
                        }else {
                            try {
                                if (!countedWait){
                                    state.hadToWaitCount++;
                                    countedWait = true;
                                }
                                log.info("Waiting as long as " + poolTimeToWait + " milliseconds for connection.");
                                long wt = System.currentTimeMillis();
                                state.wait(poolTimeToWait);
                                state.accumulatedWaitTime += System.currentTimeMillis() - wt;
                            } catch (InterruptedException e) {
                                log.info("InterruptedException");
                                break;
                            }
                        }
                    }
                }
                if (Objects.nonNull(connection)){
                    if (connection.isValid()){
                        if (!connection.getRealConnection().getAutoCommit()) {
                            connection.getRealConnection().rollback();
                        }
                        connection.setConnectionTypeCode(assembleConnectionTypeCode(dataSource.getUrl(), username, password));
                        connection.setCheckoutTimestamp(System.currentTimeMillis());
                        connection.setLastUsedTimestamp(System.currentTimeMillis());
                        state.activeConnections.add(connection);
                        state.requestCount++;
                        state.accumulatedRequestTime += System.currentTimeMillis() - t;
                    }else{
                        log.info("A bad connection (" + connection.getRealHashCode() + ") was returned from the pool, getting another connection.");
                        // 如果没拿到，统计信息：失败链接 +1
                        state.badConnectionCount++;
                        localBadConnectionCount++;
                        connection = null;
                        // 失败次数较多，抛异常
                        if (localBadConnectionCount > (poolMaximumIdleConnections + 3)) {
                            log.debug("PooledDataSource: Could not get a good connection to the database.");
                            throw new SQLException("PooledDataSource: Could not get a good connection to the database.");
                        }
                    }
                }
            }
        }
        if (Objects.isNull(connection)){
            log.error("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
            throw new SQLException("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
        }
        return connection;
    }

    public void forceCloseAll(){
        synchronized (state){
            expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
            for (int i = state.activeConnections.size(); i > 0; i--) {
                closeConnection(state.activeConnections.remove(i - 1));
            }

            for (int i = state.idleConnections.size(); i > 0; i--) {
                closeConnection(state.idleConnections.get(i - 1));
            }

            log.info("PooledDataSource forcefully closed/removed all connections.");
        }
    }

    private void closeConnection(PooledConnection connection) {
        try {
            connection.invalidate();
            Connection realConnection = connection.getRealConnection();
            if (!realConnection.getAutoCommit()){
                realConnection.rollback();
            }
        } catch (SQLException ignore) {

        }
    }

    private int assembleConnectionTypeCode(String url, String username, String password) {
        return ("" + url + username + password).hashCode();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return popConnection(dataSource.getUsername(), dataSource.getPassword()).getProxyConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return popConnection(username, password).getProxyConnection();
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

    public void setDriver(String driver) {
        dataSource.setDriver(driver);
        forceCloseAll();
    }

    public void setUrl(String url) {
        dataSource.setUrl(url);
        forceCloseAll();
    }

    public void setUsername(String username) {
        dataSource.setUsername(username);
        forceCloseAll();
    }

    public void setPassword(String password) {
        dataSource.setPassword(password);
        forceCloseAll();
    }
}
