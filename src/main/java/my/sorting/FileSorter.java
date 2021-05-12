package my.sorting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class FileSorter {

    private static Logger logger = LoggerFactory.getLogger(FileSorter.class);

    private static int THREAD_COUNT = 4;
    private static long CHUNK_SIZE = 1024 * 10;
    private static String RESULT_FILE_NAME = "result";

    public static void sort(String fileName) throws IOException {
        logger.info("Started merging intermediary files");
        List<String> intermediaryFileNames = splitAndSort(fileName);
        logger.info("Finished sorting intermediary files");

        logger.info("Started merging intermediary files");
        mergeFiles(intermediaryFileNames);
        logger.info("Finished merging intermediary files");

        deleteFiles(intermediaryFileNames);
    }

    private static List<String> splitAndSort(String fileName) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_COUNT);

        long MAX_LINES = new File(fileName).length() / 10;
        long chunkSize = getChunkSize(MAX_LINES);
        long noOfTasks = MAX_LINES / chunkSize;

        List<ChunkSorter> threads = new LinkedList<>();
        for (int i = 0; i < noOfTasks; i++) {
            long startLine = i * chunkSize + 1;
            long endLine = (i + 1) * chunkSize;
            ChunkSorter sorter = new ChunkSorter(fileName, startLine, endLine);
            threads.add(sorter);
            executor.submit(sorter);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        return threads
                .stream()
                .map(ChunkSorter::getIntermediaryFileName)
                .collect(Collectors.toList());
    }

    private static void mergeFiles(List<String> intermediaryFileNames) throws IOException {
        int[] fileIndexes = new int[intermediaryFileNames.size()];
        boolean[] closedFiles = new boolean[intermediaryFileNames.size()];
        RandomAccessFile[] rafs = new RandomAccessFile[intermediaryFileNames.size()];

        for(int i = 0; i < intermediaryFileNames.size(); i++) {
            try {
                rafs[i] = new RandomAccessFile(intermediaryFileNames.get(i), "r");
                fileIndexes[i] = 0;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(RESULT_FILE_NAME));
        int countClosed = 0;
        while (countClosed < intermediaryFileNames.size()) {
            int currentMinPos = -1;
            int currentMin = Integer.MAX_VALUE;
            for (int i = 0; i < intermediaryFileNames.size(); i++) {
                if (closedFiles[i]) continue;
                rafs[i].seek(fileIndexes[i]);
                String currentNumber = rafs[i].readLine();
                if (isNullOrEmpty(currentNumber)) {
                    closedFiles[i] = true;
                    rafs[i].close();
                    countClosed++;
                    continue;
                }
                int currentInt = Integer.parseInt(currentNumber);
                if (currentInt <= currentMin) {
                    currentMin = currentInt;
                    currentMinPos = i;
                }
            }
            if(countClosed != intermediaryFileNames.size()) {
                fileIndexes[currentMinPos] += String.valueOf(currentMin).length() + 1;
                writeLine(currentMin, bw);
            }
        }
        bw.close();
    }

    private static void deleteFiles(List<String> intermediaryFileNames) {
        intermediaryFileNames.forEach( fileName -> {
            File f = new File(fileName);
            f.delete();
        });
    }

    private static void writeLine(int lineValue,BufferedWriter bw) {
        try {
            bw.write(String.valueOf(lineValue));
            bw.write("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long getChunkSize(long maxLines) {
        return maxLines  / 10;
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
