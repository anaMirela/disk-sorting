package my.sorting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Class used for sorting a part of the file and
 * generating an intermediary output file
 */
public class ChunkSorter extends Thread {

    private static Logger logger = LoggerFactory.getLogger(ChunkSorter.class);

    private String fileName;
    private long startLine;
    private long endLine;
    private String intermediaryFileName;

    public ChunkSorter(String fileName, long startLine, long endLine) {
        this.fileName = fileName;
        this.startLine = startLine;
        this.endLine = endLine;
        this.intermediaryFileName = startLine + "-" + endLine + fileName + ".temp";
    }

    @Override
    public void run() {
        logger.info("Started sorting for lines {} - {}", startLine, endLine);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(intermediaryFileName));
            Stream<String> lines = Files.lines(Paths.get(fileName));
            lines.skip(startLine)
                    .limit(endLine - startLine + 1)
                    .mapToInt(value -> Integer.valueOf(value))
                    .sorted()
                    .forEach(x -> writeLine(x, bw));
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLine(int line, BufferedWriter bw) {
        try {
            bw.write(String.valueOf(line));
            bw.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIntermediaryFileName() {
        return intermediaryFileName;
    }
}
