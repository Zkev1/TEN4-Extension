package com.ten4ext.mixin.client;

import com.hypothetic.ten4.api.client.components.Button;
import com.hypothetic.ten4.core.client.builtin.IoFlagReader;
import com.hypothetic.ten4.core.client.builtin.IoTypeButton;
import com.ten4ext.client.io.IoStateExt;
import com.ten4ext.client.io.IoStateHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** Only cycle through types the device has. Right-click goes back. */
@Mixin(IoTypeButton.class)
public abstract class IoTypeButtonMixin extends Button implements IoStateHolder {
  @Shadow
  @Final
  IoFlagReader state;

  protected IoTypeButtonMixin(int x, int y, int w, int h) {
    super(x, y, w, h);
  }

  @Override
  public IoFlagReader ten4ext$state() {
    return state;
  }

  @Override
  public void onMouseClicked(int mouseX, int mouseY, int button) {
    if (soundOnClicked != null) {
      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(soundOnClicked, 1.0F));
    }
    ((IoStateExt) (Object) state).ten4ext$cycleType(button == 1 ? -1 : 1);
  }
}
