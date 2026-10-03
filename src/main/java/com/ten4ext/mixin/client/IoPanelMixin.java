package com.ten4ext.mixin.client;

import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.api.client.components.Panel;
import com.hypothetic.ten4.api.client.components.UiComponent;
import com.hypothetic.ten4.api.client.gui.TextureRegion;
import com.hypothetic.ten4.api.container.ContainerMenu;
import com.hypothetic.ten4.core.client.builtin.IoFlagReader;
import com.hypothetic.ten4.core.client.builtin.IoPanel;
import com.ten4ext.client.io.IoStateExt;
import com.ten4ext.client.io.IoStateHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Tells the IO model which device it configures and starts on a resource type the device actually has. */
@Mixin(IoPanel.class)
public abstract class IoPanelMixin extends Panel implements IoStateHolder {
  @Unique
  private IoFlagReader ten4ext$state;

  protected IoPanelMixin(TextureRegion tabBg, int w, int h, int side) {
    super(tabBg, w, h, side);
  }

  @Inject(method = "<init>", at = @At("RETURN"))
  private void ten4ext$bindDevice(ComponentedContainerScreen<ContainerMenu> screen, TextureRegion tabBg, int type, CallbackInfo ci) {
    for (UiComponent child : children) {
      if (child instanceof IoStateHolder holder) {
        ten4ext$state = holder.ten4ext$state();
        IoStateExt state = (IoStateExt) (Object) ten4ext$state;
        state.ten4ext$setDevice(screen.getMenu().getBlockEntity());
        if (!IoStateExt.supports(state.ten4ext$getDevice(), state.ten4ext$getType())) {
          state.ten4ext$cycleType(1);
        }
        return;
      }
    }
  }

  @Override
  public IoFlagReader ten4ext$state() {
    return ten4ext$state;
  }
}
