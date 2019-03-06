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
package com.dinstone.sqlite.jdbc.template;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.sqlite.jdbc.support.DataSourceHelper;

public class JdbcTemplate {

	private static final Logger logger = LoggerFactory.getLogger(JdbcTemplate.class);

	/** If this variable is false, we will throw exceptions on SQL warnings */
	private boolean ignoreWarnings = true;

	private DataSource dataSource;

	public JdbcTemplate(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public boolean isIgnoreWarnings() {
		return ignoreWarnings;
	}

	public void setIgnoreWarnings(boolean ignoreWarnings) {
		this.ignoreWarnings = ignoreWarnings;
	}

	public void execute(final String sql) throws SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug("Executing SQL statement [" + sql + "]");
		}

		Connection connection = DataSourceHelper.getConnection(dataSource);
		try (Statement statement = connection.createStatement()) {
			statement.execute(sql);
			handleWarnings(statement);
		} finally {
			DataSourceHelper.releaseConnection(connection, dataSource);
		}
	}

	private void handleWarnings(Statement stmt) throws SQLException {
		if (isIgnoreWarnings()) {
			if (logger.isDebugEnabled()) {
				SQLWarning warningToLog = stmt.getWarnings();
				while (warningToLog != null) {
					logger.debug("SQLWarning ignored: SQL state '" + warningToLog.getSQLState() + "', error code '"
							+ warningToLog.getErrorCode() + "', message [" + warningToLog.getMessage() + "]");
					warningToLog = warningToLog.getNextWarning();
				}
			}
		} else {
			handleWarnings(stmt.getWarnings());
		}
	}

	/**
	 * Throw an SQLException if encountering an actual warning.
	 */
	protected void handleWarnings(SQLWarning warning) throws SQLException {
		if (warning != null) {
			throw new SQLException("SQL Warning Exception", warning);
		}
	}

	public <T> T query(final String sql, final ResultSetExtractor<T> rse) throws SQLException {
		Connection connection = DataSourceHelper.getConnection(dataSource);
		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery(sql)) {
				return rse.extractData(rs);
			}
		} finally {
			DataSourceHelper.releaseConnection(connection, dataSource);
		}
	}

	public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws SQLException {
		Connection connection = DataSourceHelper.getConnection(dataSource);
		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery(sql)) {
				List<T> rl = new ArrayList<T>();
				int rowNum = 0;
				while (rs.next()) {
					rl.add(rowMapper.mapRow(rs, rowNum++));
				}
				return rl;
			}
		} finally {
			DataSourceHelper.releaseConnection(connection, dataSource);
		}
	}

	public int update(String sql) throws SQLException {
		Connection connection = DataSourceHelper.getConnection(dataSource);
		try (Statement statement = connection.createStatement()) {
			return statement.executeUpdate(sql);
		} finally {
			DataSourceHelper.releaseConnection(connection, dataSource);
		}
	}

	public int[] batchUpdate(final String... sqls) throws SQLException {
		Connection connection = DataSourceHelper.getConnection(dataSource);
		try {
			int[] states = new int[sqls.length];
			for (int i = 0; i < states.length; i++) {
				try (Statement statement = connection.createStatement()) {
					states[i] = statement.executeUpdate(sqls[i]);
				}
			}
			return states;
		} finally {
			DataSourceHelper.releaseConnection(connection, dataSource);
		}
	}
}
