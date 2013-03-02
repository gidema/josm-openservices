package org.openstreetmap.josm.plugins.openservices.test;

import org.junit.Test;
import org.openstreetmap.josm.Main;

import sun.org.mozilla.javascript.Context;
import sun.org.mozilla.javascript.Scriptable;

public class OpenServicesTest {
  Main main;
  @Test
  public void testThis() {
    Context cx = Context.enter();
    Scriptable scope = cx.initStandardObjects();
  }
}
