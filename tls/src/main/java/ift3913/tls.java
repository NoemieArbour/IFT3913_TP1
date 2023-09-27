package ift3913;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
        if (args.length != 1) {
            System.err.println("TLS must contain 1 argument: java tls <dir>");
            System.exit(1);
        }
        String root = args[0];
        File rootFile = new File(root);

        File[] content = rootFile.listFiles(pathname -> {
            int dotIndex = pathname.getName().lastIndexOf('.');
            return pathname.isFile() && pathname.getName().substring(dotIndex + 1).compareTo("java") == 0;
        });
        assert content != null;

        for (File file : content) {
            String filePath = file.getPath();
            String pack = getPackage(file);
            String className = file.getName().substring(0, file.getName().lastIndexOf('.'));
            int tloc = ift3913.tloc.computeTLOC(filePath);
            int tassert = ift3913.tassert.countAsserts(filePath);
            float tcmp = (float) tloc / tassert;
            System.out.printf("%s, %s, %s, %d, %d, %f\n", filePath, pack, className, tloc, tassert, tcmp);
        }
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
