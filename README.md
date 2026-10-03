# TEN4 Extension

An addon for [Technical Engineering 4](https://github.com/licphel/technical-engineering-4) on NeoForge 1.21.1.
It fixes a bunch of TEN4's bugs and adds the stuff I felt was missing: augments, new machines, faster ducts,
JEI energy info and recipes for other tech mods.

Made by **Zkev1**.

## Requirements

- Minecraft 1.21.1, NeoForge 21.1.x
- Technical Engineering 4 (26.1.9 or newer)
- JEI (optional)

Install it next to TEN4 on both the client and the server.

## Fixes

- Duct hitboxes follow their connections, so the wrench hits the side you're actually looking at.
  Wrench an arm to disconnect it, click the bare core to reconnect.
- Liquid Unit no longer duplicates fluid with buckets.
- Stacked Liquid/Energy Units can't be used to dupe contents.
- The IO panel shows each side's real block texture with a colored frame for the mode.
  Left-click next, right-click previous, middle-click off.
- Private mode actually keeps other players out.
- The Void Unit voids instead of filling up.
- Tanks don't lose or create fluid when augments are removed or fluids move between multi-tank machines.
- Device settings can only be changed by a player who has that device open.
- Dismantled devices list their augments in the tooltip.
- Tin dust smelts into tin ingots, and the leather pressing recipe works.
- Right-click a device with a wrench to rotate it (sneak + right-click still picks it up).

## Additions

- **Augments:** Overclocking, Coil, Piston, Vacuum, Tank and Silencing. One per slot, max 4 per machine.
- **Solar Generator**, **Biomass Generator**, **Farm Manager**, **Crucible** and **Solidifier**.
- **Titanium ducts** for energy, items and fluids.
- The **Fuel Generator** also burns lava, Toran Concentrate and Nutrient Solution.
- **JEI:** hover a machine's energy bar to see FE/t used or generated.
- **Mod compat:** Pulverizer, Press and Fuel Generator recipes for AllTheOres, Thermal, Mekanism,
  Immersive Engineering, Create, Immersive Petroleum, PneumaticCraft and Create Crafts & Additions.
  They only load when those mods are installed.

Most numbers can be changed in `config/ten4ext-common.toml`.

If a TEN4 update changes code one of the fixes depends on, that fix turns itself off and the rest keeps working.

## Building

Download `ten4-26.1.9-neoforge-1.21.1.jar` from [Modrinth](https://modrinth.com/mod/technical-engineering-4)
into `libs/`, then:

```bash
./gradlew build
```

The jar ends up in `build/libs/`. Needs JDK 21.

To run the in-game tests:

```bash
./gradlew runGameTestServer
```

## License

GPL-3.0. The augment textures are based on Technical Engineering 3's art (GPL-3.0). TEN4's own textures are
not included, the addon uses the ones from your TEN4 install. See `NOTICE.md`.
