package com.ten4ext.device;

import com.hypothetic.ten4.api.blockentity.ITickable;
import com.hypothetic.ten4.api.blockentity.device.AugmentableDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.device.DeviceInfo;
import com.hypothetic.ten4.api.capability.item.ItemSlot;
import com.hypothetic.ten4.api.capability.item.SlotOption;
import com.hypothetic.ten4.api.container.AugmentableContainerMenu;
import com.hypothetic.ten4.api.container.ContainerMenuLayout;
import com.hypothetic.ten4.api.container.sync.BuiltinSyncedFields;
import com.hypothetic.ten4.api.container.sync.Syncer;
import com.ten4ext.Ten4ExtConfig;
import com.ten4ext.registry.XMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** Harvests and replants crops around it. Based on TEN3's Farm Manager. */
public class FarmManagerBlockEntity extends AugmentableDeviceBlockEntity implements ITickable {
  public static final int SEED_SLOTS = 3;
  public static final int OUTPUT_SLOTS = 6;
  private static final int BASE_INTERVAL = 40;
  private static final int MAX_ACTIONS_PER_SWEEP = 16;
  private static final int AUTO_EJECT_BIT = 1 << 25;

  private final List<Integer> seedSlots = List.of(0, 1, 2);
  private final List<Integer> outputSlots = List.of(3, 4, 5, 6, 7, 8);
  private int timer;

