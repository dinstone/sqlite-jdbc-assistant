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

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.dinstone.sqlite.jdbc.transaction.ConnectionHolder;

public class TransactionHelper {

	private static final ThreadLocal<Map<DataSource, ConnectionHolder>> resources = new ThreadLocal<Map<DataSource, ConnectionHolder>>();

	public static ConnectionHolder getConnectionHolder(DataSource dataSource) {
		Map<DataSource, ConnectionHolder> map = resources.get();
		if (map == null) {
			return null;
		}
		return map.get(dataSource);
	}

	public static void bindConnectionHolder(DataSource dataSource, ConnectionHolder holder) {
		Map<DataSource, ConnectionHolder> map = resources.get();
		if (map == null) {
			map = new HashMap<DataSource, ConnectionHolder>();
			resources.set(map);
		}
		map.put(dataSource, holder);
	}

	public static void removeConnectionHolder(DataSource dataSource) {
		Map<DataSource, ConnectionHolder> map = resources.get();
		if (map != null) {
			map.remove(dataSource);
		}
	}

}
