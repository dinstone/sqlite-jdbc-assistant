/*
 * Copyright (C) 2017-2018 dinstone<dinstone@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dinstone.sqlite.jdbc.pool;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.sqlite.javax.SQLiteConnectionPoolDataSource;

public class SqliteJdbcDataSource implements DataSource {

    private SQLiteConnectionPoolDataSource pooledDataSource;

    private ConnectionPoolManager connectionPool;

    public SqliteJdbcDataSource(SqliteDataSourceConfig config) {
        pooledDataSource = new SQLiteConnectionPoolDataSource();
        pooledDataSource.setConfig(config.getSqLiteConfig());
        pooledDataSource.setUrl(config.getDatabaseUrl());

        int maxConnetionSize = config.getMaxConnetionSize();
        int maxWaitTimeout = config.getMaxWaitTimeout();
        connectionPool = new ConnectionPoolManager(pooledDataSource, maxConnetionSize, maxWaitTimeout);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return pooledDataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        pooledDataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        pooledDataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return pooledDataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return pooledDataSource.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return iface.cast(this);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return connectionPool.getConnection(username, password);
    }

    public synchronized void dispose() {
        try {
            connectionPool.dispose();
        } catch (SQLException e) {
            // ignore
        }
    }
}
