package ift3913;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link tassert} est un programme qui, étant donné un fichier source d'une classe de test java, calcule la métrique de
 * taille TASSERT : nombre de assertions JUnit.
 */
public class tassert {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("TASSERT must contain 1 argument: java tassert <filename>.java");
            System.exit(1);
        }

        String fileName = args[0];

        System.out.println(countAsserts(fileName));
    }

    protected static int countAsserts(String fileName) {
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            Pattern assertPattern = Pattern.compile("(assert)(True|False|Equals|NotEquals|" +
                    "ArrayEquals|NotNull|Null|Same|NotSame|That|Throws)(\\()");

            while ((line = reader.readLine()) != null) {
                Matcher matcher = assertPattern.matcher(line);
                while (matcher.find()) count++;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return count;
    }
}
