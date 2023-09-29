package ift3913;

import java.io.File;
import java.io.IOException;

public class tropcomp { 
    private double threshold1 = 0.01;
    private double threshold5 = 0.05;
    private double threshold10 = 0.1;
    private static String[] tlocDescendingOrder;
    private static String[] tmcpDescendingOrder;

    public static void main(String[] args) {
        File maindir;
        switch(args.length){
            case 0://manquer d'argument
              printErrorMessage(1);
            break;
            case 1:
                maindir = new File(args[0]);
                if(!maindir.exists()){
                   printErrorMessage(2);
                }else{
                    System.out.println("\nWorking..........\n");
                    String[] aStrings = new String[]{"java","-jar","tropcomp/resources/tls.jar", args[0]};
                    executeJarFile(aStrings);
                }
            break;
            case 2: // sortir un fichier
                maindir = new File(args[1]);   
                if(!maindir.exists()){
                   printErrorMessage(2);
                }else{
                }
            break;
            default://invalid entry
                printErrorMessage(3);
            break;
        }
    }

    private static String suspectedClasses(String tlsOutput){
        String[] strings = tlsOutput.split("\n");
        tlocDescendingOrder = strings.clone();
        tmcpDescendingOrder = strings.clone();
        tlocDescendingOrder = sort(strings, 3);
        tmcpDescendingOrder = sort(strings, 5);
        System.out.println("%%%%%%%%%%%%%%%%%%%TLOC ");
        for(int i =0;i<tlocDescendingOrder.length;i++){
            System.out.println(tlocDescendingOrder[i]);
        }
        System.out.println("\n\n%%%%%%%%%%%%%%%%%%%TCMP ");
         for(int i =0;i<tmcpDescendingOrder.length;i++){
            System.out.println(tmcpDescendingOrder[i]);
        }
        return null;
    }
    
    private static String[] sort(String[] strArr, int idxComp){
        int n = strArr.length;
        Double positiveInfinity = Double.POSITIVE_INFINITY;
        for (int i = 0; i < n-1; i++) {
            int min_idx = i;
            for (int j = i+1; j < n; j++){
                String fst = strArr[j].split(", ")[idxComp];
                String snd = strArr[min_idx].split(", ")[idxComp];
            
                if(idxComp == 3){
                    if(Integer.parseInt(fst)>Integer.parseInt(snd)){
                        min_idx = j;
                    }
                }else if(idxComp == 5){
                    Double fst_cv,snd_cv;
                    fst_cv = (!(isNumeric(fst)))? positiveInfinity : Double.parseDouble(fst);
                    snd_cv = (!(isNumeric(snd)))? positiveInfinity : Double.parseDouble(snd);
                    if(fst_cv > snd_cv){
                        min_idx = j;
                    }
                }
            }
            String temp = strArr[min_idx];
            strArr[min_idx] = strArr[i];
            strArr[i] = temp;
        }
        return strArr;
    }
 
    /**
     * Print out error message
    */
    private static void printErrorMessage(int codeMessage){
        switch(codeMessage){
            case 1:
                System.err.println("troncomp must contain 1 argument: java troncomp <project_path>");
                System.exit(1);
            break;
            case 2:
                System.err.println("Project not found! Please verify your entry path.");
                System.exit(1);
            break;
            case 3:
                System.err.println("Invalid argument! Command valide: troncomp -o <output_path.csv> <entry_path>");
                System.exit(1);
            break;
        }
    }
    //Inspired https://stackoverflow.com/questions/1320476/execute-another-jar-in-a-java-program/40544510#40544510
    private static void executeJarFile(String[] args){
        try{
            Process ps=Runtime.getRuntime().exec(args);
            ps.waitFor();
            java.io.InputStream is=ps.getInputStream();
            byte b[]=new byte[is.available()];
            is.read(b,0,b.length);
            suspectedClasses(new String(b));
        }catch(InterruptedException ex){
            ex.printStackTrace();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    /**
     * Inspired code https://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-numeric-in-java
     */
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?"); 
    }
}
