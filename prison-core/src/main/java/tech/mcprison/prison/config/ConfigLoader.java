package tech.mcprison.prison.config;

import java.lang.annotation.*;

/**
 * Annotates the method which is used by {@link Configuration} to
 * create an instance of an object in the configuration file. This method
 * must have one parameter: a String. When the method is called, the Configuration
 * will pass in whatever String is in the config file and this method will turn it into
 * an object. Magic.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ConfigLoader {
}
