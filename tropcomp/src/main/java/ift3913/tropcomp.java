package ift3913;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class tropcomp {

    private static ArrayList<String> streamOutTLS = new ArrayList<String>();

    public static void main(String[] args) {
        File maindir;
        switch (args.length) {
            case 3: // print output in command lines
                maindir = new File(args[0]);
                if (!maindir.exists()) {
                    System.err.println("Project not found! Please verify your entry path.");
                    System.exit(1);
                } else {
                    System.out.println("\nWorking..........\n");
                    if (!isNumeric(args[2])) {
                        System.out.println("\nThreadhold must be a number!\n");
                        System.exit(1);
                    } else {
                        streamOutTLS = executeJarFile(args[1]);
                        suspectedClasses(args[1], "", Integer.parseInt(args[2]), false);
                    }
                }
                break;
            case 5:// print output in csv file
                maindir = new File(args[3]);
                if (!maindir.exists()) {
                    System.err.println("Project not found! Please verify your entry path.");
                    System.exit(1);
                } else {
                    System.out.println("\nWorking..........\n");
                    streamOutTLS = executeJarFile(args[3]);
                    suspectedClasses(args[3], args[2], Integer.parseInt(args[4]), true);
                }
                break;
            default:// invalid entry
                System.err.println(
                        "tropcomp must contain at least 2 arguments: " +
                                "java tropcomp <project_path> <threshold(e.g.1,5,10,20)> " +
                                "OR " +
                                "\n java tropcomp -o <output_path.csv> <entry_path> <threshold(e.g.1,5,10,20)>");
                break;
        }
    }

    private static void suspectedClasses(String inputPath, String outputPath, int threshold, boolean isOutputCSV) {
        ArrayList<String> tlocDescendingOrder = new ArrayList<>(streamOutTLS);
        ArrayList<String> tmcpDescendingOrder = new ArrayList<>(streamOutTLS);
        sort(tlocDescendingOrder, 3);
        sort(tmcpDescendingOrder, 5);
        int step = streamOutTLS.size() * threshold / 100;
        ArrayList<String> getSameClasses = getSameObjects(tmcpDescendingOrder, tlocDescendingOrder, step);
        if (!isOutputCSV) {// printout command line
            for (int i = 0; i < getSameClasses.size(); i++)
                System.out.println(getSameClasses.get(i));
        } else { // csv file
            System.out.println("\nWriting CSV........\n");
            File csvFile = new File(outputPath);
            try (PrintWriter csvWriter = new PrintWriter(new FileWriter(csvFile));) {
                for (String item : getSameClasses)
                    csvWriter.println(item);
                System.out.println("\nFinish writing CSV file.\n");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static ArrayList<String> getSameObjects(ArrayList<String> ls1, ArrayList<String> ls2, int step) {
        ArrayList<String> temp = new ArrayList<String>();
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

    private static ArrayList<String> sort(ArrayList<String> strArr, int idxComp) {
        int n = strArr.size();
        Double positiveInfinity = Double.POSITIVE_INFINITY;
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
                        Double fst_cv, snd_cv;
                        fst_cv = (!(isNumeric(fst))) ? positiveInfinity : Double.parseDouble(fst);
                        snd_cv = (!(isNumeric(snd))) ? positiveInfinity : Double.parseDouble(snd);
                        if (fst_cv > snd_cv)
                            min_idx = j;
                    }
                }
            }
            Collections.swap(strArr, i, min_idx);
        }
        return strArr;
    }

    // Inspired
    // https://stackoverflow.com/questions/1320476/execute-another-jar-in-a-java-program/40544510#40544510
    private static ArrayList<String> executeJarFile(String path) {
        ArrayList<String> temp = new ArrayList<>();
        String[] aStrings = new String[] { "java", "-jar", "tls.jar", path };
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
     * https://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-numeric-in-java
     */
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}
