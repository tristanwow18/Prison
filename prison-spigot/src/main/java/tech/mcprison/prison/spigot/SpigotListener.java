/*
 *  Prison is a Minecraft plugin for the prison game mode.
 *  Copyright (C) 2017 The Prison Team
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package tech.mcprison.prison.spigot;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import tech.mcprison.prison.Prison;
import tech.mcprison.prison.internal.events.Cancelable;
import tech.mcprison.prison.internal.events.player.PlayerChatEvent;
import tech.mcprison.prison.internal.events.player.PlayerPickUpItemEvent;
import tech.mcprison.prison.spigot.compat.Compatibility;
import tech.mcprison.prison.spigot.game.SpigotPlayer;
import tech.mcprison.prison.spigot.game.SpigotWorld;
import tech.mcprison.prison.util.BlockType;
import tech.mcprison.prison.util.ChatColor;
import tech.mcprison.prison.util.Location;

/**
 * Posts Prison's internal events.
 *
 * @author Faizaan A. Datoo
 */
public class SpigotListener implements Listener {

    private SpigotPrison spigotPrison;

    public SpigotListener(SpigotPrison spigotPrison) {
        this.spigotPrison = spigotPrison;
    }

    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this.spigotPrison);
    }

    @EventHandler public void onPlayerJoin(PlayerJoinEvent e) {
        Prison.get().getEventBus().post(
            new tech.mcprison.prison.internal.events.player.PlayerJoinEvent(
                new SpigotPlayer(e.getPlayer())));
    }

    @EventHandler public void onPlayerQuit(PlayerQuitEvent e) {
        Prison.get().getEventBus().post(
            new tech.mcprison.prison.internal.events.player.PlayerQuitEvent(
                new SpigotPlayer(e.getPlayer())));
    }

    @EventHandler public void onPlayerKicked(PlayerKickEvent e) {
        Prison.get().getEventBus().post(
            new tech.mcprison.prison.internal.events.player.PlayerKickEvent(
                new SpigotPlayer(e.getPlayer()), e.getReason()));
    }

    @EventHandler public void onBlockPlace(BlockPlaceEvent e) {
        org.bukkit.Location block = e.getBlockPlaced().getLocation();
        tech.mcprison.prison.internal.events.block.BlockPlaceEvent event =
            new tech.mcprison.prison.internal.events.block.BlockPlaceEvent(
                BlockType.getBlock(e.getBlock().getTypeId()),
                new Location(new SpigotWorld(block.getWorld()), block.getX(), block.getY(),
                    block.getZ()), (new SpigotPlayer(e.getPlayer())));
        Prison.get().getEventBus().post(event);
        doCancelIfShould(event, e);
    }

    @EventHandler public void onBlockBreak(BlockBreakEvent e) {
        org.bukkit.Location block = e.getBlock().getLocation();
        tech.mcprison.prison.internal.events.block.BlockBreakEvent event =
            new tech.mcprison.prison.internal.events.block.BlockBreakEvent(
                BlockType.getBlock(e.getBlock().getTypeId()),
                new Location(new SpigotWorld(block.getWorld()), block.getX(), block.getY(),
                    block.getZ()), (new SpigotPlayer(e.getPlayer())),e.getExpToDrop());
        Prison.get().getEventBus().post(event);
        doCancelIfShould(event, e);
    }

    @EventHandler public void onPlayerInteract(PlayerInteractEvent e) {
        // TODO Accept air events (block is null when air is clicked...)
//        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_AIR) {
//            return;
//        }

        // Since the Prison-core and the Spigot Action enums have different members,
        // we want to make sure we only get the ones that our Prison enum has.
        try {
            tech.mcprison.prison.internal.events.player.PlayerInteractEvent.Action.valueOf(e.getAction().name());
            // throws an IllegalArgumentException if the value isn't found in valueOf.
        } catch(IllegalArgumentException ignored) {
            return; // not in Prison so we don't care about it
        }

        // This one's a workaround for the double-interact event glitch.
        // The wand can only be used in the main hand
        if (spigotPrison.compatibility.getHand(e) != Compatibility.EquipmentSlot.HAND) {
            return;
        }

        org.bukkit.Location block = e.getClickedBlock().getLocation();
        tech.mcprison.prison.internal.events.player.PlayerInteractEvent event =
            new tech.mcprison.prison.internal.events.player.PlayerInteractEvent(
                new SpigotPlayer(e.getPlayer()),
                SpigotUtil.bukkitItemStackToPrison(spigotPrison.compatibility.getItemInMainHand(e)),
                tech.mcprison.prison.internal.events.player.PlayerInteractEvent.Action
                    .valueOf(e.getAction().name()),
                new Location(new SpigotWorld(block.getWorld()), block.getX(), block.getY(),
                    block.getZ()));
        Prison.get().getEventBus().post(event);
        doCancelIfShould(event, e);
    }

    @EventHandler public void onPlayerDropItem(PlayerDropItemEvent e) {
        tech.mcprison.prison.internal.events.player.PlayerDropItemEvent event =
            new tech.mcprison.prison.internal.events.player.PlayerDropItemEvent(
                new SpigotPlayer(e.getPlayer()),
                SpigotUtil.bukkitItemStackToPrison(e.getItemDrop().getItemStack()));
        Prison.get().getEventBus().post(event);
        doCancelIfShould(event, e);
    }

    @EventHandler public void onPlayerPickUpItem(PlayerPickupItemEvent e) {
        PlayerPickUpItemEvent event = new PlayerPickUpItemEvent(new SpigotPlayer(e.getPlayer()),
            SpigotUtil.bukkitItemStackToPrison(e.getItem().getItemStack()));
        Prison.get().getEventBus().post(event);
        doCancelIfShould(event, e);
    }

    @EventHandler public void onPlayerChat(AsyncPlayerChatEvent e) {
        PlayerChatEvent event =
            new PlayerChatEvent(new SpigotPlayer(e.getPlayer()), e.getMessage(), e.getFormat());
        Prison.get().getEventBus().post(event);
        e.setFormat(ChatColor.translateAlternateColorCodes('&', event.getFormat() + "&r"));
        e.setMessage(event.getMessage());
        doCancelIfShould(event, e);
    }

    private void doCancelIfShould(Cancelable ours, Cancellable theirs) {
        if(ours.isCanceled()) {
            // We shouldn't set this to false, because some event handlers check for that.
            theirs.setCancelled(true);
        }
    }

}
