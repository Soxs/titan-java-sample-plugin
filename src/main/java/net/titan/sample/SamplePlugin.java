package net.titan.sample;

import com.google.inject.Inject;
import net.titan.api.Client;
import net.titan.api.Logger;
import net.titan.api.eventbus.Subscribe;
import net.titan.api.events.ConfigChanged;
import net.titan.api.events.GameTick;
import net.titan.api.plugins.Plugin;
import net.titan.api.plugins.PluginDescriptor;

@PluginDescriptor(
    id = "java_sample",
    name = "Java Sample",
    description = "Example Java plugin",
    author = "Titan",
    version = "0.1.0",
    config = SamplePluginConfig.class
)
public final class SamplePlugin implements Plugin {
    @Inject
    private Client client;

    @Inject
    private Logger logger;

    @Inject
    private SamplePluginConfig config;

    @Override
    public void onEnable() {
        logger.info("Java sample enabled");
    }

    @Override
    public void onDisable() {
        logger.info("Java sample disabled");
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        logger.info("Setting " + event.key() + "=" + config.verbose());
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!config.verbose()) return;
        String player = client.localPlayer()
            .map(value -> value.name())
            .orElse("<not logged in>");
        logger.info("Tick " + client.tick() + " localPlayer=" + player);
    }
}
