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
package com.DamnLol.MazchnaSkip.Models;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.List;

@Getter
public class SlayerTask {
    private final String name;
    private final List<Integer> npcIds;
    private final List<WorldPoint> worldMapLocations;
    private final List<NpcLocation> locations;
    private final String information;
    private final WorldPoint shortestPathWorldPoint;

    public SlayerTask(String name, List<Integer> npcIds, List<WorldPoint> worldMapLocations, List<NpcLocation> locations) {
        this.name = name;
        this.npcIds = npcIds;
        this.worldMapLocations = worldMapLocations;
        this.locations = locations;
        this.information = null;
        this.shortestPathWorldPoint = worldMapLocations.get(worldMapLocations.size() - 1);
    }

    public SlayerTask(String name, List<Integer> npcIds, List<WorldPoint> worldMapLocations, List<NpcLocation> locations, String information) {
        this.name = name;
        this.npcIds = npcIds;
        this.worldMapLocations = worldMapLocations;
        this.locations = locations;
        this.information = information;
        this.shortestPathWorldPoint = worldMapLocations.get(worldMapLocations.size() - 1);
    }

    public SlayerTask(String name, List<Integer> npcIds, List<WorldPoint> worldMapLocations, List<NpcLocation> locations, WorldPoint shortestPathWorldPoint) {
        this.name = name;
        this.npcIds = npcIds;
        this.worldMapLocations = worldMapLocations;
        this.locations = locations;
        this.information = null;
        this.shortestPathWorldPoint = shortestPathWorldPoint;
    }

    public SlayerTask(String name, List<Integer> npcIds, List<WorldPoint> worldMapLocations, List<NpcLocation> locations, String information, WorldPoint shortestPathWorldPoint) {
        this.name = name;
        this.npcIds = npcIds;
        this.worldMapLocations = worldMapLocations;
        this.locations = locations;
        this.information = information;
        this.shortestPathWorldPoint = shortestPathWorldPoint;
    }
}