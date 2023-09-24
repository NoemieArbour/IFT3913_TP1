package ift3913;

import java.util.*;
import java.io.*;
import java.util.regex.*;
/**
 * {@link tloc} est un programme qui, étant donné un fichier source d'une classe
 * de test java, calcule la métrique de taille TLOC : nombre de lignes de code
 * non-vides qui ne sont pas de commentaires.
 */
/**
 * Regex: Inspired from 
 * https://stackoverflow.com/questions/6640071/how-to-find-all-comments-in-the-source-code
 */
public class tloc {
	private static int countComment(String contents) {
		String strLitteral = "\"(?:\\\\.|[^\\\\\"\r\n])*\"";
		String charLitteral = "'(?:\\\\.|[^\\\\'\r\n])+'";
		String multipleComment = "/\\*[\\s\\S]*?\\*/";
		String singleComment = "//[^\r\n]*";
		String other = "[\\s\\S]";
		int count = 0;
		StringBuilder b = new StringBuilder();
		Pattern p = Pattern.compile(String.format("(%s)|(%s)|%s|%s|%s", singleComment,
				multipleComment, strLitteral, charLitteral, other));
		Matcher m = p.matcher(contents);
		while (m.find()) {
			String hit = m.group();
			if (m.group(1) != null || m.group(2) != null) {
				b.append(hit + "\n");
			}
		}
		if(!b.toString().isEmpty()) {
			String[] parts = b.toString().split("\n");
			count = parts.length;
		}
		return count;
	}

	public static void main(String[] args) throws Exception {
		int linesCount = 0;
		int lineNull = 0;
		StringBuilder sb = new StringBuilder();
		Scanner scan = new Scanner(new File("./tloc/src/main/java/ift3913/tloc.java"));
		while (scan.hasNextLine()) {
			linesCount++;
			String line = scan.nextLine();
			if (line.isEmpty())
				lineNull++;
			sb.append(line).append('\n');
		}
		int commentLineCount = countComment(sb.toString());
		System.out.println("number of lines not null not comment  :: " + (linesCount - lineNull - commentLineCount));
	}
}