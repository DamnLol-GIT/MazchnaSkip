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

import java.util.Collections;
import java.util.List;

@Getter
public class NavigationHint {

    private final List<Integer> itemIds;
    private final List<String> menuOptions;
    private final List<Integer> equippedItemIds;
    private final List<Integer> pohObjectIds;
    private final List<String> pohObjectMenuOptions;

    public NavigationHint(List<Integer> itemIds, List<String> menuOptions) {
        this(itemIds, menuOptions, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    public NavigationHint(List<Integer> itemIds, List<String> menuOptions, List<Integer> equippedItemIds) {
        this(itemIds, menuOptions, equippedItemIds, Collections.emptyList(), Collections.emptyList());
    }

    public NavigationHint(List<Integer> itemIds, List<String> menuOptions, List<Integer> equippedItemIds, List<Integer> pohObjectIds, List<String> pohObjectMenuOptions) {
        this.itemIds = itemIds != null ? itemIds : Collections.emptyList();
        this.menuOptions = menuOptions != null ? menuOptions : Collections.emptyList();
        this.equippedItemIds = equippedItemIds != null ? equippedItemIds : Collections.emptyList();
        this.pohObjectIds = pohObjectIds != null ? pohObjectIds : Collections.emptyList();
        this.pohObjectMenuOptions = pohObjectMenuOptions != null ? pohObjectMenuOptions : Collections.emptyList();
    }

    public int getPohObjectId() {
        return pohObjectIds.isEmpty() ? -1 : pohObjectIds.get(0);
    }
}