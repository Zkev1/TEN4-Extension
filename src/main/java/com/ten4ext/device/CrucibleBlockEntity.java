package com.ten4ext.device;

import com.hypothetic.ten4.api.blockentity.device.ComplexRecipeDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.device.DeviceInfo;
import com.hypothetic.ten4.api.capability.fluid.FluidTank;
import com.hypothetic.ten4.api.capability.fluid.TankOption;
import com.hypothetic.ten4.api.capability.item.ItemSlot;
import com.hypothetic.ten4.api.capability.item.SlotOption;
import com.hypothetic.ten4.api.container.AugmentableContainerMenu;
import com.hypothetic.ten4.api.container.ContainerMenuLayout;
import com.hypothetic.ten4.api.container.sync.SyncedFluidStack;
import com.hypothetic.ten4.api.container.sync.Syncer;
import com.hypothetic.ten4.api.recipe.IComplexRecipe;
import com.hypothetic.ten4.core.registry.ModSoundEvents;
import com.ten4ext.Ten4ExtConfig;
import com.ten4ext.registry.XMenus;
import com.ten4ext.registry.XRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** Melts items into fluids ({@code ten4ext:melting} recipes). */
public class CrucibleBlockEntity extends ComplexRecipeDeviceBlockEntity {
  public static final SyncedFluidStack TANK_0 = new SyncedFluidStack(0);

  public CrucibleBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
  }

  @Override
  protected DeviceInfo makeDeviceInfo() {
    Ten4ExtConfig.Machines m = Ten4ExtConfig.COMMON.machines;
    return new DeviceInfo()
        .enableEnergy()
        .enableItem()
        .enableFluid()
        .setPower(m.cruciblePower.get())
        .setEnergyCapacity(m.crucibleEnergyCapacity.get())
        .setEnergyThroughput(m.crucibleEnergyThroughput.get())
        .setItemThroughput(1)
        .setFluidThroughput(m.crucibleFluidThroughput.get())
        .addSlot(new ItemSlot(SlotOption.INPUT, augSlotLimiter()).setValidator(this::isValidInput))
        .addTank(new FluidTank(TankOption.OUTPUT, augTankLimiter(m.crucibleTankCapacity.get())));
  }

  @Override
  protected void registerAdditionalSyncFields(Syncer syncer) {
    super.registerAdditionalSyncFields(syncer);
    TANK_0.register(syncer);
  }

  @Override
  public void tick() {
    super.tick();
    if (level != null && !level.isClientSide()) {
      TANK_0.sync(syncer, fluidInventory.getTank(0));
    }
  }

  @Override
  protected RecipeType<IComplexRecipe> getRecipeType() {
    return XRecipes.MELTING.get();
  }

  @Override
  protected void initializeRecipeAutomation() {
    inputSlots.add(0);
    outputTanks.add(0);
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
    ContainerMenuLayout layout = new ContainerMenuLayout().add(0, 53, 35);
    return new AugmentableContainerMenu(XMenus.CRUCIBLE.get(), id, inv, this, layout);
  }

  @Override
  public void onSoundPlay() {
    playSound(1.0F, ModSoundEvents.DEVICE_NOISE_1.get());
  }
}
