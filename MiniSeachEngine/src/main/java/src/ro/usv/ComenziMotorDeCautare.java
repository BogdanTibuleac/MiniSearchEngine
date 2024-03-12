package src.ro.usv;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/*
*   Autor Tibuleac Bogdan-Ionut
*/

public class ComenziMotorDeCautare {

    public static void main(String[] args) throws Exception {
        MotorDeCautare motoras = new MotorDeCautare();
        Scanner scanner = new Scanner(new File("comenzi10.txt"));
        //Scanner scanner = new Scanner(System.in); // citire de la tastatura
        //System.out.println("Current Working Directory: " + System.getProperty("user.dir"));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(">>> " + line);

            String[] lineSplit = line.split(" ");
            String command = lineSplit[0].trim();

            switch (command) {
                case "build":
                    Pereche<Integer, Integer> pereche;
                    if(lineSplit.length == 2){
                        pereche = motoras.build(lineSplit[1]);
                    }else if(lineSplit.length == 3) {
                        pereche = motoras.build(lineSplit[1], lineSplit[2]);
                    }else{
                        String[] split = new String[lineSplit.length-3];
                        System.arraycopy(lineSplit, 3, split, 0, lineSplit.length-3);
                        pereche = motoras.build(lineSplit[1], lineSplit[2], split);
                    }
                    System.out.println(pereche);
                    break;
                case "getNumberOfKeywords":
                    System.out.println(parseGetNumberOfKeywordsArguments(motoras, lineSplit));
                    break;
                case "getNumberOfDocuments":
                    System.out.println(parseGetNumberOfDocumentsArguments(motoras, lineSplit));
                    break;
                case "getKeywordsOfDocument":
                    System.out.println(parseGetKeywordsOfDocumentArguments(motoras, lineSplit));
                    break;
                case "getDocumentsOfKeyword":
                    System.out.println(parseGetDocumentsOfKeywordArguments(motoras, lineSplit));
                    break;
                case "printDetails":
                    motoras.printDetails();
                    break;
                case "search":
                    System.out.println(parseSearchArguments(motoras, lineSplit, line));
                    break;
                case "addDocument":
                    System.out.println(parseAddDocumentArguments(motoras, lineSplit));
                    break;
                case "deleteDocument":
                    System.out.println(parseDeleteDocumentArguments(motoras, lineSplit));
                    break;
                case "undeleteDocument":
                    System.out.println(parseUndeleteDocumentArguments(motoras, lineSplit));
                    break;
                case "setShortDocName":
                    System.out.println(parseSetShortDocNameArguments(motoras, lineSplit));
                    break;
                case "rename":
                    System.out.println(parseRenameArguments(motoras, lineSplit));
                    break;
                case "clear":
                    System.out.println(motoras.clear());
                    break;
                case "stop":
                    System.exit(0);
                    break;
                default:
                    System.out.println("Comanda necunoascuta: " + command);
                    break;
            }
        }
    }

    private static String parseGetNumberOfKeywordsArguments(MotorDeCautare motoras, String[] lineSplit) {
        return (lineSplit.length == 1) ? String.valueOf(motoras.getNumberOfKeywords()) : String.valueOf(motoras.getNumberOfKeywords(lineSplit[1]));
    }

    private static String parseGetNumberOfDocumentsArguments(MotorDeCautare motoras, String[] lineSplit) {
        return (lineSplit.length == 1) ? String.valueOf(motoras.getNumberOfDocuments()) : String.valueOf(motoras.getNumberOfDocuments(lineSplit[1].toLowerCase()));
    }

    private static String parseGetKeywordsOfDocumentArguments(MotorDeCautare motoras, String[] lineSplit) {
        return (lineSplit.length == 1) ? "Lipsa argument" : motoras.getKeywordsOfDocument(lineSplit[1]).toString();
    }

    private static String parseGetDocumentsOfKeywordArguments(MotorDeCautare motoras, String[] lineSplit) {
        if (lineSplit.length == 1) {
            return "Lipsa argument";
        } else if (lineSplit.length > 2) {
            return "Numar incorect de argumente: " + (lineSplit.length - 1);
        } else {
            return motoras.getDocumentsOfKeyword(lineSplit[1].toLowerCase()).toString();
        }
    }
    private static String parseRenameArguments(MotorDeCautare motoras, String[] lineSplit) {
        return (lineSplit.length != 3) ? "Numar incorect de argumente " : String.valueOf(motoras.renameDocument(lineSplit[1], lineSplit[2]));
    }

    private static String parseSearchArguments(MotorDeCautare motoras, String[] lineSplit, String line) {
        return (lineSplit.length == 1) ? "Lipsa argument" : motoras.search(line.toLowerCase()).toString();
    }

    private static String parseAddDocumentArguments(MotorDeCautare motoras, String[] lineSplit) throws Exception {
        return (lineSplit.length == 1) ? "Lipsa argument" : String.valueOf(motoras.addDocument(lineSplit[1]));
    }

    private static String parseDeleteDocumentArguments(MotorDeCautare motoras, String[] lineSplit) {
        return (lineSplit.length == 1) ? "Lipsa argument" : String.valueOf(motoras.deleteDocument(lineSplit[1]));
    }


    private static String parseUndeleteDocumentArguments(MotorDeCautare motoras, String[] lineSplit) {
        return (lineSplit.length == 1) ?"Lipsa argument" : String.valueOf(motoras.undeleteDocument(lineSplit[1]));
    }

    private static String parseSetShortDocNameArguments(MotorDeCautare motoras, String[] lineSplit) {
        return (lineSplit.length == 1) ? "Lipsa argument" : String.valueOf(motoras.setShortDocName(lineSplit[1]));
    }
}
