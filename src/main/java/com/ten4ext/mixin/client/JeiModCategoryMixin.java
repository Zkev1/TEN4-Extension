package com.ten4ext.mixin.client;

import com.hypothetic.ten4.compat.jei.JeiModCategory;
import com.ten4ext.compat.jei.JeiEnergyInfo;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.recipe.category.IRecipeCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Energy tooltip on the energy bar of TEN4-style JEI categories. */
@Mixin(JeiModCategory.class)
public abstract class JeiModCategoryMixin<T> implements IRecipeCategory<T> {
  @Unique
  private int ten4ext$gaugeX = Integer.MIN_VALUE;
  @Unique
  private int ten4ext$gaugeY;

  /** Where the energy bar was placed. */
  @Inject(method = "addEnergyGauge", at = @At("HEAD"))
  private void ten4ext$rememberGauge(IRecipeExtrasBuilder builder, int x, int y, boolean increasing, CallbackInfo ci) {
    ten4ext$gaugeX = x;
    ten4ext$gaugeY = y;
  }

  @Override
  public void getTooltip(ITooltipBuilder tooltip, T recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
    if (ten4ext$gaugeX != Integer.MIN_VALUE
        && mouseX >= ten4ext$gaugeX && mouseX < ten4ext$gaugeX + 8
        && mouseY >= ten4ext$gaugeY && mouseY < ten4ext$gaugeY + 50) {
      JeiEnergyInfo.append(getRecipeType().getUid(), recipe, tooltip);
    }
  }
}
