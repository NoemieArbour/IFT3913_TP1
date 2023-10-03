package ift3913;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class tropcomp {

    private static ArrayList<String> streamOutTLS = new ArrayList<>();

    public static void main(String[] args) {
        File maindir;
        switch (args.length) {
            case 2 -> { // tropcomp.jar <input> <threshold>% : print output in command lines
                maindir = new File(args[0]);
                if (!maindir.exists()) {
                    System.err.println("Input file does not exist.");
                    System.exit(1);
                } else {
                    if (!isNumeric(args[1])) {
                        System.err.println("\nThreshold must be a number.\n");
                        System.exit(1);
                    } else {
                        streamOutTLS = executeJarFile(args[0]);
                        suspectedClasses("", Integer.parseInt(args[1]), false);
                    }
                }
            }
            case 4 -> { // tropcomp -o <output.csv> <input> <threshold>% : print output in csv file
                maindir = new File(args[2]);
                if (!maindir.exists()) {
                    System.err.println("Input file does not exist.");
                    System.exit(1);
                } else {
                    streamOutTLS = executeJarFile(args[2]);
                    suspectedClasses(args[1], Integer.parseInt(args[3]), true);
                }
            }
            default ->// invalid entry
                    System.err.println(
                            "tropcomp must contain at least 2 arguments: " +
                                    "java tropcomp <project_path> <threshold(e.g.1,5,10,20)> " +
                                    "OR " +
                                    "\n java tropcomp -o <output_path.csv> <entry_path> <threshold(e.g.1,5,10,20)>");
        }
    }

    private static void suspectedClasses(String outputPath, int threshold, boolean isOutputCSV) {
        ArrayList<String> tlocDescendingOrder = new ArrayList<>(streamOutTLS);
        ArrayList<String> tmcpDescendingOrder = new ArrayList<>(streamOutTLS);
        sort(tlocDescendingOrder, 3);
        sort(tmcpDescendingOrder, 5);
        int step = streamOutTLS.size() * threshold / 100;
        ArrayList<String> getSameClasses = getSameObjects(tmcpDescendingOrder, tlocDescendingOrder, step);
        if (!isOutputCSV) {// printout command line
            for (String getSameClass : getSameClasses) System.out.println(getSameClass);
        } else { // csv file
            File csvFile = new File(outputPath);
            try (PrintWriter csvWriter = new PrintWriter(new FileWriter(csvFile))) {
                for (String item : getSameClasses)
                    csvWriter.println(item);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static ArrayList<String> getSameObjects(ArrayList<String> ls1, ArrayList<String> ls2, int step) {
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i < step; i++) {
            String find = ls1.get(i).split(", ")[2];
            for (int j = 0; j < step; j++) {
                if (find.equals(ls2.get(j).split(", ")[2])) {
                    temp.add(ls2.get(j));
                }
            }
        }
        return temp;
    }

    private static void sort(ArrayList<String> strArr, int idxComp) {
        int n = strArr.size();
        double positiveInfinity = Double.POSITIVE_INFINITY;
        for (int i = 0; i < n - 1; i++) {
            int min_idx = i;
            for (int j = i + 1; j < n; j++) {
                String fst = strArr.get(j).split(", ")[idxComp];
                String snd = strArr.get(min_idx).split(", ")[idxComp];
                if (idxComp == 3) {
                    if (Integer.parseInt(fst) > Integer.parseInt(snd))
                        min_idx = j;
                } else if (idxComp == 5) {
                    if (!isNumeric(fst) && !isNumeric(snd)) { // if 2 infinity =>check tloc
                        fst = strArr.get(j).split(", ")[3];
                        snd = strArr.get(min_idx).split(", ")[3];
                        if (Integer.parseInt(fst) > Integer.parseInt(snd))
                            min_idx = j;
                    } else {
                        double fst_cv, snd_cv;
                        fst_cv = (!(isNumeric(fst))) ? positiveInfinity : Double.parseDouble(fst);
                        snd_cv = (!(isNumeric(snd))) ? positiveInfinity : Double.parseDouble(snd);
                        if (fst_cv > snd_cv)
                            min_idx = j;
                    }
                }
            }
            Collections.swap(strArr, i, min_idx);
        }
    }

    // Inspired
    // https://stackoverflow.com/questions/1320476/execute-another-jar-in-a-java-program/40544510#40544510
    private static ArrayList<String> executeJarFile(String path) {
        ArrayList<String> temp = new ArrayList<>();
        String[] aStrings = new String[] { "java", "-jar", "tls.jar", path};
        ProcessBuilder processBuilder = new ProcessBuilder(aStrings);
        processBuilder.redirectInput();
        try {
            final Process process = processBuilder.start();
            String line;
            InputStream stdout = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
            while ((line = reader.readLine()) != null)
                temp.add(line);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return temp;
    }

    /**
     * Inspired code
     * <a href="https://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-numeric-in-java">...</a>
     */
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}
