package com.ten4ext.mixin;

import com.hypothetic.ten4.api.blockentity.device.AugmentableDeviceBlockEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

/** One augment per slot, so max 4 per device. Old stacks only count once. */
@Mixin(AugmentableDeviceBlockEntity.class)
public abstract class AugmentableDeviceBlockEntityMixin {
  @ModifyArg(method = "<init>", at = @At(value = "INVOKE",
      target = "Lcom/hypothetic/ten4/api/capability/item/ItemSlot;<init>(Lcom/hypothetic/ten4/api/capability/item/SlotOption;I)V"),
      index = 1)
  private int ten4ext$oneAugmentPerSlot(int limit) {
    return 1;
  }

  @Redirect(method = {"applyAugments", "countAugment"}, at = @At(value = "INVOKE",
      target = "Lnet/minecraft/world/item/ItemStack;getCount()I"))
  private int ten4ext$countOncePerSlot(ItemStack stack) {
    return Math.min(1, stack.getCount());
  }
}
