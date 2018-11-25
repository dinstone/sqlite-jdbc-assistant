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

public interface RowMapper<T> {

	/**
	 * Implementations must implement this method to map each row of data in the
	 * ResultSet. This method should not call {@code next()} on the ResultSet; it is
	 * only supposed to map values of the current row.
	 * 
	 * @param rs
	 *            the ResultSet to map (pre-initialized for the current row)
	 * @param rowNum
	 *            the number of the current row
	 * @return the result object for the current row
	 * @throws SQLException
	 *             if a SQLException is encountered getting column values (that is,
	 *             there's no need to catch SQLException)
	 */
	T mapRow(ResultSet rs, int rowNum) throws SQLException;
}