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

import java.lang.reflect.UndeclaredThrowableException;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;

public class TransactionTemplate {

	private static Logger logger = LoggerFactory.getLogger(TransactionTemplate.class);

	private TransactionManager transactionManager;

	public TransactionTemplate(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public <T> T execute(TransactionCallback<T> action) throws TransactionException {
		TransactionStatus status = this.transactionManager.begin();
		T result;
		try {
			result = action.doInTransaction(status);
		} catch (RuntimeException ex) {
			// Transactional code threw application exception -> rollback
			rollbackOnException(status, ex);
			throw ex;
		} catch (Error err) {
			// Transactional code threw error -> rollback
			rollbackOnException(status, err);
			throw err;
		} catch (Exception ex) {
			// Transactional code threw unexpected exception -> rollback
			rollbackOnException(status, ex);
			throw new UndeclaredThrowableException(ex, "TransactionCallback threw undeclared checked exception");
		}
		this.transactionManager.commit(status);
		return result;
	}

	private void rollbackOnException(TransactionStatus status, Throwable ex) throws TransactionException {
		try {
			this.transactionManager.rollback(status);
		} catch (RuntimeException ex2) {
			logger.error("Application exception overridden by rollback exception", ex);
			throw ex2;
		} catch (Error err) {
			logger.error("Application exception overridden by rollback error", ex);
			throw err;
		}
	}

}
