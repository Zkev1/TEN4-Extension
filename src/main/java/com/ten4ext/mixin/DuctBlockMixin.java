package com.ten4ext.mixin;

import com.hypothetic.ten4.api.block.BridgedEntityBlock;
import com.hypothetic.ten4.core.block.duct.DuctBlock;
import com.hypothetic.ten4.core.block.duct.DuctInteractions;
import com.ten4ext.duct.DuctShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Duct hitbox follows its connections, and wrenching an arm toggles that side. */
@Mixin(DuctBlock.class)
public abstract class DuctBlockMixin extends BridgedEntityBlock {
  @Unique
  private static final TagKey<Item> TEN4EXT_WRENCH = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:tools/wrench"));

  protected DuctBlockMixin(Properties props) {
    super(props);
  }

  @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
  private void ten4ext$shape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx,
                             CallbackInfoReturnable<VoxelShape> cir) {
    cir.setReturnValue(DuctShapes.forDuct(level, pos));
  }

  @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
  private void ten4ext$collision(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx,
                                 CallbackInfoReturnable<VoxelShape> cir) {
    cir.setReturnValue(DuctShapes.forDuct(level, pos));
  }

  @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
  private void ten4ext$wrenchTargetsArm(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                        InteractionHand hand, BlockHitResult hit,
                                        CallbackInfoReturnable<ItemInteractionResult> cir) {
    if (hand != InteractionHand.MAIN_HAND || !stack.is(TEN4EXT_WRENCH)) {
      return;
    }
    if (level.isClientSide()) {
      cir.setReturnValue(ItemInteractionResult.SUCCESS);
      return;
    }
    Direction side = DuctShapes.targetSide(pos, hit.getLocation(), hit.getDirection());
    cir.setReturnValue(DuctInteractions.changeConnection(level, pos, side, player));
  }

  /** Shape depends on the block entity, so it can't be cached per state. */
  @Override
  public boolean hasDynamicShape() {
    return true;
  }

  /** Ducts never cover a full face. Also avoids BE lookups while meshing. */
  @Override
  protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
    return Shapes.empty();
  }
}
