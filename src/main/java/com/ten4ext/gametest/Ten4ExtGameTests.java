package com.ten4ext.gametest;

import com.hypothetic.ten4.api.blockentity.device.AbstractDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.device.AugmentableDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.device.SecurityMode;
import com.hypothetic.ten4.api.capability.fluid.FluidInventory;
import com.hypothetic.ten4.api.capability.fluid.FluidTank;
import com.hypothetic.ten4.api.capability.fluid.TankOption;
import com.hypothetic.ten4.api.transmission.ConnectionType;
import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import com.hypothetic.ten4.core.item.EnergyUnitItem;
import com.hypothetic.ten4.core.item.LiquidUnitItem;
import com.hypothetic.ten4.core.registry.ModBlocks;
import com.hypothetic.ten4.core.registry.ModItems;
import com.ten4ext.Ten4Ext;
import com.ten4ext.device.DeviceAccess;
import com.ten4ext.device.SolarGeneratorBlockEntity;
import com.ten4ext.duct.DuctShapes;
import com.ten4ext.item.AugmentItem;
import com.ten4ext.registry.XBlocks;
import com.ten4ext.registry.XItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.UUID;

/** ./gradlew runGameTestServer */
@GameTestHolder(Ten4Ext.ID)
@PrefixGameTestTemplate(false)
public final class Ten4ExtGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos A = new BlockPos(2, 1, 2);

  private Ten4ExtGameTests() {
  }

  private static <T> T be(GameTestHelper h, BlockPos pos, Class<T> type) {
    Object be = h.getBlockEntity(pos);
    h.assertTrue(type.isInstance(be), "expected " + type.getSimpleName() + " at " + pos + " but found " + be);
    return type.cast(be);
  }

  private static FluidTank tank(AbstractDeviceBlockEntity device, int index) {
    return ((FluidInventory) device.getFluidInventory()).getTank(index);
  }

  // Liquid Unit dupe

  @GameTest(template = EMPTY)
  public static void liquidUnitEmptiesBucketOnce(GameTestHelper h) {
    h.setBlock(A, ModBlocks.LIQUID_UNIT.get());
    AbstractDeviceBlockEntity unit = be(h, A, AbstractDeviceBlockEntity.class);
    ((IItemHandlerModifiableView) () -> unit).set(0, new ItemStack(Items.WATER_BUCKET));
    h.runAfterDelay(20, () -> {
      h.assertTrue(unit.getInventory().getStackInSlot(0).is(Items.BUCKET), "water bucket was not turned into an empty bucket");
      int stored = tank(unit, 0).getFluidAmount();
      h.assertTrue(stored == 1000, "expected exactly 1000 mB in the unit, found " + stored + " (fluid duplication)");
      h.succeed();
    });
  }

  @GameTest(template = EMPTY)
  public static void liquidUnitFillsBucket(GameTestHelper h) {
    h.setBlock(A, ModBlocks.LIQUID_UNIT.get());
    AbstractDeviceBlockEntity unit = be(h, A, AbstractDeviceBlockEntity.class);
    tank(unit, 0).setFluid(new FluidStack(Fluids.WATER, 2500));
    ((IItemHandlerModifiableView) () -> unit).set(1, new ItemStack(Items.BUCKET));
    h.runAfterDelay(20, () -> {
      h.assertTrue(unit.getInventory().getStackInSlot(1).is(Items.WATER_BUCKET), "empty bucket was not filled");
      int stored = tank(unit, 0).getFluidAmount();
      h.assertTrue(stored == 1500, "expected 1500 mB left, found " + stored + " (fluid voided)");
      h.succeed();
    });
  }

  @GameTest(template = EMPTY)
  public static void unitItemsRefuseStacks(GameTestHelper h) {
    ItemStack liquid = new ItemStack(ModItems.LIQUID_UNIT.get(), 64);
    IFluidHandlerItem fh = liquid.getCapability(Capabilities.FluidHandler.ITEM);
    h.assertTrue(fh != null, "liquid unit item has no fluid capability");
    h.assertTrue(fh.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE) == 0,
        "a stack of 64 liquid units accepted fluid (duplication)");
    h.assertTrue(LiquidUnitItem.getStoredFluid(liquid).isEmpty(), "stack of liquid units was filled");

    ItemStack single = new ItemStack(ModItems.LIQUID_UNIT.get());
    IFluidHandlerItem fh1 = single.getCapability(Capabilities.FluidHandler.ITEM);
    h.assertTrue(fh1 != null && fh1.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE) == 1000,
        "a single liquid unit no longer accepts fluid");

    ItemStack energy = new ItemStack(ModItems.ENERGY_UNIT.get(), 16);
    IEnergyStorage es = energy.getCapability(Capabilities.EnergyStorage.ITEM);
    h.assertTrue(es != null && es.receiveEnergy(1000, false) == 0, "a stack of energy units accepted energy (duplication)");
    h.assertTrue(EnergyUnitItem.getStoredEnergy(energy) == 0, "stack of energy units was charged");
    h.succeed();
  }

  // Void Unit

  @GameTest(template = EMPTY)
  public static void voidUnitVoids(GameTestHelper h) {
    h.setBlock(A, ModBlocks.VOID_UNIT.get());
    h.runAfterDelay(2, () -> {
      IItemHandler items = h.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, h.absolutePos(A), Direction.UP);
      h.assertTrue(items != null, "void unit has no item handler");
      h.assertTrue(items.insertItem(0, new ItemStack(Items.COBBLESTONE, 64), false).isEmpty(), "void unit refused items");
      h.runAfterDelay(2, () -> {
        h.assertTrue(items.insertItem(0, new ItemStack(Items.COBBLESTONE, 64), true).isEmpty(),
            "void unit is still full after ticking (never voids)");
        h.succeed();
      });
    });
  }

  // Duct hitboxes + wrench side

  @GameTest(template = EMPTY)
  public static void ductShapeFollowsConnections(GameTestHelper h) {
    BlockPos left = A;
    BlockPos right = A.east();
    h.setBlock(left, ModBlocks.COPPER_ITEM_DUCT.get());
    h.setBlock(right, ModBlocks.COPPER_ITEM_DUCT.get());
    h.setBlock(left.above(), Blocks.CHEST);
    h.runAfterDelay(5, () -> {
      BlockPos abs = h.absolutePos(left);
      ITransmitterProvider duct = be(h, left, ITransmitterProvider.class);
      h.assertTrue(duct.getTransmitter().getConnectionType(Direction.EAST) != ConnectionType.NONE, "ducts did not connect");
      VoxelShape shape = h.getBlockState(left).getShape(h.getLevel(), abs, CollisionContext.empty());
      h.assertTrue(shape.max(Direction.Axis.X) > 0.99, "outline does not include the east arm");
      h.assertTrue(shape.max(Direction.Axis.Y) > 0.99, "outline does not include the arm to the chest");
      h.assertTrue(shape.min(Direction.Axis.X) > 0.24, "outline has an arm on an unconnected side");

      // clicking the east arm should target EAST
      Vec3 armTop = Vec3.atLowerCornerOf(abs).add(0.9, 0.75, 0.5);
      h.assertTrue(DuctShapes.targetSide(abs, armTop, Direction.UP) == Direction.EAST, "arm click targets wrong side");
      Vec3 coreTop = Vec3.atLowerCornerOf(abs).add(0.5, 0.75, 0.5);
      h.assertTrue(DuctShapes.targetSide(abs, coreTop, Direction.UP) == Direction.UP, "core click targets wrong side");
      h.succeed();
    });
  }

  // Tanks

  @GameTest(template = EMPTY)
  public static void overfullTankAcceptsNothing(GameTestHelper h) {
    int[] cap = {1000};
    FluidTank t = new FluidTank(TankOption.BOTH, () -> cap[0]);
    t.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
    cap[0] = 500; // e.g. capacity augment removed
    int filled = t.fill(new FluidStack(Fluids.WATER, 100), IFluidHandler.FluidAction.EXECUTE);
    h.assertTrue(filled == 0, "over-full tank reported fill of " + filled);
    h.assertTrue(t.getFluidAmount() == 1000, "over-full tank lost fluid: " + t.getFluidAmount());

    FluidInventory inv = new FluidInventory();
    inv.add(new FluidTank(TankOption.BOTH, 1000)).add(new FluidTank(TankOption.BOTH, 1000));
    int sim = inv.fill(new FluidStack(Fluids.WATER, 1500), IFluidHandler.FluidAction.SIMULATE);
    h.assertTrue(sim == 1500, "multi-tank simulate reported " + sim + " for 1500 mB");
    h.succeed();
  }

  // Augments

  @GameTest(template = EMPTY)
  public static void augmentsModifyDevices(GameTestHelper h) {
    h.setBlock(A, ModBlocks.PULVERIZER.get());
    AugmentableDeviceBlockEntity device = be(h, A, AugmentableDeviceBlockEntity.class);
    int basePower = device.getActualPower();
    int baseCapacity = device.getEnergyCapacity();
    device.getAugments().setItem(0, new ItemStack(XItems.OVERCLOCKING_AUGMENT.get(), 1));
    device.getAugments().setItem(1, new ItemStack(XItems.OVERCLOCKING_AUGMENT.get(), 4)); // legacy stack: counts once
    device.getAugments().setItem(2, new ItemStack(XItems.COIL_AUGMENT.get(), 1));
    h.assertTrue(device.getActualPower() == AugmentItem.scale(AugmentItem.scale(basePower, 1.5), 1.5),
        "overclocking: power " + basePower + " -> " + device.getActualPower() + " (each slot must count once)");
    h.assertTrue(device.getEnergyCapacity() == baseCapacity * 2, "coil: capacity " + baseCapacity + " -> " + device.getEnergyCapacity());
    h.assertTrue(device.getAugments().getSlotLimit(3) == 1, "augment slots must hold a single augment");
    h.assertTrue(AugmentItem.scale(Integer.MAX_VALUE, 2.0) == Integer.MAX_VALUE, "augment math overflows");
    h.assertTrue(device.getAugments().isItemValid(2, new ItemStack(XItems.SILENCING_AUGMENT.get())), "silencing augment rejected");
    h.succeed();
  }

  // Security

  @GameTest(template = EMPTY)
  public static void privateDevicesAreLocked(GameTestHelper h) {
    h.setBlock(A, ModBlocks.SMELTER.get());
    AbstractDeviceBlockEntity device = be(h, A, AbstractDeviceBlockEntity.class);
    Player stranger = h.makeMockPlayer(GameType.SURVIVAL);
    h.assertFalse(DeviceAccess.isLockedFor(device, stranger), "public device locked");
    device.setSecurityMode(SecurityMode.PRIVATE, UUID.randomUUID());
    h.assertTrue(DeviceAccess.isLockedFor(device, stranger), "private device open to strangers");
    h.assertFalse(DeviceAccess.canConfigure(stranger, h.absolutePos(A)), "config accepted without the GUI open");
    h.succeed();
  }

  // Recipe overrides

  @GameTest(template = EMPTY)
  public static void brokenTen4RecipesAreFixed(GameTestHelper h) {
    var rm = h.getLevel().getRecipeManager();
    var tin = rm.byKey(net.minecraft.resources.ResourceLocation.parse("ten4:smelting/tin_ingot_from_dust"));
    h.assertTrue(tin.isPresent(), "tin dust smelting recipe failed to load");
    h.assertTrue(tin.get().value().getResultItem(h.getLevel().registryAccess()).is(ModItems.TIN_INGOT.get()),
        "tin dust does not smelt into tin ingots");
    var leather = rm.byKey(net.minecraft.resources.ResourceLocation.parse("ten4:pressing/packing/leather"));
    h.assertTrue(leather.isPresent() && leather.get().value() instanceof com.hypothetic.ten4.api.recipe.IComplexRecipe r
        && r.itemInputs().getFirst().test(new ItemStack(Items.RABBIT_HIDE, 4)), "leather packing recipe still uses a non-existent item");
    h.succeed();
  }

  // New machines

  @GameTest(template = EMPTY, timeoutTicks = 500)
  public static void crucibleMeltsNetherrack(GameTestHelper h) {
    h.setBlock(A, XBlocks.CRUCIBLE.get());
    AbstractDeviceBlockEntity crucible = be(h, A, AbstractDeviceBlockEntity.class);
    crucible.setEnergy(crucible.getEnergyCapacity());
    ((IItemHandlerModifiableView) () -> crucible).set(0, new ItemStack(Items.NETHERRACK));
    h.succeedWhen(() -> {
      FluidStack out = tank(crucible, 0).getFluid();
      h.assertTrue(out.is(Fluids.LAVA) && out.getAmount() == 250, "crucible has " + out.getAmount() + " mB");
      h.assertTrue(crucible.getInventory().getStackInSlot(0).isEmpty(), "netherrack not consumed");
    });
  }

  @GameTest(template = EMPTY, timeoutTicks = 300)
  public static void solidifierFreezesWater(GameTestHelper h) {
    h.setBlock(A, XBlocks.SOLIDIFIER.get());
    AbstractDeviceBlockEntity solidifier = be(h, A, AbstractDeviceBlockEntity.class);
    solidifier.setEnergy(solidifier.getEnergyCapacity());
    tank(solidifier, 0).setFluid(new FluidStack(Fluids.WATER, 1000));
    h.succeedWhen(() -> h.assertTrue(solidifier.getInventory().getStackInSlot(1).is(Items.ICE), "no ice produced"));
  }

  @GameTest(template = EMPTY, timeoutTicks = 100)
  public static void biomassGeneratorBurnsLogs(GameTestHelper h) {
    h.setBlock(A, XBlocks.BIOMASS_GENERATOR.get());
    AbstractDeviceBlockEntity gen = be(h, A, AbstractDeviceBlockEntity.class);
    h.assertTrue(gen.getInventory().isItemValid(0, new ItemStack(Items.OAK_LOG)), "logs are not biomass");
    h.assertFalse(gen.getInventory().isItemValid(0, new ItemStack(Items.COBBLESTONE)), "cobblestone accepted as biomass");
    ((IItemHandlerModifiableView) () -> gen).set(0, new ItemStack(Items.OAK_LOG, 2));
    h.succeedWhen(() -> {
      h.assertTrue(gen.getEnergy() > 0, "biomass generator produced no energy");
      h.assertTrue(gen.getInventory().getStackInSlot(0).getCount() < 2, "log was not consumed");
    });
  }

  @GameTest(template = EMPTY, timeoutTicks = 200)
  public static void farmManagerHarvestsAndReplants(GameTestHelper h) {
    BlockPos farm = A.above();
    BlockPos crop = A.above().east(2);
    BlockPos empty = A.above().west(2);
    for (BlockPos soil : new BlockPos[] {crop.below(), empty.below()}) {
      h.setBlock(soil, Blocks.FARMLAND);
    }
    h.setBlock(A.below(), Blocks.STONE);
    h.setBlock(crop, Blocks.WHEAT.defaultBlockState().setValue(net.minecraft.world.level.block.CropBlock.AGE, 7));
    h.setBlock(farm, XBlocks.FARM_MANAGER.get());
    AbstractDeviceBlockEntity fm = be(h, farm, AbstractDeviceBlockEntity.class);
    fm.setEnergy(fm.getEnergyCapacity());
    ((IItemHandlerModifiableView) () -> fm).set(0, new ItemStack(Items.WHEAT_SEEDS, 4));
    h.succeedWhen(() -> {
      var wheat = h.getBlockState(crop);
      h.assertTrue(wheat.is(Blocks.WHEAT) && wheat.getValue(net.minecraft.world.level.block.CropBlock.AGE) == 0,
          "mature wheat was not harvested and replanted");
      h.assertTrue(h.getBlockState(empty).is(Blocks.WHEAT), "seed was not planted on empty farmland");
      boolean gotWheat = false;
      for (int i = 3; i < 9; i++) {
        gotWheat |= fm.getInventory().getStackInSlot(i).is(Items.WHEAT);
      }
      h.assertTrue(gotWheat, "harvest did not reach the output slots");
    });
  }

  @GameTest(template = EMPTY, timeoutTicks = 60)
  public static void solarGeneratorFollowsSun(GameTestHelper h) {
    h.getLevel().setDayTime(6000); // noon
    h.setBlock(A, XBlocks.SOLAR_GENERATOR.get());
    SolarGeneratorBlockEntity solar = be(h, A, SolarGeneratorBlockEntity.class);
    h.runAfterDelay(30, () -> {
      int sun = SolarGeneratorBlockEntity.computeSunlight(h.getLevel(), h.absolutePos(A));
      if (sun > 0) {
        h.assertTrue(solar.getEnergy() > 0, "solar generator saw " + sun + "% sun but produced nothing");
      }
      h.assertTrue(solar.isAutoEject(0), "solar generator should ship with energy auto-eject on");
      h.succeed();
    });
  }

  /** Puts an item straight into a device slot. */
  @FunctionalInterface
  private interface IItemHandlerModifiableView {
    AbstractDeviceBlockEntity device();

    default void set(int slot, ItemStack stack) {
      ((net.neoforged.neoforge.items.IItemHandlerModifiable) device().getInventory()).setStackInSlot(slot, stack);
    }
  }
}
