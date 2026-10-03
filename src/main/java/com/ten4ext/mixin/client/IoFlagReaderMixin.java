package com.ten4ext.mixin.client;

import com.hypothetic.ten4.api.blockentity.device.AbstractDeviceBlockEntity;
import com.hypothetic.ten4.core.client.builtin.IoFlagReader;
import com.ten4ext.client.io.IoStateExt;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(IoFlagReader.class)
public abstract class IoFlagReaderMixin implements IoStateExt {
  @Shadow
  int type;

  @Unique
  private @Nullable AbstractDeviceBlockEntity ten4ext$device;

  @Shadow
  abstract int packedFor(int type);

  @Override
  public @Nullable AbstractDeviceBlockEntity ten4ext$getDevice() {
    return ten4ext$device;
  }

  @Override
  public void ten4ext$setDevice(@Nullable AbstractDeviceBlockEntity device) {
    ten4ext$device = device;
  }

  @Override
  public int ten4ext$getType() {
    return type;
  }

  @Override
  public void ten4ext$setType(int t) {
    type = t;
  }

  @Override
  public int ten4ext$packedFaces(int t) {
    return packedFor(t);
  }
}
