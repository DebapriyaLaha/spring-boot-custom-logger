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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class LoggableErrorLevelTest {

    @Rule
    public OutputCapture capture = new OutputCapture();

    @Autowired
    private SomeService2 someService2;

    @BeforeClass
    public static void setErrorLogging() {
        LoggingSystem.get(ClassLoader.getSystemClassLoader()).setLogLevel(Logger.ROOT_LOGGER_NAME, LogLevel.ERROR);
    }

    @Test
    public void notTraceTest() {
        someService2.withTrace();
        assertThat(capture.toString(), not(containsString(
                "TRACE org.internalservices.commons.logger.LoggableErrorLevelTest$SomeService2 - "
                        + "#withTrace([]): NULL in")));
    }

    @Test
    public void debugTest() {
        someService2.withDebug();
        assertThat(capture.toString(), not(containsString(
                "DEBUG org.internalservices.commons.logger.LoggableErrorLevelTest$SomeService2 - "
                        + "#withDebug([]): NULL in")));
    }

    @Test
    public void infoTest() {
        someService2.withInfo();
        assertThat(capture.toString(), not(containsString(
                "INFO org.internalservices.commons.logger.LoggableErrorLevelTest$SomeService2 - "
                        + "#withDebug([]): NULL in")));
    }

    @Test
    public void errorTest() {
        someService2.withError();
        assertThat(capture.toString(), containsString(
                "ERROR org.internalservices.commons.logger.LoggableErrorLevelTest$SomeService2 - "
                        + "#withError([]): NULL in"));
    }

    @Test
    public void offTest() {
        someService2.withOff();
        assertThat(capture.toString(), not(containsString(
                "org.internalservices.commons.logger.LoggableErrorLevelTest$SomeService2 - "
                        + "#withOff([]): NULL in")));
    }

    @Test
    public void fatalTest() {
        someService2.withFatal();
        assertThat(capture.toString(), containsString(
                "ERROR org.internalservices.commons.logger.LoggableErrorLevelTest$SomeService2 - "
                        + "#withFatal([]): NULL in"));
    }

    public static class SomeService2 {

        @Loggable(LogLevel.FATAL)
        public void withFatal() {

        }

        @Loggable(LogLevel.DEBUG)
        public void withDebug() {

        }

        @Loggable(LogLevel.INFO)
        public void withInfo() {

        }

        @Loggable(LogLevel.TRACE)
        public void withTrace() {

        }

        @Loggable(LogLevel.ERROR)
        public void withError() {

        }

        @Loggable(LogLevel.OFF)
        public void withOff() {

        }

    }

    @Configuration
    @EnableAspectJAutoProxy
    @EnableLogger
    public static class Application {
        @Bean
        public SomeService2 someService2() {
            return new SomeService2();
        }

    }

}