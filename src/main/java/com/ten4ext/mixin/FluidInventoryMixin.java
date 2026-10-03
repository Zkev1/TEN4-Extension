package com.ten4ext.mixin;

import com.hypothetic.ten4.api.capability.fluid.FluidInventory;
import com.hypothetic.ten4.api.capability.fluid.FluidTank;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/** Simulated fills now account for earlier tanks, so transfers don't lose fluid. */
@Mixin(FluidInventory.class)
public abstract class FluidInventoryMixin {
  @Shadow
  @Final
  private List<FluidTank> tanks;

  @Inject(method = "fill", at = @At("HEAD"), cancellable = true)
  private void ten4ext$fill(FluidStack resource, IFluidHandler.FluidAction action, CallbackInfoReturnable<Integer> cir) {
    if (resource.isEmpty()) {
      cir.setReturnValue(0);
      return;
    }
    FluidStack remaining = resource.copy();
    int total = 0;
    for (FluidTank tank : tanks) {
      if (remaining.isEmpty()) {
        break;
      }
      if (!tank.isFluidValid(remaining)) {
        continue;
      }
      int filled = tank.fill(remaining, action);
      if (filled > 0) {
        total += filled;
        remaining.shrink(filled);
      }
    }
    cir.setReturnValue(total);
  }
}
