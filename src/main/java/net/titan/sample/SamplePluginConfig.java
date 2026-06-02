package net.titan.sample;

import net.titan.api.config.Config;
import net.titan.api.config.ConfigGroup;
import net.titan.api.config.ConfigItem;

@ConfigGroup("java_sample")
public interface SamplePluginConfig extends Config {
    @ConfigItem(
        keyName = "verbose",
        name = "Verbose logging",
        description = "Log a short message every game tick."
    )
    default boolean verbose() {
        return true;
    }
}
