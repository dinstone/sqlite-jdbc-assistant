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
package com.dinstone.sqlite.jdbc.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.dinstone.sqlite.jdbc.support.DataSourceHelper;
import com.dinstone.sqlite.jdbc.support.TransactionHelper;

public class TransactionManager {

	private DataSource dataSource;

	public TransactionManager(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public TransactionStatus begin() throws TransactionException {
		ConnectionHolder holder = TransactionHelper.getConnectionHolder(dataSource);
		if (holder != null && holder.isTransactionActive()) {
			return new TransactionStatus(holder, false);
		}

		Connection con = null;
		try {
			if (holder == null) {
				holder = new ConnectionHolder(dataSource.getConnection());
				TransactionHelper.bindConnectionHolder(dataSource, holder);
			}

			con = holder.getConnection();
			if (con.getAutoCommit()) {
				con.setAutoCommit(false);
			}
			holder.setTransactionActive(true);

			return new TransactionStatus(holder, true);
		} catch (SQLException e) {
			DataSourceHelper.releaseConnection(con, dataSource);
			throw new TransactionException("can not create jdbc transaction", e);
		}
	}

	public void commit(TransactionStatus status) throws TransactionException {
		if (status.isCompleted()) {
			throw new TransactionException(
					"transaction is already completed, do not call commit or rollback more than once per transaction");
		}

		try {
			if (status.isNewTransaction()) {
				status.getConnectionHolder().getConnection().commit();
			}
		} catch (SQLException e) {
			throw new TransactionException("can not commit jdbc transaction", e);
		} finally {
			cleanupAfterCompletion(status);
		}

	}

	private void cleanupAfterCompletion(TransactionStatus status) {
		// transaction is completed
		status.setCompleted();

		// cleanup connection and transaction when is new transaction
		if (status.isNewTransaction()) {
			try {
				ConnectionHolder holder = status.getConnectionHolder();
				Connection connection = holder.getConnection();
				connection.setAutoCommit(true);

				TransactionHelper.removeConnectionHolder(dataSource);

				DataSourceHelper.releaseConnection(connection, dataSource);
			} catch (SQLException e) {
				throw new TransactionException("cleanup transaction exception", e);
			}
		}
	}

	public void rollback(TransactionStatus status) throws TransactionException {
		if (status.isCompleted()) {
			throw new TransactionException(
					"transaction is already completed, do not call commit or rollback more than once per transaction");
		}

		if (status.isNewTransaction()) {
			try {
				status.getConnectionHolder().getConnection().rollback();
			} catch (SQLException e) {
				throw new TransactionException("can not rollback jdbc transaction", e);
			} finally {
				cleanupAfterCompletion(status);
			}
		}
	}

}
