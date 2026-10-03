package com.ten4ext;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Ten4ExtConfig {
  public static final ModConfigSpec SPEC;
  public static final Ten4ExtConfig COMMON;

  static {
    ModConfigSpec.Builder b = new ModConfigSpec.Builder();
    COMMON = new Ten4ExtConfig(b);
    SPEC = b.build();
  }

  public final Augments augments;
  public final Machines machines;
  public final Ducts ducts;

  private Ten4ExtConfig(ModConfigSpec.Builder b) {
    augments = new Augments(b);
    machines = new Machines(b);
    ducts = new Ducts(b);
  }

  public static class Augments {
    public final ModConfigSpec.DoubleValue overclocking;
    public final ModConfigSpec.DoubleValue coil;
    public final ModConfigSpec.DoubleValue piston;
    public final ModConfigSpec.DoubleValue vacuum;
    public final ModConfigSpec.DoubleValue tank;

    Augments(ModConfigSpec.Builder b) {
      b.comment("Multipliers applied once per augment item installed (augments stack multiplicatively, results are capped at Integer.MAX_VALUE).")
          .push("augments");
      overclocking = b.comment("Overclocking Augment: multiplies device power (machines run faster, generators produce more FE/t; FE per operation is unchanged).")
          .defineInRange("overclocking", 1.5, 1.0, 16.0);
      coil = b.comment("Coil Augment: multiplies energy capacity and energy throughput.")
          .defineInRange("coil", 2.0, 1.0, 16.0);
      piston = b.comment("Piston Augment: multiplies item throughput (auto-eject / auto-extract speed).")
          .defineInRange("piston", 2.0, 1.0, 64.0);
      vacuum = b.comment("Vacuum Augment: multiplies fluid throughput.")
          .defineInRange("vacuum", 2.0, 1.0, 64.0);
      tank = b.comment("Tank Augment: multiplies tank capacity.")
          .defineInRange("tank", 2.0, 1.0, 16.0);
      b.pop();
    }
  }

  public static class Machines {
    public final ModConfigSpec.IntValue solarPower;
    public final ModConfigSpec.IntValue solarEnergyCapacity;
    public final ModConfigSpec.IntValue solarEnergyThroughput;

    public final ModConfigSpec.IntValue biomassPower;
    public final ModConfigSpec.IntValue biomassEnergyCapacity;
    public final ModConfigSpec.IntValue biomassEnergyThroughput;

    public final ModConfigSpec.IntValue farmPower;
    public final ModConfigSpec.IntValue farmEnergyPerAction;
    public final ModConfigSpec.IntValue farmEnergyCapacity;
    public final ModConfigSpec.IntValue farmEnergyThroughput;
    public final ModConfigSpec.IntValue farmRadius;


    public final ModConfigSpec.IntValue cruciblePower;
    public final ModConfigSpec.IntValue crucibleEnergyCapacity;
    public final ModConfigSpec.IntValue crucibleEnergyThroughput;
    public final ModConfigSpec.IntValue crucibleTankCapacity;
    public final ModConfigSpec.IntValue crucibleFluidThroughput;

    public final ModConfigSpec.IntValue solidifierPower;
    public final ModConfigSpec.IntValue solidifierEnergyCapacity;
    public final ModConfigSpec.IntValue solidifierEnergyThroughput;
    public final ModConfigSpec.IntValue solidifierTankCapacity;
    public final ModConfigSpec.IntValue solidifierFluidThroughput;

    Machines(ModConfigSpec.Builder b) {
      b.push("machines");
      solarPower = b.comment("Solar Generator FE/t at noon under a clear sky.").defineInRange("solarPower", 20, 0, Integer.MAX_VALUE);
      solarEnergyCapacity = b.defineInRange("solarEnergyCapacity", 20000, 1, Integer.MAX_VALUE);
      solarEnergyThroughput = b.defineInRange("solarEnergyThroughput", 200, 0, Integer.MAX_VALUE);

      biomassPower = b.comment("Biomass Generator FE/t while burning.").defineInRange("biomassPower", 40, 1, Integer.MAX_VALUE);
      biomassEnergyCapacity = b.defineInRange("biomassEnergyCapacity", 40000, 1, Integer.MAX_VALUE);
      biomassEnergyThroughput = b.defineInRange("biomassEnergyThroughput", 400, 0, Integer.MAX_VALUE);

      farmPower = b.comment("Farm Manager base power. Overclocking raises it, which shortens the time between field sweeps.")
          .defineInRange("farmPower", 20, 1, Integer.MAX_VALUE);
      farmEnergyPerAction = b.comment("FE used per crop harvested or seed planted.").defineInRange("farmEnergyPerAction", 200, 0, Integer.MAX_VALUE);
      farmEnergyCapacity = b.defineInRange("farmEnergyCapacity", 20000, 1, Integer.MAX_VALUE);
      farmEnergyThroughput = b.defineInRange("farmEnergyThroughput", 200, 0, Integer.MAX_VALUE);
      farmRadius = b.comment("Blocks worked in each direction (4 = a 9x9 field around the Farm Manager).").defineInRange("farmRadius", 4, 1, 16);


      cruciblePower = b.defineInRange("cruciblePower", 20, 1, Integer.MAX_VALUE);
      crucibleEnergyCapacity = b.defineInRange("crucibleEnergyCapacity", 10000, 1, Integer.MAX_VALUE);
      crucibleEnergyThroughput = b.defineInRange("crucibleEnergyThroughput", 100, 0, Integer.MAX_VALUE);
      crucibleTankCapacity = b.defineInRange("crucibleTankCapacity", 10000, 1000, Integer.MAX_VALUE);
      crucibleFluidThroughput = b.defineInRange("crucibleFluidThroughput", 1000, 0, Integer.MAX_VALUE);

      solidifierPower = b.defineInRange("solidifierPower", 20, 1, Integer.MAX_VALUE);
      solidifierEnergyCapacity = b.defineInRange("solidifierEnergyCapacity", 10000, 1, Integer.MAX_VALUE);
      solidifierEnergyThroughput = b.defineInRange("solidifierEnergyThroughput", 100, 0, Integer.MAX_VALUE);
      solidifierTankCapacity = b.defineInRange("solidifierTankCapacity", 10000, 1000, Integer.MAX_VALUE);
      solidifierFluidThroughput = b.defineInRange("solidifierFluidThroughput", 1000, 0, Integer.MAX_VALUE);
      b.pop();
    }
  }

  public static class Ducts {
    public final ModConfigSpec.IntValue titaniumEnergyBuffer;
    public final ModConfigSpec.IntValue titaniumEnergyThroughput;
    public final ModConfigSpec.IntValue titaniumFluidBuffer;
    public final ModConfigSpec.IntValue titaniumFluidThroughput;
    public final ModConfigSpec.IntValue titaniumItemStackSize;
    public final ModConfigSpec.IntValue titaniumItemTicksPerBlock;

    Ducts(ModConfigSpec.Builder b) {
      b.comment("Titanium duct tier. A network's throughput is limited by its slowest duct, so mixing tiers bottlenecks at copper.")
          .push("ducts");
      titaniumEnergyBuffer = b.defineInRange("titaniumEnergyBuffer", 1000, 0, Integer.MAX_VALUE);
      titaniumEnergyThroughput = b.defineInRange("titaniumEnergyThroughput", 1000, 0, Integer.MAX_VALUE);
      titaniumFluidBuffer = b.defineInRange("titaniumFluidBuffer", 1000, 0, Integer.MAX_VALUE);
      titaniumFluidThroughput = b.defineInRange("titaniumFluidThroughput", 1000, 0, Integer.MAX_VALUE);
      titaniumItemStackSize = b.comment("Items moved per transit (copper: 1).").defineInRange("titaniumItemStackSize", 8, 1, 64);
      titaniumItemTicksPerBlock = b.comment("Ticks an item needs to cross one duct (copper: 20).").defineInRange("titaniumItemTicksPerBlock", 8, 1, 200);
      b.pop();
    }
  }
}
