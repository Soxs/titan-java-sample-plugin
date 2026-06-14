package net.titan.sample;

import com.google.inject.Inject;
import net.titan.api.Client;
import net.titan.api.ItemDefinition;
import net.titan.api.Logger;
import net.titan.api.NPC;
import net.titan.api.eventbus.Subscribe;
import net.titan.api.events.ConfigChanged;
import net.titan.api.events.GameTick;
import net.titan.api.overlay.Overlay;
import net.titan.api.overlay.OverlayLayer;
import net.titan.api.overlay.OverlayPanel;
import net.titan.api.overlay.OverlayPanelAnchor;
import net.titan.api.panel.Panel;
import net.titan.api.panel.PanelValue;
import net.titan.api.plugins.Plugin;
import net.titan.api.plugins.PluginDescriptor;
import net.titan.api.plugins.SidePanel;
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
@SidePanel(id = "main", title = "Java Sample", icon = "awesome:cube", iconColor = 0xFF68CC92)
@SidePanel(id = "about", title = "About", icon = "lucide:info", iconColor = 0xFFBF98E3)
public final class SamplePlugin implements Plugin {
    // Panel action ids (unique within each panel).
    private static final int ACTION_LOG_STATE = 1;
    private static final int ACTION_VERBOSE = 2;

    private int panelClicks;
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

        // Exercise a cache-definition bridge call using the combo setting.
        int watchedId = config.watchedItem().itemId();
        String watchedName = client.itemDefinition(watchedId)
            .map(ItemDefinition::name)
            .orElse("<unknown>");
        logger.info("Watched item " + watchedId + " = " + watchedName);
    }

    @Override
    public void renderOverlay(OverlayLayer layer) {
        if (layer == OverlayLayer.ABOVE_SCENE) {
            int hull = config.highlightColor();
            int fill = (hull & 0x00FFFFFF) | 0x33000000;
            int radius = config.maxNpcDistance();
            Optional<NPC> nearest = client.localPlayer()
                .flatMap(self -> Queries.npcs().within(radius, self).nearestTo(self));
            nearest.ifPresent(npc -> Overlay.draw().entityHull(npc, hull, fill));
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

    @Override
    public void buildPanel(String panelId, Panel panel) {
        if ("about".equals(panelId)) {
            panel.separatorText("Java Sample")
                .wrapped("Demonstrates the Titan Java side-panel API with full "
                    + "parity to native and TypeScript plugins.")
                .spacing()
                .label("Author", "Titan")
                .label("Version", "0.1.0");
            return;
        }

        String player = client.localPlayer()
            .map(value -> value.name())
            .orElse("<not logged in>");
        panel.label("Player", player)
            .label("Tick", Integer.toString(client.tick()))
            .label("NPCs", Integer.toString(Queries.npcs().count()))
            .separator()
            .checkbox("Verbose logging", ACTION_VERBOSE, config.verbose())
            .button("Log state now", ACTION_LOG_STATE)
            .spacing()
            .label("Panel clicks", Integer.toString(panelClicks));
    }

    @Override
    public void onPanelAction(String panelId, int actionId, PanelValue value) {
        if (!"main".equals(panelId)) return;
        switch (actionId) {
            case ACTION_LOG_STATE:
                panelClicks++;
                logger.info("Panel: tick=" + client.tick()
                    + " npcs=" + Queries.npcs().count());
                break;
            case ACTION_VERBOSE:
                logger.info("Panel toggled verbose=" + value.asBoolean());
                break;
            default:
                break;
        }
    }
}
