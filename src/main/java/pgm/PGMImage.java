package pgm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import exception.PGMParseException;
import histogram.Histogram;
import histogram.HistogramUtils;

/**
 * Represents a PGM (Portable GrayMap) image with methods for parsing from a
 * file,
 * generating histograms, and accessing pixel data.
 */
public class PGMImage {

    /**
     * Index in the file name where the class label is expected.
     */
    private static final int CLASS_INDEX = 5;

    /**
     * Name of the file from which the image was loaded.
     */
    private final String FILE_NAME;

    /**
     * 2D array representing the grayscale pixel values of the image.
     */
    private final int[][] PIXELS_ARRAY;

    /**
     * Histogram array representing the frequency of pixel values in the image.
     */
    private int[] histogramArray;

    /**
     * Width of the image, currently set to 128 pixels for all images.
     */
    private final int WIDTH;

    /**
     * Height of the image, currently set to 128 pixels for all images.
     */
    private final int HEIGHT;

    /**
     * Class label for the image, extracted from the file name.
     */
    private int classLabel = -1;

    /**
     * Constructs a PGMImage from given parameters. This constructor is private and
     * is intended to be used internally by the `fromFile` method.
     *
     * @param filename    the name of the file containing the image
     * @param width       the width of the image
     * @param height      the height of the image
     * @param pixelsArray a 2D array of grayscale pixel values
     * @throws PGMParseException     if there is an error parsing the PGM file
     * @throws FileNotFoundException if the file cannot be found
     */
    private PGMImage(String filename, int width, int height, int[][] pixelsArray)
            throws PGMParseException, FileNotFoundException {
        this.FILE_NAME = filename;
        this.WIDTH = width;
        this.HEIGHT = height;
        this.PIXELS_ARRAY = pixelsArray;
    }

    /**
     * Creates a PGMImage object from a specified file by parsing its content.
     *
     * @param filename the name of the PGM file to parse
     * @return a PGMImage object with the parsed image data
     * @throws PGMParseException     if there is an error parsing the file
     * @throws FileNotFoundException if the file cannot be found
     */
    public static PGMImage fromFile(String filename) throws PGMParseException, FileNotFoundException {
        File file = new File(filename);
        PGMParser parser = new PGMParser(file);

        return new PGMImage(parser.filename, parser.width, parser.height, parser.pixelsArray);
    }

    /**
     * Returns the 2D array of pixel values for the image.
     *
     * @return a 2D array of grayscale pixel values
     */
    public int[][] getPixelsArray() {
        return PIXELS_ARRAY;
    }

    /**
     * Returns the histogram array for the image, generating it if not already
     * created.
     *
     * @return an array representing the histogram of the image
     */
    public int[] getHistogramArray() {
        if (histogramArray == null) {
            histogramArray = new Histogram(PIXELS_ARRAY).getHistogramArray();
        }
        return histogramArray;
    }

    /**
     * Returns the normalized histogram for the image.
     *
     * @return an array representing the normalized histogram of the image
     */
    public double[] getNormalizedHistogram() {
        return HistogramUtils.normalizeHistogram(getHistogramArray());
    }

    /**
     * Returns the width of the image.
     *
     * @return the width of the image
     */
    public int getWidth() {
        return WIDTH;
    }

    /**
     * Returns the height of the image.
     *
     * @return the height of the image
     */
    public int getHeight() {
        return HEIGHT;
    }

    /**
     * Returns the class label for the image, extracted from the file name.
     *
     * @return the class label of the image
     * @throws IllegalArgumentException if the file name does not contain a valid
     *                                  class label
     */
    public int getClassLabel() {
        if (classLabel == -1) {
            char classChar = FILE_NAME.charAt(CLASS_INDEX);
            if (!(classChar >= '0' && classChar <= '9')) {
                throw new IllegalArgumentException("Invalid class label format in file '" + FILE_NAME + "'");
            }
            classLabel = Character.getNumericValue(classChar);
        }
        return classLabel;
    }

    /**
     * Returns the file name of the image.
     *
     * @return the file name of the image
     */
    @Override
    public String toString() {
        return FILE_NAME;
    }

    /**
     * Parses a PGM (Portable GrayMap) file, ensuring it adheres to the expected
     * format.
     * The parser checks for the correct magic number, width, height, and pixel
     * values
     * within the allowed range. If the file format is invalid, it throws a
     * {@link PGMParseException}.
     */
    private static class PGMParser {

        /**
         * Expected PGM file magic number, which indicates the PGM format.
         */
        private static final String REQUIRED_MAGIC_NUMBER = "P2";

        /**
         * Maximum allowed pixel value in a PGM file.
         */
        private static final int MAX_PIXEL_VALUE = 255;

        /**
         * Required height of the PGM image. This implementation expects all images to
         * be 128 pixels high.
         */
        private static final int REQUIRED_HEIGHT = 128;

        /**
         * Required width of the PGM image. This implementation expects all images to be
         * 128 pixels wide.
         */
        private static final int REQUIRED_WIDTH = 128;

        /**
         * Name of the file being parsed.
         */
        private final String filename;

        /**
         * 2D array holding the parsed grayscale pixel values from the PGM file.
         */
        private int[][] pixelsArray;

        /**
         * Width of the PGM image, parsed from the file.
         */
        private int width;

        /**
         * Height of the PGM image, parsed from the file.
         */
        private int height;

