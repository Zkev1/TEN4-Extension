package com.ten4ext.item;

import com.hypothetic.ten4.api.item.MuteAugment;
import com.ten4ext.Ten4Ext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/** TEN4 already handles MuteAugment, it just never added the item. */
public class SilencingAugmentItem extends MuteAugment {
  public SilencingAugmentItem(Properties properties) {
    super(properties);
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
    super.appendHoverText(stack, ctx, tooltip, flag);
    tooltip.add(Component.translatable(Ten4Ext.lang("augment.silence")).withStyle(ChatFormatting.GOLD));
  }
}
