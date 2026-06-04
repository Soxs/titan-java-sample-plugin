package net.titan.sample;

import com.google.inject.Inject;
import net.titan.api.Client;
import net.titan.api.Logger;
import net.titan.api.NPC;
import net.titan.api.eventbus.Subscribe;
import net.titan.api.events.ConfigChanged;
import net.titan.api.events.GameTick;
import net.titan.api.overlay.Overlay;
import net.titan.api.overlay.OverlayLayer;
import net.titan.api.overlay.OverlayPanel;
import net.titan.api.overlay.OverlayPanelAnchor;
import net.titan.api.plugins.Plugin;
import net.titan.api.plugins.PluginDescriptor;
import net.titan.api.queries.Queries;
import net.titan.api.utils.Inventory;

import java.util.Optional;

@PluginDescriptor(
    id = "java_sample",
    name = "Java Sample",
    description = "Example Java plugin",
    author = "Titan",
    version = "0.1.0",
    config = SamplePluginConfig.class
)
public final class SamplePlugin implements Plugin {
    private final OverlayPanel statusPanel = new OverlayPanel(
        "status", OverlayPanelAnchor.TOP_CENTER, 50)
        .setPreferredWidth(240);
    private final OverlayPanel inventoryPanel = new OverlayPanel(
        "inventory", OverlayPanelAnchor.ABOVE_CHATBOX_RIGHT, 60)
        .setPreferredWidth(180)
        .setCornerRadius(8.0f);

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
        int npcCount = Queries.npcs().count();
        logger.info("Tick " + client.tick() + " localPlayer=" + player + " npcs=" + npcCount);
    }

    @Override
    public void renderOverlay(OverlayLayer layer) {
        if (layer == OverlayLayer.ABOVE_SCENE) {
            Optional<NPC> nearest = Queries.npcs().nearest();
            nearest.ifPresent(npc -> Overlay.draw().entityHull(npc, 0xFF00FF00, 0x3300FF00));
            return;
        }

        if (layer != OverlayLayer.ABOVE_WIDGETS) return;

        statusPanel.render(() -> {
            statusPanel.title("Java Sample");
            statusPanel.line("Tick", Integer.toString(client.tick()));
            statusPanel.line("NPCs", Integer.toString(Queries.npcs().count()));
        });

        inventoryPanel.render(() -> {
            inventoryPanel.title("Inventory");
            inventoryPanel.line("Open", Boolean.toString(Inventory.isOpen()));
            inventoryPanel.line("Slots", Inventory.size() + "/" + Inventory.CAPACITY);
            inventoryPanel.progressBar(Inventory.size(), 0, Inventory.CAPACITY);
        });
    }
}
