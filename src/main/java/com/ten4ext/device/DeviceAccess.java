package com.ten4ext.device;

import com.hypothetic.ten4.api.blockentity.device.AbstractDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.device.SecurityMode;
import com.hypothetic.ten4.api.container.ContainerMenu;
import com.ten4ext.Ten4Ext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.UUID;

/** Private mode checks. */
public final class DeviceAccess {
  public static final TagKey<Item> WRENCH = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:tools/wrench"));

  private DeviceAccess() {
  }

  /** True if the device is private and belongs to someone else. Creative ops always pass. */
  public static boolean isLockedFor(AbstractDeviceBlockEntity device, Player player) {
    if (device.getSecurityMode() != SecurityMode.PRIVATE) {
      return false;
    }
    UUID owner = device.getOwner();
    if (owner == null || owner.equals(player.getUUID())) {
      return false;
    }
    return !(player.isCreative() && player.hasPermissions(2));
  }

  public static void denyMessage(Player player) {
    player.displayClientMessage(Component.translatable(Ten4Ext.lang("message.private_device")).withStyle(ChatFormatting.RED), true);
  }

  /** Only accept config packets from a player that has the device open. */
  public static boolean canConfigure(Player player, BlockPos pos) {
    return player.containerMenu instanceof ContainerMenu menu
        && menu.getBlockEntity().getBlockPos().equals(pos)
        && menu.stillValid(player);
  }
}
