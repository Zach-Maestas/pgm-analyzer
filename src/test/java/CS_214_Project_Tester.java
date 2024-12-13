import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import clustering.Cluster;
import clustering.ClusteringAlgorithm;
import clustering.PerceptronClustering;
import clustering.quality.ClusteringQualityAnalyzer;
import exception.PGMParseException;
import histogram.Histogram;
import histogram.HistogramUtils;
import pairwise_similarity.PairwiseSimilarity;
import perceptron.Perceptron;
import pgm.PGMAnalyzer;
import pgm.PGMImage;
import run.AppRunner;

public class CS_214_Project_Tester {

    /*
     * -----------------------------------------------------------------------
     * AppRunner Tests
     * -----------------------------------------------------------------------
     */

    @Test
    public void testInvalidNumArgs() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            AppRunner.run(new String[] { "input_files/test_runner/invalid_num_files.txt", "1", "1" });
        });
    }

    @Test
    public void testInvalidFileType() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            AppRunner.run(new String[] { "input_files/random.pgm", "3" });
        });
    }

    @Test
    public void testNumClustersLessThanOne() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            AppRunner.run(new String[] { "input_files/test_files/example_files/correctfiles.txt", "0", "3" });
        });
    }

    @Test
    public void testSimilarityMeasureOutOfRange() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            AppRunner.run(new String[] { "input_files/test_files/example_files/correctfiles.txt", "2", "7" });
        });
    }

    @Test
    public void testNumberFormatExceptionWithArgs() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            AppRunner.run(
                    new String[] { "input_files/test_files/example_files/correctfiles.txt", "2", "invalid" });
        });
    }

    @Test
    public void testInvalidNumFiles() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            AppRunner.run(new String[] { "input_files/test_files/test_runner/invalid_num_files.txt", "2" });
        });
    }

    /*
     * -----------------------------------------------------------------------
     * PGMImage (File Parser) Tests
     * -----------------------------------------------------------------------
     */

    // Helper method to assert PGMParseException
    private void assertPGMParseException(String expectedMessage, String filePath) {
        PGMParseException thrown = assertThrows(PGMParseException.class, () -> {
            PGMImage.fromFile(filePath);
        });
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    public void testFileNotFound() {
        assertThrows(FileNotFoundException.class, () -> {
            PGMImage.fromFile("input_files/non_existent.pgm");
        });
    }

    @Test
    public void testEmptyFile() {
        assertPGMParseException("File 'empty.pgm' cannot be empty", "input_files/test_files/test_pgm_parser/empty.pgm");
    }

    @Test
    public void testParseHeaderMissingTokens() {
        assertPGMParseException("Invalid header format in file 'missing_tokens.pgm' (width, height), or missing tokens",
                "input_files/test_files/test_pgm_parser/missing_tokens.pgm");
    }

    @Test
    public void testParseHeaderInvalidMagicNumber()
            throws Exception {
        assertPGMParseException("'invalid_magic_num.pgm' must start with '" + "P2" + "'\nFound: " + "P4",
                "input_files/test_files/test_pgm_parser/invalid_magic_num.pgm");
    }

    @Test
    public void testParseHeaderInvalidHeightOrWidth() {
        assertPGMParseException("Invalid header format in file 'invalid_height.pgm' (width, height), or missing tokens",
                "input_files/test_files/test_pgm_parser/invalid_height.pgm");
    }

    @Test
    public void testParseHeaderInvalidMaxPixelVal()
            throws Exception {
        assertPGMParseException("'invalid_max_pixel.pgm' maximum pixel value must be " + 255 + " Found: " + 256,
                "input_files/test_files/test_pgm_parser/invalid_max_pixel.pgm");
    }

    @Test
    public void testTooManyPixelValues() {
        assertPGMParseException("'too_many_pixels.pgm' contains too many pixel values",
                "input_files/test_files/test_pgm_parser/too_many_pixels.pgm");
    }

    @Test
    public void testInvalidToken() {
        assertPGMParseException(
                "'invalid_token.pgm' contains invalid pixel data, pixels missing or non-integers detected",
                "input_files/test_files/test_pgm_parser/invalid_token.pgm");
    }

    @Test
    public void testOutOfRangeValue() {
        assertPGMParseException("Pixel in 'out_of_range.pgm' out of range (0-255): 993",
                "input_files/test_files/test_pgm_parser/out_of_range.pgm");
    }

    @Test
    public void testGetInvalidClassLabel() throws Exception {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            PGMImage pgm = PGMImage.fromFile("input_files/test_files/test_pgm_parser/invalid_label.pgm");
            pgm.getClassLabel();
        });

        assertEquals("Invalid class label format in file 'invalid_label.pgm'", thrown.getMessage());

    }

    @Test
    public void testGetClassLabel() throws Exception {
        PGMImage pgm = PGMImage.fromFile("input_files/test_files/test_pgm_parser/class2_2.pgm");
        assertEquals(2, pgm.getClassLabel());
    }

    /*
     * -----------------------------------------------------------------------
     * Histogram Tests (implemented via PGMImage)
     * -----------------------------------------------------------------------
     */

    @Test
    public void testHistogramGeneration()
            throws Exception {
        PGMImage pgm = PGMImage.fromFile("input_files/test_files/test_histogram/test1.pgm");
        Histogram hist = new Histogram(pgm.getPixelsArray());
        int[] expectedHistogram = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 7, 12, 12, 5, 30,
                36, 61, 55, 105, 171, 135, 149, 135, 124, 163, 162, 178, 152, 134, 57, 39,
                44, 28, 32, 16, 24, 37, 18,
                16, 24, 21, 35, 37, 45, 40, 54, 65, 50, 70, 73, 82, 68, 91, 93, 81, 13318 };

        for (int i = 0; i < expectedHistogram.length; i++) {
            assertEquals(expectedHistogram[i], hist.getHistogramArray()[i]);
        }
    }

    /*
     * -----------------------------------------------------------------------
     * HistogramUtils Test (implemented via PGMImage)
     * -----------------------------------------------------------------------
     */

    @Test
    public void testNormalizeHistogram()
            throws Exception {
        PGMImage pgm = PGMImage.fromFile("input_files/test_files/test_histogram/test2.pgm");
        double[] normalizedHistogram = pgm.getNormalizedHistogram();

        double sum = 0.0;
        for (double value : normalizedHistogram) {
            sum += value;
        }

        assertEquals(1.0, sum);
    }

    // testNormalizeHistogramSumZero()

    @Test
    public void testComputeDotProduct()
            throws Exception {
        PGMImage pgm1 = PGMImage.fromFile("input_files/test_files/test_histogram/test1.pgm");
        PGMImage pgm2 = PGMImage.fromFile("input_files/test_files/test_histogram/test2.pgm");

        double dotProduct = HistogramUtils.computeDotProduct(pgm1.getNormalizedHistogram(), pgm2.getNormalizedHistogram());
        assertEquals("0.597841", String.format("%.6f", dotProduct));
    }

    @Test
    public void testComputeHistogramIntersection()
            throws Exception {

        PGMImage pgm1 = PGMImage.fromFile("input_files/test_files/test_histogram/intersection1.pgm");
        PGMImage pgm2 = PGMImage.fromFile("input_files/test_files/test_histogram/intersection2.pgm");
        double expected = 0.72198486328125;

        assertEquals(expected, HistogramUtils.computeIntersection(pgm1.getNormalizedHistogram(), pgm2.getNormalizedHistogram()));
    }

    /*
     * -----------------------------------------------------------------------
     * PGMAnalyzer Tests
     * -----------------------------------------------------------------------
     */

    // testNoImagesToAnalyze()

    @Test
    public void testPixelByPixelComparison()
            throws Exception {
        PGMImage pgm1 = PGMImage.fromFile("input_files/test_files/test_analyzer/sample1.pgm");
        PGMImage pgm2 = PGMImage.fromFile("input_files/test_files/test_analyzer/sample2.pgm");

        assertEquals(PGMAnalyzer.pixelByPixelComparison(pgm1.getPixelsArray(), pgm2.getPixelsArray()), 99840042);
    }

    /*
     * -----------------------------------------------------------------------
     * PairwiseSimilarity Tests (implemented via PGMAnalyzer)
     * -----------------------------------------------------------------------
     */

    @Test
    public void testPairwiseSimilarity()
            throws Exception {

        PGMAnalyzer analyzer = new PGMAnalyzer(Arrays.asList(
                PGMImage.fromFile("input_files/test_files/test_analyzer/example1.pgm"),
                PGMImage.fromFile("input_files/test_files/test_analyzer/example2.pgm")));

        PairwiseSimilarity ps = analyzer.analyzePairwiseSimilarities();

        assertEquals("example1.pgm example2.pgm 0.990784\nexample2.pgm example1.pgm 0.990784\n", ps.toString());

    }

    /*
     * -----------------------------------------------------------------------
     * Cluster Tests
     * -----------------------------------------------------------------------
     */

    @Test
    public void testMergeCluster() throws Exception {
        PGMImage pgm1 = PGMImage.fromFile("input_files/test_files/test_cluster/example1.pgm");
        PGMImage pgm2 = PGMImage.fromFile("input_files/test_files/test_cluster/example2.pgm");
        Cluster cluster1 = new Cluster(pgm1);
        Cluster cluster2 = new Cluster(pgm2);
        cluster1.merge(cluster2);

        List<PGMImage> merged = cluster1.getImages();

        assertTrue(merged.contains(pgm1) && merged.contains(pgm2));
    }

    /*
     * -----------------------------------------------------------------------
     * Clustering Tests
     * -----------------------------------------------------------------------
     */

    @Test
    public void testClusteringAgglomerativeClustering() throws Exception {
        List<PGMImage> pgmImages = getTestImageList("input_files/test_files/example_files/example_files.txt");
        String expected = "example1.pgm example2.pgm\nexample3.pgm\nexample4.pgm example5.pgm\nexample6.pgm example7.pgm";

        PGMAnalyzer analyzer = new PGMAnalyzer(pgmImages);
        ClusteringAlgorithm cs = analyzer.performClusteringAnalysis(4, 1);
        assertEquals(expected, cs.toString());
    }

    @Test
    public void testQuarterClustering() throws Exception {
        List<PGMImage> pgmImages = getTestImageList("input_files/test_files/example_files/example_files.txt");
        String expected = "example1.pgm example2.pgm\nexample3.pgm\nexample4.pgm example5.pgm example6.pgm example7.pgm";

        PGMAnalyzer analyzer = new PGMAnalyzer(pgmImages);
        ClusteringAlgorithm cs = analyzer.performClusteringAnalysis(3, 2);
        assertEquals(expected, cs.toString());
    }

    @Test
    public void testInvSquareDiffClustering() throws Exception {
        List<PGMImage> pgmImages = getTestImageList("input_files/test_files/example_files/example_files.txt");
        String expected = "example1.pgm example2.pgm\nexample3.pgm\nexample4.pgm example5.pgm\nexample6.pgm example7.pgm";

        PGMAnalyzer analyzer = new PGMAnalyzer(pgmImages);
        ClusteringAlgorithm cs = analyzer.performClusteringAnalysis(4, 3);
        assertEquals(expected, cs.toString());
    }

    @Test
    public void testPerceptronClustering() throws Exception {
        List<PGMImage> testImages = getTestImageList("input_files/test.txt");
        List<PGMImage> trainImages = getTestImageList("input_files/train.txt");
        String expected = "class0_1.pgm class0_7.pgm class0_8.pgm class0_9.pgm\n" +
                "class0_10.pgm class0_11.pgm class0_12.pgm class0_13.pgm class0_14.pgm " +
                "class0_15.pgm class0_2.pgm class0_3.pgm class0_4.pgm class0_5.pgm class0_6.pgm";

        PerceptronClustering pc = new PerceptronClustering(2, trainImages,
                testImages);
        assertEquals(expected, pc.toString());
    }

    /*
     * -----------------------------------------------------------------------
     * ClusteringQualityAnalyzer Tests
     * -----------------------------------------------------------------------
     */

    @Test
    public void testQualityAnalyzerInvSquareDiff() throws Exception {
        double expected = 0.714286;

        PGMAnalyzer analyzer = new PGMAnalyzer(
                getTestImageList("input_files/test_files/test_perceptron/correctfiles.txt")); // TODO: fix using
                                                                                              // perceptron folder
        ClusteringAlgorithm clustering = analyzer.performClusteringAnalysis(3, 3);

        double quality = Double
                .parseDouble(String.format("%.6f", ClusteringQualityAnalyzer.computeClusteringQuality(clustering)));

        assertEquals(expected, quality);
    }

    /*
     * -----------------------------------------------------------------------
     * Perceptron Tests
     * -----------------------------------------------------------------------
     */

    @Test
    public void testPerceptron() throws Exception {
        String expected = "0.000000 0.000000 -0.163387 -0.151285 -0.066565 -0.344929 -0.580933 -0.596682 " +
                "-0.803491 -0.537688 -0.546648 -0.159860 -0.237191 -0.229397 -0.264899 -0.204398 " +
                "-0.156277 -0.259867 -0.306215 -1.402892 -1.135745 -0.554350 -0.170574 1.645242 " +
                "2.672255 4.107011 5.207983 3.011428 2.062676 1.119031 0.690420 0.929341 " +
                "0.438889 0.501041 0.475619 0.533738 0.553464 0.259871 0.331111 -0.062162 " +
                "-0.058675 0.288081 -0.217577 -0.519618 -0.276495 -0.467983 -0.356379 -0.580643 " +
                "-0.123592 -0.597473 -0.454268 -0.640858 -0.453260 -0.592533 -0.390420 -0.484363 " +
                "-0.327973 -0.360029 -0.336971 -0.316612 -0.569286 -0.309974 -0.311961 -4.807033 2.337790 ";

        List<PGMImage> pgmImages = AppRunner
                .loadImagesFromFile("input_files/test_files/test_perceptron/correctfiles.txt");
        Perceptron perceptron = new Perceptron(pgmImages, 1);

        assertEquals(expected, perceptron.toString());
    }

    @Test
    public void testPerceptronInvalidTargetNum() throws Exception {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            List<PGMImage> pgmImages = AppRunner
                    .loadImagesFromFile("input_files/test_files/test_perceptron/correctfiles.txt");
            new Perceptron(pgmImages, 9);
        });

        assertEquals("Target class label not found in provided samples", thrown.getMessage());
    }

    /*
     * -----------------------------------------------------------------------
     * Helper Methods
     * -----------------------------------------------------------------------
     */

    private List<PGMImage> getTestImageList(String pathString)
            throws Exception {
        return AppRunner.loadImagesFromFile(pathString);
    }

}
