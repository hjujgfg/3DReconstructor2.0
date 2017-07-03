package org.hjujgfg.io;

import org.hjujgfg.exceptions.DirectoryCreationException;

/**
 * Created by egor.lapidus on 30/06/2017.
 */
public class DirectoryMain {

    public static void main (String[] args) {
        DirectoryHelper hlp = new DirectoryHelper();
        try {
            hlp.createDefaultDirs();
        } catch (DirectoryCreationException ex) {}
    }
}
