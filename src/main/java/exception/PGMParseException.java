package exception;

import java.io.IOException;

/**
 * Exception thrown when an error occurs while parsing a PGM (Portable GrayMap)
 * file.
 * This exception indicates issues such as incorrect format, invalid data, or
 * other parsing errors specific to PGM files.
 */
public class PGMParseException extends IOException {

    /**
     * Constructs a new PGMParseException with a specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public PGMParseException(String message) {
        super(message);
    }

}
