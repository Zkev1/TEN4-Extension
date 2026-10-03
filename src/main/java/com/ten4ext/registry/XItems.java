package com.ten4ext.registry;

import com.hypothetic.ten4.api.item.IAugment.AugmentableField;
import com.hypothetic.ten4.core.item.DeviceItem;
import com.ten4ext.Ten4Ext;
import com.ten4ext.Ten4ExtConfig;
import com.ten4ext.item.AugmentItem;
import com.ten4ext.item.SilencingAugmentItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class XItems {
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Ten4Ext.ID);

  // Augments
  public static final DeferredHolder<Item, Item> OVERCLOCKING_AUGMENT = augment("overclocking_augment",
      () -> Ten4ExtConfig.COMMON.augments.overclocking, "power", AugmentableField.POWER);
  public static final DeferredHolder<Item, Item> COIL_AUGMENT = augment("coil_augment",
      () -> Ten4ExtConfig.COMMON.augments.coil, "energy", AugmentableField.ENERGY_CAPACITY, AugmentableField.ENERGY_THROUGHPUT);
  public static final DeferredHolder<Item, Item> PISTON_AUGMENT = augment("piston_augment",
      () -> Ten4ExtConfig.COMMON.augments.piston, "item", AugmentableField.ITEM_THROUGHPUT);
  public static final DeferredHolder<Item, Item> VACUUM_AUGMENT = augment("vacuum_augment",
      () -> Ten4ExtConfig.COMMON.augments.vacuum, "fluid", AugmentableField.FLUID_THROUGHPUT);
  public static final DeferredHolder<Item, Item> TANK_AUGMENT = augment("tank_augment",
      () -> Ten4ExtConfig.COMMON.augments.tank, "tank", AugmentableField.TANK_CAPACITY);
  public static final DeferredHolder<Item, Item> SILENCING_AUGMENT = ITEMS.register("silencing_augment",
      () -> new SilencingAugmentItem(new Item.Properties().stacksTo(16)));

  // Devices
  public static final DeferredHolder<Item, BlockItem> SOLAR_GENERATOR = device(XBlocks.SOLAR_GENERATOR);
  public static final DeferredHolder<Item, BlockItem> BIOMASS_GENERATOR = device(XBlocks.BIOMASS_GENERATOR);
  public static final DeferredHolder<Item, BlockItem> FARM_MANAGER = device(XBlocks.FARM_MANAGER);
  public static final DeferredHolder<Item, BlockItem> CRUCIBLE = device(XBlocks.CRUCIBLE);
  public static final DeferredHolder<Item, BlockItem> SOLIDIFIER = device(XBlocks.SOLIDIFIER);

  // Ducts
  public static final DeferredHolder<Item, BlockItem> TITANIUM_ENERGY_DUCT = block(XBlocks.TITANIUM_ENERGY_DUCT);
  public static final DeferredHolder<Item, BlockItem> TITANIUM_ITEM_DUCT = block(XBlocks.TITANIUM_ITEM_DUCT);
  public static final DeferredHolder<Item, BlockItem> TITANIUM_FLUID_DUCT = block(XBlocks.TITANIUM_FLUID_DUCT);

  private XItems() {
  }

  private static DeferredHolder<Item, Item> augment(String name, Supplier<net.neoforged.neoforge.common.ModConfigSpec.DoubleValue> mult,
                                                     String effect, AugmentableField first, AugmentableField... rest) {
    return ITEMS.register(name, () -> new AugmentItem(new Item.Properties().stacksTo(16), mult.get(), effect, first, rest));
  }

  private static DeferredHolder<Item, BlockItem> device(DeferredHolder<Block, Block> block) {
    return ITEMS.register(block.getId().getPath(), () -> new DeviceItem(block.get(), new Item.Properties()));
  }

  private static DeferredHolder<Item, BlockItem> block(DeferredHolder<Block, Block> block) {
    return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
  }
}
