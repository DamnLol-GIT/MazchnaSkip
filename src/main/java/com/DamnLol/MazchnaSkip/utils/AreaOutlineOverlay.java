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
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Polygon;
import java.util.List;

public class AreaOutlineOverlay extends Overlay {
    private final Client client;
    private final MazchnaSkipConfig config;
    private List<WorldArea> areas;
    private boolean useAlternativeOutline;

    @Inject
    public AreaOutlineOverlay(Client client, MazchnaSkipConfig config) {
        this.client = client;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public void setAreas(List<WorldArea> areas) {
        this.areas = areas;
    }

    public void setUseAlternativeOutline(boolean useAlternativeOutline) {
        this.useAlternativeOutline = useAlternativeOutline;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (areas == null || areas.isEmpty()) {
            return null;
        }

        if (useAlternativeOutline) {
            graphics.setColor(Color.ORANGE);
            graphics.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                    new float[]{9}, 0));
        } else {
            graphics.setColor(config.getSlayerAreaOutlineColour());
            graphics.setStroke(new BasicStroke(2));
        }

        for (WorldArea area : areas) {
            drawAreaOutline(graphics, area);
        }

        return null;
    }

    private void drawAreaOutline(Graphics2D graphics, WorldArea area) {
        int minX = area.getX();
        int minY = area.getY();
        int maxX = minX + area.getWidth() - 1;
        int maxY = minY + area.getHeight() - 1;
        int plane = area.getPlane();

        WorldPoint sw = new WorldPoint(minX, minY, plane);
        WorldPoint nw = new WorldPoint(minX, maxY + 1, plane);
        WorldPoint ne = new WorldPoint(maxX + 1, maxY + 1, plane);
        WorldPoint se = new WorldPoint(maxX + 1, minY, plane);

        WorldView worldView = client.getLocalPlayer().getWorldView();

        LocalPoint swLp = LocalPoint.fromWorld(worldView, sw);
        LocalPoint nwLp = LocalPoint.fromWorld(worldView, nw);
        LocalPoint neLp = LocalPoint.fromWorld(worldView, ne);
        LocalPoint seLp = LocalPoint.fromWorld(worldView, se);

        if (swLp == null || nwLp == null || neLp == null || seLp == null)
            return;

        Point swC = Perspective.localToCanvas(client, swLp, plane);
        Point nwC = Perspective.localToCanvas(client, nwLp, plane);
        Point neC = Perspective.localToCanvas(client, neLp, plane);
        Point seC = Perspective.localToCanvas(client, seLp, plane);

        if (swC == null || nwC == null || neC == null || seC == null)
            return;

        Polygon poly = new Polygon();

        poly.addPoint(swC.getX(), swC.getY());
        poly.addPoint(nwC.getX(), nwC.getY());
        poly.addPoint(neC.getX(), neC.getY());
        poly.addPoint(seC.getX(), seC.getY());

        graphics.drawPolygon(poly);
    }
}