package my.sorting;

import org.junit.*;

import java.io.*;
import java.util.Scanner;

public class FileSorterTest {

    private static String GENERATED_FILE = "generatedFile";
    private static String RESULT_FILE = "result";

    @BeforeClass
    public static void init() {
        FileGenerator.generate(1024 * 1024);
    }

    @Test
    public void testFileExists() {
        Assert.assertTrue(new File(GENERATED_FILE).exists());
    }

    @Test
    public void testSorting() throws IOException {
        FileSorter.sort(GENERATED_FILE);
        Assert.assertTrue(checkFileSorted());
    }

    private boolean checkFileSorted() throws IOException {
        Scanner scanner = new Scanner(new File(RESULT_FILE));
        while (scanner.hasNext()) {
            int x1 = Integer.parseInt(scanner.next());
            if (scanner.hasNext()) {
                int x2 = Integer.parseInt(scanner.next());
                if (x1 > x2) {
                    return false;
                }
            }
        }
        return true;
    }

}
