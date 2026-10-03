package com.ten4ext.mixin.client;

import com.hypothetic.ten4.api.blockentity.device.FaceMode;
import com.hypothetic.ten4.api.blockentity.device.FaceModePacker;
import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.api.client.components.Button;
import com.hypothetic.ten4.api.client.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.api.container.ContainerMenu;
import com.hypothetic.ten4.api.network.device.IoFacePayload;
import com.hypothetic.ten4.core.client.builtin.IoFaceButton;
import com.hypothetic.ten4.core.client.builtin.IoFlagReader;
import com.ten4ext.Ten4Ext;
import com.ten4ext.client.io.IoFaceRenderer;
import com.ten4ext.client.io.IoStateExt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/** IO sides show the block texture and mode color. Right-click goes back, middle-click turns off. */
@Mixin(IoFaceButton.class)
public abstract class IoFaceButtonMixin extends Button {
  @Shadow
  @Final
  Direction dir;
  @Shadow
  @Final
  ComponentedContainerScreen<ContainerMenu> screen;
  @Shadow
  @Final
  IoFlagReader state;

  protected IoFaceButtonMixin(int x, int y, int w, int h) {
    super(x, y, w, h);
  }

  @Inject(method = "onRender", at = @At("HEAD"), cancellable = true)
  private void ten4ext$render(EnhancedGuiGraphics g, float pt, CallbackInfo ci) {
    ci.cancel();
    IoFaceRenderer.render(g.inner(), x, y, screen.getMenu().getBlockEntity().getBlockState(), dir, ten4ext$mode(), hovering);
  }

  @Inject(method = "onCollectingTooltips", at = @At("TAIL"))
  private void ten4ext$hint(List<Component> tooltips, int mx, int my, CallbackInfo ci) {
    tooltips.add(Component.translatable(Ten4Ext.lang("io.hint")).withStyle(ChatFormatting.DARK_GRAY));
  }

  @Override
  public void onMouseClicked(int mouseX, int mouseY, int button) {
    if (button != 1 && button != 2) {
      super.onMouseClicked(mouseX, mouseY, button);
      return;
    }
    if (soundOnClicked != null) {
      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(soundOnClicked, 1.0F));
    }
    IoStateExt s = (IoStateExt) (Object) state;
    int type = s.ten4ext$getType();
    int packed = s.ten4ext$packedFaces(type);
    FaceMode current = FaceModePacker.get(packed, dir);
    int count = FaceMode.values().length;
    FaceMode next = button == 2 ? FaceMode.NONE : FaceMode.of((current.ordinal() + count - 1) % count);
    PacketDistributor.sendToServer(new IoFacePayload(screen.getMenu().getBlockEntity().getBlockPos(), type,
        FaceModePacker.set(packed, dir, next)));
  }

  private FaceMode ten4ext$mode() {
    IoStateExt s = (IoStateExt) (Object) state;
    return FaceModePacker.get(s.ten4ext$packedFaces(s.ten4ext$getType()), dir);
  }
}
