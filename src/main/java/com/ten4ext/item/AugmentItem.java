package com.ten4ext.item;

import com.hypothetic.ten4.api.blockentity.device.AugmentableDeviceBlockEntity;
import com.hypothetic.ten4.api.item.IAugment;
import com.ten4ext.Ten4Ext;
import com.ten4ext.Ten4ExtConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/** Augment that multiplies device stats. Math is done in longs and clamped. */
public class AugmentItem extends Item implements IAugment<AugmentableDeviceBlockEntity> {
  private final Set<AugmentableField> fields;
  private final ModConfigSpec.DoubleValue multiplier;
  private final String effectKey;

  public AugmentItem(Properties props, ModConfigSpec.DoubleValue multiplier, String effectKey, AugmentableField first, AugmentableField... rest) {
    super(props);
    this.fields = EnumSet.of(first, rest);
    this.multiplier = multiplier;
    this.effectKey = effectKey;
  }

  public static int scale(int value, double factor) {
    if (value <= 0) {
      return value;
    }
    double scaled = Math.floor((double) value * factor);
    if (scaled >= Integer.MAX_VALUE) {
      return Integer.MAX_VALUE;
    }
    return Math.max(1, (int) scaled);
  }

  @Override
  public int modifier(AugmentableField field, int value) {
    if (!fields.contains(field)) {
      return value;
    }
    return scale(value, multiplier.get());
  }

  public Set<AugmentableField> fields() {
    return fields;
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
    super.appendHoverText(stack, ctx, tooltip, flag);
    String mult = formatMultiplier();
    tooltip.add(Component.translatable(Ten4Ext.lang("augment." + effectKey), mult).withStyle(ChatFormatting.GOLD));
    tooltip.add(Component.translatable(Ten4Ext.lang("augment.stacking")).withStyle(ChatFormatting.DARK_GRAY));
  }

  private String formatMultiplier() {
    if (!Ten4ExtConfig.SPEC.isLoaded()) {
      return "?";
    }
    double m = multiplier.get();
    return m == Math.floor(m) ? String.valueOf((int) m) : String.valueOf(m);
  }
}
