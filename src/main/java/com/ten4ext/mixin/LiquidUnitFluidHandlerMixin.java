package com.ten4ext.mixin;

import com.hypothetic.ten4.core.capability.LiquidUnitFluidHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Stacked Liquid Units can't be filled or drained (stack dupe). */
@Mixin(LiquidUnitFluidHandler.class)
public abstract class LiquidUnitFluidHandlerMixin {
  @Shadow
  @Final
  private ItemStack stack;

  @Inject(method = "fill", at = @At("HEAD"), cancellable = true)
  private void ten4ext$fill(FluidStack resource, IFluidHandler.FluidAction action, CallbackInfoReturnable<Integer> cir) {
    if (stack.getCount() != 1) {
      cir.setReturnValue(0);
    }
  }

  @Inject(method = "drain(Lnet/neoforged/neoforge/fluids/FluidStack;Lnet/neoforged/neoforge/fluids/capability/IFluidHandler$FluidAction;)Lnet/neoforged/neoforge/fluids/FluidStack;",
      at = @At("HEAD"), cancellable = true)
  private void ten4ext$drainStack(FluidStack resource, IFluidHandler.FluidAction action, CallbackInfoReturnable<FluidStack> cir) {
    if (stack.getCount() != 1) {
      cir.setReturnValue(FluidStack.EMPTY);
    }
  }

  @Inject(method = "drain(ILnet/neoforged/neoforge/fluids/capability/IFluidHandler$FluidAction;)Lnet/neoforged/neoforge/fluids/FluidStack;",
      at = @At("HEAD"), cancellable = true)
  private void ten4ext$drainAmount(int maxDrain, IFluidHandler.FluidAction action, CallbackInfoReturnable<FluidStack> cir) {
    if (stack.getCount() != 1) {
      cir.setReturnValue(FluidStack.EMPTY);
    }
  }
}
