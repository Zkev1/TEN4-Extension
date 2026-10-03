package com.ten4ext.mixin;

import com.hypothetic.ten4.api.capability.item.ItemInventory;
import com.hypothetic.ten4.core.item.DeviceItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Show installed augments in the device item tooltip. */
@Mixin(DeviceItem.class)
public abstract class DeviceItemMixin {
  @Redirect(method = "appendHoverText", at = @At(value = "INVOKE",
      target = "Lcom/hypothetic/ten4/api/capability/item/ItemInventory;fromTag(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V"))
  private void ten4ext$readAugments(ItemInventory inventory, CompoundTag tag, HolderLookup.Provider registries) {
    inventory.fromTag(tag.getCompound("augments"), registries);
  }
}
