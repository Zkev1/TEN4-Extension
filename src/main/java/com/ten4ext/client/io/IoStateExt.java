package com.ten4ext.client.io;

import com.hypothetic.ten4.api.blockentity.device.AbstractDeviceBlockEntity;
import org.jetbrains.annotations.Nullable;

/** Extra state added to TEN4's IoFlagReader. */
public interface IoStateExt {
  int ENERGY = 0;
  int ITEM = 1;
  int FLUID = 2;

  static boolean supports(@Nullable AbstractDeviceBlockEntity device, int type) {
    if (device == null) {
      return true;
    }
    return switch (type) {
      case ENERGY -> device.getEnergyStorage(null) != null;
      case ITEM -> device.getItemHandler(null) != null;
      default -> device.getFluidHandler(null) != null;
    };
  }

  @Nullable AbstractDeviceBlockEntity ten4ext$getDevice();

  void ten4ext$setDevice(@Nullable AbstractDeviceBlockEntity device);

  int ten4ext$getType();

  void ten4ext$setType(int type);

  int ten4ext$packedFaces(int type);

  /** Next/previous resource type the device has. */
  default void ten4ext$cycleType(int step) {
    int t = ten4ext$getType();
    for (int i = 0; i < 3; i++) {
      t = Math.floorMod(t + step, 3);
      if (supports(ten4ext$getDevice(), t)) {
        ten4ext$setType(t);
        return;
      }
    }
  }
}
