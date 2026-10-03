package com.ten4ext.registry;

import com.hypothetic.ten4.api.blockentity.duct.DuctInfo;
import com.hypothetic.ten4.api.blockentity.duct.EnergyDuctBlockEntity;
import com.hypothetic.ten4.api.blockentity.duct.FluidDuctBlockEntity;
import com.hypothetic.ten4.api.blockentity.duct.ItemDuctBlockEntity;
import com.hypothetic.ten4.api.registry.BlockEntityBridges;
import com.ten4ext.Ten4Ext;
import com.ten4ext.Ten4ExtConfig;
import com.ten4ext.device.BiomassGeneratorBlockEntity;
import com.ten4ext.device.CrucibleBlockEntity;
import com.ten4ext.device.FarmManagerBlockEntity;
import com.ten4ext.device.SolarGeneratorBlockEntity;
import com.ten4ext.device.SolidifierBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class XBlockEntities {
  public static final DeferredRegister<BlockEntityType<?>> BES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Ten4Ext.ID);

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> SOLAR_GENERATOR = register("solar_generator", SolarGeneratorBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> BIOMASS_GENERATOR = register("biomass_generator", BiomassGeneratorBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> FARM_MANAGER = register("farm_manager", FarmManagerBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> CRUCIBLE = register("crucible", CrucibleBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> SOLIDIFIER = register("solidifier", SolidifierBlockEntity::new);

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> TITANIUM_ENERGY_DUCT = register("titanium_energy_duct",
      (p, s) -> new EnergyDuctBlockEntity(p, s, titaniumEnergy()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> TITANIUM_ITEM_DUCT = register("titanium_item_duct",
      (p, s) -> new ItemDuctBlockEntity(p, s, titaniumItem()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> TITANIUM_FLUID_DUCT = register("titanium_fluid_duct",
      (p, s) -> new FluidDuctBlockEntity(p, s, titaniumFluid()));

  private XBlockEntities() {
  }

  /** TEN4 looks up BE types here, so fill it before registration. */
  public static void bridge() {
    BlockEntityBridges.register(XBlocks.SOLAR_GENERATOR, SOLAR_GENERATOR);
    BlockEntityBridges.register(XBlocks.BIOMASS_GENERATOR, BIOMASS_GENERATOR);
    BlockEntityBridges.register(XBlocks.FARM_MANAGER, FARM_MANAGER);
    BlockEntityBridges.register(XBlocks.CRUCIBLE, CRUCIBLE);
    BlockEntityBridges.register(XBlocks.SOLIDIFIER, SOLIDIFIER);
    BlockEntityBridges.register(XBlocks.TITANIUM_ENERGY_DUCT, TITANIUM_ENERGY_DUCT);
    BlockEntityBridges.register(XBlocks.TITANIUM_ITEM_DUCT, TITANIUM_ITEM_DUCT);
    BlockEntityBridges.register(XBlocks.TITANIUM_FLUID_DUCT, TITANIUM_FLUID_DUCT);
  }

  private static DuctInfo titaniumEnergy() {
    Ten4ExtConfig.Ducts d = Ten4ExtConfig.COMMON.ducts;
    return new DuctInfo().setBufferCapacity(d.titaniumEnergyBuffer.get()).setThroughput(d.titaniumEnergyThroughput.get());
  }

  private static DuctInfo titaniumFluid() {
    Ten4ExtConfig.Ducts d = Ten4ExtConfig.COMMON.ducts;
    return new DuctInfo().setBufferCapacity(d.titaniumFluidBuffer.get()).setThroughput(d.titaniumFluidThroughput.get());
  }

  private static DuctInfo titaniumItem() {
    // item ducts: buffer = items per trip, throughput = ticks per block
    Ten4ExtConfig.Ducts d = Ten4ExtConfig.COMMON.ducts;
    return new DuctInfo().setBufferCapacity(d.titaniumItemStackSize.get()).setThroughput(d.titaniumItemTicksPerBlock.get());
  }

  private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> register(String id, BlockEntityType.BlockEntitySupplier<T> supplier) {
    return BES.register(id, () -> BlockEntityBridges.makeType(Ten4Ext.id(id), supplier));
  }
}