        /**
         * Constructs a PGMParser for the specified PGM file, initializing fields
         * and parsing the file content to validate its format and extract pixel data.
         *
         * @param pgmFile the PGM file to parse
         * @throws PGMParseException     if the file format is invalid
         * @throws FileNotFoundException if the file cannot be found
         */
        public PGMParser(File pgmFile) throws PGMParseException, FileNotFoundException {
            this.filename = pgmFile.getName();
            parseFile(pgmFile);
        }

        /**
         * Parses the content of the PGM file, including its header and pixel data.
         *
         * @param pgmFile the PGM file to parse
         * @throws PGMParseException     if the file format is invalid or if pixel
         *                               values are out of range
         * @throws FileNotFoundException if the file cannot be found
         */
        private void parseFile(File pgmFile) throws PGMParseException, FileNotFoundException {
            try (Scanner scanner = new Scanner(pgmFile)) {
                validateFileNotEmpty(pgmFile);
                parseHeader(scanner);
                parsePixels(scanner);
            }
        }

        /**
         * Parses the header of the PGM file, which includes the magic number, width,
         * height, and maximum pixel value. Verifies that these values match the
         * required
         * format and dimensions.
         *
         * @param scanner the Scanner object reading the PGM file
         * @throws PGMParseException if the header format is invalid
         */
        private void parseHeader(Scanner scanner) throws PGMParseException {
            try {
                parseMagicNumber(scanner);
                parseWidthAndHeight(scanner);
                parseMaxPixelValue(scanner);
                pixelsArray = new int[height][width];
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new PGMParseException(
                        "Invalid header format in file '" + filename + "' (width, height), or missing tokens");
            }
        }

        /**
         * Parses the magic number to confirm the PGM format. Only files starting with
         * the "P2" magic number are accepted.
         *
         * @param scanner the Scanner object reading the PGM file
         * @throws PGMParseException if the magic number is incorrect
         */
        private void parseMagicNumber(Scanner scanner) throws PGMParseException {
            String magicNumber = scanner.next();
            if (!magicNumber.equals(REQUIRED_MAGIC_NUMBER)) {
                throw new PGMParseException(
                        "'" + filename + "' must start with '" + REQUIRED_MAGIC_NUMBER + "'\nFound: " + magicNumber);
            }
        }

        /**
         * Parses the width and height of the PGM image. Ensures that they match the
         * required
         * dimensions of 128x128.
         *
         * @param scanner the Scanner object reading the PGM file
         * @throws PGMParseException if the dimensions do not match the expected values
         */
        private void parseWidthAndHeight(Scanner scanner)
                throws PGMParseException, NumberFormatException, NoSuchElementException {
            width = Integer.parseInt(scanner.next());
            height = Integer.parseInt(scanner.next());
            if (width != REQUIRED_WIDTH || height != REQUIRED_HEIGHT) {
                throw new PGMParseException(
                        "'" + filename + "'' dimensions must be " + REQUIRED_WIDTH + "x" + REQUIRED_HEIGHT
                                + " Found: " + width + "x" + height);
            }
        }

        /**
         * Parses the maximum pixel value specified in the PGM file. This value must be
         * 255.
         *
         * @param scanner the Scanner object reading the PGM file
         * @throws PGMParseException if the maximum pixel value is not 255
         */
        private void parseMaxPixelValue(Scanner scanner)
                throws PGMParseException, NumberFormatException, NoSuchElementException {
            int maxPixelVal = Integer.parseInt(scanner.next());
            if (maxPixelVal != MAX_PIXEL_VALUE) {
                throw new PGMParseException(
                        "'" + filename + "' maximum pixel value must be " + MAX_PIXEL_VALUE + " Found: " + maxPixelVal);
            }
        }

        /**
         * Parses the pixel values of the PGM image, populating the `pixelsArray` field.
         * Ensures that all values are within the allowed range [0, 255].
         *
         * @param scanner the Scanner object reading the PGM file
         * @throws PGMParseException if pixel values are out of range or if the file has
         *                           an incorrect number of pixel values
         */
        private void parsePixels(Scanner scanner) throws PGMParseException {
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    if (!scanner.hasNextInt()) {
                        throw new PGMParseException(
                                "'" + filename
                                        + "' contains invalid pixel data, pixels missing or non-integers detected");
                    }
                    int value = scanner.nextInt();
                    validateValueInRange(value);
                    pixelsArray[row][col] = value;
                }
            }
            if (scanner.hasNextInt()) {
                throw new PGMParseException("'" + filename + "' contains too many pixel values");
            }
        }

        /**
         * Validates that the PGM file is not empty.
         *
         * @param file the PGM file to check
         * @throws PGMParseException if the file is empty
         */
        private void validateFileNotEmpty(File file) throws PGMParseException {
            if (file.length() == 0) {
                throw new PGMParseException("File '" + filename + "' cannot be empty");
            }
        }

        /**
         * Validates that a pixel value is within the allowed range [0, 255].
         *
         * @param value the pixel value to validate
         * @throws PGMParseException if the pixel value is out of range
         */
        private void validateValueInRange(int value) throws PGMParseException {
            if (value < 0 || value > MAX_PIXEL_VALUE) {
                throw new PGMParseException(
                        "Pixel in '" + filename + "' out of range (0-" + MAX_PIXEL_VALUE + "): " + value);
            }
        }
    }
}
