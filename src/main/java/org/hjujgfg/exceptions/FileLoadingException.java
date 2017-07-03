package org.hjujgfg.exceptions;

/**
 * Created by egor.lapidus on 30/06/2017.
 */
public class FileLoadingException extends Exception {

    public FileLoadingException () {
        super();
    }

    public FileLoadingException (String message, Throwable cause) {
        super(message, cause);
    }

    public FileLoadingException (Throwable cause) {
        super (cause);
    }
}
