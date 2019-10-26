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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.sqlite.jdbc.template.JdbcTemplate;
import com.dinstone.sqlite.jdbc.template.RowMapper;

public class Service {

    private static Logger logger = LoggerFactory.getLogger(TransactionTemplate.class);

    private JdbcTemplate jdbcTemplate;

    private TransactionTemplate transactionTemplate;

    public Service(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    public void action() throws SQLException {
        // init();

        transactionTemplate.execute(new TransactionCallback<Void>() {

            @Override
            public Void doInTransaction(TransactionStatus status) {
                try {
                    doAction();
                } catch (Exception e) {
                    throw new RuntimeException("execute error", e);
                }
                return null;
            }
        });

        clear();
    }

    public void init() throws SQLException {
        jdbcTemplate.update("drop table if exists test;");
        jdbcTemplate.update("create table test(name varchar(20));");
        jdbcTemplate.update("insert into test values('dinstone');");
        List<String> sList = jdbcTemplate.query("select name from test", new RowMapper<String>() {

            @Override
            public String mapRow(ResultSet rs, int index) throws SQLException {
                return rs.getString("name");
            }
        });
        logger.info("name is {}", sList.get(0));

    }

    private void doAction() throws SQLException {
        jdbcTemplate.update("insert into test values('sqliteHelper test');");
        List<String> sList = jdbcTemplate.query("select name from test", new RowMapper<String>() {

            @Override
            public String mapRow(ResultSet rs, int index) throws SQLException {
                return rs.getString("name");
            }
        });
        logger.info("list size is {}", sList.size());

        doAction1();
    }

    private void doAction1() {
        transactionTemplate.execute(new TransactionCallback<Void>() {

            @Override
            public Void doInTransaction(TransactionStatus status) {
                try {
                    doAction2();
                } catch (Exception e) {
                    throw new RuntimeException("execute error", e);
                }
                return null;
            }

        });
    }

    private void doAction2() throws SQLException {
        jdbcTemplate.update("insert into test values('sqliteHelper test');");
        List<String> sList = jdbcTemplate.query("select name from test", new RowMapper<String>() {

            @Override
            public String mapRow(ResultSet rs, int index) throws SQLException {
                return rs.getString("name");
            }
        });
        logger.info("list size is {}", sList.size());
    }

    public void clear() throws SQLException {
        jdbcTemplate.update("insert into test values('sqliteHelper test');");
        List<String> sList = jdbcTemplate.query("select name from test", new RowMapper<String>() {

            @Override
            public String mapRow(ResultSet rs, int index) throws SQLException {
                return rs.getString("name");
            }
        });
        logger.info("list size is {}", sList.size());
    }

    public void insert() throws SQLException {
        transactionTemplate.execute(new TransactionCallback<Void>() {

            @Override
            public Void doInTransaction(TransactionStatus status) {
                try {
                    for (int i = 0; i < 10; i++) {
                        jdbcTemplate.update("insert into test values('" + i + "')");
                    }
                } catch (Exception e) {
                    throw new RuntimeException("execute error", e);
                }
                return null;
            }
        });
    }

    public void query() throws SQLException {
        List<String> sList = jdbcTemplate.query("select name from test", new RowMapper<String>() {

            @Override
            public String mapRow(ResultSet rs, int index) throws SQLException {
                return rs.getString("name");
            }
        });
        logger.info("query 1 list size is {}", sList.size());

        sList = jdbcTemplate.query("select name from test", new RowMapper<String>() {

            @Override
            public String mapRow(ResultSet rs, int index) throws SQLException {
                return rs.getString("name");
            }
        });
        logger.info("query 2 list size is {}", sList.size());
    }
}
