package com.ten4ext.duct;

import com.hypothetic.ten4.api.transmission.ConnectionType;
import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import com.hypothetic.ten4.api.transmission.Transmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** Duct hitboxes matching the model: core, one arm per connection, flange for pull. */
public final class DuctShapes {
  public static final VoxelShape CORE = Block.box(4, 4, 4, 12, 12, 12);
  private static final double CORE_MIN = 4 / 16D;
  private static final double CORE_MAX = 12 / 16D;
  private static final double EPS = 1.0E-4;

  // 2 bits per side: 0 = none, 1 = arm, 2 = arm + pull flange.
  private static final VoxelShape[] CACHE = new VoxelShape[1 << 12];
  private static final VoxelShape[] ARMS = new VoxelShape[6];
  private static final VoxelShape[] FLANGES = new VoxelShape[6];

  static {
    for (Direction d : Direction.values()) {
      ARMS[d.ordinal()] = arm(d, 4, 12, 0, 4);
      FLANGES[d.ordinal()] = arm(d, 3, 13, 0, 3);
    }
  }

  private DuctShapes() {
  }

  /** Box spanning [lo,hi] on the two cross axes and [near,far] measured inward from side {@code d}. */
  private static VoxelShape arm(Direction d, double lo, double hi, double near, double far) {
    return switch (d) {
      case DOWN -> Block.box(lo, near, lo, hi, far, hi);
      case UP -> Block.box(lo, 16 - far, lo, hi, 16 - near, hi);
      case NORTH -> Block.box(lo, lo, near, hi, hi, far);
      case SOUTH -> Block.box(lo, lo, 16 - far, hi, hi, 16 - near);
      case WEST -> Block.box(near, lo, lo, far, hi, hi);
      case EAST -> Block.box(16 - far, lo, lo, 16 - near, hi, hi);
    };
  }

  public static VoxelShape forDuct(BlockGetter level, BlockPos pos) {
    Transmitter<?, ?, ?> t = transmitterAt(level, pos);
    if (t == null) {
      return CORE;
    }
    int key = 0;
    for (Direction d : Direction.values()) {
      ConnectionType ct = t.getConnectionType(d);
      int bits = ct == ConnectionType.NONE ? 0 : ct == ConnectionType.PULL ? 2 : 1;
      key |= bits << (d.ordinal() * 2);
    }
    VoxelShape shape = CACHE[key];
    if (shape == null) {
      shape = CORE;
      for (Direction d : Direction.values()) {
        int bits = (key >> (d.ordinal() * 2)) & 3;
        if (bits >= 1) {
          shape = Shapes.or(shape, ARMS[d.ordinal()]);
        }
        if (bits == 2) {
          shape = Shapes.or(shape, FLANGES[d.ordinal()]);
        }
      }
      shape = shape.optimize();
      CACHE[key] = shape;
    }
    return shape;
  }

  /** Side a click is for: the arm's side if an arm was hit, otherwise the hit face. */
  public static Direction targetSide(BlockPos pos, Vec3 hitLocation, Direction hitFace) {
    double x = hitLocation.x - pos.getX();
    double y = hitLocation.y - pos.getY();
    double z = hitLocation.z - pos.getZ();
    // nudge inside so hits exactly on a face count
    x -= hitFace.getStepX() * EPS;
    y -= hitFace.getStepY() * EPS;
    z -= hitFace.getStepZ() * EPS;

    // the arm we hit is the axis sticking out furthest (pull flanges are wider than the core)
    Direction best = hitFace;
    double bestOut = 0;
    double[] coords = {x, y, z};
    Direction.Axis[] axes = {Direction.Axis.X, Direction.Axis.Y, Direction.Axis.Z};
    for (int i = 0; i < 3; i++) {
      double c = coords[i];
      double out = c < CORE_MIN ? CORE_MIN - c : c > CORE_MAX ? c - CORE_MAX : 0;
      if (out > bestOut) {
        bestOut = out;
        best = Direction.fromAxisAndDirection(axes[i], c > CORE_MAX ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);
      }
    }
    return best;
  }

  private static @Nullable Transmitter<?, ?, ?> transmitterAt(BlockGetter level, BlockPos pos) {
    BlockEntity be;
    try {
      be = level.getBlockEntity(pos);
    } catch (RuntimeException e) {
      // some BlockGetters don't allow BE access, use the core
      return null;
    }
    if (be instanceof ITransmitterProvider provider) {
      return provider.getTransmitter();
    }
    return null;
  }
}
