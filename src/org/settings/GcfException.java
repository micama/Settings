package org.settings;

/**
 * Defines an exception to throw when something related to the
 * configuration file goes wrong.
 * @author Miguel Cardoso Martins
 */
class GcfException extends RuntimeException {

    public GcfException(String message) {
        super("*** " + message + " ***");
    }
    
}