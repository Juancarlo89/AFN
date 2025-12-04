package org.netbeans.lib.awtextra;

import java.awt.Rectangle;

/** Minimal stub of NetBeans AbsoluteConstraints to allow compilation. */
public class AbsoluteConstraints extends Rectangle {
    public AbsoluteConstraints(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public AbsoluteConstraints(int x, int y) {
        super(x, y, 0, 0);
    }
}
