package com.ten4ext.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/** Skips the JEI mixin when JEI isn't installed. */
public class Ten4ExtMixinPlugin implements IMixinConfigPlugin {
  private static final Logger LOGGER = LoggerFactory.getLogger("TEN4 Extension");

  @Override
  public void onLoad(String mixinPackage) {
  }

  @Override
  public String getRefMapperConfig() {
    return null;
  }

  @Override
  public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
    if (mixinClassName.endsWith("JeiModCategoryMixin")) {
      return isPresent("mezz/jei/api/IModPlugin.class");
    }
    return true;
  }

  @Override
  public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
  }

  @Override
  public List<String> getMixins() {
    return null;
  }

  @Override
  public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
  }

  @Override
  public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    LOGGER.debug("Applied {} to {}", mixinClassName, targetClassName);
  }

  private static boolean isPresent(String resource) {
    return Ten4ExtMixinPlugin.class.getClassLoader().getResource(resource) != null;
  }
}
