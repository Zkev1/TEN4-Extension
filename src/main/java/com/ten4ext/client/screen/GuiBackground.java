package com.ten4ext.client.screen;

import com.hypothetic.ten4.Ten4;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/** Builds device GUI backgrounds from parts of TEN4's GUI textures. */
public final class GuiBackground {
  private static final ResourceLocation BASE = Ten4.id("textures/gui/pulverizer.png");
  private static final ResourceLocation TANKS = Ten4.id("textures/gui/refiner.png");
  private static final int BG = 0xFFCACAD1;

  private final List<int[]> energy = new ArrayList<>();
  private final List<int[]> tanks = new ArrayList<>();
  private final List<int[]> slots = new ArrayList<>();
  private final List<int[]> arrows = new ArrayList<>();

  public GuiBackground energy(int x, int y) {
    energy.add(new int[] {x, y});
    return this;
  }

  public GuiBackground tank(int x, int y) {
    tanks.add(new int[] {x, y});
    return this;
  }

  /** Slot at menu-layout coordinates (the frame sits one pixel up/left). */
  public GuiBackground slot(int x, int y) {
    slots.add(new int[] {x, y});
    return this;
  }

  public GuiBackground arrow(int x, int y) {
    arrows.add(new int[] {x, y});
    return this;
  }

  public void draw(GuiGraphics g, int left, int top) {
    g.blit(BASE, left, top, 0, 0, 176, 167);
    g.fill(left + 5, top + 5, left + 171, top + 80, BG); // clear the pulverizer's own machine area
    for (int[] p : energy) {
      g.blit(BASE, left + p[0], top + p[1], 9, 18, 8, 50);
    }
    for (int[] p : tanks) {
      g.blit(TANKS, left + p[0], top + p[1], 29, 18, 18, 50);
    }
    for (int[] p : slots) {
      g.blit(BASE, left + p[0] - 1, top + p[1] - 1, 43, 34, 18, 18);
    }
    for (int[] p : arrows) {
      g.blit(BASE, left + p[0], top + p[1], 68, 35, 22, 16);
    }
  }
}
