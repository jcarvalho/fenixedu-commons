package org.fenixedu.commons.configuration;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationProperties {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationProperties.class);

    private static final Collection<ConfigurationPropertySource> sources = new ArrayList<>();

    static {
        ServiceLoader.load(ConfigurationPropertySource.class).forEach(sources::add);
        logger.debug("Loaded {} property sources: {}", sources.size(), sources);
    }

    static Optional<String> getProperty(String key) {
        for (ConfigurationPropertySource source : sources) {
            Optional<String> value = source.getProperty(key);
            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }

    public static Stream<String> availableProperties() {
        return sources.stream().flatMap(ConfigurationPropertySource::availableProperties);
    }

    public static void reload() {
        sources.stream().forEach(ConfigurationPropertySource::reload);
        ConfigurationInvocationHandler.all().map(Proxy::getInvocationHandler).map(ConfigurationInvocationHandler.class::cast)
                .forEach(ConfigurationInvocationHandler::reload);
    }

}
