package com.ten4ext.device;

import com.hypothetic.ten4.api.blockentity.device.DeviceInfo;
import com.hypothetic.ten4.api.blockentity.device.SimpleGeneratorBlockEntity;
import com.hypothetic.ten4.api.capability.item.ItemSlot;
import com.hypothetic.ten4.api.capability.item.SlotOption;
import com.hypothetic.ten4.api.container.AugmentableContainerMenu;
import com.hypothetic.ten4.api.container.ContainerMenuLayout;
import com.ten4ext.Ten4ExtConfig;
import com.ten4ext.registry.XMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** Burns plant matter for energy. Based on TEN3's biomass engine. */
public class BiomassGeneratorBlockEntity extends SimpleGeneratorBlockEntity {
  private static final int AUTO_EJECT_BIT = 1 << 25;

  public BiomassGeneratorBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
    setRawEnergyAutoFlags(AUTO_EJECT_BIT); // push power out by default, like the Solar Generator
  }

  @Override
  protected DeviceInfo makeDeviceInfo() {
    Ten4ExtConfig.Machines m = Ten4ExtConfig.COMMON.machines;
    return new DeviceInfo()
        .enableEnergy()
        .enableItem()
        .setPower(m.biomassPower.get())
        .setEnergyCapacity(m.biomassEnergyCapacity.get())
        .setEnergyThroughput(m.biomassEnergyThroughput.get())
        .setItemThroughput(4)
        .addSlot(new ItemSlot(SlotOption.INPUT, augSlotLimiter()).setValidator(this::isValidInput));
  }

  @Override
  public boolean isValidInput(ItemStack stack) {
    return BiomassFuels.ticks(stack) > 0;
  }

  @Override
  public int tryFueling(boolean simulate) {
    ItemStack stack = inventory.getItem(0);
    int ticks = BiomassFuels.ticks(stack);
    if (!simulate && ticks > 0) {
      ItemStack rest = stack.copy();
      rest.shrink(1);
      inventory.setItem(0, rest);
    }
    return ticks;
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
    return new AugmentableContainerMenu(XMenus.BIOMASS_GENERATOR.get(), id, inv, this, new ContainerMenuLayout().add(0, 44, 35));
  }

  @Override
  public void onSoundPlay() {
    playSound(1.0F, SoundEvents.FURNACE_FIRE_CRACKLE);
  }
}
