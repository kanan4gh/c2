/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dg.util;

import org.openide.windows.IOProvider;
import org.openide.windows.OutputWriter;

/**
 *
 * @author akira
 */
public class DGLogger {

    private static final DGLogger instance = new DGLogger();

    public static DGLogger getDefault() {
        return instance;
    }

    private OutputWriter w;

    private DGLogger() {
        w = IOProvider.getDefault().getStdOut();
    }

    public void println(String msg) {
        w.println(msg);
    }

}
