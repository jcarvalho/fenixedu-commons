package org.fenixedu.commons.configuration;

import java.util.Optional;
import java.util.stream.Stream;

public interface ConfigurationPropertySource {

    public Optional<String> getProperty(String key);

    public Stream<String> availableProperties();

    public void reload();

}
