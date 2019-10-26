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

import java.sql.SQLException;

import org.sqlite.SQLiteConfig.JournalMode;

import com.dinstone.sqlite.jdbc.pool.SqliteDataSourceConfig;
import com.dinstone.sqlite.jdbc.pool.SqliteJdbcDataSource;
import com.dinstone.sqlite.jdbc.template.JdbcTemplate;

public class TransactionTemplateTest {

    public static void main(String[] args) throws SQLException {
        SqliteDataSourceConfig config = new SqliteDataSourceConfig();
        config.getSqLiteConfig().setJournalMode(JournalMode.WAL);
        config.getSqLiteConfig().setBusyTimeout(10000);
        config.setUrl("jdbc:sqlite:data/jdbc.db");
        SqliteJdbcDataSource dataSource = new SqliteJdbcDataSource(config);

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        final TransactionTemplate tranTemplate = new TransactionTemplate(new TransactionManager(dataSource));

        final Service service = new Service(jdbcTemplate, tranTemplate);
        service.init();

        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    service.insert();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    service.query();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();

        try {
            service.action();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dataSource.dispose();
    }

}
