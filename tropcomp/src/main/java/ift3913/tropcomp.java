package ift3913;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

public class tropcomp {

    private static ArrayList<String> streamOutTLS = new ArrayList<String>();

    // private static ArrayList<String>[] tlocDescendingOrder;
    // private static String[] tmcpDescendingOrder;
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
                        suspectedClasses(args[1], Integer.parseInt(args[2]));
                    }
                }
                break;
            case 4:// print output in csv file
                maindir = new File(args[1]);
                if (!maindir.exists()) {
                    System.err.println("Project not found! Please verify your entry path.");
                    System.exit(1);
                } else {
                    // System.out.println("\nWorking..........\n");
                    // suspectedClasses(2, args[1]);

                }
                break;
            default:// invalid entry
                System.err.println(
                        "troncomp must contain at least 2 arguments: java troncomp <project_path> <threshold(e.g.1,5,10,20)> OR \n java troncomp -o <output_path.csv> <entry_path>");
                break;
        }
    }

    private static void suspectedClasses(String path, int seuil) {
        ArrayList<String> tlocDescendingOrder = new ArrayList<>(streamOutTLS);
        ArrayList<String> tmcpDescendingOrder = new ArrayList<>(streamOutTLS);
        sort(tlocDescendingOrder, 3);
        sort(tmcpDescendingOrder, 5);
        int totalOfClass = streamOutTLS.size();
        System.out.println("%%%%%%%%%%%%%%%%%%%TLOC ");
        for (int i = 0; i < tlocDescendingOrder.size(); i++) {
            System.out.println(tlocDescendingOrder.get(i).split(", ")[2]);
            System.out.print("-- tloc: " + tlocDescendingOrder.get(i).split(", ")[3]);
            System.out.print("-- tcmp: " + tlocDescendingOrder.get(i).split(", ")[5]);
            System.out.print("\n");
        }
        System.out.println("%%%%%%%%%%%%%%%%%%%TCMP ");
        for (int i = 0; i < tmcpDescendingOrder.size(); i++) {
            System.out.println(tmcpDescendingOrder.get(i).split(", ")[2]);
            System.out.print("-- tloc: " + tmcpDescendingOrder.get(i).split(", ")[3]);
            System.out.print("-- tcmp: " + tmcpDescendingOrder.get(i).split(", ")[5]);
            System.out.print("\n");
        }

        System.out.println("%%%%%%%%%%%%%%%%%%%SAMEEE ");
        int step = streamOutTLS.size() * seuil / 100;
        System.out.println("############## " + step);
        System.out.println("############## " + totalOfClass);
        for (int i = 0; i < step; i++) {
            String find = tmcpDescendingOrder.get(i).split(", ")[2];
            for (int j = 0; j < step; j++) {
                if (find.equals(tlocDescendingOrder.get(j).split(", ")[2])) {
                    System.out.println(tlocDescendingOrder.get(j));
                }
            }
        }

        // if(typeSortie == 1){// printout command line

        // }else { // csv file

        // }

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
                    if (Integer.parseInt(fst) > Integer.parseInt(snd)) {
                        min_idx = j;
                    }
                } else if (idxComp == 5) {
                    if (!isNumeric(fst) && !isNumeric(snd)) {// if 2 infinity=>check tloc
                        fst = strArr.get(j).split(", ")[3];
                        snd = strArr.get(min_idx).split(", ")[3];
                        if (fst.compareTo(snd) > 0) {
                            min_idx = j;
                        }
                    } else {
                        Double fst_cv, snd_cv;
                        fst_cv = (!(isNumeric(fst))) ? positiveInfinity : Double.parseDouble(fst);
                        snd_cv = (!(isNumeric(snd))) ? positiveInfinity : Double.parseDouble(snd);
                        if (fst_cv > snd_cv) {
                            min_idx = j;
                        }
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
        processBuilder.redirectError(new File(Paths.get("extJar_out_put.txt").toString()));
        processBuilder.redirectInput();
        try {
            final Process process = processBuilder.start();
            String line;
            InputStream stdout = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
            // int count = 0;
            while ((line = reader.readLine()) != null) {
                temp.add(line);
            }
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
