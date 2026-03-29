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

import javax.inject.Inject;

import com.DamnLol.MazchnaSkip.Models.NpcLocation;
import com.DamnLol.MazchnaSkip.Models.SlayerTask;
import com.DamnLol.MazchnaSkip.utils.AreaOutlineOverlay;
import com.DamnLol.MazchnaSkip.utils.SlayerTaskOverlay;
import com.DamnLol.MazchnaSkip.utils.SlayerTaskWorldMapPoint;
import com.DamnLol.MazchnaSkip.utils.WorldAreaUtils;
import com.google.inject.Provides;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.npcoverlay.HighlightedNpc;
import net.runelite.client.game.npcoverlay.NpcOverlayService;
import net.runelite.client.events.PluginMessage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.Text;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
		name = "Mazchna Skipping",
		configName = "mazchnaskip",
		enabledByDefault = true,
		description = "Mazchna Slayer Boosting",
		tags = {"slayer", "boost", "overlay", "task", "mazchna", "canfis"}
)
public class MazchnaSkipPlugin extends Plugin {
	private static final String MAZCHNA = "Mazchna";

	private static final Pattern SLAYER_ASSIGN_MESSAGE = Pattern.compile("Your new task is to kill \\d+ (?<name>.+)\\.");
	private static final Pattern SLAYER_CURRENT_MESSAGE = Pattern.compile("You're still hunting (?<name>.+)[,;] you have \\d+ to go\\.");
	private static final Pattern SLAYER_CURRENT_CHAT_MESSAGE = Pattern.compile("You're assigned to kill (?<name>.+)[,;] only \\d+ more to go\\.");

	private static final Pattern SLAYER_TASK_STREAK_10 = Pattern.compile("You've completed ([\\d,]*)9 tasks");
	private static final Pattern SLAYER_TASK_STREAK_50 = Pattern.compile("You've completed ([\\d,]*(49|99)) tasks");

	private final Set<NPC> targets = new HashSet<>();

	private final String DEBUG_MENU_WORLD_POINT_ONE = "Set WorldPoint1 (Mazchna Skipping)";
	private final String DEBUG_MENU_WORLD_POINT_TWO = "Set WorldPoint2 (Mazchna Skipping)";
	private final String DEBUG_MENU_RESET_WORLD_POINTS = "Reset WorldPoints (Mazchna Skipping)";
	private final String DEBUG_MENU_COPY_TO_CLIPBOARD = "Copy WorldPoints to clipboard (Mazchna Skipping)";

	private WorldPoint debugWorldPointOne;
	private WorldPoint debugWorldPointTwo;

	private String lastStreakMessage = "";

	@Inject
	private Client client;

	@Inject
	private MazchnaSkipConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private NpcOverlayService npcOverlayService;

	@Inject
	private WorldMapPointManager worldMapPointManager;

	@Inject
	private AreaOutlineOverlay areaOutlineOverlay;

	@Inject
	private AreaOutlineOverlay debugAreaOutlineOverlay;

	@Inject
	private SlayerTaskOverlay slayerTaskOverlay;

	@Inject
	private EventBus eventBus;

	@Getter
	private SlayerTask currentSlayerTask;

	@Override
	protected void startUp() {
		overlayManager.add(slayerTaskOverlay);
		overlayManager.add(debugAreaOutlineOverlay);

		debugAreaOutlineOverlay.setUseAlternativeOutline(true);
	}

	@Override
	protected void shutDown() {
		overlayManager.remove(slayerTaskOverlay);
		overlayManager.remove(debugAreaOutlineOverlay);
		npcOverlayService.unregisterHighlighter(npcHighlighter);
		worldMapPointManager.removeIf(SlayerTaskWorldMapPoint.class::isInstance);

		completeTask(true);
	}

