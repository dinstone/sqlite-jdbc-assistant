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

import org.sqlite.JDBC;
import org.sqlite.SQLiteConfig;

public class SqliteDataSourceConfig {

	/**
	 * use memory database in default
	 */
	private String url = JDBC.PREFIX;

	/**
	 * the name of the current database
	 */
	private String databaseName = "";

	/**
	 * sqlite config.
	 */
	private SQLiteConfig sqLiteConfig = new SQLiteConfig();

	/**
	 * max connetion size.
	 */
	private int maxSize = Runtime.getRuntime().availableProcessors();

	/**
	 * the maximum time in seconds to wait for a free connection.
	 */
	private int maxWait = Runtime.getRuntime().availableProcessors();

	public SQLiteConfig getSqLiteConfig() {
		return sqLiteConfig;
	}

	public void setSqLiteConfig(SQLiteConfig sqLiteConfig) {
		this.sqLiteConfig = sqLiteConfig;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public int getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

}
