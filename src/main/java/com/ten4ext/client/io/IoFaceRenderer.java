package com.ten4ext.client.io;

import com.hypothetic.ten4.api.blockentity.device.FaceMode;
import com.hypothetic.ten4.core.client.builtin.BuiltinComponents;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.List;

/** Draws a device side in the IO panel with the block's real texture, tinted by IO mode. */
public final class IoFaceRenderer {
  private static final RandomSource RANDOM = RandomSource.create();

  private IoFaceRenderer() {
  }

  public static int modeColor(FaceMode mode) {
    return switch (mode) {
      case NONE -> 0xFF262626;
      case INPUT -> 0xFF3A78E8;
      case OUTPUT -> 0xFFE8822A;
      case BIPASS -> 0xFF46B446;
    };
  }

  public static void render(GuiGraphics g, int x, int y, BlockState state, Direction side, FaceMode mode, boolean hovered) {
    int color = modeColor(mode);
    g.fill(x - 1, y - 1, x + 17, y + 17, color);

    // every layer on that side (base + overlay)
    RenderSystem.enableBlend();
    for (TextureAtlasSprite sprite : faceSprites(state, side)) {
      g.blit(x, y, 0, 16, 16, sprite);
    }
    RenderSystem.disableBlend();

    if (mode == FaceMode.NONE) {
      g.fill(x, y, x + 16, y + 16, 0xA0101010);
    } else {
      g.fill(x, y, x + 16, y + 16, (color & 0x00FFFFFF) | 0x48000000);
    }
    // TEN4's mode icon in the corner
    g.blit(BuiltinComponents.TEXTURE, x + 9, y + 8, 52 + 9 * mode.ordinal(), 218, 7, 8, 256, 256);

    if (hovered) {
      g.fill(x, y, x + 16, y + 16, 0x40FFFFFF);
    }
  }

  public static List<TextureAtlasSprite> faceSprites(BlockState state, Direction side) {
    BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
    List<TextureAtlasSprite> sprites = new ArrayList<>();
    for (BakedQuad q : model.getQuads(state, side, RANDOM, ModelData.EMPTY, null)) {
      sprites.add(q.getSprite());
    }
    if (sprites.isEmpty()) {
      for (BakedQuad q : model.getQuads(state, null, RANDOM, ModelData.EMPTY, null)) {
        if (q.getDirection() == side) {
          sprites.add(q.getSprite());
        }
      }
    }
    if (sprites.isEmpty()) {
      sprites.add(model.getParticleIcon(ModelData.EMPTY));
    }
    return sprites;
  }
}
