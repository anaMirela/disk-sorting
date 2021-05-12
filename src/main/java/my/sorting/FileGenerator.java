package my.sorting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class used to generate a file of a given size with random integers
 */
public class FileGenerator {

    private static Logger logger = LoggerFactory.getLogger(FileGenerator.class);

    private static final String FILE_NAME = "generatedFile";

    public static void generate(long sizeInBytes) {
        long currentSize = 0;
        try {
            File generatedFile = new File(FILE_NAME);
            BufferedWriter bw = new BufferedWriter(new FileWriter(generatedFile));
            do {
                int randomNum = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
                String newLine = writeLine(randomNum, bw);
                currentSize += newLine.length();
            } while (currentSize < sizeInBytes);
            bw.close();
            logger.info("Generated file {}", FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String writeLine(int no, BufferedWriter bw) {
        String lineString = "";
        try {
            lineString = String.valueOf(no) + "\n";
            bw.write(lineString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineString;
    }

}
