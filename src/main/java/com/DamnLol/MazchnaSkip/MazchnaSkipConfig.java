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
package com.DamnLol.MazchnaSkip;

import com.DamnLol.MazchnaSkip.Models.SweatMode;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.Color;

@ConfigGroup(MazchnaSkipConfig.CONFIG_GROUP_NAME)
public interface MazchnaSkipConfig extends Config {
    String CONFIG_GROUP_NAME = "Mazchna Skipping";

    // General settings
    @ConfigSection(
            position = 0,
            name = "General Settings",
            description = "General Settings"
    )
    String generalSettings = "generalSettings";

    @ConfigItem(
            position = 0,
            keyName = "displayInfo",
            name = "Enable information box",
            description = "Show Information box.",
            section = generalSettings
    )
    default boolean enableInformationBox() {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "automaticallyHideInformationBox",
            name = "Auto hide information box",
            description = "Temporarily hide the info box when at your current task.",
            section = generalSettings
    )
    default boolean automaticallyHideInformationBox() {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "displayMapIcon",
            name = "Enable world map icon",
            description = "Displays on the world map where your current task is located.",
            section = generalSettings
    )
    default boolean enableWorldMapIcon() {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "useShortestPath",
            name = "Use 'Shortest Path' plugin",
            description = "Uses Shortest path plugin to current task.<br/>" +
                    "The 'Shortest Path' plugin is required to work.",
            section = generalSettings
    )
    default boolean useShortestPath() {
        return false;
    }

    @ConfigItem(
            position = 4,
            keyName = "useSkipReminder",
            name = "Hide Mazchna at Streak",
            description = "Blocks Mazchna in the NPC Contact spell at x Tasks.",
            section = generalSettings
    )
    default SlayerTaskStreak getUseSkipReminder() {
        return SlayerTaskStreak.Off;
    }

    @ConfigItem(
            position = 5,
            keyName = "enableNavigationHints",
            name = "Enable Teleport Highlights",
            description = "Highlights the teleport item and relevant menu option.",
            section = generalSettings
    )
    default boolean enableNavigationHints() {
        return true;
    }


    // Highlight settings
    @ConfigSection(
            position = 1,
            name = "NPC Highlight settings",
            description = "NPC highlight settings"
    )
    String npcHighlightSettings = "npcHighlightSettings";

    @ConfigItem(
            position = 0,
            keyName = "enableNpcHighlight",
            name = "Enable NPC highlight",
            description = "Highlight the NPC's from your current slayer task.",
            section = npcHighlightSettings
    )
    default boolean enableNpcHighlight() {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "npcHighlightMode",
            name = "NPC highlight mode",
            description = "NPC highlight options.",
            section = npcHighlightSettings
    )
    default NpcHighlightMode getNpcHighlightMode() {
        return NpcHighlightMode.Tile;
    }

    @ConfigItem(
            position = 2,
            keyName = "npcColour",
            name = "NPC highlight color",
            description = "Select the color of the highlighted NPC's.",
            section = npcHighlightSettings
    )
    default Color getNpcColour() {
        return Color.decode("#2EDACA");
    }

    // NPC Area outline
    @ConfigSection(
            position = 2,
            name = "NPC Area",
            description = "NPC area outline"
    )
    String slayerAreaOutline = "slayerAreaOutline";

    @ConfigItem(
            position = 0,
            keyName = "enableSlayerAreaOutline",
            name = "Enable slayer area outline",
            description = "Draws an outline around the slayer locations.",
            section = slayerAreaOutline
    )
    default boolean enableSlayerAreaOutline() {
        return false;
    }

    @ConfigItem(
            position = 1,
            keyName = "slayerAreaOutlineColour",
            name = "Slayer area outline colour",
            description = "Select the colour for the slayer area outline",
            section = slayerAreaOutline
    )
    default Color getSlayerAreaOutlineColour() {
        return Color.decode("#2EDACA");
    }

    // Misc settings
    @ConfigSection(
            position = 3,
            name = "Misc",
            description = "Miscellaneous settings"
    )
    String miscSettings = "miscSettings";

    @ConfigItem(
            position = 0,
            keyName = "teleColor",
            name = "Teleport Highlights",
            description = "Color used to highlight the teleport item and menu option.",
            section = miscSettings
    )
    default Color getTeleColor() {
        return Color.decode("#F44BD8");
    }

    @ConfigSection(
            position = 4,
            name = "Sweat Mode (Work In Progress)",
            closedByDefault = true,
            description = "Max Efficiency for the Sweats"
    )
    String sweatModeSettings = "sweatModeSettings";

    //SweatMode = *Testing cannon placements for max k/ph and optimal tiles.*
    @ConfigItem(
            position = 0,
            keyName = "sweatMode",
            name = "PoH/Desert between tasks",
            description = "Highlights proper teleport after task to restore pray/run/hp.",
            section = sweatModeSettings
    )
    default SweatMode getSweatMode() {
        return SweatMode.Off;
    }

    // Debug settings
    @ConfigSection(
            position = 5,
            name = "Debugging",
            closedByDefault = true,
            description = "Debug settings"
    )
    String debugSettings = "debugSettings";

    @ConfigItem(
            position = 0,
            keyName = "debugTask",
            name = "Select a task",
            description = "Select your current task.",
            section = debugSettings
    )
    default DebugSlayerTask getDebugTask() {
        return DebugSlayerTask.None;
    }

    @ConfigItem(
            position = 1,
            keyName = "enableWorldPointSelector",
            name = "Enable WorldPoint selector",
            description = "Enables WorldPoint selector in the right click menu.",
            section = debugSettings
    )
    default boolean enableWorldPointSelector() {
        return false;
    }
}