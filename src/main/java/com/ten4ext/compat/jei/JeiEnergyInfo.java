package com.ten4ext.compat.jei;

import com.hypothetic.ten4.api.recipe.Complex;
import com.hypothetic.ten4.api.recipe.IComplexRecipe;
import com.hypothetic.ten4.core.registry.config.CfgCommon;
import com.hypothetic.ten4.core.registry.config.ModConfigs;
import com.ten4ext.Ten4Ext;
import com.ten4ext.Ten4ExtConfig;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;

import java.util.List;
import java.util.function.IntSupplier;

/** Energy numbers for the JEI energy bar tooltip. */
public final class JeiEnergyInfo {
  private JeiEnergyInfo() {
  }

  private record Spec(IntSupplier power, boolean generator) {
  }

  private static Spec spec(ResourceLocation category) {
    try {
      CfgCommon.Devices d = ModConfigs.COMMON.devices;
      Ten4ExtConfig.Machines m = Ten4ExtConfig.COMMON.machines;
      return switch (category.toString()) {
        case "ten4:pulverizing" -> new Spec(d.pulverizerPower::get, false);
        case "ten4:pressing" -> new Spec(d.pressPower::get, false);
        case "ten4:refining" -> new Spec(d.refinerPower::get, false);
        case "ten4:electrical_smelting" -> new Spec(d.smelterPower::get, false);
        case "ten4:heat_generating" -> new Spec(d.heatGeneratorPower::get, true);
        case "ten4:fuel_generating" -> new Spec(d.fuelGeneratorPower::get, true);
        case "ten4ext:melting" -> new Spec(m.cruciblePower::get, false);
        case "ten4ext:solidifying" -> new Spec(m.solidifierPower::get, false);
        case "ten4ext:biomass_generating" -> new Spec(m.biomassPower::get, true);
        default -> null;
      };
    } catch (RuntimeException e) {
      return null; // config not loaded yet, or a TEN4 config field moved
    }
  }

  private static int ticks(Object recipe) {
    if (recipe instanceof IComplexRecipe r) {
      return r.time();
    }
    if (recipe instanceof SmeltingRecipe r) {
      return r.getCookingTime();
    }
    if (recipe instanceof ItemStack fuel) {
      return fuel.getBurnTime(RecipeType.SMELTING);
    }
    if (recipe instanceof Ten4ExtJeiPlugin.BiomassFuel fuel) {
      return fuel.ticks();
    }
    return 0;
  }

  /** Fluid used per operation. */
  private static int fluidAmount(Object recipe) {
    if (recipe instanceof IComplexRecipe r) {
      List<Complex> fluids = r.fluidInputs();
      return fluids.isEmpty() ? 0 : fluids.getFirst().count();
    }
    return 0;
  }

  public static void append(ResourceLocation category, Object recipe, ITooltipBuilder tooltip) {
    Spec spec = spec(category);
    int ticks = ticks(recipe);
    if (spec == null || ticks <= 0) {
      return;
    }
    int power = spec.power().getAsInt();
    long total = (long) power * ticks;
    String key = spec.generator() ? "jei.energy.generates" : "jei.energy.uses";
    tooltip.add(Component.translatable(Ten4Ext.lang(key), format(power)).withStyle(ChatFormatting.GOLD));
    tooltip.add(Component.translatable(Ten4Ext.lang("jei.energy.total"), format(total), seconds(ticks)).withStyle(ChatFormatting.GRAY));
    int amount = fluidAmount(recipe);
    if (spec.generator() && amount > 0) {
      tooltip.add(Component.translatable(Ten4Ext.lang("jei.energy.per_mb"), format(total / amount)).withStyle(ChatFormatting.GRAY));
    }
    tooltip.add(Component.translatable(Ten4Ext.lang(spec.generator() ? "jei.energy.hint_generator" : "jei.energy.hint_machine"))
        .withStyle(ChatFormatting.DARK_GRAY));
  }

  static String format(long fe) {
    if (fe >= 1_000_000) {
      return trim(fe / 1_000_000.0) + "M";
    }
    if (fe >= 10_000) {
      return trim(fe / 1_000.0) + "k";
    }
    return String.format("%,d", fe);
  }

  private static String seconds(int ticks) {
    return trim(ticks / 20.0);
  }

  private static String trim(double v) {
    String s = String.format(java.util.Locale.ROOT, "%.1f", v);
    return s.endsWith(".0") ? s.substring(0, s.length() - 2) : s;
  }
}
