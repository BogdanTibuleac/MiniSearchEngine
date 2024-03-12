package src.ro.usv;

import java.util.*;

public class AnalizaText {

    private String text;
    private String[] atomiLexicali;
    private SortedSet<String> cuvDist;
    private SortedSet<String> cuvDistDia;
    private HashMap<String, List<String>> dictAnagrame;
    private SortedMap<Character, Integer> frecvLitere;
    private long nrcuvinte;
    private boolean acceptSiIdentificatori;
    private String regexMatcher;
    private String regexSplit = "[^a-zA-Z0-9_@$]+";
    //private String regexSplit = "[^a-zA-Z0-9_@$ăîâșț]+";
    public AnalizaText(String text) {
        this(text, false);
    }

    public AnalizaText(String text, boolean acceptSiIdentificatori) {
        this.text = text;
        this.acceptSiIdentificatori = acceptSiIdentificatori;
        regexMatcher = acceptSiIdentificatori ? "[a-zA-Z_$@][\\w$]*" : "[a-zA-Z]+";
        parcurgeText();
    }

    public void parcurgeText() {
        atomiLexicali = text.split(regexSplit);
        nrcuvinte = 0;
        cuvDist = new TreeSet<>();
        cuvDistDia = new TreeSet<>();
        dictAnagrame = new HashMap<>();
        frecvLitere = new TreeMap<>();

        for (String cuv : atomiLexicali) {
            int _1 = 1;
            if (cuv.matches(regexMatcher)) {
                nrTotalLitere += cuv.length();
                nrcuvinte++;
                cuv = cuv.toLowerCase();
                cuvDist.add(cuv);  // TreeSet retine doar cheile (cuvintele) distincte
                if (!acceptSiIdentificatori) {
                    char[] litere = cuv.toCharArray();
                    addAnagrame(cuv, litere);
                    addFrecventaLitere(litere);
                }
            }
        }
    }

    private void addAnagrame(String cuv, char[] litere) {
        if (cuv.length() == 1)  // || cuv.matches("\\d*"))
            return;
        Arrays.sort(litere);
        String sirLitere = String.valueOf(litere);
        List lcuv = dictAnagrame.get(sirLitere);
        if (lcuv == null) {
            lcuv = new ArrayList();
        }
        if (!lcuv.contains(cuv)) {
            lcuv.add(cuv);
            dictAnagrame.put(sirLitere, lcuv);
        }
    }

    private long nrTotalLitere = 0;

    private void addFrecventaLitere(char[] litere) {
        Integer nr;
        for (char car : litere) {
            if (!Character.isLetter(car)) continue;
            nr = frecvLitere.get(car);
            if (nr == null) {
                frecvLitere.put(car, 1);
            } else {
                frecvLitere.put(car, ++nr);
            }
        }
    }


    public void setText(String text) {
        this.text = text;
        parcurgeText();
    }

    public SortedSet<String> getCuvDist() {
        return cuvDist;
    }

    public SortedSet<String> getCuvDistDia() {
        return cuvDistDia;
    }

    public HashMap<String, List<String>> getDictAnagrame() {
        return dictAnagrame;
    }

    public long getNrcuvinte() {
        return nrcuvinte;
    }

    public long getNrTotalLitere() {
        return nrTotalLitere;
    }

    public String getText() {
        return text;
    }

    public SortedMap<Character, Integer> getFrecvLitere() {
        return frecvLitere;
    }

    public List<Map.Entry<Character, Integer>> getIntrariLitereFrecvDesc() {
        Collection<Map.Entry<Character, Integer>> frecvIntrari =
                (Collection<Map.Entry<Character, Integer>>) frecvLitere.entrySet();
        List<Map.Entry<Character, Integer>> lstIntrari = new ArrayList<>(frecvIntrari);
        Collections.sort(lstIntrari,
                Comparator.comparing(x -> ((Map.Entry<Character, Integer>) x).getValue()).reversed());

        return lstIntrari;
    }

    public String[] getAtomiLexicali() {
        return atomiLexicali;
    }

    public void afisInfo(boolean afisTextInitial, boolean afisCuvinteDistincte, boolean afisListanagrame, boolean afisFrecvLitere) {
        if (afisTextInitial)
            System.out.println("Textul initial:\n" + getText());

        if (afisCuvinteDistincte) {
            System.out.println("Cuvinte distincte " + getCuvDist());
            System.out.println("Total cuvinte distincte:" + getCuvDist().size() + " / " + getNrcuvinte());
        }
        if (afisListanagrame) {
            int ncuvAnagrame = 0;
            for (String ls : dictAnagrame.keySet()) {
                List lstAnagrame = dictAnagrame.get(ls);
                if (lstAnagrame.size() > 1) {
                    ncuvAnagrame++;
                    System.out.println(ls + "->" + lstAnagrame);
                }
            }
            System.out.println("Total " + ncuvAnagrame + " grupuri de anagrame");
        }

        if (afisFrecvLitere) {
            SortedMap<Character, Integer> frecv = getFrecvLitere();
            System.out.println("Nr.litere distincte=" + frecv.size() + " / Nr.total litere=" + nrTotalLitere);
            System.out.println("Freventa litere:");
            for (Map.Entry e : frecv.entrySet())
                System.out.printf("%c = %.2f%%  ", (char) e.getKey(),
                        ((Integer) e.getValue()).floatValue() / nrTotalLitere * 100.);

            System.out.println("\nIn ordinea descrecatoare a frecventelor:");
            StringBuilder sbfrecv = new StringBuilder();
//          for(Map.Entry e: getFrecvLitereDescrescator().entrySet()) { //1. LinkedHashMap sortat
            for (Map.Entry e : getIntrariLitereFrecvDesc()) {     // solutia 2.List<Map.Entry>  sortata
                System.out.printf("%c = %.2f%%  ", (char) e.getKey(),
                        ((Integer) e.getValue()).floatValue() / nrTotalLitere * 100.);
                sbfrecv.append(e.getKey());
            }
            System.out.println("\n" + sbfrecv.toString().toUpperCase());
        }
    }


    public static void main(String[] args) {
        String continut = "/*Un exemplu de program `aproape corect`\n" +
                "util doar pt. test identificatori si adnotari*/\n" +
                "class Aplicatie1{ @Override\n" +
                "    public String toString() { $a$simbol$generat$de$compilator \n" +
                "        return \"AnalizaText\";}" +
                "public static void main(String args[]){a123=b_-_c; _++; F+=G+H;" +
                "System.out.println(\"Succes!\"); }}\n";
        SortedSet<String> keywords = new AnalizaText(continut, true).getCuvDist();
        System.out.println(keywords.size() + " de cuvinte detectate\n" + keywords);
        SortedSet<String> cuvinte = new AnalizaText(continut, false).getCuvDist();
        System.out.println(cuvinte.size() + " de cuvinte distincte\n" + cuvinte);
        keywords.removeAll(cuvinte);
        System.out.println("Accptate doar ca identificatori/adnotari: " + keywords);
    }
}