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
package com.dinstone.sqlite.jdbc.support;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.sqlite.jdbc.transaction.ConnectionHolder;
import com.dinstone.sqlite.jdbc.transaction.TransactionTemplate;

public class DataSourceHelper {

	private static Logger logger = LoggerFactory.getLogger(TransactionTemplate.class);

	public static Connection getConnection(DataSource dataSource) throws SQLException {
		ConnectionHolder conHolder = TransactionHelper.getConnectionHolder(dataSource);
		if (conHolder == null) {
			conHolder = new ConnectionHolder(dataSource.getConnection());
		}
		return conHolder.getConnection();
	}

	public static void releaseConnection(Connection con, DataSource dataSource) {
		try {
			doReleaseConnection(con, dataSource);
		} catch (SQLException ex) {
			logger.debug("can't close jdbc connection", ex);
		} catch (Throwable ex) {
			logger.debug("unexpected exception on closing jdbc connection", ex);
		}
	}

	private static void doReleaseConnection(Connection con, DataSource dataSource) throws SQLException {
		if (con == null) {
			return;
		}
		if (dataSource != null) {
			ConnectionHolder conHolder = TransactionHelper.getConnectionHolder(dataSource);
			if (conHolder != null && connectionEquals(conHolder.getConnection(), con)) {
				// It's the transactional Connection: Don't close it.
				return;
			}
		}

		doCloseConnection(con, dataSource);
	}

	private static void doCloseConnection(Connection con, DataSource dataSource) throws SQLException {
		con.close();
	}

	private static boolean connectionEquals(Connection con1, Connection con2) {
		return con1 == con2 || con1.equals(con2);
	}

}
