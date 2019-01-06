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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.sqlite.SQLiteConfig.HexKeyMode;
import org.sqlite.SQLiteConfig.JournalMode;

public class SqliteJdbcDataSourceTest {

	@Test
	public void testGetConnection() throws SQLException {
		final SqliteJdbcDataSource dataSource = getDataSource();
		try {
			long startTime = System.currentTimeMillis();
			int measurementTime = 10000;
			long cycles = 0;
			while (System.currentTimeMillis() - startTime < measurementTime) {
				Connection conn = dataSource.getConnection();
				conn.close();
				cycles++;
			}
			long totalTime = System.currentTimeMillis() - startTime;
			System.out.println("Total: " + totalTime + "ms, Cycles: " + cycles + "ms, Per Cycle: "
					+ ((float) totalTime * 1000 / cycles));
		} finally {
			dataSource.dispose();
		}
	}

	@Test
	public void testWithTransaction() throws Exception {
		final SqliteJdbcDataSource dataSource = getDataSource();
		try {
			initDb(dataSource.getConnection());

			final CountDownLatch starter = new CountDownLatch(1);
			final CountDownLatch stopper = new CountDownLatch(10);
			for (int i = 0; i < 10; i++) {
				new Thread(new Runnable() {

					public void run() {
						try {
							starter.await();

							long s = System.nanoTime();
							actionWithTransaction(dataSource);
							long l = System.nanoTime() - s;
							System.out.println(Thread.currentThread().getName() + " finish, " + (l / 1000000));
						} catch (SQLException e) {
							System.out.println(Thread.currentThread().getName() + " error :");
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} finally {
							stopper.countDown();
						}
					}
				}, "sqlite-thread-" + i).start();
			}

			long s = System.nanoTime();
			starter.countDown();

			stopper.await();
			long e = System.nanoTime();

			System.out.println("testWithTransaction take's " + (e - s) / 1000000);
		} finally {
			dataSource.dispose();
		}
	}

	private SqliteJdbcDataSource getDataSource() {
		SqliteDataSourceConfig config = new SqliteDataSourceConfig();
		config.getSqLiteConfig().setJournalMode(JournalMode.WAL);
		config.getSqLiteConfig().setJounalSizeLimit(1048567);
		config.getSqLiteConfig().setBusyTimeout(10000);
		config.getSqLiteConfig().setHexKeyMode(HexKeyMode.SSE);
		config.setUrl("jdbc:sqlite:data/jdbc-datasource.db");
		config.setMaxSize(10);

		return new SqliteJdbcDataSource(config);
	}

	@Test
	public void testWithoutTransaction() throws Exception {
		final SqliteJdbcDataSource dataSource = getDataSource();
		try {
			initDb(dataSource.getConnection());

			final CountDownLatch starter = new CountDownLatch(1);
			final CountDownLatch stopper = new CountDownLatch(10);
			for (int i = 0; i < 10; i++) {
				new Thread(new Runnable() {

					public void run() {
						try {
							starter.await();

							long s = System.nanoTime();
							actionWithoutTransaction(dataSource);
							long l = System.nanoTime() - s;
							System.out.println(Thread.currentThread().getName() + " finish, " + (l / 1000000));
						} catch (SQLException e) {
							System.out.println(Thread.currentThread().getName() + " error :");
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} finally {
							stopper.countDown();
						}
					}
				}, "sqlite-thread-" + i).start();
			}

			long s = System.nanoTime();
			starter.countDown();

			stopper.await();
			long e = System.nanoTime();

			System.out.println("testWithoutTransaction take's " + (e - s) / 1000000);
		} finally {
			dataSource.dispose();
		}
	}

	private static void initDb(Connection conn) throws SQLException {
		execSql(conn, "create table if not exists temp (threadNo text, ctr integer)");
	}

	private static void execSql(Connection conn, String sql) throws SQLException {
		Statement st = null;
		try {
			st = conn.createStatement();
			st.executeUpdate(sql);
		} finally {
			if (st != null)
				st.close();
		}
	}

	protected void actionWithTransaction(SqliteJdbcDataSource dataSource) throws SQLException {
		Connection conn = dataSource.getConnection();
		try {
			conn.setAutoCommit(false);

			for (int i = 0; i < 10000; i++) {
				execSql(conn, "insert into temp values('" + Thread.currentThread().getName() + "'," + i + ")");
			}

			execSql(conn, "update temp set ctr = ctr + 1 where threadNo='" + Thread.currentThread().getName() + "'");

			conn.commit();

			conn.setAutoCommit(true);
		} finally {
			conn.close();
		}
	}

	protected void actionWithoutTransaction(SqliteJdbcDataSource dataSource) throws SQLException {
		Connection conn = dataSource.getConnection();
		try {
			for (int i = 0; i < 10000; i++) {
				execSql(conn, "insert into temp values('" + Thread.currentThread().getName() + "'," + i + ")");
			}

			execSql(conn, "update temp set ctr = ctr + 1 where threadNo='" + Thread.currentThread().getName() + "'");
		} finally {
			conn.close();
		}
	}

}
