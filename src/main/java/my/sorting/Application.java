package my.sorting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Main class for disk sorting application
 */
public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    private static String CMD_INSTRUCTIONS = "Program requires 2 arguments as:\n" +
            "java -jar disk-sorting -generate <file-size>\n" +
            "java -jar disk-sorting -sort <file-name>";

    private static String CMD_GENERATE = "-generate";
    private static String CMD_SORT = "-sort";

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            logger.error(CMD_INSTRUCTIONS);
            return;
        } else if(args[0].equals(CMD_GENERATE)) {
            if (!args[1].matches("\\d+")) {
                logger.error("Second argument is an invalid number value");
                return;
            }
            FileGenerator.generate(Long.valueOf(args[1]));
        } else if(args[0].equals(CMD_SORT)) {
            if(!new File(args[1]).exists()) {
                logger.error("Second argument is an invalid file");
            }
            FileSorter.sort(args[1]);
        } else {
            logger.error("Invalid command");
        }
    }

}
