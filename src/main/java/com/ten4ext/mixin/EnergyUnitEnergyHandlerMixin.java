package com.ten4ext.mixin;

import com.hypothetic.ten4.core.capability.EnergyUnitEnergyHandler;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Same stack size dupe as the Liquid Unit. */
@Mixin(EnergyUnitEnergyHandler.class)
public abstract class EnergyUnitEnergyHandlerMixin {
  @Shadow
  @Final
  private ItemStack stack;

  @Inject(method = {"receiveEnergy", "extractEnergy"}, at = @At("HEAD"), cancellable = true)
  private void ten4ext$singleItemOnly(int amount, boolean simulate, CallbackInfoReturnable<Integer> cir) {
    if (stack.getCount() != 1) {
      cir.setReturnValue(0);
    }
  }
}
