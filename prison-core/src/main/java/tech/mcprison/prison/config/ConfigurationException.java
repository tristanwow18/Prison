package tech.mcprison.prison.config;

/**
 * An exception that is thrown when an error occurs within the
 * configuration system. This is usually due to a parsing error.
 *
 * @since 3.1
 */
public class ConfigurationException extends Exception {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }

}
