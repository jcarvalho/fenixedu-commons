/**
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 *
 * This file is part of fenixedu-commons.
 *
 * fenixedu-commons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * fenixedu-commons is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with fenixedu-commons. If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.commons.configuration;

import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ConfigurationInvocationHandlerTest {

    @BeforeClass
    public static void configureLogging() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }

    @ConfigurationManager
    public static interface TestConfiguration {

        @ConfigurationProperty(key = "test.primitive.boolean", defaultValue = "true")
        public boolean primitiveBoolean();

        @ConfigurationProperty(key = "test.primitive.byte", defaultValue = "3")
        public byte primitiveByte();

        @ConfigurationProperty(key = "test.primitive.short", defaultValue = "5")
        public short primitiveShort();

        @ConfigurationProperty(key = "test.primitive.int", defaultValue = "-1")
        public int primitiveInt();

        @ConfigurationProperty(key = "test.primitive.long", defaultValue = "9")
        public long primitiveLong();

        @ConfigurationProperty(key = "test.primitive.float", defaultValue = "1.1")
        public float primitiveFloat();

        @ConfigurationProperty(key = "test.primitive.double", defaultValue = "7.5")
        public double primitiveDouble();

    }

    @Before
    public void reloadProperties() {
        System.setProperty(PropertiesFileSource.FILE_LOCATION_PROPERTY, "/some-non-existing-file-here");
        ConfigurationProperties.reload();
    }

    @After
    public void cleanupSystemProperties() {
        for (Method method : TestConfiguration.class.getDeclaredMethods()) {
            ConfigurationProperty prop = method.getAnnotation(ConfigurationProperty.class);
            if (prop != null) {
                System.clearProperty(prop.key());
            }
        }
    }

    @Test
    public void testPrimitiveTypes() {
        TestConfiguration config = ConfigurationInvocationHandler.getConfiguration(TestConfiguration.class);

        Assert.assertEquals(true, config.primitiveBoolean());
        Assert.assertEquals(3, config.primitiveByte());
        Assert.assertEquals(5, config.primitiveShort());
        Assert.assertEquals(-1, config.primitiveInt());
        Assert.assertEquals(9, config.primitiveLong());
        Assert.assertEquals(1.1, config.primitiveFloat(), 0.1f);
        Assert.assertEquals(7.5d, config.primitiveDouble(), 0.1d);
    }

    @Test
    public void testSystemProperties() {
        System.setProperty("test.primitive.boolean", "false");
        System.setProperty("test.primitive.byte", "2");
        System.setProperty("test.primitive.short", "3");
        System.setProperty("test.primitive.int", "-2");
        System.setProperty("test.primitive.long", "0");
        System.setProperty("test.primitive.float", "4.6");
        System.setProperty("test.primitive.double", "4.2");
        TestConfiguration config = ConfigurationInvocationHandler.getConfiguration(TestConfiguration.class);

        Assert.assertEquals(false, config.primitiveBoolean());
        Assert.assertEquals(2, config.primitiveByte());
        Assert.assertEquals(3, config.primitiveShort());
        Assert.assertEquals(-2, config.primitiveInt());
        Assert.assertEquals(0, config.primitiveLong());
        Assert.assertEquals(4.6, config.primitiveFloat(), 0.1f);
        Assert.assertEquals(4.2d, config.primitiveDouble(), 0.1d);
    }

    @Test
    public void testConfigurationFileDefaultLocation() {
        System.clearProperty(PropertiesFileSource.FILE_LOCATION_PROPERTY);
        ConfigurationProperties.reload();

        TestConfiguration config = ConfigurationInvocationHandler.getConfiguration(TestConfiguration.class);

        Assert.assertEquals(false, config.primitiveBoolean());
        Assert.assertEquals(5, config.primitiveByte());
        Assert.assertEquals(19, config.primitiveShort());
        Assert.assertEquals(95, config.primitiveInt());
        Assert.assertEquals(454546, config.primitiveLong());
        Assert.assertEquals(0.45, config.primitiveFloat(), 0.1f);
        Assert.assertEquals(5.65d, config.primitiveDouble(), 0.1d);
    }

    @Test
    public void testConfigurationFileCustomLocation() {
        System.setProperty(PropertiesFileSource.FILE_LOCATION_PROPERTY, "/configuration_with_other_name.properties");
        ConfigurationProperties.reload();

        TestConfiguration config = ConfigurationInvocationHandler.getConfiguration(TestConfiguration.class);

        Assert.assertEquals(false, config.primitiveBoolean());
        Assert.assertEquals(7, config.primitiveByte());
        Assert.assertEquals(10, config.primitiveShort());
        Assert.assertEquals(15, config.primitiveInt());
        Assert.assertEquals(444, config.primitiveLong());
        Assert.assertEquals(1.23, config.primitiveFloat(), 0.1f);
        Assert.assertEquals(9.42d, config.primitiveDouble(), 0.1d);
    }
}
