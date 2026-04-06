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

import com.DamnLol.MazchnaSkip.Models.NavigationHint;
import com.DamnLol.MazchnaSkip.Models.NpcLocation;
import com.DamnLol.MazchnaSkip.Models.SlayerTask;
import com.DamnLol.MazchnaSkip.utils.WorldAreaUtils;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.NpcID;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SlayerTaskRegistry {
    private static final List<Integer> SLAYER_RING = List.of(11866, 11867, 11868, 11869, 11870, 11871, 11872, 11873, 21268);
    private static final List<Integer> QUEST_CAPE = List.of(9813, 13068);
    private static final List<Integer> MAX_CAPE = List.of(13280, 13342);
    private static final List<Integer> CONSTRUCTION_CAPE = List.of(9789, 9790);
    private static final List<Integer> TELEPORT_TO_HOUSE = List.of(8013);
    private static final List<Integer> CRAFTING_CAPE = List.of(9780, 9781);
    private static final List<Integer> MORYTANIA_LEGS = List.of(13114, 13115);
    private static final List<Integer> DESERT_AMULET = List.of(13133, 13134, 13135, 13136);
    private static final List<Integer> ACHIEVEMENT_DIARY_CAPE = List.of(13069, 19476);
    private static final List<Integer> XERIC_TALISMAN = List.of(13393);
    private static final List<Integer> SKULL_SCEPTRE = List.of(9013, 21276);
    private static final List<Integer> RING_OF_ELEMENTS = List.of(26818);
    private static final List<Integer> ECTOPHIAL = List.of(4251, 4252);
    private static final List<Integer> ROYAL_SEED_POD = List.of(19564);

    private static final List<Integer> JEWELLERY_BOX = List.of(29156, 37520, 37521, 37522, 37523, 37524, 37525, 37526, 37527, 37528, 37529, 37530, 37531, 37532, 37533, 37534, 37535, 37536, 37537, 37538, 37539, 37540, 37541, 37542, 37543, 37544, 37545, 37546, 50712);
    private static final List<Integer> MOUNTED_XERIC = List.of(33411, 33412, 33413, 33414, 33415, 33419);
    private static final List<Integer> MOUNTED_DIGSITE = List.of(33416, 33417, 33418, 33420);
    private static final List<Integer> MOUNTED_QUEST_CAPE = List.of(29178, 29179);
    private static final List<Integer> MOUNTED_MAX_CAPE = List.of(31925);
    private static final List<Integer> MOUNTED_CRAFTING_CAPE = List.of(29188, 29189);
    private static final List<Integer> SPIRIT_TREE_OVERWORLD = List.of(1293, 1294, 1295, 33733);
    private static final List<Integer> SPIRIT_TREE_POH_ONLY = List.of(29227, 40778, 44936);
    private static final List<Integer> SPIRIT_TREE_FAIRY_RING_COMBO = List.of(29229, 27097, 40779);
    private static final List<Integer> SPIRIT_TREE_POH = List.of(29227, 40778, 44936, 29229, 27097, 40779);
    private static final List<Integer> SPIRIT_TREE_ALL = List.of(1293, 1294, 1295, 33733, 29227, 40778, 44936, 29229, 27097, 40779);
    private static final List<Integer> FAIRY_RING_POH = List.of(29228, 29229, 27097, 40779);
    private static final List<Integer> FAIRY_RING_OVERWORLD = List.of(29495, 29560);
    private static final List<Integer> FAIRY_RING = List.of(29228, 29229, 27097, 40779, 29495, 29560);


    // Max cape > (POH) Mounted max cape > Construction cape > Quest cape > (POH) Mounted quest cape > Teleport to house
    private static List<NavigationHint> pohChain() {
        return List.of(
                new NavigationHint(MAX_CAPE, List.of("Teleports"), MAX_CAPE),
                new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), MOUNTED_MAX_CAPE, List.of("Teleports")),
                new NavigationHint(CONSTRUCTION_CAPE, List.of("Tele to POH", "Teleport"), CONSTRUCTION_CAPE),
                new NavigationHint(QUEST_CAPE, List.of("Teleport"), QUEST_CAPE),
                new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), MOUNTED_QUEST_CAPE, List.of("Teleport")),
                new NavigationHint(TELEPORT_TO_HOUSE, List.of("Break"))
        );
    }

    // Max cape > (POH) Mounted max cape > Construction cape > Teleport to house
    private static List<NavigationHint> pohChainNoQuest() {
        return List.of(
                new NavigationHint(MAX_CAPE, List.of("Teleports"), MAX_CAPE),
                new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), MOUNTED_MAX_CAPE, List.of("Teleports")),
                new NavigationHint(CONSTRUCTION_CAPE, List.of("Tele to POH", "Teleport"), CONSTRUCTION_CAPE),
                new NavigationHint(TELEPORT_TO_HOUSE, List.of("Break"))
        );
    }

    // Slayer ring > Quest cape > Max cape > (POH) Mounted max cape > Construction cape > Teleport to house > (POH/OW) Fairy ring AJR
    private static List<NavigationHint> slayerRingFremChain() {
        return List.of(
                new NavigationHint(SLAYER_RING, List.of("Teleport", "Fremennik Dungeon"), SLAYER_RING),
                new NavigationHint(QUEST_CAPE, List.of("Teleport"), QUEST_CAPE),
                new NavigationHint(MAX_CAPE, List.of("Teleports"), MAX_CAPE),
                new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), MOUNTED_MAX_CAPE, List.of("Teleports")),
                new NavigationHint(CONSTRUCTION_CAPE, List.of("Tele to POH", "Teleport"), CONSTRUCTION_CAPE),
                new NavigationHint(TELEPORT_TO_HOUSE, List.of("Break")),
                new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), FAIRY_RING, List.of("AJR", "Teleport Menu"))
        );
    }

    // Max cape > (POH) Mounted max cape > Construction cape > POH object > Teleport to house
    private static List<NavigationHint> pohChainNoQuestPoh(List<Integer> objectIds, List<String> objectMenuOptions) {
        List<String> primaryOption = objectMenuOptions.isEmpty()
                ? Collections.emptyList()
                : List.of(objectMenuOptions.get(0));
        return List.of(
                new NavigationHint(MAX_CAPE, List.of("Teleports"), MAX_CAPE),
                new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), MOUNTED_MAX_CAPE, List.of("Teleports")),
                new NavigationHint(CONSTRUCTION_CAPE, List.of("Tele to POH", "Teleport"), CONSTRUCTION_CAPE),
                new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), objectIds, primaryOption),
                new NavigationHint(TELEPORT_TO_HOUSE, List.of("Break"))
        );
    }

    //  Achievement diary cape > Xeric > Max > (POH) Mounted max cape > Construction > POH Xeric > Teleport to house
    private static List<NavigationHint> ghostsChain() {
        return List.of(
                new NavigationHint(ACHIEVEMENT_DIARY_CAPE, List.of("Teleport", "Kourend: Castle"), ACHIEVEMENT_DIARY_CAPE),
                new NavigationHint(XERIC_TALISMAN, List.of("Rub", "Xeric's Heart"), XERIC_TALISMAN),
                new NavigationHint(MAX_CAPE, List.of("Teleports"), MAX_CAPE),
                new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), MOUNTED_MAX_CAPE, List.of("Teleports")),
                new NavigationHint(CONSTRUCTION_CAPE, List.of("Tele to POH", "Teleport"), CONSTRUCTION_CAPE),
                new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), MOUNTED_XERIC, List.of("Heart","Teleport menu")),
                new NavigationHint(TELEPORT_TO_HOUSE, List.of("Break"))
        );
    }


    // Quest cape > Max cape > (POH) Mounted max cape > Construction cape > Teleport to house > (POH) Fairy ring
    private static List<NavigationHint> pohChainFairyRing(String fairyRingCode) {
        return List.of(
                new NavigationHint(QUEST_CAPE, List.of("Teleport"), QUEST_CAPE),
                new NavigationHint(MAX_CAPE, List.of("Teleports"), MAX_CAPE),
                new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), MOUNTED_MAX_CAPE, List.of("Teleports")),
                new NavigationHint(CONSTRUCTION_CAPE, List.of("Tele to POH", "Teleport"), CONSTRUCTION_CAPE),
                new NavigationHint(TELEPORT_TO_HOUSE, List.of("Break")),
                new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), FAIRY_RING, List.of(fairyRingCode, "Teleport Menu"))
        );
    }

    // Bears: Quest cape > (POH) Mounted quest cape OR fairy ring BLR (proximity-decided in plugin) > Max cape > (POH) Mounted max cape > Construction cape > Teleport to house > (POH) Fairy ring BLR
    private static List<NavigationHint> bearsFairyRingChain() {
        return List.of(
                new NavigationHint(QUEST_CAPE, List.of("Teleport"), QUEST_CAPE),
                new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), MOUNTED_QUEST_CAPE, List.of("Teleport")),
                new NavigationHint(MAX_CAPE, List.of("Teleports"), MAX_CAPE),
                new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), MOUNTED_MAX_CAPE, List.of("Teleports")),
                new NavigationHint(CONSTRUCTION_CAPE, List.of("Tele to POH", "Teleport"), CONSTRUCTION_CAPE),
                new NavigationHint(TELEPORT_TO_HOUSE, List.of("Break")),
                new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), FAIRY_RING, List.of("BLR", "Teleport Menu"))
        );
    }

    // Royal seed pod > Spirit tree (overworld) > Max cape > (POH) Mounted max cape > Construction cape > Teleport to house > (POH) Spirit tree
    private static List<NavigationHint> seedPodChain() {
        return List.of(
                new NavigationHint(ROYAL_SEED_POD, List.of("Commune")),
                new NavigationHint(MAX_CAPE, List.of("Teleports"), MAX_CAPE),
                new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), MOUNTED_MAX_CAPE, List.of("Teleports")),
                new NavigationHint(CONSTRUCTION_CAPE, List.of("Tele to POH", "Teleport"), CONSTRUCTION_CAPE),
                new NavigationHint(TELEPORT_TO_HOUSE, List.of("Break")),
                new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), SPIRIT_TREE_POH, List.of("Gnome Stronghold", "Travel"))
        );
    }

    private final static Map<String, SlayerTask> MAZCHNA_TASKS = Map.ofEntries(
            Map.entry("banshees", new SlayerTask("Banshees", List.of(NpcID.SLAYER_BANSHEE_1, NpcID.SUPERIOR_BANSHEE), List.of(
                            new WorldPoint(3442, 3542, 0)
                    ), List.of(
                            new NpcLocation("Morytania Slayer Tower", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3430, 3530, 0),
                                            new WorldPoint(3454, 3567, 0)
                                    )
                            ), new String[]{"Slayer Ring: Morytania Slayer Tower (Option 2)"},
                                    List.of(new NavigationHint(SLAYER_RING, List.of("Teleport", "Slayer Tower"), SLAYER_RING)))
                    ))
            ),
            Map.entry("bats", new SlayerTask("Bats", List.of(NpcID.SMALL_BAT), List.of(
                            new WorldPoint(3351, 3490, 0)
                    ), List.of(
                            new NpcLocation("Silvarea, North of Digsite", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3327, 3475, 0),
                                            new WorldPoint(3398, 3507, 0)
                                    )
                            ), new String[]{"Ring of the elements: Earth Altar (Option 3)", "(POH) Digsite Pendant"},
                                    List.of(
                                            new NavigationHint(RING_OF_ELEMENTS, List.of("Rub", "Earth Altar"), RING_OF_ELEMENTS),
                                            new NavigationHint(MAX_CAPE, List.of("Teleports"), MAX_CAPE),
                                            new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), MOUNTED_MAX_CAPE, List.of("Teleports")),
                                            new NavigationHint(CONSTRUCTION_CAPE, List.of("Tele to POH", "Teleport"), CONSTRUCTION_CAPE),
                                            new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), MOUNTED_DIGSITE, List.of("Digsite", "Teleport menu")),
                                            new NavigationHint(TELEPORT_TO_HOUSE, List.of("Break"))
                                    ))
                    ))
            ),
            Map.entry("bears", new SlayerTask("Bears", List.of(NpcID.BROWNBEAR), List.of(
                            new WorldPoint(2700, 3330, 0)
                    ), List.of(
                            new NpcLocation("South-west of Legends' Guild", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2687, 3318, 0),
                                            new WorldPoint(2722, 3351, 0)
                                    )
                            ), new String[]{"Quest Cape teleport", "Fairy ring: BLR"},
                                    bearsFairyRingChain())
                    ))
            ),
            Map.entry("catablepon", new SlayerTask("Catablepon", List.of(NpcID.SOS_PEST_CATABLEPON, NpcID.SOS_PEST_CATABLEPON2, NpcID.SOS_PEST_CATABLEPON3), List.of(
                            new WorldPoint(3084, 3417, 0),
                            new WorldPoint(1956, 4997, 0)
                    ), List.of(
                            new NpcLocation("Third level of the Stronghold of Security.", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2139, 5262, 0),
                                            new WorldPoint(2160, 5244, 0)
                                    )
                            ), new String[]{"Skull Sceptre: Invoke"},
                                    List.of(new NavigationHint(SKULL_SCEPTRE, List.of("Invoke"), SKULL_SCEPTRE)))
                    ), "**Block**")
            ),
            Map.entry("cave bugs", new SlayerTask("Cave bugs", List.of(NpcID.SWAMP_CAVE_BUG), List.of(
                            new WorldPoint(2789, 3617, 0),
                            new WorldPoint(2786, 5318, 0)
                    ), List.of(
                            new NpcLocation("Dorgesh-Kaan South Dungeon", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2708, 5221, 0),
                                            new WorldPoint(2721, 5245, 0)
                                    )
                            ), new String[]{"Fairy ring: AJQ"},
                                    pohChainFairyRing("AJQ"))
                    ), "Bring a light source", new WorldPoint(2715, 5235, 0))
            ),
            Map.entry("cave crawlers", new SlayerTask("Cave Crawlers", List.of(
                            NpcID.SLAYER_CAVE_CRAWLER_1, NpcID.SLAYER_CAVE_CRAWLER_2,
                            NpcID.SLAYER_CAVE_CRAWLER_3, NpcID.SLAYER_CAVE_CRAWLER_4,
                            NpcID.SUPERIOR_CAVE_CRAWLER_ICE), List.of(
                            new WorldPoint(2789, 3617, 0),
                            new WorldPoint(2787, 9996, 0)
                    ), List.of(
                            new NpcLocation("Fremennik Slayer Dungeon", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2776, 9989, 0),
                                            new WorldPoint(2812, 10007, 0)
                                    )
                            ), new String[]{"Slayer ring: Fremennik Slayer Dungeon (Option 3)", "Fairy ring: AJR"},
                                    slayerRingFremChain())
                    ))
            ),
            Map.entry("cave slimes", new SlayerTask("Cave slimes", List.of(NpcID.SWAMP_CAVE_SLIME), List.of(
                            new WorldPoint(2728, 5235, 0)
                    ), List.of(
                            new NpcLocation("Dorgesh-Kaan South Dungeon", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2692, 5227, 0),
                                            new WorldPoint(2708, 5242, 0)
                                    ),
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2722, 5232, 0),
                                            new WorldPoint(2735, 5239, 0)
                                    )
                            ), new String[]{"Fairy ring: AJQ"},
                                    pohChainFairyRing("AJQ"))
                    ), "Bring a light source", new WorldPoint(2730, 5234, 0))
            ),
            Map.entry("cockatrice", new SlayerTask("Cockatrice", List.of(NpcID.SLAYER_COCKATRICE, NpcID.SUPERIOR_COCKATRICE), List.of(
                            new WorldPoint(2789, 3617, 0),
                            new WorldPoint(2792, 10035, 0)
                    ), List.of(
                            new NpcLocation("Fremennik Slayer Dungeon", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2780, 10026, 0),
                                            new WorldPoint(2805, 10041, 0)
                                    )
                            ), new String[]{"Slayer ring: Fremennik Slayer Dungeon (Option 3)", "Fairy ring: AJR"},
                                    slayerRingFremChain())
                    ), "**Block**")
            ),
            Map.entry("crabs", new SlayerTask("Crabs", List.of(NpcID.HORROR_ROCKCRAB, NpcID.HORROR_ROCKCRAB_SMALL, NpcID.HORROR_ROCKCRAB_INACTIVE, NpcID.HORROR_ROCKCRAB_SMALL_INACTIVE), List.of(
                            new WorldPoint(2702, 3724, 0)
                    ), List.of(
                            new NpcLocation("North of Rellekka", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2691, 3733, 0),
                                            new WorldPoint(2721, 3712, 0)
                                    )
                            ), new String[]{"Fairy ring: DKS"},
                                    pohChainFairyRing("DKS"))
                    ))
            ),
            Map.entry("crawling hands", new SlayerTask("Crawling Hands", List.of(
                            NpcID.SLAYER_CRAWLING_HAND_1, NpcID.SLAYER_CRAWLING_HAND_2,
                            NpcID.SLAYER_CRAWLING_HAND_3, NpcID.SLAYER_CRAWLING_HAND_4,
                            NpcID.SLAYER_CRAWLING_HAND_5, NpcID.SLAYER_CRAWLING_HAND_BIG_1,
                            NpcID.SLAYER_CRAWLING_HAND_BIG_2, NpcID.SLAYER_CRAWLING_HAND_BIG_3,
                            NpcID.SLAYER_CRAWLING_HAND_BIG_4, NpcID.SLAYER_CRAWLING_HAND_BIG_5,
                            NpcID.SUPERIOR_CRAWLING_HAND), List.of(
                            new WorldPoint(3419, 3571, 0)
                    ), List.of(
                            new NpcLocation("Morytania Slayer Tower", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3406, 3578, 0),
                                            new WorldPoint(3433, 3541, 0)
                                    )
                            ), new String[]{"Slayer Ring: Morytania Slayer Tower (Option 2)"},
                                    List.of(new NavigationHint(SLAYER_RING, List.of("Teleport", "Slayer Tower"), SLAYER_RING)))
                    ))
            ),
            Map.entry("dogs", new SlayerTask("Dogs", List.of(NpcID.ICS_LITTLE_JACKAL), List.of(
                            new WorldPoint(3382, 2913, 0)
                    ), List.of(
                            new NpcLocation("West of Nardah", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3365, 2939, 0),
                                            new WorldPoint(3394, 2889, 0)
                                    )
                            ), new String[]{"Desert Amulet to Nardah", "Fairy Ring DLQ"},
                                    List.of(
                                            new NavigationHint(DESERT_AMULET, List.of("Nardah"), DESERT_AMULET),
                                            new NavigationHint(QUEST_CAPE, List.of("Teleport"), QUEST_CAPE),
                                            new NavigationHint(MAX_CAPE, List.of("Teleports"), MAX_CAPE),
                                            new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), MOUNTED_MAX_CAPE, List.of("Teleports")),
                                            new NavigationHint(CONSTRUCTION_CAPE, List.of("Tele to POH", "Teleport"), CONSTRUCTION_CAPE),
                                            new NavigationHint(TELEPORT_TO_HOUSE, List.of("Break")),
                                            new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), FAIRY_RING, List.of("DLQ", "Teleport Menu"))
                                    ))
                    ))
            ),
            Map.entry("fleshcrawlers", new SlayerTask("Flesh Crawlers", List.of(NpcID.SOS_FAM_FLESHCRAWLER, NpcID.SOS_FAM_FLESHCRAWLER2, NpcID.SOS_FAM_FLESHCRAWLER3), List.of(
                            new WorldPoint(3084, 3417, 0),
                            new WorldPoint(2006, 5202, 0)
                    ), List.of(
                            new NpcLocation("Second Layer of the Stronghold Of Security", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(1997, 5194, 0),
                                            new WorldPoint(2013, 5211, 0)
                                    )
                            ), new String[]{"Skull Sceptre: Invoke"},
                                    List.of(new NavigationHint(SKULL_SCEPTRE, List.of("Invoke"), SKULL_SCEPTRE)))
                    ))
            ),
            Map.entry("ghosts", new SlayerTask("Ghosts", List.of(NpcID.KOUREND_GHOST1), List.of(
                            new WorldPoint(1639, 3673, 0),
                            new WorldPoint(1693, 10063, 0)
                    ), List.of(
                            new NpcLocation("Catacombs of Kourend", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(1680, 10053, 0),
                                            new WorldPoint(1703, 10070, 0)
                                    )
                            ), new String[]{"Xeric's talisman: Xeric's Heart (Option 4)", "Achievement diary cape: Kourend Castle (Option A)"},
                                    ghostsChain())
                    ))
            ),
            Map.entry("ghouls", new SlayerTask("Ghouls", List.of(NpcID.GHOUL), List.of(
                            new WorldPoint(3433, 3462, 0)
                    ), List.of(
                            new NpcLocation("West of Canifis", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3443, 3456, 0),
                                            new WorldPoint(3422, 3469, 0)
                                    )
                            ), new String[]{"Fairy Ring: CKS"},
                                    pohChainFairyRing("CKS"))
                    ))
            ),
            Map.entry("hill giants", new SlayerTask("Hill Giants", List.of(
                            NpcID.GIANT, NpcID.GIANT2, NpcID.GIANT3,
                            NpcID.GIANT4, NpcID.GIANT5, NpcID.GIANT6), List.of(
                            new WorldPoint(1444, 3613, 0)
                    ), List.of(
                            new NpcLocation("Giants Den", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(1437, 3622, 0),
                                            new WorldPoint(1451, 3604, 0)
                                    )
                            ), new String[]{"Fairy Ring: DJR"},
                                    pohChainFairyRing("DJR"))
                    ))
            ),
            Map.entry("hobgoblins", new SlayerTask("Hobgoblins", List.of(
                            NpcID.HOBGOBLIN_ARMED, NpcID.HOBGOBLIN_UNARMED,
                            NpcID.RIMMINGTON_HOBGOBLIN_UNARMED_1, NpcID.RIMMINGTON_HOBGOBLIN_UNARMED_2,
                            NpcID.RIMMINGTON_HOBGOBLIN_UNARMED_3, NpcID.RIMMINGTON_HOBGOBLIN_ARMED_1,
                            NpcID.HOBGOBLIN_UNARMED), List.of(
                            new WorldPoint(2909, 3285, 0)
                    ), List.of(
                            new NpcLocation("Hobgoblin Peninsula", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2901, 3299, 0),
                                            new WorldPoint(2921, 3263, 0)
                                    )
                            ), new String[]{"Crafting Cape Teleport", "(POH) Skills Necklace: Crafting Guild"},
                                    List.of(
                                            new NavigationHint(MAX_CAPE, List.of("Teleports"), MAX_CAPE),
                                            new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), MOUNTED_MAX_CAPE, List.of("Teleports")),
                                            new NavigationHint(CRAFTING_CAPE, List.of("Teleport"), CRAFTING_CAPE),
                                            new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), MOUNTED_CRAFTING_CAPE, List.of("Teleport")),
                                            new NavigationHint(CONSTRUCTION_CAPE, List.of("Tele to POH", "Teleport"), CONSTRUCTION_CAPE),
                                            new NavigationHint(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                                                    JEWELLERY_BOX, List.of("Crafting Guild")),
                                            new NavigationHint(TELEPORT_TO_HOUSE, List.of("Break"))
                                    ))
                    ))
            ),
            Map.entry("ice warriors", new SlayerTask("Ice Warriors", List.of(NpcID.ICEWARRIOR_LOW_WANDERRANGE, NpcID.ICEWARRIOR), List.of(
                            new WorldPoint(3008, 3150, 0),
                            new WorldPoint(3052, 9581, 0)
                    ), List.of(
                            new NpcLocation("Asgarnian Ice Dungeon", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3037, 9592, 0),
                                            new WorldPoint(3066, 9563, 0)
                                    )
                            ), new String[]{"Fairy Ring: AIQ"},
                                    pohChainFairyRing("AIQ"))
                    ), "**Block**")
            ),
            Map.entry("kalphites", new SlayerTask("Kalphites", List.of(NpcID.KALPHITE_WORKER_STRONGHOLDCAVE), List.of(
                            new WorldPoint(3326, 3122, 0),
                            new WorldPoint(3388, 9504, 0)
                    ), List.of(
                            new NpcLocation("Kalphite Cave", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3314, 9493, 0),
                                            new WorldPoint(3333, 9511, 0)
                                    )
                            ), new String[]{"Desert amulet 4: Kalphite cave"},
                                    List.of(new NavigationHint(DESERT_AMULET, List.of("Kalphite cave"), DESERT_AMULET)))
                    ), new WorldPoint(3323, 9503, 0))
            ),
            Map.entry("killerwatts", new SlayerTask("Killerwatts", List.of(NpcID.SLAYER_KILLERWATT, NpcID.SLAYER_KILLERWATT_BALL), List.of(
                            new WorldPoint(3108, 3361, 0),
                            new WorldPoint(3104, 3364, 1),
                            new WorldPoint(3111, 3363, 2),
                            new WorldPoint(2679, 5213, 0)
                    ), List.of(
                            new NpcLocation("Killerwatt Plane", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2658, 5196, 2),
                                            new WorldPoint(2678, 5220, 2)
                                    ),
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2650, 5215, 0),
                                            new WorldPoint(2696, 5184, 0)
                                    )
                            ), new String[]{"Draynor Manor via Nexus (Option N) ", "Amulet of Glory (Option Q)"})
                    ), "Bring Insulated Boots. **Block**")
            ),
            Map.entry("lizards", new SlayerTask("Lizards", List.of(
                            NpcID.SLAYER_LIZARD_SMALL2_SANDY, NpcID.SLAYER_LIZARD_SMALL1_GREEN,
                            NpcID.SLAYER_LIZARD_MASSIVE, NpcID.SLAYER_LIZARD_LARGE1_GREEN,
                            NpcID.SLAYER_LIZARD_LARGE2_SANDY, NpcID.SLAYER_LIZARD_LARGE3_SANDY), List.of(
                            new WorldPoint(3413, 3035, 0)
                    ), List.of(
                            new NpcLocation("North of Nardah Fairy Ring", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3390, 3049, 0),
                                            new WorldPoint(3441, 3018, 0)
                                    )
                            ), new String[]{"Fairy ring: DLQ"},
                                    pohChainFairyRing("DLQ"))
                    ), "Bring Ice coolers")
            ),
            Map.entry("mogres", new SlayerTask("Mogres", List.of(NpcID.MUDSKIPPER_OGRE), List.of(
                            new WorldPoint(2995, 3109, 0)
                    ), List.of(
                            new NpcLocation("Mudskipper Point", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2993, 3116, 0),
                                            new WorldPoint(2999, 3105, 0)
                                    )
                            ), new String[]{"Fairy Ring: AIQ"},
                                    pohChainFairyRing("AIQ"))
                    ), "Bring Fishing Explosives. **Block**")
            ),
            Map.entry("pyrefiends", new SlayerTask("Pyrefiends", List.of(
                            NpcID.SLAYER_PYREFIEND_1, NpcID.SLAYER_PYREFIEND_2,
                            NpcID.SLAYER_PYREFIEND_3, NpcID.SLAYER_PYREFIEND_4,
                            NpcID.SLAYER_PYREBEAST_1, NpcID.SLAYER_PYREBEAST_2,
                            NpcID.SUPERIOR_PYREFIEND, NpcID.SUPERIOR_PYRELORD), List.of(
                            new WorldPoint(2256, 2964, 0)
                    ), List.of(
                            new NpcLocation("Isle of Souls", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2243, 2971, 0),
                                            new WorldPoint(2264, 2956, 0)
                                    )
                            ), new String[]{"Fairy Ring: BJP"},
                                    pohChainFairyRing("BJP"))
                    ))
            ),
            Map.entry("rockslugs", new SlayerTask("Rockslugs", List.of(NpcID.SUPERIOR_ROCKSLUG, NpcID.SLAYER_ROCKSLUG, NpcID.SLAYER_ROCKSLUG_BABY), List.of(
                            new WorldPoint(2789, 3617, 0),
                            new WorldPoint(2799, 10018, 0)
                    ), List.of(
                            new NpcLocation("Fremennik Slayer Dungeon", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2787, 10024, 0),
                                            new WorldPoint(2809, 10010, 0)
                                    )
                            ), new String[]{"Slayer ring: Fremennik Slayer Dungeon (Option 3)", "Fairy ring: AJR"},
                                    slayerRingFremChain())
                    ), "Bring Bags of Salt")
            ),
            Map.entry("scorpions", new SlayerTask("Scorpions", List.of(NpcID.SCORPION), List.of(
                            new WorldPoint(3061, 3376, 0),
                            new WorldPoint(3049, 9771, 0)
                    ), List.of(
                            new NpcLocation("Mining Guild", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3061, 9784, 0),
                                            new WorldPoint(3031, 9759, 0)
                                    )
                            ), new String[]{"(POH) Skill's Necklace: Mining Guild (Option F)"},
                                    pohChainNoQuestPoh(JEWELLERY_BOX, List.of("Mining Guild", "Teleport Menu")))
                    ))
            ),
            Map.entry("shades", new SlayerTask("Shades", List.of(NpcID.SHADESHADOW_LEVEL1, NpcID.SHADE_LEVEL1), List.of(
                            new WorldPoint(3488, 3275, 0)
                    ), List.of(
                            new NpcLocation("Mort'ton", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3462, 3308, 0),
                                            new WorldPoint(3514, 3262, 0)
                                    )
                            ), new String[]{"Morytania legs 3/4 to Burgh de Rott", "Mort'ton Teleport"},
                                    List.of(new NavigationHint(MORYTANIA_LEGS, List.of("Burgh Teleport"), MORYTANIA_LEGS)))
                    ), "**Block**")
            ),
            Map.entry("skeletons", new SlayerTask("Skeletons", List.of(
                            NpcID.SKELETON_UNARMED, NpcID.SKELETON_UNARMED2,
                            NpcID.SKELETON_UNARMED3, NpcID.SKELETON_UNARMED4), List.of(
                            new WorldPoint(3352, 3417, 0)
                    ), List.of(
                            new NpcLocation("Digsite Dungeon", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3359, 9739, 0),
                                            new WorldPoint(3387, 9763, 0)
                                    )
                            ), new String[]{"(POH) Digsite pendant (Option 1)"},
                                    pohChainNoQuestPoh(MOUNTED_DIGSITE, List.of("Digsite", "Teleport menu")))
                    ), "Bring a rope")
            ),
            Map.entry("vampyres", new SlayerTask("Vampyres", List.of(NpcID.VAMPIRE_JUVE), List.of(
                            new WorldPoint(3457, 3234, 0)
                    ), List.of(
                            new NpcLocation("West of Burgh De Rott", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3468, 3214, 0),
                                            new WorldPoint(3447, 3247, 0)
                                    )
                            ), new String[]{"Morytania legs 3/4 to Burgh de Rott", "Mort'ton Teleport"},
                                    List.of(new NavigationHint(MORYTANIA_LEGS, List.of("Burgh Teleport"), MORYTANIA_LEGS)))
                    ))
            ),
            Map.entry("wall beasts", new SlayerTask("Wall Beasts", List.of(NpcID.SWAMP_WALLBEAST, NpcID.SWAMP_WALLBEAST_COMBAT), List.of(
                            new WorldPoint(3167, 3172, 0),
                            new WorldPoint(3163, 9573, 0)
                    ), List.of(
                            new NpcLocation("Lumbridge Swamp Caves", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3160, 9575, 0),
                                            new WorldPoint(3164, 9572, 0)
                                    )
                            ), new String[]{"Ring of the elements: Water Altar (Option 2) (Bring rope if first visit)"},
                                    List.of(new NavigationHint(RING_OF_ELEMENTS, List.of("Rub", "Water Altar"), RING_OF_ELEMENTS)))
                    ), "Bring a light source. Requires Slayer Helmet. Climb up/down for fast reset. **Block**")
            ),
            Map.entry("wolves", new SlayerTask("Wolves", List.of(NpcID.WHITEWOLF_SENTRY, NpcID.WHITEWOLF, NpcID.WOLFPACK_LEADER_WHITER, NpcID.PACK_WOLF_WHITER), List.of(
                            new WorldPoint(2847, 3498, 0)
                    ), List.of(
                            new NpcLocation("White Wolf Mountain", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2828, 3477, 0),
                                            new WorldPoint(2865, 3519, 0)
                                    )
                            ), new String[]{"Royal Seed Pod to Gnome Glider: Sindarpos", "Spirit Tree to Glider"},
                                    seedPodChain())
                    ))
            ),
            Map.entry("zombies", new SlayerTask("Zombies", List.of(NpcID.AHOY_UNDEAD_COW, NpcID.AHOY_UNDEAD_CHICKEN), List.of(
                            new WorldPoint(3630, 3530, 0)
                    ), List.of(
                            new NpcLocation("Alice's Farm, west of the Ectofuntus", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3608, 3521, 0),
                                            new WorldPoint(3637, 3534, 0)
                                    )
                            ), new String[]{"Ectophial: Empty"},
                                    List.of(new NavigationHint(ECTOPHIAL, List.of("Empty"))))
                    ))
            )
    );

    public static SlayerTask getSlayerTaskByNpcName(String npcName) {
        return MAZCHNA_TASKS.get(npcName);
    }
}