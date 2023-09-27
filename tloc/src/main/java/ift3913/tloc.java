package ift3913;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * {@link tloc} est un programme qui, étant donné un fichier source d'une classe
 * de test java, calcule la métrique de taille TLOC : nombre de lignes de code
 * non-vides qui ne sont pas de commentaires.
 * Regex: Inspired from
 * <a href="https://stackoverflow.com/questions/6640071/how-to-find-all-comments-in-the-source-code">...</a>
 */
public class tloc {
    private static int countComment(String contents) {
        String strLiteral = "\"(?:\\\\.|[^\\\\\"\r\n])*\"";
        String charLiteral = "'(?:\\\\.|[^\\\\'\r\n])+'";
        String multipleComment = "/\\*[\\s\\S]*?\\*/";
        String singleComment = "//[^\r\n]*";
        String other = "[\\s\\S]";
        int count = 0;
        StringBuilder b = new StringBuilder();
        Pattern p = Pattern.compile(String.format("(%s)|(%s)|%s|%s|%s", singleComment,
                multipleComment, strLiteral, charLiteral, other));
        Matcher m = p.matcher(contents);
        while (m.find()) {
            String hit = m.group();
            if (m.group(1) != null || m.group(2) != null) {
                b.append(hit).append("\n");
            }
        }
        if (!b.toString().isEmpty()) {
            String[] parts = b.toString().split("\n");
            count = parts.length;
        }
        return count;
    }

    protected static int computeTLOC(String fileName) {
        int linesCount = 0;
        int lineNull = 0;
        StringBuilder sb = new StringBuilder();
        Scanner scan;
        try {
            scan = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        while (scan.hasNextLine()) {
            linesCount++;
            String line = scan.nextLine();
            if (line.isEmpty())
                lineNull++;
            sb.append(line).append('\n');
        }
        int commentLineCount = countComment(sb.toString());
        return linesCount - lineNull - commentLineCount;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("TLOC must contain 1 argument: java tloc <filename>.java");
            System.exit(1);
        }
        System.out.println(computeTLOC(args[0]));
    }
}