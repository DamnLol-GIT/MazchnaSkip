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
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Perspective;
import net.runelite.api.TileObject;
import net.runelite.api.WallObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class POHObjectOverlay extends Overlay {

    private final Client client;
    private final MazchnaSkipConfig config;

    private final Set<TileObject> trackedObjects = new HashSet<>();

    @Setter
    private List<Integer> highlightObjectIds = Collections.emptyList();

    @Setter
    private List<String> pohUiTargetTexts = Collections.emptyList();

    @Setter
    private boolean active = false;

    @Inject
    public POHObjectOverlay(Client client, MazchnaSkipConfig config) {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }


    private boolean objectIdMatches(TileObject obj) {
        if (obj == null || highlightObjectIds.isEmpty()) return false;
        if (highlightObjectIds.contains(obj.getId())) return true;
        ObjectComposition comp = client.getObjectDefinition(obj.getId());
        if (comp != null && comp.getImpostorIds() != null) {
            for (int impostorId : comp.getImpostorIds()) {
                if (highlightObjectIds.contains(impostorId)) return true;
            }
        }
        return false;
    }

    private void trackIfMatch(TileObject obj) {
        if (objectIdMatches(obj)) {
            trackedObjects.add(obj);
        }
    }

    public void trackObject(GameObject obj) { trackIfMatch(obj); }
    public void trackWallObject(WallObject obj) { trackIfMatch(obj); }
    public void trackDecorativeObject(DecorativeObject obj) { trackIfMatch(obj); }
    public void trackGroundObject(GroundObject obj) { trackIfMatch(obj); }

    public void untrackObject(GameObject obj) { trackedObjects.remove(obj); }
    public void untrackWallObject(WallObject obj) { trackedObjects.remove(obj); }
    public void untrackDecorativeObject(DecorativeObject obj) { trackedObjects.remove(obj); }
    public void untrackGroundObject(GroundObject obj) { trackedObjects.remove(obj); }

    public void clearTracked() {
        trackedObjects.clear();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!active) {
            return null;
        }

        Color color = config.getTeleColor();
        renderObjectHighlights(graphics, color);
        renderWidgetHighlights(graphics, color);
        return null;
    }

    private void renderObjectHighlights(Graphics2D graphics, Color color) {
        if (highlightObjectIds.isEmpty() || trackedObjects.isEmpty()) {
            return;
        }

        Color fill = new Color(color.getRed(), color.getGreen(), color.getBlue(), 50);
        Color border = new Color(color.getRed(), color.getGreen(), color.getBlue(), 220);

        for (TileObject obj : trackedObjects) {
            if (!objectIdMatches(obj)) {
                continue;
            }

            Shape clickbox = obj.getClickbox();
            if (clickbox != null) {
                try {
                    graphics.setColor(fill);
                    graphics.fill(clickbox);
                    graphics.setColor(border);
                    graphics.setStroke(new BasicStroke(2f));
                    graphics.draw(clickbox);
                } catch (Exception e) {
                    LocalPoint lp = obj.getLocalLocation();
                    if (lp != null) {
                        Polygon tilePoly = Perspective.getCanvasTilePoly(client, lp);
                        if (tilePoly != null) {
                            graphics.setColor(fill);
                            graphics.fill(tilePoly);
                            graphics.setColor(border);
                            graphics.setStroke(new BasicStroke(2f));
                            graphics.draw(tilePoly);
                        }
                    }
                }
                continue;
            }

            LocalPoint lp = obj.getLocalLocation();
            if (lp == null) continue;

            Polygon tilePoly = Perspective.getCanvasTilePoly(client, lp);
            if (tilePoly == null) continue;

            graphics.setColor(fill);
            graphics.fill(tilePoly);
            graphics.setColor(border);
            graphics.setStroke(new BasicStroke(2f));
            graphics.draw(tilePoly);
        }
    }

    private void renderWidgetHighlights(Graphics2D graphics, Color color) {
        if (pohUiTargetTexts.isEmpty()) {
            return;
        }

        Color fill = new Color(color.getRed(), color.getGreen(), color.getBlue(), 60);
        Color border = new Color(color.getRed(), color.getGreen(), color.getBlue(), 200);

        Widget fairyLogContents = client.getWidget(InterfaceID.FairyringsLog.CONTENTS);
        if (fairyLogContents != null) {
            Widget[] rows = fairyLogContents.getDynamicChildren();
            if (rows != null) {
                for (Widget row : rows) {
                    if (row == null) continue;
                    String rawText = row.getText();
                    if (rawText == null || rawText.isEmpty()) continue;
                    String cleanedNoSpaces = Text.removeTags(rawText).trim().replaceAll("\\s+", "");
                    for (String target : pohUiTargetTexts) {
                        if (cleanedNoSpaces.contains(target.replaceAll("\\s+", ""))) {
                            Rectangle containerBounds = fairyLogContents.getBounds();
                            if (containerBounds != null && containerBounds.width > 0) {
                                int x = containerBounds.x;
                                int y = containerBounds.y + row.getRelativeY() - fairyLogContents.getScrollY();
                                int w = containerBounds.width;
                                int h = Math.max(row.getHeight(), 28);
                                // Only draw if within the visible container area
                                if (y >= containerBounds.y && y < containerBounds.y + containerBounds.height) {
                                    graphics.setColor(fill);
                                    graphics.fillRect(x, y, w, h);
                                    graphics.setColor(border);
                                    graphics.setStroke(new BasicStroke(1.5f));
                                    graphics.drawRect(x, y, w, h);
                                }
                            }
                            break;
                        }
                    }
                }
            }
            return;
        }

        // Fall back to scanning all roots for other POH UI
        Widget[] roots = client.getWidgetRoots();
        if (roots == null) return;

        for (Widget root : roots) {
            if (root == null || root.isHidden()) continue;
            scanWidget(graphics, root, fill, border);
        }
    }

    private void scanWidget(Graphics2D graphics, Widget widget, Color fill, Color border) {
        if (widget == null || widget.isHidden()) {
            return;
        }

        String rawText = widget.getText();
        if (rawText != null && !rawText.isEmpty()) {
            String cleaned = Text.removeTags(rawText).trim();
            String cleanedNoSpaces = cleaned.replaceAll("\\s+", "");
            for (String target : pohUiTargetTexts) {
                String targetNoSpaces = target.replaceAll("\\s+", "");
                if (cleaned.contains(target) || cleanedNoSpaces.contains(targetNoSpaces)) {
                    Rectangle bounds = widget.getBounds();
                    if (bounds != null && bounds.width > 0 && bounds.height > 0) {
                        int rowHeight = Math.max(bounds.height, 28);
                        graphics.setColor(fill);
                        graphics.fillRect(bounds.x, bounds.y, bounds.width, rowHeight);
                        graphics.setColor(border);
                        graphics.setStroke(new BasicStroke(1.5f));
                        graphics.drawRect(bounds.x, bounds.y, bounds.width, rowHeight);
                    }
                    break;
                }
            }
        }

        Widget[] children = widget.getStaticChildren();
        if (children != null) {
            for (Widget child : children) {
                scanWidget(graphics, child, fill, border);
            }
        }

        Widget[] dynamic = widget.getDynamicChildren();
        if (dynamic != null) {
            for (Widget child : dynamic) {
                scanWidget(graphics, child, fill, border);
            }
        }
    }
}