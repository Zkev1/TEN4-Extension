package com.ten4ext.mixin;

import com.hypothetic.ten4.api.blockentity.device.AbstractDeviceBlockEntity;
import com.hypothetic.ten4.core.item.WrenchItem;
import com.ten4ext.device.DeviceAccess;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Fix: sneak-wrenching dismantled private devices owned by other players. */
@Mixin(WrenchItem.class)
public abstract class WrenchItemMixin {
  @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
  private void ten4ext$respectPrivate(UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir) {
    Player player = ctx.getPlayer();
    if (player != null && !ctx.getLevel().isClientSide()
        && ctx.getLevel().getBlockEntity(ctx.getClickedPos()) instanceof AbstractDeviceBlockEntity device
        && DeviceAccess.isLockedFor(device, player)) {
      DeviceAccess.denyMessage(player);
      cir.setReturnValue(InteractionResult.FAIL);
    }
  }
}
