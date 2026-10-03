package com.ten4ext.registry;

import com.hypothetic.ten4.api.blockentity.device.AbstractDeviceBlockEntity;
import com.hypothetic.ten4.api.container.ContainerMenu;
import com.ten4ext.Ten4Ext;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class XMenus {
  public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Ten4Ext.ID);

  public static final DeferredHolder<MenuType<?>, MenuType<ContainerMenu>> SOLAR_GENERATOR = MENUS.register("solar_generator", XMenus::deviceMenu);
  public static final DeferredHolder<MenuType<?>, MenuType<ContainerMenu>> BIOMASS_GENERATOR = MENUS.register("biomass_generator", XMenus::deviceMenu);
  public static final DeferredHolder<MenuType<?>, MenuType<ContainerMenu>> FARM_MANAGER = MENUS.register("farm_manager", XMenus::deviceMenu);
  public static final DeferredHolder<MenuType<?>, MenuType<ContainerMenu>> CRUCIBLE = MENUS.register("crucible", XMenus::deviceMenu);
  public static final DeferredHolder<MenuType<?>, MenuType<ContainerMenu>> SOLIDIFIER = MENUS.register("solidifier", XMenus::deviceMenu);

  private XMenus() {
  }

  /** Rebuilds the menu from the client-side block entity, like TEN4 does. */
  private static MenuType<ContainerMenu> deviceMenu() {
    return IMenuTypeExtension.create((id, inv, buf) -> {
      if (inv.player.level().getBlockEntity(buf.readBlockPos()) instanceof AbstractDeviceBlockEntity be) {
        AbstractContainerMenu menu = be.createMenu(id, inv, inv.player);
        if (menu instanceof ContainerMenu cm) {
          return cm;
        }
      }
      throw new IllegalStateException("TEN4 Extension: device menu opened without a matching block entity");
    });
  }
}
