package com.github.coreyshupe.buildproducer;

import com.github.coreyshupe.buildproducer.step.BuildStepBlockBreak;
import com.github.coreyshupe.buildproducer.step.BuildStepBlockPlace;
import com.github.coreyshupe.buildproducer.util.LocationWrapper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerListener implements Listener {
    private final BuildProducerPlugin producerPlugin;

    public PlayerListener(BuildProducerPlugin producerPlugin) {
        this.producerPlugin = producerPlugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        producerPlugin.getProgressScript(event.getPlayer()).ifPresent(script -> script.addBuildStep(
                new BuildStepBlockPlace(event.getBlockPlaced())
        ));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        producerPlugin.getProgressScript(event.getPlayer()).ifPresent(script -> script.addBuildStep(
                new BuildStepBlockBreak(new LocationWrapper(event.getBlock().getLocation()))
        ));
    }
}
