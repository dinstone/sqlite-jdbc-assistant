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

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetExtractor<T> {

	/**
	 * Implementations must implement this method to process the entire ResultSet.
	 * 
	 * @param rs
	 *            ResultSet to extract data from. Implementations should not close
	 *            this: it will be closed by the calling JdbcTemplate.
	 * @return an arbitrary result object, or {@code null} if none (the extractor
	 *         will typically be stateful in the latter case).
	 * @throws SQLException
	 *             if a SQLException is encountered getting column values or
	 *             navigating (that is, there's no need to catch SQLException)
	 */
	public abstract T extractData(ResultSet rs) throws SQLException;

}
