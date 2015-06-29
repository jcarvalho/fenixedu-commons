package org.fenixedu.commons.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesFileSource implements ConfigurationPropertySource {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesFileSource.class);

    public static final String FILE_LOCATION_PROPERTY = "properties.file.location";
    public static final String DEFAULT_FILE_LOCATION = "/configuration.properties";

    private final AtomicReference<Properties> properties;

    public PropertiesFileSource() {
        this.properties = new AtomicReference<>(load());
    }

    @Override
    public Optional<String> getProperty(String key) {
        return Optional.ofNullable(properties.get().getProperty(key));
    }

    @Override
    public Stream<String> availableProperties() {
        return properties.get().keySet().stream().map(String::valueOf);
    }

    @Override
    public void reload() {
        properties.set(load());
    }

    private Properties load() {
        String fileName = System.getProperty(FILE_LOCATION_PROPERTY, DEFAULT_FILE_LOCATION);
        Properties properties = new Properties();
        logger.debug("Loading properties from {}", fileName);
        try (InputStream stream = PropertiesFileSource.class.getResourceAsStream(fileName)) {
            if (stream != null) {
                properties.load(stream);
                logger.debug("Loaded {} properties", properties.size());
            } else {
                logger.debug("File not found, moving on...");
            }
        } catch (IOException e) {
            logger.warn("Could not load properties from '" + fileName + "'.", e);
        }
        return properties;
    }

}
