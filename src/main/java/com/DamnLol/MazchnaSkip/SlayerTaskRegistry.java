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

import com.DamnLol.MazchnaSkip.Models.NpcLocation;
import com.DamnLol.MazchnaSkip.Models.SlayerTask;
import com.DamnLol.MazchnaSkip.utils.WorldAreaUtils;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.NpcID;

import java.util.List;
import java.util.Map;

public class SlayerTaskRegistry {
    private final static Map<String, SlayerTask> MAZCHNA_TASKS = Map.ofEntries(
            Map.entry("banshees", new SlayerTask("Banshees", List.of(NpcID.SLAYER_BANSHEE_1, NpcID.SUPERIOR_BANSHEE), List.of(
                            new WorldPoint(3442, 3542, 0)
                    ), List.of(
                            new NpcLocation("Morytania Slayer Tower", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3431, 3530, 0),
                                            new WorldPoint(3452, 3567, 0)
                                    )
                            ), new String[]{"Slayer Ring: Morytania Slayer Tower (Option 2)"})
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
                            ), new String[]{"Ring of the elements: Earth Altar (Option 3)", "Digsite pendant (Option 1)", "Digsite/Lumberyard teleport"})
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
                            ), new String[]{"Fairy ring: BLR", "Quest Cape teleport"})
                    ))
            ),
            Map.entry("catablepons", new SlayerTask("Catablepons", List.of(NpcID.SOS_PEST_CATABLEPON, NpcID.SOS_PEST_CATABLEPON2, NpcID.SOS_PEST_CATABLEPON3), List.of(
                            new WorldPoint(3084, 3417, 0),
                            new WorldPoint(1956, 4997, 0)
                    ), List.of(
                            new NpcLocation("Third level of the Stronghold of Security.", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2139, 5262, 0),
                                            new WorldPoint(2160, 5244, 0)
                                    )
                            ), new String[]{"Skull Sceptre: Invoke"})
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
                            ), new String[]{"Fairy ring: AJQ"})
                    ), "Bring a light source", new WorldPoint(2715, 5235, 0))
            ),
            Map.entry("cave crawlers", new SlayerTask("Cave Crawlers", List.of(
                            NpcID.SLAYER_CAVE_CRAWLER_1,
                            NpcID.SLAYER_CAVE_CRAWLER_2,
                            NpcID.SLAYER_CAVE_CRAWLER_3,
                            NpcID.SLAYER_CAVE_CRAWLER_4,
                            NpcID.SUPERIOR_CAVE_CRAWLER_ICE), List.of(
                            new WorldPoint(2789, 3617, 0),
                            new WorldPoint(2787, 9996, 0)
                    ), List.of(
                            new NpcLocation("Fremennik Slayer Dungeon", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2776, 9990, 0),
                                            new WorldPoint(2812, 10007, 0)
                                    )
                            ), new String[]{"Slayer ring: Fremennik Slayer Dungeon (Option 3)", "Fairy ring: AJR"})
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
                            ), new String[]{"Fairy ring: AJQ"})
                    ), "Bring a light source", new WorldPoint(2730, 5234, 0))
            ),
            Map.entry("cockatrices", new SlayerTask("Cockatrices", List.of(NpcID.SLAYER_COCKATRICE, NpcID.SUPERIOR_COCKATRICE), List.of(
                            new WorldPoint(2789, 3617, 0),
                            new WorldPoint(2792, 10035, 0)
                    ), List.of(
                            new NpcLocation("Fremennik Slayer Dungeon", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2780, 10026, 0),
                                            new WorldPoint(2805, 10041, 0)
                                    )
                            ), new String[]{"Slayer ring: Fremennik Slayer Dungeon (Option 3)", "Fairy ring: AJR"})
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
                            ), new String[]{"Fairy ring: DKS"})
                    ))
            ),
            Map.entry("crawling hands", new SlayerTask("Crawling Hands", List.of(
                            NpcID.SLAYER_CRAWLING_HAND_1,
                            NpcID.SLAYER_CRAWLING_HAND_2,
                            NpcID.SLAYER_CRAWLING_HAND_3,
                            NpcID.SLAYER_CRAWLING_HAND_4,
                            NpcID.SLAYER_CRAWLING_HAND_5,
                            NpcID.SLAYER_CRAWLING_HAND_BIG_1,
                            NpcID.SLAYER_CRAWLING_HAND_BIG_2,
                            NpcID.SLAYER_CRAWLING_HAND_BIG_3,
                            NpcID.SLAYER_CRAWLING_HAND_BIG_4,
                            NpcID.SLAYER_CRAWLING_HAND_BIG_5,
                            NpcID.SUPERIOR_CRAWLING_HAND), List.of(
                            new WorldPoint(3419, 3571, 0)
                    ), List.of(
                            new NpcLocation("Morytania Slayer Tower", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3406, 3578, 0),
                                            new WorldPoint(3433, 3541, 0)
                                    )
                            ), new String[]{"Slayer Ring: Morytania Slayer Tower (Option 2)"})
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
                            ), new String[]{"Desert Amulet (Option 2)", "Fairy Ring DLQ"})
                    ))
            ),
            Map.entry("flesh crawlers", new SlayerTask("Flesh Crawlers", List.of(NpcID.SOS_FAM_FLESHCRAWLER, NpcID.SOS_FAM_FLESHCRAWLER2, NpcID.SOS_FAM_FLESHCRAWLER3), List.of(
                            new WorldPoint(3084, 3417, 0),
                            new WorldPoint(2006, 5202, 0)
                    ), List.of(
                            new NpcLocation("Second Layer of the Stronghold Of Security", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(1997, 5194, 0),
                                            new WorldPoint(2013, 5211, 0)
                                    )
                            ), new String[]{"Skull Sceptre: Invoke"})
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
                            ), new String[]{"Achievement diary cape: Kourend Castle (Option A)", "Xeric's talisman: Xeric's Heart (Option 4)"})
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
                            ), new String[]{"Fairy Ring: CKS"})
                    ))
            ),
            Map.entry("hill giants", new SlayerTask("Hill Giants", List.of(
                            NpcID.GIANT,
                            NpcID.GIANT2,
                            NpcID.GIANT3,
                            NpcID.GIANT4,
                            NpcID.GIANT5,
                            NpcID.GIANT6), List.of(
                            new WorldPoint(1444, 3613, 0)
                    ), List.of(
                            new NpcLocation("Giants Den", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(1437, 3622, 0),
                                            new WorldPoint(1451, 3604, 0)
                                    )
                            ), new String[]{"Fairy Ring: DJR"})
                    ))
            ),
            Map.entry("hobgoblins", new SlayerTask("Hobgoblins", List.of(
                            NpcID.HOBGOBLIN_ARMED,
                            NpcID.HOBGOBLIN_UNARMED,
                            NpcID.RIMMINGTON_HOBGOBLIN_UNARMED_1,
                            NpcID.RIMMINGTON_HOBGOBLIN_UNARMED_2,
                            NpcID.RIMMINGTON_HOBGOBLIN_UNARMED_3,
                            NpcID.RIMMINGTON_HOBGOBLIN_ARMED_1,
                            NpcID.HOBGOBLIN_UNARMED), List.of(
                            new WorldPoint(2909, 3285, 0)
                    ), List.of(
                            new NpcLocation("Hobgoblin Peninsula", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2901, 3299, 0),
                                            new WorldPoint(2921, 3263, 0)
                                    )
                            ), new String[]{"Crafting Cape Teleport", "Skills Necklace (Option G)"})
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
                            ), new String[]{"Fairy Ring: AIQ"})
                    ), "**Block**")
            ),
            Map.entry("kalphites", new SlayerTask("Kalphites", List.of(NpcID.KALPHITE_WORKER_STRONGHOLDCAVE), List.of(
                            new WorldPoint(3326, 3122, 0),
                            new WorldPoint(3372, 9528, 0),
                            new WorldPoint(3324, 9502, 0)
                    ), List.of(
                            new NpcLocation("Kalphite Cave", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3314, 9493, 0),
                                            new WorldPoint(3333, 9511, 0)
                                    ),
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3297, 9513, 0),
                                            new WorldPoint(3319, 9539, 0)
                                    )
                            ), new String[]{"Desert amulet 4: Kalphite cave"})
                    ), new WorldPoint(3309, 9526, 0))
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
                            NpcID.SLAYER_LIZARD_SMALL2_SANDY,
                            NpcID.SLAYER_LIZARD_SMALL1_GREEN,
                            NpcID.SLAYER_LIZARD_MASSIVE,
                            NpcID.SLAYER_LIZARD_LARGE1_GREEN,
                            NpcID.SLAYER_LIZARD_LARGE2_SANDY,
                            NpcID.SLAYER_LIZARD_LARGE3_SANDY), List.of(
                            new WorldPoint(3413, 3035, 0)
                    ), List.of(
                            new NpcLocation("North of Nardah Fairy Ring", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3402, 3049, 0),
                                            new WorldPoint(3441, 3022, 0)
                                    )
                            ), new String[]{"Fairy ring: DLQ"})
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
                            ), new String[]{"Fairy Ring: AIQ"})
                    ), "Bring Fishing Explosives. **Block**")
            ),
            Map.entry("pyrefiends", new SlayerTask("Pyrefiends", List.of(
                            NpcID.SLAYER_PYREFIEND_1,
                            NpcID.SLAYER_PYREFIEND_2,
                            NpcID.SLAYER_PYREFIEND_3,
                            NpcID.SLAYER_PYREFIEND_4,
                            NpcID.SLAYER_PYREBEAST_1,
                            NpcID.SLAYER_PYREBEAST_2,
                            NpcID.SUPERIOR_PYREFIEND,
                            NpcID.SUPERIOR_PYRELORD), List.of(
                            new WorldPoint(2256, 2964, 0)
                    ), List.of(
                            new NpcLocation("Isle of Souls", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(2243, 2971, 0),
                                            new WorldPoint(2264, 2956, 0)
                                    )
                            ), new String[]{"Fairy Ring: BJP"})
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
                            ), new String[]{"Slayer ring: Fremennik Slayer Dungeon (Option 3)", "Fairy ring: AJR"})
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
                            ), new String[]{"Skill's Necklace: Mining Guild (Option F)"})
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
                            ), new String[]{"Morytania legs 3/4 to Burgh de Rott", "Mort'ton Teleport"})
                    ), "**Block**")
            ),
            Map.entry("skeletons", new SlayerTask("Skeletons", List.of(
                            NpcID.SKELETON_UNARMED,
                            NpcID.SKELETON_UNARMED2,
                            NpcID.SKELETON_UNARMED3,
                            NpcID.SKELETON_UNARMED4
                    ), List.of(
                            new WorldPoint(3352, 3417, 0)
                    ), List.of(
                            new NpcLocation("Digsite Dungeon", List.of(
                                    WorldAreaUtils.fromCorners(
                                            new WorldPoint(3359, 9739, 0),
                                            new WorldPoint(3387, 9757, 0)
                                    )
                            ), new String[]{"Digsite pendant (Option 1)", "Digsite teleport"})
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
                            ), new String[]{"Morytania legs 3/4 to Burgh de Rott", "Mort'ton Teleport"})
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
                            ), new String[]{"Lumbridge tele and run South to the Swamps. Run West to the Cave entrance. (Bring rope if first visit)"})
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
                            ), new String[]{"Royal Seed Pod to Gnome Glider: Sindarpos"})
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
                            ), new String[]{"Ectophial: Empty"})
                    ))
            )
    );

    public static SlayerTask getSlayerTaskByNpcName(String npcName) {
        return MAZCHNA_TASKS.get(npcName);
    }
}