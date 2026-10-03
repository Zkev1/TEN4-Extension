package com.ten4ext.registry;

import com.hypothetic.ten4.core.block.BlockProperties;
import com.hypothetic.ten4.core.block.DeviceBlock;
import com.hypothetic.ten4.core.block.duct.EnergyDuctBlock;
import com.hypothetic.ten4.core.block.duct.FluidDuctBlock;
import com.hypothetic.ten4.core.block.duct.ItemDuctBlock;
import com.ten4ext.Ten4Ext;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class XBlocks {
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Ten4Ext.ID);

  private static final BlockBehaviour.Properties TITANIUM_DUCT = Block.Properties.of()
      .mapColor(MapColor.COLOR_LIGHT_GRAY)
      .strength(2.0F)
      .requiresCorrectToolForDrops()
      .sound(SoundType.METAL)
      .explosionResistance(2.0F);

  // Generators
  public static final DeferredHolder<Block, Block> SOLAR_GENERATOR = BLOCKS.register("solar_generator",
      () -> new DeviceBlock(BlockProperties.METAL_DEVICE).tickServer());

  public static final DeferredHolder<Block, Block> BIOMASS_GENERATOR = BLOCKS.register("biomass_generator",
      () -> new DeviceBlock(BlockProperties.METAL_DEVICE).tickServer());

  // Devices
  public static final DeferredHolder<Block, Block> FARM_MANAGER = BLOCKS.register("farm_manager",
      () -> new DeviceBlock(BlockProperties.METAL_DEVICE).tickServer());
  public static final DeferredHolder<Block, Block> CRUCIBLE = BLOCKS.register("crucible",
      () -> new DeviceBlock(BlockProperties.METAL_DEVICE).tickServer());
  public static final DeferredHolder<Block, Block> SOLIDIFIER = BLOCKS.register("solidifier",
      () -> new DeviceBlock(BlockProperties.METAL_DEVICE).tickServer());

  // Titanium duct tier
  public static final DeferredHolder<Block, Block> TITANIUM_ENERGY_DUCT = BLOCKS.register("titanium_energy_duct",
      () -> new EnergyDuctBlock(TITANIUM_DUCT).tickBothSide());
  public static final DeferredHolder<Block, Block> TITANIUM_ITEM_DUCT = BLOCKS.register("titanium_item_duct",
      () -> new ItemDuctBlock(TITANIUM_DUCT).tickBothSide());
  public static final DeferredHolder<Block, Block> TITANIUM_FLUID_DUCT = BLOCKS.register("titanium_fluid_duct",
      () -> new FluidDuctBlock(TITANIUM_DUCT).tickBothSide());

  private XBlocks() {
  }
}