  public FarmManagerBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
    setRawItemAutoFlags(AUTO_EJECT_BIT); // harvest leaves through ducts/chests next to it by default
  }

  public static boolean isPlantable(ItemStack stack) {
    return stack.getItem() instanceof BlockItem bi
        && (bi.getBlock() instanceof CropBlock || bi.getBlock() instanceof NetherWartBlock);
  }

  @Override
  protected DeviceInfo makeDeviceInfo() {
    Ten4ExtConfig.Machines m = Ten4ExtConfig.COMMON.machines;
    DeviceInfo info = new DeviceInfo()
        .enableEnergy()
        .enableItem()
        .setPower(m.farmPower.get())
        .setEnergyCapacity(m.farmEnergyCapacity.get())
        .setEnergyThroughput(m.farmEnergyThroughput.get())
        .setItemThroughput(4);
    for (int i = 0; i < SEED_SLOTS; i++) {
      info.addSlot(new ItemSlot(SlotOption.INPUT, augSlotLimiter()).setValidator(FarmManagerBlockEntity::isPlantable));
    }
    for (int i = 0; i < OUTPUT_SLOTS; i++) {
      info.addSlot(new ItemSlot(SlotOption.OUTPUT, augSlotLimiter()));
    }
    return info;
  }

  @Override
  protected void registerAdditionalSyncFields(Syncer syncer) {
    syncer.register(BuiltinSyncedFields.ENERGY);
    syncer.register(BuiltinSyncedFields.MAX_ENERGY);
    syncer.register(BuiltinSyncedFields.PROGRESS);
    syncer.register(BuiltinSyncedFields.MAX_PROGRESS);
  }

  @Override
  protected List<Integer> getComparatorSignalSlots() {
    return outputSlots;
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
    ContainerMenuLayout layout = new ContainerMenuLayout();
    for (int i = 0; i < SEED_SLOTS; i++) {
      layout.add(i, 44, 17 + i * 18);
    }
    for (int i = 0; i < OUTPUT_SLOTS; i++) {
      layout.add(SEED_SLOTS + i, 98 + (i % 3) * 18, 26 + (i / 3) * 18);
    }
    return new AugmentableContainerMenu(XMenus.FARM_MANAGER.get(), id, inv, this, layout);
  }

  /** Ticks between sweeps, shorter with Overclocking. */
  public int sweepInterval() {
    int base = Ten4ExtConfig.COMMON.machines.farmPower.get();
    return Math.max(1, Math.round((float) BASE_INTERVAL * base / Math.max(1, getActualPower())));
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

    int interval = sweepInterval();
    int perAction = Ten4ExtConfig.COMMON.machines.farmEnergyPerAction.get();
    boolean powered = getEnergy() >= perAction;
    if (enabled && powered) {
      if (++timer >= interval) {
        timer = 0;
        sweep((ServerLevel) level, perAction);
      }
    }
    setActive(enabled && powered);

    syncer.set(BuiltinSyncedFields.ENERGY, getEnergy());
    syncer.set(BuiltinSyncedFields.MAX_ENERGY, getEnergyCapacity());
    syncer.set(BuiltinSyncedFields.PROGRESS, Math.min(timer, interval));
    syncer.set(BuiltinSyncedFields.MAX_PROGRESS, interval);
    synchronizeBasicData();
  }

  private void sweep(ServerLevel level, int perAction) {
    int r = Ten4ExtConfig.COMMON.machines.farmRadius.get();
    int actions = 0;
    for (int dy = -1; dy <= 1; dy++) {
      for (int dx = -r; dx <= r; dx++) {
        for (int dz = -r; dz <= r; dz++) {
          if (actions >= MAX_ACTIONS_PER_SWEEP || getEnergy() < perAction) {
            return;
          }
          BlockPos p = worldPosition.offset(dx, dy, dz);
          if (p.equals(worldPosition) || !level.isLoaded(p)) {
            continue;
          }
          if (harvest(level, p) || plant(level, p)) {
            setEnergy(getEnergy() - perAction);
            actions++;
            setChanged();
          }
        }
      }
    }
  }

  private static boolean isMature(BlockState state) {
    if (state.getBlock() instanceof CropBlock crop) {
      return crop.isMaxAge(state);
    }
    return state.getBlock() instanceof NetherWartBlock && state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
  }

  private static BlockState replanted(BlockState state) {
    if (state.getBlock() instanceof CropBlock crop) {
      return crop.getStateForAge(0);
    }
    return state.setValue(NetherWartBlock.AGE, 0);
  }

  private boolean harvest(ServerLevel level, BlockPos p) {
    BlockState state = level.getBlockState(p);
    if (!isMature(state)) {
      return false;
    }
    ItemStack seed = state.getBlock().getCloneItemStack(level, p, state);
    List<ItemStack> drops = new ArrayList<>(Block.getDrops(state, level, p, level.getBlockEntity(p)));

    // keep one seed to replant
    boolean replant = false;
    for (ItemStack drop : drops) {
      if (!seed.isEmpty() && ItemStack.isSameItem(drop, seed) && !drop.isEmpty()) {
        drop.shrink(1);
        replant = true;
        break;
      }
    }

    // only harvest if the drops fit
    List<ItemStack> outputsBefore = new ArrayList<>();
    for (int slot : outputSlots) {
      outputsBefore.add(inventory.getStackInSlot(slot).copy());
    }
    for (ItemStack drop : drops) {
      if (!drop.isEmpty() && !inventory.forceInsert(drop, outputSlots, false).isEmpty()) {
        for (int i = 0; i < outputSlots.size(); i++) {
          inventory.setStackInSlot(outputSlots.get(i), outputsBefore.get(i)); // roll back: outputs are full
        }
        return false;
      }
    }

    level.levelEvent(2001, p, Block.getId(state)); // break particles + sound
    level.setBlockAndUpdate(p, replant ? replanted(state) : net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
    return true;
  }

  private boolean plant(ServerLevel level, BlockPos p) {
    if (!level.getBlockState(p).isAir()) {
      return false;
    }
    for (int slot : seedSlots) {
      ItemStack seeds = inventory.getStackInSlot(slot);
      if (seeds.isEmpty() || !(seeds.getItem() instanceof BlockItem bi)) {
        continue;
      }
      BlockState plant = bi.getBlock().defaultBlockState();
      if (plant.canSurvive(level, p)) {
        level.setBlockAndUpdate(p, plant);
        ItemStack rest = seeds.copy();
        rest.shrink(1);
        inventory.setStackInSlot(slot, rest);
        return true;
      }
    }
    return false;
  }

  @Override
  protected void loadAdditional(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider reg) {
    super.loadAdditional(tag, reg);
    timer = tag.getInt("SweepTimer");
  }

  @Override
  protected void saveAdditional(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider reg) {
    super.saveAdditional(tag, reg);
    tag.putInt("SweepTimer", timer);
  }
}
