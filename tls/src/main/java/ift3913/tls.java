package ift3913;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link tls} est un programme qui prend en entrée le chemin d'accès d'un dossier qui contient du code test java organisé
 * en paquets (sous-dossiers, organisés selon les normes Java et Maven) et produise en sortie en format CSV:
 * <ul>
 *     <li>chemin du fichier</li>
 *     <li>nom du paquet</li>
 *     <li>nom de la classe</li>
 *     <li>tloc de la classe</li>
 *     <li>tassert de la classe</li>
 *     <li>tcmp de la classe = tloc / tassert</li>
 * </ul>
 */
public class tls {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("TLS must contain at least 1 argument: java tls <dir>");
            System.exit(1);
        }
        if (args.length < 4) {
            boolean writeOutput = false;
            String outputPath = null;
            String root = null;
            if (args.length == 1) {
                root = args[0];
            } else if (args.length == 3) {
                if (args[0].compareTo("-o") != 0) {
                    System.err.println("TLS must be called with arguments: java tls <dir> " +
                            "OR java tls -o <output-path.csv> <input-path>");
                    System.exit(1);
                }
                writeOutput = true;
                outputPath = args[1];
                root = args[2];
            } else {
                System.err.println("TLS must be called with arguments: java tls <dir> " +
                        "OR java tls -o <output-path.csv> <input-path>");
                System.exit(1);
            }

            File output = null;
            if (writeOutput) {
                output = new File(outputPath);
                try {
                    if (output.exists()) output.delete();
                    if (!output.createNewFile()) {
                        System.err.printf("Could not create the output file %s\n", outputPath);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            
            File rootFile = new File(root);

            if (rootFile.isFile()) {
                int dotIndex = rootFile.getName().lastIndexOf('.');
                if (rootFile.getName().substring(dotIndex + 1).compareTo("java") == 0) {
                    System.out.println(computeTLS(rootFile));
                }
            }

            computeTLS(rootFile.getPath(), writeOutput, output);
        } else {
            System.err.println("TLS must be called with arguments: java tls <dir> " +
                    "OR java tls -o <output-path.csv> <input-path>");
            System.exit(1);
        }
    }

    protected static String computeTLS(File file) {
        String filePath = file.getPath();
        String pack = getPackage(file);
        String className = file.getName().substring(0, file.getName().lastIndexOf('.'));
        int tloc = ift3913.tloc.computeTLOC(filePath);
        int tassert = ift3913.tassert.countAsserts(filePath);
        float tcmp = (float) tloc / tassert;

        return new Formatter().format("%s, %s, %s, %d, %d, %f\n", filePath, pack, className, tloc, tassert, tcmp).toString();
    }

    protected static void computeTLS(String folderPath, boolean writeOutput, File output) {
        File folder = new File(folderPath);
        File[] content = getJavaFiles(folder);
        File[] subdir = folder.listFiles(File::isDirectory);

        if (content != null) {
            for (File file : content) {
                String line = computeTLS(file);
                if (writeOutput) {
                    try {
                        Files.write(output.toPath(), line.getBytes(), StandardOpenOption.APPEND);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.print(line);
            }
        }

        if (subdir != null) {
            for (File dir : subdir) computeTLS(dir.getPath(), writeOutput, output);
        }
    }

    private static File[] getJavaFiles(File folder) {
        return folder.listFiles(file -> {
            int dotIndex = file.getName().lastIndexOf('.');
            return file.isFile() && file.getName().substring(dotIndex + 1).compareTo("java") == 0;
        });
    }

    private static String getPackage(File file) {
        String pack = "package ";

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Pattern pattern = Pattern.compile(pack);

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    int index = line.lastIndexOf(pack) + pack.length();
                    int end = line.indexOf(';', index);
                    return line.substring(index, end);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }
}
