package run;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import clustering.PerceptronClustering;
import pgm.PGMAnalyzer;
import pgm.PGMImage;

/**
 * The main application runner that initializes and executes the perceptron
 * algorithm
 * on a set of PGM images. Handles command-line argument parsing, image file
 * loading,
 * and perceptron execution.
 */
public class AppRunner {

    /**
     * Expected file extension for the input file.
     */
    private static final String FILE_EXTENSION = ".txt";

    /**
     * Usage message displayed for incorrect command-line argument formats.
     */
    private static final String USAGE_MESSAGE = "Usage: java AppRunner <training_files.txt> <test_files.txt> <num_clusters>";

    /**
     * Expected number of arguments.
     */
    private static final int NUM_ARGUMENTS = 3;

    /**
     * Minimum number of images required for perceptron execution.
     */
    private static final int MINIMUM_NUM_ALLOWED_FILES = 2;

    /**
     * Executes the application by validating input arguments, loading training
     * and test images, and performing perceptron-based clustering on the provided
     * data.
     * 
     * This method performs the following steps:
     * - Validates the command-line arguments for correctness.
     * - Loads training and test images from the specified text files.
     * - Initializes and executes the clustering algorithm using the specified
     * number of clusters.
     * - Outputs the clustering results to the console.
     * 
     * @param args an array of command-line arguments, expected in the format:
     *             <training_files.txt> <test_files.txt> <num_clusters>
     *             - <training_files.txt>: Path to the text file listing training
     *             image paths.
     *             - <test_files.txt>: Path to the text file listing test image
     *             paths.
     *             - <num_clusters>: Number of target clusters for the algorithm.
     * @throws IllegalArgumentException if the arguments are invalid or the input
     *                                  files are malformed.
     * @throws Exception                if loading images or clustering fails due to
     *                                  I/O or processing errors.
     */
    public static void run(String[] args) throws Exception {
        validateAndParseArguments(args);

        List<PGMImage> trainingImages = AppRunner.loadImagesFromFile(args[0]);
        List<PGMImage> testImages = AppRunner.loadImagesFromFile(args[1]);
        int targetNumClusters = Integer.parseInt(args[2]);

        PGMAnalyzer analyzer = new PGMAnalyzer(testImages);
        PerceptronClustering pc = analyzer.performPerceptronClustering(targetNumClusters, trainingImages, testImages);
        System.out.println(pc.toString());
    }

    /**
     * Validates the command-line arguments provided to the application.
     * 
     * This method checks that:
     * - Exactly three arguments are provided.
     * - The first two arguments are paths to text files with the expected `.txt`
     * extension.
     * - The third argument is a valid integer representing the number of target
     * clusters.
     * 
     * If any of these conditions are not met, an {@link IllegalArgumentException}
     * is thrown with a
     * descriptive error message.
     * 
     * @param args an array of command-line arguments to validate.
     * @throws IllegalArgumentException if the arguments are invalid or improperly
     *                                  formatted.
     */
    private static void validateAndParseArguments(String[] args) {
        if (args.length != NUM_ARGUMENTS) {
            throw new IllegalArgumentException("Invalid number of arguments. " + USAGE_MESSAGE);
        }
        if (!args[0].endsWith(FILE_EXTENSION) || !args[1].endsWith(FILE_EXTENSION)) {
            throw new IllegalArgumentException(
                    "<training_files.txt> <test_files.txt> must be a non-empty files with extension '" + FILE_EXTENSION
                            + "' Given:\n" + args[0] + " " + args[1]);
        }
        try {
            Integer.valueOf(args[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "ERROR! Argument <num_clusters> must be a valid integer. " + USAGE_MESSAGE);
        }
    }

    /**
     * Loads a list of PGM images from the specified text file. Each line in the
     * text file
     * represents the file path to a single PGM image.
     *
     * @param filePath the path to the text file containing image paths
     * @return a list of PGMImage objects loaded from the specified file
     * @throws Exception if the text file cannot be read or contains invalid paths
     */
    public static List<PGMImage> loadImagesFromFile(String filePath) throws Exception {
        File txtFile = new File(filePath);
        List<PGMImage> images = new ArrayList<>();

        try (Scanner fileScanner = new Scanner(txtFile)) {
            while (fileScanner.hasNextLine()) {
                String filename = fileScanner.nextLine().trim();
                if (filename.equals("")) {
                    continue;
                }
                images.add(PGMImage.fromFile(filename));
            }

            if (images.size() < MINIMUM_NUM_ALLOWED_FILES) {
                throw new IllegalArgumentException(
                        "At least " + MINIMUM_NUM_ALLOWED_FILES + " images expected in '" + txtFile.getName() + "'");
            }
        }
        return images;
    }
}