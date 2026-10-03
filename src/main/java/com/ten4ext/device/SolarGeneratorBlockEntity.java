package com.ten4ext.device;

import com.hypothetic.ten4.api.blockentity.ITickable;
import com.hypothetic.ten4.api.blockentity.device.AugmentableDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.device.DeviceInfo;
import com.hypothetic.ten4.api.container.AugmentableContainerMenu;
import com.hypothetic.ten4.api.container.ContainerMenuLayout;
import com.hypothetic.ten4.api.container.sync.BuiltinSyncedFields;
import com.hypothetic.ten4.api.container.sync.SyncedField;
import com.hypothetic.ten4.api.container.sync.Syncer;
import com.ten4ext.Ten4ExtConfig;
import com.ten4ext.registry.XMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** Makes energy during the day when it can see the sky. */
public class SolarGeneratorBlockEntity extends AugmentableDeviceBlockEntity implements ITickable {
  /** Sunlight, 0-100. */
  public static final SyncedField<Integer> SUNLIGHT = SyncedField.ofInt("ten4ext_sunlight");
  /** FE generated last tick. */
  public static final SyncedField<Integer> GENERATION = SyncedField.ofInt("ten4ext_generation");

  private static final int AUTO_EJECT_BIT = 1 << 25;

  private int sunlight;
  private int generation;

  public SolarGeneratorBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
    // auto-eject energy by default (saved config overrides it)
    setRawEnergyAutoFlags(AUTO_EJECT_BIT);
  }

  public static int computeSunlight(Level level, BlockPos pos) {
    if (!level.dimensionType().hasSkyLight() || !level.canSeeSky(pos.above())) {
      return 0;
    }
    // cos(angle) is 1 at noon, 0 at sunrise/sunset and negative at night.
    float height = Mth.cos(level.getSunAngle(1.0F));
    if (height <= 0) {
      return 0;
    }
    float factor = Math.min(1.0F, height * 1.5F);
    if (level.isThundering() && level.isRainingAt(pos.above())) {
      factor *= 0.4F;
    } else if (level.isRainingAt(pos.above())) {
      factor *= 0.6F;
    }
    return Math.round(factor * 100);
  }

  @Override
  protected DeviceInfo makeDeviceInfo() {
    Ten4ExtConfig.Machines m = Ten4ExtConfig.COMMON.machines;
    return new DeviceInfo()
        .enableEnergy()
        .setPower(m.solarPower.get())
        .setEnergyCapacity(m.solarEnergyCapacity.get())
        .setEnergyThroughput(m.solarEnergyThroughput.get());
  }

  @Override
  protected void registerAdditionalSyncFields(Syncer syncer) {
    syncer.register(BuiltinSyncedFields.ENERGY);
    syncer.register(BuiltinSyncedFields.MAX_ENERGY);
    syncer.register(SUNLIGHT);
    syncer.register(GENERATION);
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
    return new AugmentableContainerMenu(XMenus.SOLAR_GENERATOR.get(), id, inv, this, new ContainerMenuLayout());
  }

  @Override
  public void tick() {
    if (level == null || level.isClientSide()) {
      return;
    }

    boolean enabled = isSignalEnabled();
    if (enabled) {
      queuedPushPull();
    }

    if (level.getGameTime() % 10 == 0) {
      sunlight = computeSunlight(level, worldPosition);
    }

    generation = 0;
    if (enabled && sunlight > 0) {
      int space = getEnergyCapacity() - getEnergy();
      generation = Math.min(space, (int) ((long) getActualPower() * sunlight / 100));
      if (generation > 0) {
        setEnergy(getEnergy() + generation);
        setChanged();
      }
    }
    setActive(generation > 0);

    syncer.set(BuiltinSyncedFields.ENERGY, getEnergy());
    syncer.set(BuiltinSyncedFields.MAX_ENERGY, getEnergyCapacity());
    syncer.set(SUNLIGHT, sunlight);
    syncer.set(GENERATION, generation);
    synchronizeBasicData();
  }
}
