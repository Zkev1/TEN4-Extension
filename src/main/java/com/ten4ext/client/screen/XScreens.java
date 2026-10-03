package com.ten4ext.client.screen;

import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.api.client.components.GaugeVertical;
import com.hypothetic.ten4.api.client.components.UiComponent;
import com.hypothetic.ten4.api.client.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.api.client.gui.TextureRegion;
import com.hypothetic.ten4.api.container.ContainerMenu;
import com.hypothetic.ten4.api.container.sync.SyncedFieldReader;
import com.hypothetic.ten4.core.client.builtin.BuiltinComponents;
import com.hypothetic.ten4.util.DisplayUtil;
import com.ten4ext.Ten4Ext;
import com.ten4ext.device.CrucibleBlockEntity;
import com.ten4ext.device.SolarGeneratorBlockEntity;
import com.ten4ext.device.SolidifierBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

/** Screens for the addon's devices. */
public final class XScreens {
  public static final ResourceLocation COMPONENTS = Ten4Ext.id("textures/gui/components.png");

  private XScreens() {
  }

  /** Base device screen, background drawn by GuiBackground. */
  public abstract static class DeviceScreen extends ComponentedContainerScreen<ContainerMenu> {
    private final GuiBackground background;

    protected DeviceScreen(ContainerMenu menu, Inventory inv, Component title, GuiBackground background) {
      super(menu, inv, title);
      this.background = background;
    }

    @Override
    protected ResourceLocation getBackground() {
      return com.hypothetic.ten4.Ten4.id("textures/gui/pulverizer.png");
    }

    @Override
    protected void renderBg(GuiGraphics g, float pt, int mx, int my) {
      graphics = new EnhancedGuiGraphics(g);
      background.draw(g, leftPos, topPos);
      for (UiComponent e : components) {
        if (e.isVisible()) {
          e.onRender(graphics, pt);
        }
      }
    }
  }

  public static class SolarGenerator extends DeviceScreen {
    public SolarGenerator(ContainerMenu menu, Inventory inv, Component title) {
      super(menu, inv, title, new GuiBackground().energy(120, 18).slot(79, 35));
    }

    @Override
    protected void buildElements() {
      SyncedFieldReader r = menu.fieldsReader();
      add(BuiltinComponents.standardDeviceUI(this));
      add(BuiltinComponents.energyGauge(120, 18, r));
      add(new GaugeVertical(80, 36, 14, 14,
          () -> r.getInt(SolarGeneratorBlockEntity.SUNLIGHT), () -> 100) {
        @Override
        public void onCollectingTooltips(List<Component> tooltips, int mx, int my) {
          tooltips.add(Component.translatable(Ten4Ext.lang("gui.sunlight"), r.getInt(SolarGeneratorBlockEntity.SUNLIGHT) + "%"));
          tooltips.add(Component.translatable(Ten4Ext.lang("gui.generating"),
              DisplayUtil.compactInt(r.getInt(SolarGeneratorBlockEntity.GENERATION)) + " FE/t").withStyle(ChatFormatting.GRAY));
          if (r.getInt(SolarGeneratorBlockEntity.SUNLIGHT) == 0) {
            tooltips.add(Component.translatable(Ten4Ext.lang("gui.solar_hint")).withStyle(ChatFormatting.DARK_GRAY));
          }
        }
      }.withTexture(TextureRegion.of(COMPONENTS, 0, 0, 14, 14), TextureRegion.of(COMPONENTS, 14, 0, 14, 14)));
    }
  }

  public static class BiomassGenerator extends DeviceScreen {
    public BiomassGenerator(ContainerMenu menu, Inventory inv, Component title) {
      super(menu, inv, title, new GuiBackground().slot(44, 35).energy(120, 18));
    }

    @Override
    protected void buildElements() {
      SyncedFieldReader r = menu.fieldsReader();
      add(BuiltinComponents.standardDeviceUI(this));
      add(BuiltinComponents.energyGauge(120, 18, r));
      add(BuiltinComponents.fuelGauge(80, 36, r));
    }
  }

  public static class FarmManager extends DeviceScreen {
    public FarmManager(ContainerMenu menu, Inventory inv, Component title) {
      super(menu, inv, title, new GuiBackground().energy(9, 18)
          .slot(44, 17).slot(44, 35).slot(44, 53).arrow(68, 35)
          .slot(98, 26).slot(116, 26).slot(134, 26).slot(98, 44).slot(116, 44).slot(134, 44));
    }

    @Override
    protected void buildElements() {
      SyncedFieldReader r = menu.fieldsReader();
      add(BuiltinComponents.standardDeviceUI(this));
      add(BuiltinComponents.energyGauge(9, 18, r));
      add(BuiltinComponents.progressGauge(68, 35, r).tagged(null));
    }
  }

  public static class Crucible extends DeviceScreen {
    public Crucible(ContainerMenu menu, Inventory inv, Component title) {
      super(menu, inv, title, new GuiBackground().energy(9, 18).slot(53, 35).arrow(78, 35).tank(116, 18));
    }

    @Override
    protected void buildElements() {
      SyncedFieldReader r = menu.fieldsReader();
      add(BuiltinComponents.standardDeviceUI(this));
      add(BuiltinComponents.energyGauge(9, 18, r));
      add(BuiltinComponents.progressGauge(78, 35, r));
      add(BuiltinComponents.fluidGauge(116, 18, r, CrucibleBlockEntity.TANK_0));
    }
  }

  public static class Solidifier extends DeviceScreen {
    public Solidifier(ContainerMenu menu, Inventory inv, Component title) {
      super(menu, inv, title, new GuiBackground().energy(9, 18).tank(29, 18).slot(54, 35).arrow(78, 35).slot(116, 35));
    }

    @Override
    protected void buildElements() {
      SyncedFieldReader r = menu.fieldsReader();
      add(BuiltinComponents.standardDeviceUI(this));
      add(BuiltinComponents.energyGauge(9, 18, r));
      add(BuiltinComponents.fluidGauge(29, 18, r, SolidifierBlockEntity.TANK_0));
      add(BuiltinComponents.progressGauge(78, 35, r));
    }
  }
}
