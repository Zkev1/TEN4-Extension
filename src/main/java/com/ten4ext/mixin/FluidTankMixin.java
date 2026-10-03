package com.ten4ext.mixin;

import com.hypothetic.ten4.api.capability.fluid.FluidTank;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.IntSupplier;

/** Over-full tanks (after removing augments) just accept nothing. */
@Mixin(FluidTank.class)
public abstract class FluidTankMixin {
  @Shadow
  protected FluidStack fluid;
  @Shadow
  protected IntSupplier capacity;

  @Inject(method = "fill(Lnet/neoforged/neoforge/fluids/FluidStack;Lnet/neoforged/neoforge/fluids/capability/IFluidHandler$FluidAction;Z)I",
      at = @At("HEAD"), cancellable = true)
  private void ten4ext$noOverfill(FluidStack resource, IFluidHandler.FluidAction action, boolean force,
                                 CallbackInfoReturnable<Integer> cir) {
    if (!fluid.isEmpty() && fluid.getAmount() >= capacity.getAsInt()) {
      cir.setReturnValue(0);
    }
  }
}