	@Subscribe
	public void onGameTick(GameTick gameTick) {
		Widget chatBoxNpcName = client.getWidget(InterfaceID.ChatLeft.NAME);
		Widget chatBoxNpcText = client.getWidget(InterfaceID.ChatLeft.TEXT);

		// Check if current widget is Mazchna
		if (chatBoxNpcName != null && chatBoxNpcText != null && (chatBoxNpcName.getText().equals(MAZCHNA))) {
			String npcText = Text.sanitizeMultilineText(chatBoxNpcText.getText());
			String taskName = getTaskName(npcText);

			if (taskName != null) {
				startTask(taskName);
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event) {
		String chatMessage = Text.removeTags(event.getMessage());

		boolean matchesTen = SLAYER_TASK_STREAK_10.matcher(chatMessage).find();
		boolean matchesFifty = SLAYER_TASK_STREAK_50.matcher(chatMessage).find();

		if (matchesFifty || matchesTen) {
			lastStreakMessage = chatMessage;
			log.debug("onChatMessage - Updated lastStreakMessage to: '{}'", lastStreakMessage);
		}

		if (event.getType() == ChatMessageType.GAMEMESSAGE) {
			if (!matchesTen && !matchesFifty && chatMessage.startsWith("You've completed")) {
				lastStreakMessage = "";
				lastActiveStreak = SlayerTaskStreak.Off;
				mazchnaHidden = false;
			}
		} else {
			mazchnaHidden = false;
		}

		if (event.getType() != ChatMessageType.GAMEMESSAGE) {
			return;
		}

		if (currentSlayerTask == null) {
			Matcher matcher = SLAYER_CURRENT_CHAT_MESSAGE.matcher(chatMessage);

			if (matcher.find()) {
				String taskName = matcher.group("name");

				if (taskName != null) {
					startTask(taskName);
				}
			}
		} else {

			if (chatMessage.startsWith("You've completed") && chatMessage.toLowerCase().contains("slayer master")) {
				completeTask(false); // This will clear lastStreakMessage, but we want to keep it
			}
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		// Ignore changes from other plugins
		if (!event.getGroup().equals(MazchnaSkipConfig.CONFIG_GROUP_NAME)) {
			return;
		}

		// Log when the streak config changes
		if (event.getKey().equals("useSkipReminder")) {
			SlayerTaskStreak newStreak = config.getUseSkipReminder();
			log.debug("onConfigChanged - useSkipReminder changed to: {}", newStreak);
		}

		// Set a dummy task
		if (event.getKey().equals("debugTask")) {
			if (event.getNewValue() == null) {
				return;
			}

			// Always clear task to reset area outline/tagged NPC's
			this.completeTask();

			if (!event.getNewValue().equals("None")) {
				this.startTask(event.getNewValue().toLowerCase().replace("_", " "));
			}
		}

		// Re-select the slayer task, so it re-draws the outline if enabled or removes the outline when disabled
		if (event.getKey().equals("enableSlayerAreaOutline")) {
			if (this.currentSlayerTask != null) {
				this.startTask(currentSlayerTask.getName());
			}
		}

		// Set the debug WorldPoint values to null to remove the outline
		if (event.getKey().equals("enableWorldPointSelector")) {
			if (event.getNewValue() != null && event.getNewValue().equals("false")) {
				debugAreaOutlineOverlay.setAreas(null);
			}
		}

		// Rebuild the NPC highlighter with the updated settings
		npcOverlayService.rebuild();
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned) {
		NPC npc = npcSpawned.getNpc();

		// Add the NPC to the targets for NPC highlighting
		if (currentSlayerTask != null) {
			for (int targetNpcId : currentSlayerTask.getNpcIds()) {
				if (npc.getId() == targetNpcId) {
					targets.add(npc);
				}
			}
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned) {
		// Remove the NPC from the targets
		NPC npc = npcDespawned.getNpc();
		targets.remove(npc);
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded menuEntryAdded) {
		if (!config.enableWorldPointSelector()) {
			return;
		}

		// Only add the menu entry when you can walk, so it doesn't get added when you are right-clicking in the bank
		if (menuEntryAdded.getOption().equals("Walk here")) {
			// Add options in reverse, so it shows up correctly in the right click menu
			client.getMenu()
					.createMenuEntry(-1)
					.setOption(DEBUG_MENU_RESET_WORLD_POINTS)
					.setTarget(menuEntryAdded.getTarget())
					.setType(MenuAction.RUNELITE)
					.onClick(menuEntry -> {
						debugWorldPointOne = null;
						debugWorldPointTwo = null;

						debugAreaOutlineOverlay.setAreas(null);
					});

			client.getMenu()
					.createMenuEntry(-1)
					.setOption(DEBUG_MENU_COPY_TO_CLIPBOARD)
					.setTarget(menuEntryAdded.getTarget())
					.setType(MenuAction.RUNELITE)
					.onClick(menuEntry -> {
						if (debugWorldPointOne != null && debugWorldPointTwo != null) {
							String copyString = "new WorldPoint(" + debugWorldPointOne.getX() + ", " + debugWorldPointOne.getY() + ", " + debugWorldPointOne.getPlane() + "), " +
									"new WorldPoint(" + debugWorldPointTwo.getX() + ", " + debugWorldPointTwo.getY() + ", " + debugWorldPointTwo.getPlane() + ")";

							StringSelection selection = new StringSelection(copyString);
							Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
							clipboard.setContents(selection, null);

							client.addChatMessage(ChatMessageType.GAMEMESSAGE, "Mazchna Skipping", "Copied the WorldPoints to your clipboard.", "Mazchna Skipping");
						}
					});

			client.getMenu()
					.createMenuEntry(-1)
					.setOption(DEBUG_MENU_WORLD_POINT_TWO)
					.setTarget(menuEntryAdded.getTarget())
					.setType(MenuAction.RUNELITE)
					.setIdentifier(menuEntryAdded.getIdentifier());

			client.getMenu()
					.createMenuEntry(-1)
					.setOption(DEBUG_MENU_WORLD_POINT_ONE)
					.setTarget(menuEntryAdded.getTarget())
					.setType(MenuAction.RUNELITE)
					.setIdentifier(menuEntryAdded.getIdentifier());
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		if (!event.getMenuOption().equals(DEBUG_MENU_WORLD_POINT_ONE) && !event.getMenuOption().equals(DEBUG_MENU_WORLD_POINT_TWO)) {
			return;
		}

		WorldView worldView = client.getLocalPlayer().getWorldView();
		Tile selectedSceneTile = worldView.getSelectedSceneTile();

		if (selectedSceneTile == null) {
			return;
		}

		if (event.getMenuOption().equals(DEBUG_MENU_WORLD_POINT_ONE)) {
			this.debugWorldPointOne = selectedSceneTile.getWorldLocation();

			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "Mazchna Skipping", "First WorldPoint has been selected.", "Mazchna Skipping");
		} else if (event.getMenuOption().equals(DEBUG_MENU_WORLD_POINT_TWO)) {
			this.debugWorldPointTwo = selectedSceneTile.getWorldLocation();

			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "Mazchna Skipping", "Second WorldPoint has been selected.", "Mazchna Skipping");
		}

		if (this.debugWorldPointOne != null && debugWorldPointTwo != null) {
			this.debugAreaOutlineOverlay.setAreas(List.of(
					WorldAreaUtils.fromCorners(debugWorldPointOne, debugWorldPointTwo)
			));
		}
	}

	@Provides
	MazchnaSkipConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(MazchnaSkipConfig.class);
	}

	private void startTask(String taskName) {
		lastStreakMessage = "";
		lastActiveStreak = SlayerTaskStreak.Off;

		SlayerTask lookupSlayerTask = SlayerTaskRegistry.getSlayerTaskByNpcName(taskName.toLowerCase());

		if (lookupSlayerTask != null) {
			this.currentSlayerTask = lookupSlayerTask;

			if (config.enableSlayerAreaOutline()) {
				List<WorldArea> allAreas = new ArrayList<>();

				for (NpcLocation npcLocation : currentSlayerTask.getLocations()) {
					allAreas.addAll(npcLocation.getWorldAreas());
				}

				areaOutlineOverlay.setAreas(allAreas);
				overlayManager.add(areaOutlineOverlay);
			} else {
				areaOutlineOverlay.setAreas(null);
				overlayManager.remove(areaOutlineOverlay);
			}

			if (config.enableWorldMapIcon()) {
				for (WorldPoint worldPoint : currentSlayerTask.getWorldMapLocations()) {
					worldMapPointManager.add(new SlayerTaskWorldMapPoint(worldPoint));
				}
			}

			if (config.useShortestPath()) {
				WorldPoint location = currentSlayerTask.getShortestPathWorldPoint();
				setShortestPath(location);
			}

			// Target NPC's visible to the player in case they are already at the location
			Player player = client.getLocalPlayer();

			// Player is null when you select a task from the debug menu whe not logged in
			if (player != null) {
				WorldView worldView = player.getWorldView();

				for (NPC npc : worldView.npcs()) {
					for (int targetNpcId : currentSlayerTask.getNpcIds()) {
						if (npc.getId() == targetNpcId) {
							targets.add(npc);
						}
					}
				}

				npcOverlayService.registerHighlighter(npcHighlighter);
			}
		}
	}

	private void completeTask() {
		completeTask(false);
	}

	private void completeTask(boolean resetMazchnaHidden) {
		areaOutlineOverlay.setAreas(null);
		overlayManager.remove(areaOutlineOverlay);

		currentSlayerTask = null;
		targets.clear();

		npcOverlayService.unregisterHighlighter(npcHighlighter);

		worldMapPointManager.removeIf(SlayerTaskWorldMapPoint.class::isInstance);

		lastActiveStreak = SlayerTaskStreak.Off;
		mazchnaHidden = false;
	}

	private String getTaskName(String npcText) {
		Pattern[] patterns = {SLAYER_ASSIGN_MESSAGE, SLAYER_CURRENT_MESSAGE};

		for (Pattern pattern : patterns) {
			Matcher matcher = pattern.matcher(npcText);

			if (matcher.find()) {
				return matcher.group("name");
			}
		}

		return null;
	}

	private void setShortestPath(WorldPoint target) {
		if (target == null) {
			return;
		}

		Map<String, Object> data = new HashMap<>();
		data.put("target", target);
		eventBus.post(new PluginMessage("shortestpath", "path", data));
	}

	public Function<NPC, HighlightedNpc> npcHighlighter = (n) -> {
		if (targets.contains(n) && config.enableNpcHighlight()) {
			return HighlightedNpc.builder()
					.npc(n)
					.highlightColor(config.getNpcColour())
					.outline(config.getNpcHighlightMode().equals(NpcHighlightMode.Outline))
					.hull(config.getNpcHighlightMode().equals(NpcHighlightMode.Hull))
					.tile(config.getNpcHighlightMode().equals(NpcHighlightMode.Tile))
					.trueTile(config.getNpcHighlightMode().equals(NpcHighlightMode.TrueTile))
					.render(npc -> !npc.isDead())
					.build();
		}

		return null;
	};

	private boolean mazchnaHidden = false;
	private SlayerTaskStreak lastActiveStreak = SlayerTaskStreak.Off;


	//*Special thanks to Jewel and Pine for the helpful advice*//
	@Subscribe(priority = -1)
	public void onMenuOpened(MenuOpened event) {
		MenuEntry[] menuEntries = client.getMenuEntries();
		List<MenuEntry> alteredMenuEntries = new ArrayList<>();

		SlayerTaskStreak streakConfig = config.getUseSkipReminder();

		log.debug("onMenuOpened - streakConfig: {}, lastStreakMessage: '{}', lastActiveStreak: {}",
				streakConfig, lastStreakMessage, lastActiveStreak);

		for (MenuEntry menuEntry : menuEntries) {
			boolean shouldRemove = false;

			if (menuEntry.getOption().equals("Mazchna")) {
				boolean streakConditionMet = false;

				boolean messageMatchesTen = SLAYER_TASK_STREAK_10.matcher(lastStreakMessage).find();
				boolean messageMatchesFifty = SLAYER_TASK_STREAK_50.matcher(lastStreakMessage).find();

				if (streakConfig == SlayerTaskStreak.Ten && (messageMatchesTen || messageMatchesFifty)) {
					streakConditionMet = true;
					lastActiveStreak = SlayerTaskStreak.Ten;
				} else if (streakConfig == SlayerTaskStreak.Fifty && messageMatchesFifty) {
					streakConditionMet = true;
					lastActiveStreak = SlayerTaskStreak.Fifty;
				}

				if (streakConditionMet) {
					shouldRemove = true;
					mazchnaHidden = true;
				} else {
					mazchnaHidden = false;
				}

				log.debug("onMenuOpened - Mazchna entry found - streakConditionMet: {}, shouldRemove: {}, streakConfig: {}, lastActiveStreak: {}",
						streakConditionMet, shouldRemove, streakConfig, lastActiveStreak);
			}

			if (!shouldRemove) {
				alteredMenuEntries.add(menuEntry);
			}
		}
		client.setMenuEntries(alteredMenuEntries.toArray(new MenuEntry[0]));
	}
}