package com.ten4ext.mixin;

import com.hypothetic.ten4.api.block.BridgedEntityBlock;
import com.hypothetic.ten4.core.block.VoidUnitBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** The Void Unit had no ticker, so it never voided anything. */
@Mixin(VoidUnitBlock.class)
public abstract class VoidUnitBlockMixin {
  @Inject(method = "<init>", at = @At("RETURN"))
  private void ten4ext$enableTicking(BlockBehaviour.Properties props, CallbackInfo ci) {
    ((BridgedEntityBlock) (Object) this).tickServer();
  }
}
