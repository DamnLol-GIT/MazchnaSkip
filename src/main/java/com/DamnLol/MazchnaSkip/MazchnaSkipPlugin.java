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

import com.DamnLol.MazchnaSkip.Models.NavigationHint;
import com.DamnLol.MazchnaSkip.Models.NpcLocation;
import com.DamnLol.MazchnaSkip.Models.SlayerTask;
import com.DamnLol.MazchnaSkip.Models.SweatMode;
import com.DamnLol.MazchnaSkip.utils.AreaOutlineOverlay;
import com.DamnLol.MazchnaSkip.utils.TeleItemOverlay;
import com.DamnLol.MazchnaSkip.utils.POHObjectOverlay;
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
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
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
import java.util.Collections;
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

	private List<Integer> navHintItemIds = Collections.emptyList();
	private List<String> navHintMenuOptions = Collections.emptyList();
	private List<Integer> navHintEquippedItemIds = Collections.emptyList();
	private List<String> navHintPohObjectMenuOptions = Collections.emptyList();
	private List<Integer> navHintPohObjectIds = Collections.emptyList();
	private boolean playerOutsideTaskArea = false;
	private boolean playerInPoh = false;
	private List<String> navHintTargets = Collections.emptyList();

	private int teleportCooldownTicksRemaining = 0;
	private static final int TELEPORT_COOLDOWN_TICKS = 60;   // max ticks to suppress after teleport click
	private static final int TELEPORT_ARRIVE_RADIUS  = 32;   // tiles from task edge = task area; suppress highlight
	private static final int TELEPORT_CANCEL_RADIUS  = 64;   // tiles from task edge = reset highlight

	private static final List<Integer> SM_POH_ITEMS = List.of(13280, 13342, 9789, 9790, 8013);
	private static final List<Integer> SM_DESERT_ITEMS = List.of(13133, 13134, 13135, 13136);
	private boolean sweatModeActive = false;
	private List<Integer> sweatModeItemIds = Collections.emptyList();
	private List<String> sweatModeMenuOptions = Collections.emptyList();

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
	private TeleItemOverlay teleItemOverlay;

	@Inject
	private POHObjectOverlay POHObjectOverlay;

	@Inject
	private ClientThread clientThread;

	@Inject
	private EventBus eventBus;

	@Getter
	private SlayerTask currentSlayerTask;

	@Inject
	private ItemManager itemManager;

	@Override
	protected void startUp() {
		overlayManager.add(slayerTaskOverlay);
		overlayManager.add(debugAreaOutlineOverlay);
		overlayManager.add(teleItemOverlay);
		overlayManager.add(POHObjectOverlay);

		debugAreaOutlineOverlay.setUseAlternativeOutline(true);
	}

	@Override
	protected void shutDown() {
		overlayManager.remove(slayerTaskOverlay);
		overlayManager.remove(debugAreaOutlineOverlay);
		overlayManager.remove(teleItemOverlay);
		overlayManager.remove(POHObjectOverlay);
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

		// Update nav hint
		if (currentSlayerTask != null) {
			Player player = client.getLocalPlayer();
			if (player != null) {
				WorldPoint playerPos = player.getWorldLocation();
				playerInPoh = client.isInInstancedRegion();

				// Check if inside the task area (with 24-tile buffer)
				boolean inside = false;
				locCheck:
				for (NpcLocation loc : currentSlayerTask.getLocations()) {
					for (WorldArea area : loc.getWorldAreas()) {
						WorldArea buffered = new WorldArea(
								area.getX() - 24, area.getY() - 24,
								area.getWidth() + 48, area.getHeight() + 48,
								area.getPlane());
						if (buffered.contains(playerPos)) {
							inside = true;
							break locCheck;
						}
					}
				}
				playerOutsideTaskArea = !inside;

				if (config.enableNavigationHints()) {
					if (teleportCooldownTicksRemaining > 0) {
						teleportCooldownTicksRemaining--;
						// Cancel early if timer expires OR player is >64 tiles from every task area edge
						if (teleportCooldownTicksRemaining <= 0 || isFarFromTaskArea(playerPos)) {
							teleportCooldownTicksRemaining = 0;
						}
					}

					boolean suppressHighlight = teleportCooldownTicksRemaining > 0
							&& isNearTaskArea(playerPos);

					if (playerOutsideTaskArea && !suppressHighlight) {
						List<Integer> allItemIds = new ArrayList<>();
						List<String> allMenuOptions = new ArrayList<>();
						List<Integer> allEquippedItemIds = new ArrayList<>();
						List<Integer> pohObjectIds = Collections.emptyList();
						List<String> pohObjectMenuOptions = Collections.emptyList();

						if (playerInPoh) {
							pohSearch:
							for (NpcLocation loc : currentSlayerTask.getLocations()) {
								for (NavigationHint hint : loc.getNavigationHints()) {
									if (!hint.getPohObjectIds().isEmpty() && hint.getItemIds().isEmpty()) {
										pohObjectIds = hint.getPohObjectIds();
										pohObjectMenuOptions = hint.getPohObjectMenuOptions();
										break pohSearch;
									}
								}
							}
						}

						// Find priority highlight.
						hintSearch:
						for (NpcLocation loc : currentSlayerTask.getLocations()) {
							for (NavigationHint hint : loc.getNavigationHints()) {
								if (!hint.getPohObjectIds().isEmpty() && hint.getItemIds().isEmpty()) {
									continue;
								}
								// In POH - skip house teletabs and construction cape highlights
								if (playerInPoh && (isHouseTeleOnlyHint(hint) || isConstructionCapeHint(hint))) {
									continue;
								}

								boolean foundInInv = false;
								boolean foundInEquip = false;

								ItemContainer inv = client.getItemContainer(InventoryID.INV);
								if (inv != null) {
									for (Item item : inv.getItems()) {
										if (hint.getItemIds().contains(item.getId())) {
											foundInInv = true;
											break;
										}
									}
								}

								List<Integer> checkIds = new ArrayList<>(hint.getItemIds());
								checkIds.addAll(hint.getEquippedItemIds());
								ItemContainer worn = client.getItemContainer(InventoryID.WORN);
								if (worn != null) {
									for (Item item : worn.getItems()) {
										if (checkIds.contains(item.getId())) {
											foundInEquip = true;
											break;
										}
									}
								}

								if (foundInInv || foundInEquip) {
									allItemIds.addAll(hint.getItemIds());
									allMenuOptions.addAll(hint.getMenuOptions());
									if (foundInEquip && !foundInInv) {
										allEquippedItemIds.addAll(checkIds);
									}
									break hintSearch;
								}
							}
						}

						navHintItemIds = allItemIds;
						navHintMenuOptions = allMenuOptions;
						navHintEquippedItemIds = allEquippedItemIds;

						boolean pohIdsChanged = !pohObjectIds.equals(navHintPohObjectIds);
						navHintPohObjectIds = pohObjectIds;
						navHintPohObjectMenuOptions = pohObjectMenuOptions;

						teleItemOverlay.setHighlightItemIds(navHintItemIds);
						teleItemOverlay.setEquippedItemIds(navHintEquippedItemIds);
						teleItemOverlay.setActive(!navHintItemIds.isEmpty() || !navHintEquippedItemIds.isEmpty());

						POHObjectOverlay.setHighlightObjectIds(navHintPohObjectIds);
						if (!navHintPohObjectIds.isEmpty()) {
							if (pohIdsChanged) {
								POHObjectOverlay.clearTracked();
								List<String> uiTexts = navHintPohObjectMenuOptions.isEmpty()
										? Collections.emptyList()
										: List.of(navHintPohObjectMenuOptions.get(0));
								POHObjectOverlay.setPohUiTargetTexts(uiTexts);
							}
							scanSceneForPohObjects();
						}
						POHObjectOverlay.setActive(!navHintPohObjectIds.isEmpty());
					} else {
						teleItemOverlay.setActive(false);
						POHObjectOverlay.setActive(false);
						POHObjectOverlay.setPohUiTargetTexts(Collections.emptyList());
						POHObjectOverlay.clearTracked();
					}
				}
			}
		} else {
			playerOutsideTaskArea = false;
			playerInPoh = client.isInInstancedRegion();
			if (sweatModeActive && config.getSweatMode() != SweatMode.Off
					&& config.enableNavigationHints() && !playerInPoh) {
				List<Integer> found = new ArrayList<>();
				ItemContainer inv = client.getItemContainer(InventoryID.INV);
				ItemContainer worn = client.getItemContainer(InventoryID.WORN);
				for (int id : sweatModeItemIds) {
					boolean match = false;
					if (inv != null) {
						for (Item item : inv.getItems()) {
							if (item.getId() == id) { match = true; break; }
						}
					}
					if (!match && worn != null) {
						for (Item item : worn.getItems()) {
							if (item.getId() == id) { match = true; break; }
						}
					}
					if (match) { found.add(id); break; }
				}
				teleItemOverlay.setHighlightItemIds(found);
				teleItemOverlay.setEquippedItemIds(Collections.emptyList());
				teleItemOverlay.setActive(!found.isEmpty());
			} else {
				teleItemOverlay.setActive(false);
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
				completeTask(false);
			}
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		// Ignore changes from other plugins
		if (!event.getGroup().equals(MazchnaSkipConfig.CONFIG_GROUP_NAME)) {
			return;
		}

		clientThread.invokeLater(() -> {
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
		});
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
	public void onGameObjectSpawned(GameObjectSpawned event) {
		POHObjectOverlay.trackObject(event.getGameObject());
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event) {
		POHObjectOverlay.untrackObject(event.getGameObject());
	}

	@Subscribe
	public void onWallObjectSpawned(WallObjectSpawned event) {
		POHObjectOverlay.trackWallObject(event.getWallObject());
	}

	@Subscribe
	public void onWallObjectDespawned(WallObjectDespawned event) {
		POHObjectOverlay.untrackWallObject(event.getWallObject());
	}

	@Subscribe
	public void onDecorativeObjectSpawned(DecorativeObjectSpawned event) {
		POHObjectOverlay.trackDecorativeObject(event.getDecorativeObject());
	}

	@Subscribe
	public void onDecorativeObjectDespawned(DecorativeObjectDespawned event) {
		POHObjectOverlay.untrackDecorativeObject(event.getDecorativeObject());
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event) {
		if (!config.enableWorldPointSelector() && !playerOutsideTaskArea && !sweatModeActive) {
			return;
		}

		if (playerOutsideTaskArea && config.enableNavigationHints() && !navHintMenuOptions.isEmpty()) {
			String rawOption = Text.removeTags(event.getOption());
			String hexColor = String.format("%06x", config.getTeleColor().getRGB() & 0xFFFFFF);
			String rawTarget = Text.removeTags(event.getTarget());

			for (String hintOption : navHintMenuOptions) {
				if (rawOption.equals(hintOption)) {
					if (!navHintTargets.isEmpty() && !rawTarget.isEmpty()
							&& navHintTargets.stream().noneMatch(rawTarget::contains)) {
						break;
					}
					String current = event.getMenuEntry().getOption();
					if (!current.contains(hexColor)) {
						event.getMenuEntry().setOption("<col=" + hexColor + ">" + rawOption + "</col>");
						event.getMenuEntry().setTarget("<col=" + hexColor + ">" + rawTarget + "</col>");
					}
					break;
				}
			}
		}

		if (sweatModeActive && config.getSweatMode() != SweatMode.Off
				&& config.enableNavigationHints() && !sweatModeMenuOptions.isEmpty()) {
			String rawOption = Text.removeTags(event.getOption());
			String rawTarget = Text.removeTags(event.getTarget());
			String hexColor = String.format("%06x", config.getTeleColor().getRGB() & 0xFFFFFF);
			boolean hasItem = false;
			ItemContainer inv = client.getItemContainer(InventoryID.INV);
			if (inv != null) {
				for (Item item : inv.getItems()) {
					if (sweatModeItemIds.contains(item.getId())) { hasItem = true; break; }
				}
			}
			if (!hasItem) {
				ItemContainer worn = client.getItemContainer(InventoryID.WORN);
				if (worn != null) {
					for (Item item : worn.getItems()) {
						if (sweatModeItemIds.contains(item.getId())) { hasItem = true; break; }
					}
				}
			}
			if (hasItem) {
				for (String opt : sweatModeMenuOptions) {
					if (rawOption.equals(opt)) {
						String current = event.getMenuEntry().getOption();
						if (!current.contains(hexColor)) {
							event.getMenuEntry().setOption("<col=" + hexColor + ">" + rawOption + "</col>");
							event.getMenuEntry().setTarget("<col=" + hexColor + ">" + rawTarget + "</col>");
						}
						break;
					}
				}
			}
		}

		// WorldPoint selector debug menu entries
		if (config.enableWorldPointSelector() && event.getOption().equals("Walk here")) {
			// Add options in reverse, so it shows up correctly in the right click menu
			client.getMenu()
					.createMenuEntry(-1)
					.setOption(DEBUG_MENU_RESET_WORLD_POINTS)
					.setTarget(event.getTarget())
					.setType(MenuAction.RUNELITE)
					.onClick(menuEntry -> {
						debugWorldPointOne = null;
						debugWorldPointTwo = null;

						debugAreaOutlineOverlay.setAreas(null);
					});

			client.getMenu()
					.createMenuEntry(-1)
					.setOption(DEBUG_MENU_COPY_TO_CLIPBOARD)
					.setTarget(event.getTarget())
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
					.setTarget(event.getTarget())
					.setType(MenuAction.RUNELITE)
					.setIdentifier(event.getIdentifier());

			client.getMenu()
					.createMenuEntry(-1)
					.setOption(DEBUG_MENU_WORLD_POINT_ONE)
					.setTarget(event.getTarget())
					.setType(MenuAction.RUNELITE)
					.setIdentifier(event.getIdentifier());
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		if (playerOutsideTaskArea && config.enableNavigationHints()) {
			String rawOption = Text.removeTags(event.getMenuOption());
			boolean isNavHintClick = navHintMenuOptions.contains(rawOption)
					|| navHintPohObjectMenuOptions.contains(rawOption);
			if (isNavHintClick) {
				teleportCooldownTicksRemaining = TELEPORT_COOLDOWN_TICKS;
			}
		}

		if (sweatModeActive && config.getSweatMode() != SweatMode.Off) {
			String rawOption = Text.removeTags(event.getMenuOption());
			if (sweatModeMenuOptions.contains(rawOption)) {
				sweatModeActive = false;
				sweatModeItemIds = Collections.emptyList();
				sweatModeMenuOptions = Collections.emptyList();
				teleItemOverlay.setActive(false);
			}
		}

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
		teleportCooldownTicksRemaining = 0;
		sweatModeActive = false;
		sweatModeItemIds = Collections.emptyList();
		sweatModeMenuOptions = Collections.emptyList();

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

			// Get nav - inventory first then equipment
			List<Integer> allItemIds = new ArrayList<>();
			List<String> allMenuOptions = new ArrayList<>();
			List<Integer> allEquippedItemIds = new ArrayList<>();

			outer:
			for (NpcLocation loc : currentSlayerTask.getLocations()) {
				for (NavigationHint hint : loc.getNavigationHints()) {
					// Check inventory for any tele item
					ItemContainer inventory = client.getItemContainer(InventoryID.INV);
					if (inventory != null) {
						for (Item item : inventory.getItems()) {
							if (hint.getItemIds().contains(item.getId())) {
								allItemIds.addAll(hint.getItemIds());
								allMenuOptions.addAll(hint.getMenuOptions());
								break outer;
							}
						}
					}

					// Check equipment for any tele item
					ItemContainer equipment = client.getItemContainer(InventoryID.WORN);
					if (equipment != null) {
						List<Integer> idsToCheckInEquipment = new ArrayList<>(hint.getItemIds());
						idsToCheckInEquipment.addAll(hint.getEquippedItemIds());
						for (Item item : equipment.getItems()) {
							if (idsToCheckInEquipment.contains(item.getId())) {
								allItemIds.addAll(hint.getItemIds());
								allMenuOptions.addAll(hint.getMenuOptions());
								allEquippedItemIds.addAll(idsToCheckInEquipment);
								break outer;
							}
						}
					}
				}
			}
			navHintItemIds = allItemIds;
			navHintMenuOptions = allMenuOptions;
			navHintEquippedItemIds = allEquippedItemIds;
			teleItemOverlay.setHighlightItemIds(navHintItemIds);
			teleItemOverlay.setEquippedItemIds(navHintEquippedItemIds);

			List<String> allTargets = new ArrayList<>();
			for (NpcLocation loc : currentSlayerTask.getLocations()) {
				for (NavigationHint hint : loc.getNavigationHints()) {
					for (int id : hint.getItemIds()) {
						String name = itemManager.getItemComposition(id).getName();
						if (name != null) allTargets.add(name);
					}
				}
			}
			navHintTargets = allTargets;
		}
		log.debug("startTask complete - navHintItemIds: {}, playerOutsideTaskArea: {}", navHintItemIds, playerOutsideTaskArea);
	}

	private void completeTask() {
		completeTask(false);
		navHintTargets = Collections.emptyList();
	}

	private void completeTask(boolean resetMazchnaHidden) {
		areaOutlineOverlay.setAreas(null);
		overlayManager.remove(areaOutlineOverlay);

		currentSlayerTask = null;
		targets.clear();

		npcOverlayService.unregisterHighlighter(npcHighlighter);

		worldMapPointManager.removeIf(SlayerTaskWorldMapPoint.class::isInstance);

		navHintItemIds = Collections.emptyList();
		navHintMenuOptions = Collections.emptyList();
		navHintEquippedItemIds = Collections.emptyList();
		navHintPohObjectIds = Collections.emptyList();
		navHintPohObjectMenuOptions = Collections.emptyList();
		teleportCooldownTicksRemaining = 0;
		teleItemOverlay.setHighlightItemIds(Collections.emptyList());
		teleItemOverlay.setEquippedItemIds(Collections.emptyList());
		teleItemOverlay.setActive(false);
		POHObjectOverlay.setHighlightObjectIds(Collections.emptyList());
		POHObjectOverlay.setPohUiTargetTexts(Collections.emptyList());
		POHObjectOverlay.setActive(false);
		POHObjectOverlay.clearTracked();
		playerOutsideTaskArea = false;

		// Clear the shortest path when a task ends
		setShortestPath(null);

		lastActiveStreak = SlayerTaskStreak.Off;
		mazchnaHidden = false;

		SweatMode sweatMode = config.getSweatMode();
		if (sweatMode == SweatMode.PoH_Tele) {
			sweatModeActive = true;
			sweatModeItemIds = SM_POH_ITEMS;
			sweatModeMenuOptions = List.of("Teleports", "Tele to POH", "Teleport", "Break");
		} else if (sweatMode == SweatMode.Desert_4) {
			sweatModeActive = true;
			sweatModeItemIds = SM_DESERT_ITEMS;
			sweatModeMenuOptions = List.of("Nardah");
		} else {
			sweatModeActive = false;
			sweatModeItemIds = Collections.emptyList();
			sweatModeMenuOptions = Collections.emptyList();
		}
	}

	private void scanSceneForPohObjects() {
		Player player = client.getLocalPlayer();
		if (player == null) return;
		Scene scene = player.getWorldView().getScene();
		Tile[][][] tiles = scene.getTiles();
		int matched = 0;
		for (Tile[][] value : tiles) {
			for (Tile[] item : value) {
				for (Tile tile : item) {
					if (tile == null) continue;
					for (GameObject obj : tile.getGameObjects()) {
						if (obj != null) {
							if (navHintPohObjectIds.contains(obj.getId())) matched++;
							POHObjectOverlay.trackObject(obj);
						}
					}
					if (tile.getWallObject() != null) {
						if (navHintPohObjectIds.contains(tile.getWallObject().getId())) matched++;
						POHObjectOverlay.trackWallObject(tile.getWallObject());
					}
					if (tile.getDecorativeObject() != null) {
						if (navHintPohObjectIds.contains(tile.getDecorativeObject().getId())) matched++;
						POHObjectOverlay.trackDecorativeObject(tile.getDecorativeObject());
					}
					if (tile.getGroundObject() != null) {
						if (navHintPohObjectIds.contains(tile.getGroundObject().getId())) matched++;
						POHObjectOverlay.trackGroundObject(tile.getGroundObject());
					}
				}
			}
		}
		log.debug("scanSceneForPohObjects: looking for IDs={}, matched {} objects by base ID", navHintPohObjectIds, matched);
	}

	private boolean isFarFromTaskArea(WorldPoint pos) {
		if (currentSlayerTask == null) return true;
		for (NpcLocation loc : currentSlayerTask.getLocations()) {
			for (WorldArea area : loc.getWorldAreas()) {
				if (area.distanceTo(pos) <= TELEPORT_CANCEL_RADIUS) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isNearTaskArea(WorldPoint pos) {
		if (currentSlayerTask == null) return false;
		for (NpcLocation loc : currentSlayerTask.getLocations()) {
			for (WorldArea area : loc.getWorldAreas()) {
				if (area.distanceTo(pos) <= TELEPORT_ARRIVE_RADIUS) {
					return true;
				}
			}
		}
		return false;
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
		Map<String, Object> data = new HashMap<>();
		if (target != null) {
			data.put("target", target);
		}
		// Posting without a "target" key signals the Shortest Path plugin to clear its path
		eventBus.post(new PluginMessage("shortestpath", "path", data));
	}

	private boolean isHouseTeleOnlyHint(NavigationHint hint) {
		List<String> opts = hint.getMenuOptions();
		return opts != null && !opts.isEmpty() && opts.get(0).equals("Break");
	}

	private boolean isConstructionCapeHint(NavigationHint hint) {
		List<String> opts = hint.getMenuOptions();
		return opts != null && opts.contains("Tele to POH");
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

		if (playerOutsideTaskArea && config.enableNavigationHints() && !navHintPohObjectMenuOptions.isEmpty()
				&& !navHintPohObjectIds.isEmpty()) {
			String hexColor = String.format("%06x", config.getTeleColor().getRGB() & 0xFFFFFF);
			MenuEntry[] entries = client.getMenuEntries();

			// matches target object IDs
			boolean menuIsForOurObject = false;
			for (MenuEntry entry : entries) {
				if (navHintPohObjectIds.contains(entry.getIdentifier())) {
					menuIsForOurObject = true;
					break;
				}
			}

			if (menuIsForOurObject) {
				outer:
				for (String pohOption : navHintPohObjectMenuOptions) {
					for (MenuEntry entry : entries) {
						String rawOpt = Text.removeTags(entry.getOption());
						if (rawOpt.equals(pohOption)) {
							if (!entry.getOption().contains(hexColor)) {
								String rawTgt = Text.removeTags(entry.getTarget());
								entry.setOption("<col=" + hexColor + ">" + rawOpt + "</col>");
								entry.setTarget("<col=" + hexColor + ">" + rawTgt + "</col>");
							}
							break outer;
						}
					}
				}
			}
		}
	}
}