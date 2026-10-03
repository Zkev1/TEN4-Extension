package com.ten4ext.mixin;

import com.hypothetic.ten4.api.blockentity.device.AugmentableDeviceBlockEntity;
import com.hypothetic.ten4.core.blockentity.LiquidUnitBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Put the emptied/filled container back in the slot (bucket dupe). */
@Mixin(LiquidUnitBlockEntity.class)
public abstract class LiquidUnitBlockEntityMixin extends AugmentableDeviceBlockEntity {
  protected LiquidUnitBlockEntityMixin(BlockPos pos, BlockState state) {
    super(pos, state);
  }

  @Redirect(method = "tick", at = @At(value = "INVOKE", ordinal = 0,
      target = "Lnet/neoforged/neoforge/fluids/FluidUtil;tryFluidTransfer(Lnet/neoforged/neoforge/fluids/capability/IFluidHandler;Lnet/neoforged/neoforge/fluids/capability/IFluidHandler;IZ)Lnet/neoforged/neoforge/fluids/FluidStack;"))
  private FluidStack ten4ext$emptyContainer(IFluidHandler tank, IFluidHandler item, int max, boolean doTransfer) {
    return ten4ext$transfer(0, tank, item, item, max);
  }

  @Redirect(method = "tick", at = @At(value = "INVOKE", ordinal = 1,
      target = "Lnet/neoforged/neoforge/fluids/FluidUtil;tryFluidTransfer(Lnet/neoforged/neoforge/fluids/capability/IFluidHandler;Lnet/neoforged/neoforge/fluids/capability/IFluidHandler;IZ)Lnet/neoforged/neoforge/fluids/FluidStack;"))
  private FluidStack ten4ext$fillContainer(IFluidHandler item, IFluidHandler tank, int max, boolean doTransfer) {
    return ten4ext$transfer(1, item, tank, item, max);
  }

  @Unique
  private FluidStack ten4ext$transfer(int slot, IFluidHandler dest, IFluidHandler source, IFluidHandler itemSide, int max) {
    ItemStack inSlot = inventory.getStackInSlot(slot);
    // only ever act on one container
    if (inSlot.getCount() != 1 || !(itemSide instanceof IFluidHandlerItem itemHandler)) {
      return FluidStack.EMPTY;
    }
    FluidStack moved = FluidUtil.tryFluidTransfer(dest, source, max, true);
    if (!moved.isEmpty()) {
      inventory.setStackInSlot(slot, itemHandler.getContainer());
      setChanged();
    }
    return moved;
  }
}
