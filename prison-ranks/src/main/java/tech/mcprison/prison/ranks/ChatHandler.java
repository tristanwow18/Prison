/*
 * Prison is a Minecraft plugin for the prison game mode.
 * Copyright (C) 2018 MC-Prison Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package tech.mcprison.prison.ranks;

import com.google.common.eventbus.Subscribe;
import tech.mcprison.prison.Prison;
import tech.mcprison.prison.PrisonAPI;
import tech.mcprison.prison.internal.events.player.PlayerChatEvent;
import tech.mcprison.prison.ranks.data.Rank;
import tech.mcprison.prison.ranks.data.RankLadder;
import tech.mcprison.prison.ranks.data.RankPlayer;
import tech.mcprison.prison.util.Text;

import java.util.Map;
import java.util.Optional;

/**
 * Handles replacing chat messages for all players.
 *
 * @author Faizaan A. Datoo
 */
public class ChatHandler {

    /*
     * Constructor
     */

    public ChatHandler() {
        Prison.get().getEventBus().register(this);
    }

    /*
     * Listeners
     */

    @Subscribe public void onPlayerChat(PlayerChatEvent e) {
        Optional<RankPlayer> player =
            PrisonRanks.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUUID());
        String prefix = "";

        if(player.isPresent()) {
            StringBuilder builder = new StringBuilder();
            for(Map.Entry<RankLadder, Rank> entry: player.get().getRanks().entrySet()) {
                builder.append(entry.getValue().tag);
            }
            prefix = builder.toString();
        }


        String newFormat = e.getFormat().replace("{PRISON_RANK}", Text.translateAmpColorCodes(prefix));
        e.setFormat(newFormat);
    }

}
