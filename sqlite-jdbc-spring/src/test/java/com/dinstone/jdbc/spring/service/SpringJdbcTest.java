package com.dinstone.jdbc.spring.service;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringJdbcTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext sac = new AnnotationConfigApplicationContext(SpringJdbcTest.class);
		DataSource ds = (DataSource) sac.getBean("dataSource");
		try {
			ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Bean(name = "dataSource")
	public DataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.sqlite.JDBC");
		dataSource.setUrl("jdbc:sqlite:config.db");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		return dataSource;
	}
}
