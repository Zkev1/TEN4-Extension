package com.ten4ext.client.io;

import com.hypothetic.ten4.core.client.builtin.IoFlagReader;

/** Exposes the IoFlagReader held by TEN4's (package-private) IO type button. */
public interface IoStateHolder {
  IoFlagReader ten4ext$state();
}
