package com.ten4ext.client;

import com.hypothetic.ten4.api.blockentity.duct.EnergyDuctBlockEntity;
import com.hypothetic.ten4.api.blockentity.duct.FluidDuctBlockEntity;
import com.hypothetic.ten4.api.blockentity.duct.ItemDuctBlockEntity;
import com.hypothetic.ten4.core.client.renderer.RenderEnergyDuct;
import com.hypothetic.ten4.core.client.renderer.RenderFluidDuct;
import com.hypothetic.ten4.core.client.renderer.RenderItemDuct;
import com.ten4ext.Ten4Ext;
import com.ten4ext.client.screen.XScreens;
import com.ten4ext.registry.XBlockEntities;
import com.ten4ext.registry.XMenus;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class Ten4ExtClient {
  private Ten4ExtClient() {
  }

  public static void init(IEventBus modBus) {
    modBus.addListener(Ten4ExtClient::onRegisterScreens);
    modBus.addListener(Ten4ExtClient::onRegisterRenderers);
  }

  private static void onRegisterScreens(RegisterMenuScreensEvent event) {
    event.register(XMenus.SOLAR_GENERATOR.get(), XScreens.SolarGenerator::new);
    event.register(XMenus.BIOMASS_GENERATOR.get(), XScreens.BiomassGenerator::new);
    event.register(XMenus.FARM_MANAGER.get(), XScreens.FarmManager::new);
    event.register(XMenus.CRUCIBLE.get(), XScreens.Crucible::new);
    event.register(XMenus.SOLIDIFIER.get(), XScreens.Solidifier::new);
  }

  @SuppressWarnings("unchecked")
  private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer((BlockEntityType<EnergyDuctBlockEntity>) XBlockEntities.TITANIUM_ENERGY_DUCT.get(),
        ctx -> new RenderEnergyDuct(ctx, Ten4Ext.id("block/duct/titanium_energy_duct")));
    event.registerBlockEntityRenderer((BlockEntityType<ItemDuctBlockEntity>) XBlockEntities.TITANIUM_ITEM_DUCT.get(),
        ctx -> new RenderItemDuct(ctx, Ten4Ext.id("block/duct/titanium_item_duct")));
    event.registerBlockEntityRenderer((BlockEntityType<FluidDuctBlockEntity>) XBlockEntities.TITANIUM_FLUID_DUCT.get(),
        ctx -> new RenderFluidDuct(ctx, Ten4Ext.id("block/duct/titanium_fluid_duct")));
  }
}
