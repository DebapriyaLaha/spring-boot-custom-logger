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

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the logger.
 *
 * @see EnableLogger
 *
 * @author Debapriya Laha
 *
 */
@Configuration
@ComponentScan
public class LoggerConfiguration {

	@Bean
	@ConditionalOnMissingBean(LoggerFormats.class)
	public LoggerFormats loggerFormats() {
		LoggerFormats format = new LoggerFormats();
		format.setAfter("#${method.name}(${method.args}): " + "${method.result} in ${method.duration}");
		format.setEnter("#${method.name}(${method.args}): " + "entered");
		format.setError("#${method.name}(${method.args}): " + "thrown ${error.class.name}(${error.message}) "
				+ "from ${error.source.class.name}[${error.source.line}] in ${method.duration}");
		return format;
	}
}
