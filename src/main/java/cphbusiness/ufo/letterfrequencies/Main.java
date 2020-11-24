package cphbusiness.ufo.letterfrequencies;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * Frequency analysis Inspired by
 * https://en.wikipedia.org/wiki/Frequency_analysis
 *
 * @author kasper
 */
public class Main {

    private static long entryTime = 0;
    private static int iterations = 50;
    private static ArrayList<Long> observations = new ArrayList<Long>();
    private static String fileName = new File("src/main/resources/FoundationSeries.txt").getAbsolutePath();
    private static Map<Integer, Long> freq = new HashMap<>();

    public static void main(String[] args) throws FileNotFoundException, IOException {

        FileWriter fileWriter = new FileWriter("src/analysis/observations.csv", false);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.close();

        // tallyChars
        for (int i = 0; i < iterations; i++) {
            freq.clear();
            entryTime = System.nanoTime();
            Reader reader = new FileReader(fileName);
            tallyChars(reader, freq);
            observations.add(System.nanoTime() - entryTime);
        }
        write_csv("Tally_Chars", observations);
        observations.clear();

        // tallyCharsCustom1
        for (int i = 0; i < iterations; i++) {
            freq.clear();
            entryTime = System.nanoTime();
            Reader reader = new FileReader(fileName);
            tallyCharsCustom1(reader, freq);
            observations.add(System.nanoTime() - entryTime);
        }
        write_csv("Tally_Chars_Custom_1", observations);
        observations.clear();

        // tallyCharsCustom2
        for (int i = 0; i < iterations; i++) {
            freq.clear();
            entryTime = System.nanoTime();
            Reader reader = new FileReader(fileName);
            tallyCharsCustom2(reader, freq);
            observations.add(System.nanoTime() - entryTime);
        }
        write_csv("Tally_Chars_Custom_2", observations);
        observations.clear();


        // tallyCharsCustom3
        for (int i = 0; i < iterations; i++) {
            freq.clear();
            entryTime = System.nanoTime();
            byte [] fileBytes = Files.readAllBytes(new File(fileName).toPath());
            tallyCharsCustom3(fileBytes, freq);
            observations.add(System.nanoTime() - entryTime);
        }
        write_csv("Tally_Chars_Custom_3", observations);
        // observations.clear();

        /*
        print_tally(freq);
        long avg = observations.stream().reduce(0L, Long::sum)/iterations;
        System.out.print("Average:: ");
        System.out.println(avg);
        */
    }

    // saves data to csv
    private static void write_csv(String name, ArrayList<Long> observations) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append(name);

        for (long s : observations) {
            sb.append(",");
            sb.append(s);
        }
        String str = sb.toString();

        FileWriter fileWriter = new FileWriter("src/analysis/observations.csv", true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println(str);
        printWriter.close();
    }

    private static void tallyCharsCustom1(Reader reader, Map<Integer, Long> freq) throws IOException {
        int b;
        while ((b = reader.read()) != -1) {
            freq.put(b, freq.getOrDefault(b, 0L) + 1L);
        }
    }

    private static void tallyCharsCustom2(Reader reader, Map<Integer, Long> freq) throws IOException {
        int b;
        BufferedReader br = new BufferedReader(reader);

        while ((b = br.read()) != -1) {
            freq.put(b, freq.getOrDefault(b, 0L) + 1L);
        }
    }

    // https://funnelgarden.com/java_read_file/#FilesreadAllBytes
    private static void tallyCharsCustom3(byte [] fileBytes, Map<Integer, Long> freq) throws IOException {
        int singleInt;
        for(byte b : fileBytes) {
            singleInt = (int) b;
            freq.put(singleInt, freq.getOrDefault(singleInt, 0L) + 1);
        }
    }

    private static void tallyChars(Reader reader, Map<Integer, Long> freq) throws IOException {
        int b;
        while ((b = reader.read()) != -1) {
            try {
                freq.put(b, freq.get(b) + 1);
            } catch (NullPointerException np) {
                freq.put(b, 1L);
            };
        }
    }

    private static void print_tally(Map<Integer, Long> freq) {
        int dist = 'a' - 'A';
        Map<Character, Long> upperAndlower = new LinkedHashMap();
        for (Character c = 'A'; c <= 'Z'; c++) {
            upperAndlower.put(c, freq.getOrDefault(c, 0L) + freq.getOrDefault(c + dist, 0L));
        }
        Map<Character, Long> sorted = upperAndlower
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
        for (Character c : sorted.keySet()) {
            System.out.println("" + c + ": " + sorted.get(c));;
        }
    }
}
