package com.ten4ext;

import com.ten4ext.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Ten4Ext.ID)
public class Ten4Ext {
  public static final String ID = "ten4ext";
  public static final Logger LOGGER = LoggerFactory.getLogger("TEN4 Extension");

  /** TEN4 version the mixins were tested against. */
  public static final String TESTED_TEN4 = "26.1.9";

  public Ten4Ext(IEventBus modBus, ModContainer container) {
    net.neoforged.fml.ModList.get().getModContainerById("ten4").ifPresent(ten4 -> {
      String version = ten4.getModInfo().getVersion().toString();
      if (!TESTED_TEN4.equals(version)) {
        LOGGER.warn("TEN4 {} is installed; TEN4 Extension's fixes were written for {}. Fixes whose target code changed are "
            + "skipped automatically (see Mixin messages above) and the rest keep working.", version, TESTED_TEN4);
      }
    });

    XBlockEntities.bridge();

    XBlocks.BLOCKS.register(modBus);
    XItems.ITEMS.register(modBus);
    XBlockEntities.BES.register(modBus);
    XMenus.MENUS.register(modBus);
    XRecipes.TYPES.register(modBus);
    XRecipes.SERIALIZERS.register(modBus);

    modBus.addListener(XCommonEvents::onRegisterCapabilities);
    modBus.addListener(XCommonEvents::onBuildCreativeTab);

    container.registerConfig(ModConfig.Type.COMMON, Ten4ExtConfig.SPEC);

    if (FMLEnvironment.dist.isClient()) {
      com.ten4ext.client.Ten4ExtClient.init(modBus);
    }
  }

  public static ResourceLocation id(String path) {
    return ResourceLocation.fromNamespaceAndPath(ID, path);
  }

  public static String lang(String path) {
    return ID + "." + path;
  }
}
