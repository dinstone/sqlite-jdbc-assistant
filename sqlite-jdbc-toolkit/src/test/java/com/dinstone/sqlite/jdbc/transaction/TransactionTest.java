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

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import org.sqlite.SQLiteConfig.JournalMode;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.sqlite.jdbc.pool.SqliteDataSourceConfig;
import com.dinstone.sqlite.jdbc.pool.SqliteJdbcDataSource;
import com.dinstone.sqlite.jdbc.template.JdbcTemplate;
import com.dinstone.sqlite.jdbc.template.RowMapper;

/**
 * sqlite transaction isolation is seralizable.
 * 
 * @author dinstone
 *
 */
public class TransactionTest {

	private static final Logger logger = LoggerFactory.getLogger(TransactionTest.class);

	public static void main(String[] args) {
		SqliteDataSourceConfig config = new SqliteDataSourceConfig();
		config.getSqLiteConfig().setJournalMode(JournalMode.WAL);
		config.getSqLiteConfig().setBusyTimeout(10000);
		// config.getSqLiteConfig().setTransactionMode(TransactionMode.IMMEDIATE);
		config.setUrl("jdbc:sqlite:data/tsdb.db");
		SqliteJdbcDataSource dataSource = new SqliteJdbcDataSource(config);

		final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		final TransactionTemplate transTemplate = new TransactionTemplate(new TransactionManager(dataSource));

		Thread tar = new Thread(new Runnable() {

			@Override
			public void run() {
				transA(jdbcTemplate, transTemplate);
			}
		});
		tar.start();

		Thread tbr = new Thread(new Runnable() {

			@Override
			public void run() {
				transB(jdbcTemplate, transTemplate);
			}
		});
		tbr.start();

		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}

		dataSource.dispose();
	}

	protected static void query(final JdbcTemplate jdbcTemplate) throws SQLException {
		List<String> sList = jdbcTemplate.query("select * from tb_user", new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int index) throws SQLException {
				return rs.getInt("id") + " " + rs.getString("name") + " " + rs.getInt("age") + " " + rs.getInt("sex")
						+ " " + rs.getTimestamp("createTime");
			}
		});
		for (String line : sList) {
			logger.info("row is {}", line);
		}
	}

	protected static void update(JdbcTemplate jdbcTemplate) throws SQLException {
		int s = jdbcTemplate.update("update tb_user set sex=sex-1 where id=1");
		logger.info("update size is " + s);
	}

	protected static void insert(JdbcTemplate jdbcTemplate) throws SQLException {
		int a = new Random().nextInt(1000);
		int s = jdbcTemplate.update("insert into tb_user(name,age) values('user-" + a + "'," + a + ")");
		logger.info("insert size is " + s);
	}

	private static void transA(final JdbcTemplate jdbcTemplate, TransactionTemplate transTemplate) {

		transTemplate.execute(new TransactionCallback<Void>() {

			@Override
			public Void doInTransaction(TransactionStatus status) {
				try {
					logger.info("trans A first query");
					query(jdbcTemplate);

					logger.info("trans A second qurey");
					query(jdbcTemplate);

					logger.info("trans A third qurey");
					query(jdbcTemplate);
				} catch (Exception e) {
					throw new TransactionException("execute error", e);
				}
				return null;
			}
		});

		logger.info("out trans A qurey");
		try {
			query(jdbcTemplate);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void transB(final JdbcTemplate jdbcTemplate, TransactionTemplate transTemplate) {

		transTemplate.execute(new TransactionCallback<Void>() {

			@Override
			public Void doInTransaction(TransactionStatus status) {
				try {
					logger.info("trans B first update");
					update(jdbcTemplate);

					logger.info("trans B first qurey");
					query(jdbcTemplate);

					logger.info("trans B first insert");
					insert(jdbcTemplate);

					logger.info("trans B second qurey");
					query(jdbcTemplate);
				} catch (Exception e) {
					throw new TransactionException("execute error", e);
				}
				return null;
			}
		});

	}

}
