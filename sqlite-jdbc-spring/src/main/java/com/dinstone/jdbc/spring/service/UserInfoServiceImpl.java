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
package com.dinstone.jdbc.spring.service;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.dinstone.jdbc.spring.UserInfo;

/**
 * @author guojf
 * @version 1.0.0.2013-6-4
 */
public class UserInfoServiceImpl implements UserInfoService {

	private JdbcTemplate jdbcTemplate;

	public int addUserInfo(UserInfo user) {
		String sql = "create table if not exists user (userId integer, name text, age integer)";
		jdbcTemplate.execute(sql);

		sql = "insert into user(userId,name,age) values(?,?,?)";
		return jdbcTemplate.update(sql, user.getUserId(), user.getName(), user.getAge());
	}

	public UserInfo getUserInfo(String userName) {
		String sql = "select * from user where name=?";
		List<UserInfo> ut = jdbcTemplate.query(sql, new Object[] { userName },
				new BeanPropertyRowMapper<>(UserInfo.class));
		if (ut != null && ut.size() > 0) {
			return ut.get(0);
		}

		return null;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
