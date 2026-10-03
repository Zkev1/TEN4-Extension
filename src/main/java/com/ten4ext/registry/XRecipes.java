package com.ten4ext.registry;

import com.hypothetic.ten4.api.recipe.ComplexRecipeSerializer;
import com.hypothetic.ten4.api.recipe.IComplexRecipe;
import com.ten4ext.Ten4Ext;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** New recipe types reuse TEN4's JSON format ("inputs"/"outputs"/"time") via its serializer. */
public final class XRecipes {
  public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Ten4Ext.ID);
  public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Ten4Ext.ID);

  public static final DeferredHolder<RecipeType<?>, RecipeType<IComplexRecipe>> MELTING = type("melting");
  public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<IComplexRecipe>> MELTING_SER =
      SERIALIZERS.register("melting", () -> new ComplexRecipeSerializer(MELTING));

  public static final DeferredHolder<RecipeType<?>, RecipeType<IComplexRecipe>> SOLIDIFYING = type("solidifying");
  public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<IComplexRecipe>> SOLIDIFYING_SER =
      SERIALIZERS.register("solidifying", () -> new ComplexRecipeSerializer(SOLIDIFYING));

  private XRecipes() {
  }

  private static DeferredHolder<RecipeType<?>, RecipeType<IComplexRecipe>> type(String id) {
    return TYPES.register(id, () -> RecipeType.simple(Ten4Ext.id(id)));
  }
}
