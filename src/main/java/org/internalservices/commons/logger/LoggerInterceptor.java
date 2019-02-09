/**
 * Copyright (C) 2018 Debapriya Laha the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.internalservices.commons.logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

/**
 * AspectJ to intercept {@link Loggable} methods or classes.
 *
 */
@Aspect
@Component
public class LoggerInterceptor {

	private Logger logger;

	private LoggerMsgFormatter formatter;

	@Autowired
	public LoggerInterceptor(Logger logger, LoggerFormats formats) {
		this.formatter = new LoggerMsgFormatter(formats);
		this.logger = logger;
	}

	@Pointcut("execution(public * *(..))" + " && !execution(String *.toString())" + " && !execution(int *.hashCode())"
			+ " && !execution(boolean *.canEqual(Object))" + " && !execution(boolean *.equals(Object))")
	protected void publicMethod() {
	}

	@Pointcut("@annotation(loggable)")
	protected void loggableMethod(Loggable loggable) {
	}

	@Pointcut("@within(loggable)")
	protected void loggableClass(Loggable loggable) {
	}

	@Around(value = "publicMethod() && loggableMethod(loggable)", argNames = "joinPoint,loggable")
	public Object logExecutionMethod(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {
		return logMethod(joinPoint, loggable);
	}

	@Around(value = "publicMethod() && loggableClass(loggable) && !loggableMethod(org.internalservices.commons.logger.Loggable)", argNames = "joinPoint,loggable")
	public Object logExecutionClass(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {
		return logMethod(joinPoint, loggable);
	}

	public Object logMethod(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {
		long start = System.nanoTime();
		Object returnVal = null;

		if (loggable.entered()) {
			log(loggable.value(), formatter.enter(joinPoint, loggable), joinPoint, loggable);
		}

		try {
			returnVal = joinPoint.proceed();
			long nano = System.nanoTime() - start;
			log(loggable.value(), formatter.after(joinPoint, loggable, returnVal, nano), joinPoint, loggable);
			return returnVal;
		} catch (Throwable ex) {
			if (contains(loggable.ignore(), ex)) {
				log(LogLevel.ERROR, formatter.error(joinPoint, loggable, System.nanoTime() - start, ex), joinPoint,
						loggable);
			} else {
				log(formatter.error(joinPoint, loggable, System.nanoTime() - start, ex), joinPoint, loggable, ex);
			}
			throw ex;
		}
	}

	private void log(LogLevel level, String message, ProceedingJoinPoint joinPoint, Loggable loggable) {
		logger.log(level, MethodSignature.class.cast(joinPoint.getSignature()).getMethod().getDeclaringClass(),
				message);
	}

	private void log(String message, ProceedingJoinPoint joinPoint, Loggable loggable, Throwable ex) {
		logger.log(MethodSignature.class.cast(joinPoint.getSignature()).getMethod().getDeclaringClass(), message, ex);
	}

	private boolean contains(Class<? extends Throwable>[] array, Throwable exp) {
		boolean contains = false;
		for (final Class<? extends Throwable> type : array) {
			if (instanceOf(exp.getClass(), type)) {
				contains = true;
				break;
			}
		}
		return contains;
	}

	private boolean instanceOf(Class<?> child, Class<?> parent) {
		boolean instance = child.equals(parent)
				|| child.getSuperclass() != null && instanceOf(child.getSuperclass(), parent);
		if (!instance) {
			for (final Class<?> iface : child.getInterfaces()) {
				instance = instanceOf(iface, parent);
				if (instance) {
					break;
				}
			}
		}
		return instance;
	}

}
