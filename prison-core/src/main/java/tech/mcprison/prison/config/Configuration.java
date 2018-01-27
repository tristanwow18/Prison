package tech.mcprison.prison.config;

/**
 * Represents a configuration file, where plugin and module configuration is stored.
 *
 * @since 3.1
 */
public interface Configuration {

    <T> T getAs(String key, Class<T> type);

    String getAsString(String key);

    Integer getAsInt(String key);

    Float getAsFloat(String key);

    Double getAsDouble(String key);

    Long getAsLong(String key);

    Byte getAsByte(String key);

    Short getAsShort(String key);

    // TODO Setters, defaults

}
