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
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeleItemOverlay extends Overlay {

    private static final float BORDER_ALPHA = 0.90f;
    private static final int BORDER_RADIUS = 2;
    private static final float FILL_ALPHA = 0.10f;
    private static final int ALPHA_THRESHOLD = 30;

    private final Client client;
    private final MazchnaSkipConfig config;
    private final ItemManager itemManager;

    private final SpriteManager spriteManager;
    private final Map<Integer, BufferedImage[]> imageCache = new HashMap<>();
    private Color cachedColor = null;

    @Setter
    private List<Integer> highlightItemIds = Collections.emptyList();

    @Setter
    private List<Integer> equippedItemIds = Collections.emptyList();

    @Getter
    @Setter
    private boolean active = false;

    @Inject
    public TeleItemOverlay(Client client, MazchnaSkipConfig config, ItemManager itemManager, SpriteManager spriteManager) {
        this.client = client;
        this.config = config;
        this.itemManager = itemManager;
        this.spriteManager = spriteManager;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!active) {
            return null;
        }

        Color color = config.getTeleColor();
        if (!color.equals(cachedColor)) {
            imageCache.clear();
            cachedColor = color;
        }

        Composite original = graphics.getComposite();
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        if (!highlightItemIds.isEmpty() && client.getDraggedWidget() == null) {
            renderInventoryHighlights(graphics, color);
        }

        if (!equippedItemIds.isEmpty()) {
            renderEquipmentHighlight(graphics, color);
        }

        graphics.setComposite(original);
        return null;
    }


    private void renderInventoryHighlights(Graphics2D graphics, Color color) {
        ItemContainer inventory = client.getItemContainer(InventoryID.INV);
        if (inventory == null) {
            return;
        }

        Widget inventoryWidget = client.getWidget(InterfaceID.Inventory.ITEMS);
        if (inventoryWidget == null || inventoryWidget.isHidden()) {
            return;
        }

        Widget[] slots = inventoryWidget.getDynamicChildren();
        if (slots == null) {
            return;
        }

        Item[] items = inventory.getItems();
        for (int i = 0; i < items.length && i < slots.length; i++) {
            int itemId = items[i].getId();
            if (!highlightItemIds.contains(itemId)) {
                continue;
            }

            Rectangle bounds = slots[i].getBounds();
            if (bounds == null || bounds.width <= 0) {
                continue;
            }

            BufferedImage[] highlight = getOrBuildHighlight(itemId, items[i].getQuantity(), color);
            if (highlight == null) {
                continue;
            }

            graphics.drawImage(highlight[1], bounds.x, bounds.y, null);
            graphics.drawImage(highlight[0], bounds.x, bounds.y, null);
        }
    }


    private void renderEquipmentHighlight(Graphics2D graphics, Color color) {
        ItemContainer equipment = client.getItemContainer(InventoryID.WORN);
        if (equipment == null) {
            return;
        }

        int foundItemId = -1;
        int foundQuantity = 1;
        for (Item item : equipment.getItems()) {
            if (equippedItemIds.contains(item.getId())) {
                foundItemId = item.getId();
                foundQuantity = Math.max(1, item.getQuantity());
                break;
            }
        }

        if (foundItemId == -1) {
            return;
        }


        Widget side4 = client.getWidget(InterfaceID.TOPLEVEL_OSRS_STRETCH, 80);
        if (side4 == null) {
            side4 = client.getWidget(InterfaceID.TOPLEVEL_PRE_EOC, 80);
        }
        if (side4 == null) {
            side4 = client.getWidget(InterfaceID.TOPLEVEL, 80);
        }
        boolean equipTabOpen = side4 != null && !side4.isHidden();

        if (!equipTabOpen) {
            highlightEquipmentTabIcon(graphics, color);
        } else {
            highlightEquipmentSlot(graphics, color, foundItemId, foundQuantity);
        }
    }

    private void highlightEquipmentTabIcon(Graphics2D graphics, Color color) {
        Widget stone = client.getWidget(InterfaceID.TOPLEVEL_OSRS_STRETCH, 63);
        if (stone == null) stone = client.getWidget(InterfaceID.TOPLEVEL_PRE_EOC, 63);
        if (stone == null) stone = client.getWidget(InterfaceID.TOPLEVEL, 63);
        if (stone == null) return;

        Rectangle sb = stone.getBounds();
        if (sb == null || sb.width <= 0 || sb.height <= 0) return;

        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 160));
        graphics.fillRect(sb.x, sb.y, sb.width, sb.height);
    }

    private void highlightEquipmentSlot(Graphics2D graphics, Color color, int itemId, int quantity) {
        Widget equipSideCheck = client.getWidget(InterfaceID.TOPLEVEL_OSRS_STRETCH, 80);
        if (equipSideCheck == null) equipSideCheck = client.getWidget(InterfaceID.TOPLEVEL_PRE_EOC, 80);
        if (equipSideCheck == null) equipSideCheck = client.getWidget(InterfaceID.TOPLEVEL, 80);
        if (equipSideCheck == null || equipSideCheck.isHidden()) {
            return;
        }

        Widget equipPanel = client.getWidget(InterfaceID.Wornitems.UNIVERSE);
        if (equipPanel == null) {
            return;
        }

        Widget[] children = equipPanel.getDynamicChildren();
        if (children == null || children.length == 0) {
            // Fallback
            children = equipPanel.getStaticChildren();
        }
        if (children == null) {
            return;
        }

        BufferedImage[] highlight = getOrBuildHighlight(itemId, quantity, color);
        if (highlight == null) {
            return;
        }

        for (Widget child : children) {
            if (child == null || child.isHidden()) {
                continue;
            }

            if (child.getItemId() == itemId) {
                Rectangle bounds = child.getBounds();
                if (bounds != null && bounds.width > 0) {
                    graphics.drawImage(highlight[1], bounds.x, bounds.y, null);
                    graphics.drawImage(highlight[0], bounds.x, bounds.y, null);
                }
                continue;
            }

            Widget[] grandchildren = child.getDynamicChildren();
            if (grandchildren == null) continue;
            for (Widget gc : grandchildren) {
                if (gc != null && !gc.isHidden() && gc.getItemId() == itemId) {
                    Rectangle bounds = gc.getBounds();
                    if (bounds != null && bounds.width > 0) {
                        graphics.drawImage(highlight[1], bounds.x, bounds.y, null);
                        graphics.drawImage(highlight[0], bounds.x, bounds.y, null);
                    }
                }
            }
        }
    }

    private BufferedImage[] getOrBuildHighlightFromSprite(BufferedImage sprite, Color color) {
        return new BufferedImage[]{
                buildBorder(sprite, color),
                buildFill(sprite, color)
        };
    }

    private BufferedImage[] getOrBuildHighlight(int itemId, int quantity, Color color) {
        if (imageCache.containsKey(itemId)) {
            return imageCache.get(itemId);
        }

        BufferedImage sprite = itemManager.getImage(itemId, quantity, false);
        if (sprite == null) {
            return null;
        }

        BufferedImage[] pair = new BufferedImage[]{
                buildBorder(sprite, color),
                buildFill(sprite, color)
        };

        imageCache.put(itemId, pair);
        return pair;
    }

    private BufferedImage buildBorder(BufferedImage source, Color color) {
        int w = source.getWidth();
        int h = source.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        int packed = packARGB(color, TeleItemOverlay.BORDER_ALPHA);
        int r = BORDER_RADIUS;

        for (int px = 0; px < w; px++) {
            for (int py = 0; py < h; py++) {
                if (alphaOf(source, px, py) > ALPHA_THRESHOLD) {
                    continue;
                }

                outer:
                for (int dx = -r; dx <= r; dx++) {
                    for (int dy = -r; dy <= r; dy++) {
                        if (dx == 0 && dy == 0) continue;
                        int nx = px + dx;
                        int ny = py + dy;
                        if (nx < 0 || ny < 0 || nx >= w || ny >= h) continue;
                        if (alphaOf(source, nx, ny) > ALPHA_THRESHOLD) {
                            out.setRGB(px, py, packed);
                            break outer;
                        }
                    }
                }
            }
        }

        return out;
    }

    private BufferedImage buildFill(BufferedImage source, Color color) {
        int w = source.getWidth();
        int h = source.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        int packed = packARGB(color, TeleItemOverlay.FILL_ALPHA);

        for (int px = 0; px < w; px++) {
            for (int py = 0; py < h; py++) {
                if (alphaOf(source, px, py) > ALPHA_THRESHOLD) {
                    out.setRGB(px, py, packed);
                }
            }
        }

        return out;
    }

    private static int alphaOf(BufferedImage img, int x, int y) {
        return (img.getRGB(x, y) >> 24) & 0xFF;
    }

    private static int packARGB(Color color, float alpha) {
        int a = Math.round(alpha * 255);
        return (a << 24) | (color.getRed() << 16) | (color.getGreen() << 8) | color.getBlue();
    }
}