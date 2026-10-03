package com.ten4ext.mixin;

import com.hypothetic.ten4.api.network.device.SigmodePayload;
import com.ten4ext.device.DeviceAccess;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Only apply if the player has the device open. */
@Mixin(SigmodePayload.class)
public abstract class SigmodePayloadMixin {
  @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
  private static void ten4ext$validate(SigmodePayload pkt, IPayloadContext ctx, CallbackInfo ci) {
    if (!DeviceAccess.canConfigure(ctx.player(), pkt.pos())) {
      ci.cancel();
    }
  }
}
