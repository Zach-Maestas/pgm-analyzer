import java.io.FileNotFoundException;
import exception.PGMParseException;
import run.AppRunner;

/**
 * Entry point for the CS 214 project application, which initializes and runs
 * the application through the AppRunner. Handles all major exceptions related
 * to file parsing, arithmetic errors, and invalid arguments.
 */
public class CS_214_Project {

    /**
     * Main method to run the application. Executes the AppRunner's `run` method
     * and handles any potential exceptions, providing informative error messages
     * for each case.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        try {
            AppRunner.run(args);
        } catch (FileNotFoundException e) {
            System.err.println("ERROR! File Not Found:\n" + e.getMessage());
        } catch (PGMParseException e) {
            System.err.println("ERROR! File Parse:\n" + e.getMessage());
        } catch (ArithmeticException e) {
            System.err.println("ERROR! Arithmetic:\n" + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("ERROR! Illegal Argument:\n" + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR! An Unexpected Error Occurred: " + e.getMessage());
        }
    }
}