package com.ten4ext.compat.jei;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.recipe.Complex;
import com.hypothetic.ten4.api.recipe.IComplexRecipe;
import com.hypothetic.ten4.compat.jei.JeiModCategory;
import com.hypothetic.ten4.compat.jei.util.JeiRecipeClickDetector;
import com.hypothetic.ten4.core.registry.ModItems;
import com.ten4ext.Ten4Ext;
import com.ten4ext.client.screen.XScreens;
import com.ten4ext.device.BiomassFuels;
import com.ten4ext.registry.XItems;
import com.ten4ext.registry.XRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class Ten4ExtJeiPlugin implements IModPlugin {
  public static final RecipeType<IComplexRecipe> MELTING = new RecipeType<>(Ten4Ext.id("melting"), IComplexRecipe.class);
  public static final RecipeType<IComplexRecipe> SOLIDIFYING = new RecipeType<>(Ten4Ext.id("solidifying"), IComplexRecipe.class);
  public static final RecipeType<BiomassFuel> BIOMASS = new RecipeType<>(Ten4Ext.id("biomass_generating"), BiomassFuel.class);

  // TEN4's JEI textures
  private static final ResourceLocation PULVERIZING_BG = Ten4.id("textures/gui/compat/pulverizing.png");
  private static final ResourceLocation REFINING_BG = Ten4.id("textures/gui/compat/refining.png");
  private static final ResourceLocation HEAT_BG = Ten4.id("textures/gui/compat/heat_generating.png");

  /** Group of biomass fuels that burn for the same time. */
  public record BiomassFuel(List<ItemStack> items, int ticks) {
  }

  @Override
  public ResourceLocation getPluginUid() {
    return Ten4Ext.id("jei_plugin");
  }

  @Override
  public void registerCategories(IRecipeCategoryRegistration registry) {
    IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
    registry.addRecipeCategories(new MeltingCategory(helper));
    registry.addRecipeCategories(new SolidifyingCategory(helper));
    registry.addRecipeCategories(new BiomassCategory(helper));
  }

  @Override
  public void registerRecipes(IRecipeRegistration registry) {
    if (Minecraft.getInstance().level != null) {
      var rm = Minecraft.getInstance().level.getRecipeManager();
      registry.addRecipes(MELTING, rm.getAllRecipesFor(XRecipes.MELTING.get()).stream().map(RecipeHolder::value).toList());
      registry.addRecipes(SOLIDIFYING, rm.getAllRecipesFor(XRecipes.SOLIDIFYING.get()).stream().map(RecipeHolder::value).toList());
    }
    List<BiomassFuel> fuels = new ArrayList<>();
    for (BiomassFuels.Rule rule : BiomassFuels.RULES) {
      List<ItemStack> items = rule.examples();
      if (!items.isEmpty()) {
        fuels.add(new BiomassFuel(items, rule.ticks()));
      }
    }
    registry.addRecipes(BIOMASS, fuels);

    info(registry, XItems.SOLAR_GENERATOR.get(), "solar_generator");
    info(registry, XItems.BIOMASS_GENERATOR.get(), "biomass_generator");
    info(registry, XItems.FARM_MANAGER.get(), "farm_manager");
    for (ItemLike augment : List.of(XItems.OVERCLOCKING_AUGMENT.get(), XItems.COIL_AUGMENT.get(), XItems.PISTON_AUGMENT.get(),
        XItems.VACUUM_AUGMENT.get(), XItems.TANK_AUGMENT.get(), XItems.SILENCING_AUGMENT.get())) {
      info(registry, augment, "augments");
    }
    info(registry, ModItems.WRENCH.get(), "wrench");
  }

  private static void info(IRecipeRegistration registry, ItemLike item, String key) {
    registry.addIngredientInfo(new ItemStack(item), VanillaTypes.ITEM_STACK, Component.translatable(Ten4Ext.lang("jei.info." + key)));
  }

  @Override
  public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
    registry.addRecipeCatalyst(new ItemStack(XItems.CRUCIBLE.get()), MELTING);
    registry.addRecipeCatalyst(new ItemStack(XItems.SOLIDIFIER.get()), SOLIDIFYING);
    registry.addRecipeCatalyst(new ItemStack(XItems.BIOMASS_GENERATOR.get()), BIOMASS);
  }

  @Override
  public void registerGuiHandlers(IGuiHandlerRegistration registry) {
    registry.addGuiContainerHandler(XScreens.Crucible.class, new JeiRecipeClickDetector(MELTING));
    registry.addGuiContainerHandler(XScreens.Solidifier.class, new JeiRecipeClickDetector(SOLIDIFYING));
    registry.addGuiContainerHandler(XScreens.BiomassGenerator.class, new JeiRecipeClickDetector(BIOMASS));
  }

  /** Background made from parts of TEN4's JEI textures: {texture, u, v, w, h, x, y, texW, texH}. */
  private static IDrawable background(int width, int height, Object[]... pieces) {
    return new IDrawable() {
      @Override
      public int getWidth() {
        return width;
      }

      @Override
      public int getHeight() {
        return height;
      }

      @Override
      public void draw(GuiGraphics g, int xOffset, int yOffset) {
        for (Object[] p : pieces) {
          g.blit((ResourceLocation) p[0], xOffset + (int) p[5], yOffset + (int) p[6], (int) p[1], (int) p[2],
              (int) p[3], (int) p[4], (int) p[7], (int) p[8]);
        }
      }
    };
  }

  /** Item -> fluid. Title is under ten4 because TEN4 builds the key from its own namespace. */
  static class MeltingCategory extends JeiModCategory<IComplexRecipe> {
    MeltingCategory(IGuiHelper helper) {
      super(helper, MELTING, new ItemStack(XItems.CRUCIBLE.get()));
    }

    @Override
    public int getWidth() {
      return 84;
    }

    @Override
    public int getHeight() {
      return 54;
    }

    @Override
    protected IDrawable createBackground() {
      // energy bar, slot and arrow from the pulverizer, tank from the refiner
      return background(84, 54,
          new Object[] {PULVERIZING_BG, 0, 0, 60, 54, 0, 0, 102, 54},
          new Object[] {REFINING_BG, 112, 0, 22, 54, 62, 0, 151, 54});
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IComplexRecipe recipe, IFocusGroup focuses) {
      List<Complex> in = recipe.itemInputs();
      addItemInput(builder, in.isEmpty() ? Complex.EMPTY : in.getFirst(), 15, 18);
      List<Complex> out = recipe.fluidOutputs();
      addFluidOutput(builder, out.isEmpty() ? Complex.EMPTY : out.getFirst(), 64, 3);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, IComplexRecipe recipe, IFocusGroup focuses) {
      super.createRecipeExtras(builder, recipe, focuses);
      addEnergyGauge(builder, 2, 2, false);
      addProgressGauge(builder, recipe.time(), 35, 18);
    }
  }

  /** Fluid (+ item) -> item. */
  static class SolidifyingCategory extends JeiModCategory<IComplexRecipe> {
    SolidifyingCategory(IGuiHelper helper) {
      super(helper, SOLIDIFYING, new ItemStack(XItems.SOLIDIFIER.get()));
    }

    @Override
    public int getWidth() {
      return 112;
    }

    @Override
    public int getHeight() {
      return 54;
    }

    @Override
    protected IDrawable createBackground() {
      return background(112, 54, new Object[] {REFINING_BG, 0, 0, 112, 54, 0, 0, 151, 54});
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IComplexRecipe recipe, IFocusGroup focuses) {
      List<Complex> fluids = recipe.fluidInputs();
      addFluidInput(builder, fluids.isEmpty() ? Complex.EMPTY : fluids.getFirst(), 17, 3);
      List<Complex> items = recipe.itemInputs();
      if (!items.isEmpty()) {
        addItemInput(builder, items.getFirst(), 40, 18);
      }
      List<Complex> out = recipe.itemOutputs();
      addItemOutput(builder, out.isEmpty() ? Complex.EMPTY : out.getFirst(), 92, 18);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, IComplexRecipe recipe, IFocusGroup focuses) {
      super.createRecipeExtras(builder, recipe, focuses);
      addEnergyGauge(builder, 2, 2, false);
      addProgressGauge(builder, recipe.time(), 63, 18);
    }
  }

  /** Biomass Generator fuels. */
  static class BiomassCategory extends JeiModCategory<BiomassFuel> {
    BiomassCategory(IGuiHelper helper) {
      super(helper, BIOMASS, new ItemStack(XItems.BIOMASS_GENERATOR.get()));
    }

    @Override
    public int getWidth() {
      return 102;
    }

    @Override
    public int getHeight() {
      return 54;
    }

    @Override
    protected IDrawable createBackground() {
      return background(102, 54, new Object[] {HEAT_BG, 0, 0, 102, 54, 0, 0, 102, 54});
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BiomassFuel fuel, IFocusGroup focuses) {
      builder.addInputSlot(15, 18).addItemStacks(fuel.items());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, BiomassFuel fuel, IFocusGroup focuses) {
      super.createRecipeExtras(builder, fuel, focuses);
      addEnergyGauge(builder, 75, 2, true);
      addFuelGauge(builder, fuel.ticks(), 45, 19);
    }
  }
}
