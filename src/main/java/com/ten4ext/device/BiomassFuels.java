package com.ten4ext.device;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/** Biomass Generator fuels. Uses tags so modded plants work too. First match wins. */
public final class BiomassFuels {
  public record Rule(String key, int ticks, List<TagKey<Item>> tags, List<Item> items) {
    public boolean matches(ItemStack stack) {
      for (TagKey<Item> tag : tags) {
        if (stack.is(tag)) {
          return true;
        }
      }
      return items.contains(stack.getItem());
    }

    /** All items matching this rule, for JEI. */
    public List<ItemStack> examples() {
      List<ItemStack> out = new ArrayList<>();
      for (Item item : BuiltInRegistries.ITEM) {
        ItemStack stack = new ItemStack(item);
        if (matches(stack) && BiomassFuels.ticks(stack) == ticks) {
          out.add(stack);
        }
      }
      return out;
    }
  }

  private static TagKey<Item> tag(String id) {
    return TagKey.create(Registries.ITEM, ResourceLocation.parse(id));
  }

  public static final List<Rule> RULES = List.of(
      new Rule("hay", 540, List.of(tag("c:storage_blocks/wheat")), List.of(Items.HAY_BLOCK)),
      new Rule("logs", 300, List.of(ItemTags.LOGS), List.of()),
      new Rule("saplings", 100, List.of(ItemTags.SAPLINGS), List.of(Items.MANGROVE_PROPAGULE)),
      new Rule("planks", 75, List.of(ItemTags.PLANKS), List.of()),
      new Rule("crops", 60, List.of(tag("c:crops"), tag("c:mushrooms")), List.of(Items.SUGAR_CANE, Items.CACTUS,
          Items.PUMPKIN, Items.MELON_SLICE, Items.APPLE, Items.SWEET_BERRIES, Items.GLOW_BERRIES, Items.CHORUS_FRUIT)),
      new Rule("leaves", 40, List.of(ItemTags.LEAVES, ItemTags.FLOWERS), List.of(Items.MOSS_BLOCK, Items.AZALEA,
          Items.FLOWERING_AZALEA, Items.LILY_PAD, Items.BIG_DRIPLEAF, Items.SMALL_DRIPLEAF)),
      new Rule("plants", 25, List.of(tag("c:seeds")), List.of(Items.KELP, Items.DRIED_KELP, Items.BAMBOO, Items.VINE,
          Items.SHORT_GRASS, Items.TALL_GRASS, Items.FERN, Items.LARGE_FERN, Items.SEAGRASS, Items.GLOW_LICHEN,
          Items.HANGING_ROOTS, Items.MOSS_CARPET, Items.WEEPING_VINES, Items.TWISTING_VINES, Items.NETHER_SPROUTS))
  );

  private BiomassFuels() {
  }

  /** Burn time in ticks, 0 if it's not a fuel. */
  public static int ticks(ItemStack stack) {
    if (stack.isEmpty()) {
      return 0;
    }
    for (Rule rule : RULES) {
      if (rule.matches(stack)) {
        return rule.ticks();
      }
    }
    return 0;
  }
}
