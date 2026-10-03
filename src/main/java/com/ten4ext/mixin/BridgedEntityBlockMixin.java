package com.ten4ext.mixin;

import com.hypothetic.ten4.api.block.BridgedEntityBlock;
import com.hypothetic.ten4.api.blockentity.device.AbstractDeviceBlockEntity;
import com.ten4ext.device.DeviceAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Private mode check, and wrench right-click rotates devices. */
@Mixin(BridgedEntityBlock.class)
public abstract class BridgedEntityBlockMixin {
  @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
  private void ten4ext$deviceUse(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<ItemInteractionResult> cir) {
    if (!(level.getBlockEntity(pos) instanceof AbstractDeviceBlockEntity device)) {
      return;
    }

    if (!level.isClientSide() && DeviceAccess.isLockedFor(device, player)) {
      DeviceAccess.denyMessage(player);
      cir.setReturnValue(ItemInteractionResult.FAIL);
      return;
    }

    if (hand == InteractionHand.MAIN_HAND && stack.is(DeviceAccess.WRENCH) && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
      if (!level.isClientSide()) {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.HORIZONTAL_FACING, facing.getClockWise()));
        level.playSound(null, pos, SoundEvents.COPPER_PLACE, SoundSource.BLOCKS, 0.6F, 1.4F);
      }
      cir.setReturnValue(ItemInteractionResult.sidedSuccess(level.isClientSide()));
    }
  }
}
