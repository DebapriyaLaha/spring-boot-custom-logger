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

import java.io.FileNotFoundException;
import java.io.IOException;

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
public class LoggableTest {

    @Rule
    public OutputCapture capture = new OutputCapture();

    @Autowired
    private SomeService someService;

    @Autowired
    private SomeClassService someClassService;

    @BeforeClass
    public static void setErrorLogging() {
        LoggingSystem.get(ClassLoader.getSystemClassLoader()).setLogLevel(Logger.ROOT_LOGGER_NAME, LogLevel.DEBUG);
    }

    @Test
    public void defaultTest() {
        someService.withDefault();
        assertThat(capture.toString(), containsString(
                "INFO org.internalservices.commons.logger.LoggableTest$SomeService - "
                        + "#withDefault([]): NULL in"));
    }

    @Test
    public void defaultSkipArgsAndResultTest() {
        someService.withSkipArgsAndResultDefault(0);
        assertThat(capture.toString(), containsString(
                "INFO org.internalservices.commons.logger.LoggableTest$SomeService - "
                        + "#withSkipArgsAndResultDefault(..): .. in"));
    }

    @Test
    public void paramsTest() {
        someService.withParams("str", 10);
        assertThat(capture.toString(), containsString(
                "INFO org.internalservices.commons.logger.LoggableTest$SomeService - "
                        + "#withParams(['str', 10]): NULL in"));
    }

    @Test
    public void paramsArrayBoolTest() {
        someService.withParamsArrayBool(new boolean[]{true, false, true});
        assertThat(capture.toString(), containsString(
                "INFO org.internalservices.commons.logger.LoggableTest$SomeService - "
                        + "#withParamsArrayBool([[true, false, true]]): NULL in"));
    }

    @Test
    public void paramsArrayFloatTest() {
        someService.withParamsArrayFloat(new float[]{1.2f, 3.4f, 5.6f});
        assertThat(capture.toString(), containsString(
                "INFO org.internalservices.commons.logger.LoggableTest$SomeService - "
                        + "#withParamsArrayFloat([[1.2, 3.4, 5.6]]): NULL in"));
    }

    @Test
    public void paramsArrayTest() {
        someService.withParamsArray(new int[]{1, 2, 3});
        assertThat(capture.toString(), containsString(
                "INFO org.internalservices.commons.logger.LoggableTest$SomeService - "
                        + "#withParamsArray([[1, 2, 3]]): NULL in"));
    }

    @Test
    public void paramsReturnTest() {
        someService.withParamsReturn("str", 10);
        assertThat(capture.toString(), containsString(
                "INFO org.internalservices.commons.logger.LoggableTest$SomeService - "
                        + "#withParamsReturn(['str', 10]): 10 in"));
    }

    @Test
    public void notTraceTest() {
        someService.withTrace();
        assertThat(capture.toString(), not(containsString(
                "TRACE org.internalservices.commons.logger.LoggableTest$SomeService - "
                        + "#withTrace([]): NULL in")));
    }

    @Test
    public void debugTest() {
        someService.withDebug();
        assertThat(capture.toString(), containsString(
                "DEBUG org.internalservices.commons.logger.LoggableTest$SomeService - "
                        + "#withDebug([]): NULL in"));
    }

    @Test
    public void errorTest() {
        someService.withError();
        assertThat(capture.toString(), containsString(
                "ERROR org.internalservices.commons.logger.LoggableTest$SomeService - "
                        + "#withError([]): NULL in"));
    }

    @Test
    public void offTest() {
        someService.withOff();
        assertThat(capture.toString(), not(containsString(
                "org.internalservices.commons.logger.LoggableTest$SomeService - "
                        + "#withOff([]): NULL in")));
    }

    @Test
    public void fatalTest() {
        someService.withFatal();
        assertThat(capture.toString(), containsString(
                "ERROR org.internalservices.commons.logger.LoggableTest$SomeService - "
                        + "#withFatal([]): NULL in"));
    }

    @Test
    public void enterTest() {
        someService.withEnter();
        assertThat(capture.toString(), containsString(
                "INFO org.internalservices.commons.logger.LoggableTest$SomeService - "
                        + "#withEnter([]): entered"));
    }

    @Test
    public void defaultClassTest() {
        someClassService.withClassDefault();
        assertThat(capture.toString(), containsString(
                "INFO org.internalservices.commons.logger.LoggableTest$SomeClassService - "
                        + "#withClassDefault([]): NULL in "));
    }

    @Loggable
    public static class SomeClassService {
        public void withClassDefault() {

        }
    }

    public static class SomeService {


        @Loggable
        public void withNameThrow() throws Exception {
            throw new Exception("withNameThrow");
        }

        @Loggable
        public void withDefault() {

        }

        @Loggable(skipArgs = true, skipResult = true)
        public int withSkipArgsAndResultDefault(int num) {
            return 2;
        }

        @Loggable
        public void withParamsArrayBool(boolean[] bools) {

        }

        @Loggable
        public void withParamsArrayFloat(float[] floats) {

        }

        @Loggable
        public void withParams(String str, int num) {

        }

        @Loggable
        public void withParamsArray(int[] nums) {

        }

        @Loggable
        public int withParamsReturn(String str, int num) {
            return num;
        }

        @Loggable(LogLevel.FATAL)
        public void withFatal() {

        }

        @Loggable(LogLevel.DEBUG)
        public void withDebug() {

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

        @Loggable(entered = true)
        public void withEnter() {

        }

        @Loggable
        public void withThrow() throws Exception {
            throw new Exception("withThrow");
        }

        @Loggable(ignore = IOException.class)
        public void withThrowChildIgnore() throws Exception {
            throw new FileNotFoundException("withThrowChildIgnore");
        }

        @Loggable(ignore = Exception.class)
        public void withThrowIgnore() throws Exception {
            throw new Exception("withThrowIgnore");
        }

        @Loggable(ignore = RuntimeException.class)
        public void withThrowChildNotFoundIgnore() throws Exception {
            throw new FileNotFoundException("withThrowChildNotFoundIgnore");
        }

    }

    @Configuration
    @EnableAspectJAutoProxy
    @EnableLogger
    public static class Application {
        @Bean
        public SomeService someService() {
            return new SomeService();
        }

        @Bean
        public SomeClassService someClassService() {
            return new SomeClassService();
        }
    }

}
