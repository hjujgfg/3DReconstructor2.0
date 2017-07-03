package org.hjujgfg.exceptions;

/**
 * Created by egor.lapidus on 30/06/2017.
 */
public class DirectoryCreationException extends Exception {

    public DirectoryCreationException(String cause) {
        super(cause);
    }
    public DirectoryCreationException(String cause, Throwable e) {
        super(cause, e);
    }
}
