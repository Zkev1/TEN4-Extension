package com.ten4ext.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.device.AbstractDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.duct.EnergyDuctBlockEntity;
import com.hypothetic.ten4.api.blockentity.duct.FluidDuctBlockEntity;
import com.hypothetic.ten4.api.blockentity.duct.ItemDuctBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.jetbrains.annotations.Nullable;

public final class XCommonEvents {
  private static final ResourceKey<CreativeModeTab> TEN4_TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Ten4.id("default"));

  private XCommonEvents() {
  }

  public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
    device(event, XBlockEntities.SOLAR_GENERATOR.get(), true, false, false);
    device(event, XBlockEntities.BIOMASS_GENERATOR.get(), true, true, false);
    device(event, XBlockEntities.FARM_MANAGER.get(), true, true, false);
    device(event, XBlockEntities.CRUCIBLE.get(), true, true, true);
    device(event, XBlockEntities.SOLIDIFIER.get(), true, true, true);

    register(event, Capabilities.EnergyStorage.BLOCK, XBlockEntities.TITANIUM_ENERGY_DUCT.get(), EnergyDuctBlockEntity::getEnergyStorage);
    register(event, Capabilities.ItemHandler.BLOCK, XBlockEntities.TITANIUM_ITEM_DUCT.get(), ItemDuctBlockEntity::getItemHandler);
    register(event, Capabilities.FluidHandler.BLOCK, XBlockEntities.TITANIUM_FLUID_DUCT.get(), FluidDuctBlockEntity::getFluidHandler);
  }

  /** Our items go in TEN4's creative tab. */
  public static void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
    if (event.getTabKey().equals(TEN4_TAB)) {
      XItems.ITEMS.getEntries().forEach(holder -> event.accept(holder.get()));
    }
  }

  private static void device(RegisterCapabilitiesEvent event, BlockEntityType<?> type, boolean energy, boolean items, boolean fluids) {
    if (energy) {
      register(event, Capabilities.EnergyStorage.BLOCK, type, AbstractDeviceBlockEntity::getEnergyStorage);
    }
    if (items) {
      register(event, Capabilities.ItemHandler.BLOCK, type, AbstractDeviceBlockEntity::getItemHandler);
    }
    if (fluids) {
      register(event, Capabilities.FluidHandler.BLOCK, type, AbstractDeviceBlockEntity::getFluidHandler);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T, BE extends BlockEntity> void register(RegisterCapabilitiesEvent event, BlockCapability<T, @Nullable Direction> cap,
                                                           BlockEntityType<?> type, ICapabilityProvider<? super BE, @Nullable Direction, T> provider) {
    event.registerBlockEntity(cap, (BlockEntityType<BE>) type, provider);
  }
}
