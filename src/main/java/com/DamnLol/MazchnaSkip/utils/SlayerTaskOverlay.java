/*
 * Copyright (c) 2026 - DamnLol-GIT
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.DamnLol.MazchnaSkip.utils;

import com.DamnLol.MazchnaSkip.MazchnaSkipConfig;
import com.DamnLol.MazchnaSkip.MazchnaSkipPlugin;
import com.DamnLol.MazchnaSkip.Models.NpcLocation;
import com.DamnLol.MazchnaSkip.Models.SlayerTask;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldArea;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Color;

public class SlayerTaskOverlay extends OverlayPanel {
    private final Client client;
    private final MazchnaSkipPlugin plugin;
    private final MazchnaSkipConfig config;

    @Inject
    public SlayerTaskOverlay(Client client, MazchnaSkipPlugin MazchnaSkipPlugin, MazchnaSkipConfig config) {
        this.client = client;
        this.plugin = MazchnaSkipPlugin;
        this.config = config;

        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setPreferredSize(new Dimension(216, 40));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.enableInformationBox()) {
            return null;
        }

        SlayerTask task = plugin.getCurrentSlayerTask();

        if (task == null) {
            return null;
        }

        boolean inArea = false;

        for (NpcLocation npcLocation : task.getLocations()) {
            for (WorldArea worldArea : npcLocation.getWorldAreas()) {
                if (worldArea.contains(client.getLocalPlayer().getWorldLocation())) {
                    inArea = true;
                }
            }
        }

        // If disabled, set inArea to false so the information box will always be drawn
        if (!config.automaticallyHideInformationBox()) {
            inArea = false;
        }

        if (!inArea) {
            for (NpcLocation npcLocation : task.getLocations()) {
                panelComponent.getChildren().add(LineComponent.builder().left(npcLocation.getName()).leftColor(Color.YELLOW).build());

                for (String teleport : npcLocation.getTeleports()) {
                    panelComponent.getChildren().add(LineComponent.builder().left("- " + teleport).build());
                }
            }

            if (task.getInformation() != null) {
                panelComponent.getChildren().add(LineComponent.builder().left(" ").build());
                panelComponent.getChildren().add(LineComponent.builder().left(task.getInformation()).build());
            }
        }

        return super.render(graphics);
    }
}