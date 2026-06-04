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

    @ConfigItem(
        keyName = "highlightColor",
        name = "Highlight color",
        description = "Color used for the nearest-NPC hull.",
        color = true
    )
    default int highlightColor() {
        return 0xFF00FF00;
    }

    @ConfigItem(
        keyName = "maxNpcDistance",
        name = "Max NPC distance",
        description = "Highlight NPCs within this radius.",
        min = 1,
        max = 32
    )
    default int maxNpcDistance() {
        return 10;
    }

    @ConfigItem(
        keyName = "watchedItem",
        name = "Watched item",
        description = "Item whose cache definition is logged each tick."
    )
    default WatchedItem watchedItem() {
        return WatchedItem.COINS;
    }

    enum WatchedItem {
        COINS(995),
        SHARK(385),
        RUNE_SCIMITAR(1333);

        private final int itemId;

        WatchedItem(int itemId) {
            this.itemId = itemId;
        }

        public int itemId() {
            return itemId;
        }
    }
}
