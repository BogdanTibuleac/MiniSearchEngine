package src.ro.usv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MotorDeCautare implements IMiniMotorDeCautare{
    TreeMap<String, SortedSet<String>> keywordToDocumentsMap;
    TreeMap<String, SortedSet<String>> documentToKeywordsMap;
    TreeMap<String, SortedSet<String>> inactiveDocumentToKeywordsMap = new TreeMap<>();
    private boolean showOnlyDocName;
    ArrayList<String> extensions = new ArrayList<>(Arrays.asList("txt", "cpp", "java", "c", "xml", "csv", "html", "css", "js", "py", "docx", "pdf", "pptx"));

    private final SearchLogic searchLogic;
    public MotorDeCautare() {
        this.documentToKeywordsMap = new TreeMap<>();
        this.keywordToDocumentsMap = new TreeMap<>();
        this.showOnlyDocName = true;
        this.searchLogic = new SearchLogic(keywordToDocumentsMap, documentToKeywordsMap, showOnlyDocName);
    }
    @Override
    public Pereche<Integer, Integer> build(String numeFisSetSimplificat) throws FileNotFoundException {
        Pereche<Integer, Integer> pereche = new Pereche<>(0, 0);
        Scanner scanner = new Scanner(new File(numeFisSetSimplificat));

        while (scanner.hasNextLine()) {
            String pathLine = scanner.nextLine();

            if (scanner.hasNextLine()) {
                String contentLine = scanner.nextLine();

                // Process the pathLine and contentLine similarly to the original implementation
                pathLine = pathLine.trim();
                contentLine = contentLine.trim();

                if (!contentLine.isEmpty()) {
                    documentToKeywordsMap.putIfAbsent(pathLine, new TreeSet<>());
                    SortedSet<String> keywords = new AnalizaText(contentLine, true).getCuvDist();

                    for (String s : keywords) {
                        s = s.toLowerCase().trim();
                        if (!s.isEmpty()) {
                            documentToKeywordsMap.get(pathLine).add(s);
                            keywordToDocumentsMap.putIfAbsent(s, new TreeSet<>());
                            keywordToDocumentsMap.get(s).add(pathLine);
                        }
                    }
                } else {
                    System.out.println("Lipsa continut pentru documentul " + pathLine);
                }
            } else {
                System.out.println("Lipsa continut pentru documentul " + pathLine);
                break;
            }
        }

        pereche.n1 = documentToKeywordsMap.size();
        pereche.n2 = keywordToDocumentsMap.size();
        return pereche;
    }

    @Override
    public int getNumberOfKeywords() {
        return keywordToDocumentsMap.keySet().size();
    }

    @Override
    public int getNumberOfDocuments() {
        return documentToKeywordsMap.keySet().size();
    }

    @Override
    public int getNumberOfKeywords(String document) {
        SortedSet<String> keywords =documentToKeywordsMap.get(document);
        return keywords != null ? keywords.size() : -1;
    }

    @Override
    public SortedSet<String> getKeywordsOfDocument(String document) {
        return !documentToKeywordsMap.containsKey(document) ? new TreeSet<>() : documentToKeywordsMap.get(document);
    }

    @Override
    public int getNumberOfDocuments(String cuvantCheie) {
        return (keywordToDocumentsMap.get(cuvantCheie)).size();
    }

    @Override
    public SortedSet<String> getDocumentsOfKeyword(String cuvantCheie) {
        if (!keywordToDocumentsMap.containsKey(cuvantCheie)) {
            return new TreeSet<>();
        } else {
            Stream<String> documentStream = keywordToDocumentsMap.get(cuvantCheie).stream();

            if (!showOnlyDocName) {
                return documentStream.map(this::getNameFromPath)
                        .collect(Collectors.toCollection(TreeSet::new));
            } else {
                return documentStream.collect(Collectors.toCollection(TreeSet::new));
            }
        }
    }

    @Override
    public void printDetails() {
        System.out.println("Numar documente: " + getNumberOfDocuments());

        AtomicInteger i = new AtomicInteger(0);
        documentToKeywordsMap.keySet().stream()
                .map(s -> !showOnlyDocName ? getNameFromPath(s) : s)
                .forEach(s -> System.out.println(i.incrementAndGet() + ". " + s));

        i.set(0);
        System.out.println("Numar cuvinte cheie: " + getNumberOfKeywords());
        keywordToDocumentsMap.entrySet().stream()
                .map(entry -> i.incrementAndGet() + ". " + entry.getKey() + " - " + entry.getValue().size() + " doc., active: " + entry.getValue())
                .forEach(System.out::println);
    }

    @Override
    public SortedSet<String> search(String interogare) {
        return searchLogic.search(interogare);
    }
    @Override
    public Pereche<Integer, Integer> build(String dir, String fisierStopWords) throws Exception {
        File directory = new File(dir);

        // Check if the provided path is a directory
        if (directory.isDirectory()) {
            File[] listFiles = directory.listFiles();

            // Iterate through each file in the directory
            if (listFiles != null) {
                for (File file : listFiles) {
                    // Recursively call the build method for subdirectories
                    if (file.isDirectory()) {
                        build(file.getPath(), fisierStopWords);
                    } else {
                        // Process regular files
                        processRegularFile(file, fisierStopWords);
                    }
                }
            }
        } else {
            // Process the provided path if it's a regular file
            if (directory.isFile()) {
                processRegularFile(directory, fisierStopWords);
            } else {
                System.out.println("The path is not a directory name");
            }
        }

        return new Pereche<>(documentToKeywordsMap.size(), keywordToDocumentsMap.size());
    }

    private void processRegularFile(File file, String fisierStopWords) throws Exception {
        String[] path = file.getPath().split("\\.");
        String extension = path[path.length - 1];

        // Check if the file extension is in the list of allowed extensions
        if (extensions.contains(extension)) {
            // Check if stop words file is provided
            if (!fisierStopWords.equalsIgnoreCase("null")) {
                // Add the document with content and stop words processing
                addDocument(file.getPath(), FilesUtil.extractContent(file.getPath()), getFisierStopWords(fisierStopWords));
            } else {
                // Add the document without stop words processing
                addDocument(file.getPath());
            }
        }
    }


    Set<String> getFisierStopWords(String fisierStopWords) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(fisierStopWords));
        TreeSet<String> stopWords = new TreeSet<>();
        while(scanner.hasNext()){
            stopWords.add(scanner.nextLine());
        }
        return stopWords;
    }
    @Override
    public Pereche<Integer, Integer> build(String dir, String fisierStopWords, String[] alteExtensii) throws Exception {
        extensions.addAll(Arrays.asList(alteExtensii));
        Pereche<Integer, Integer> pereche = build(dir, fisierStopWords);
        extensions.removeAll(Arrays.asList(alteExtensii));
        return pereche;
    }

    @Override
    public boolean addDocument(String numeFisierDocument) throws Exception {
        String content = FilesUtil.extractContent(numeFisierDocument);
        return addDocument(numeFisierDocument, content, null);
    }

    @Override
    public boolean addDocument(String numeDoc, String continut, Set<String> stopWords) {
        SortedSet<String> keywords = new AnalizaText(continut, true).getCuvDist();
        if(stopWords != null){
            keywords.removeAll(stopWords);
            if(continut.isEmpty()){
                return false;
            }
        }
        for(String s: keywords){
            s= s.toLowerCase().trim();
            documentToKeywordsMap.putIfAbsent(numeDoc, new TreeSet<>());
            documentToKeywordsMap.get(numeDoc).add(s);
            keywordToDocumentsMap.putIfAbsent(s, new TreeSet<>());
            keywordToDocumentsMap.get(s).add(numeDoc);
        }
        return true;
    }

    @Override
    public boolean deleteDocument(String numeDocument) {
        if(!documentToKeywordsMap.containsKey(numeDocument)){
            return false;
        }
        inactiveDocumentToKeywordsMap.put(numeDocument,documentToKeywordsMap.get(numeDocument));
        documentToKeywordsMap.remove(numeDocument);
        for(String s: inactiveDocumentToKeywordsMap.get(numeDocument)){
            keywordToDocumentsMap.get(s).remove(numeDocument);
            if(keywordToDocumentsMap.get(s).isEmpty()){
                keywordToDocumentsMap.remove(s);
            }
        }
        return true;
    }

    @Override
    public boolean undeleteDocument(String numeDocument) {
        documentToKeywordsMap.put(numeDocument, inactiveDocumentToKeywordsMap.get(numeDocument));
        inactiveDocumentToKeywordsMap.remove(numeDocument);
        for(String s:documentToKeywordsMap.get(numeDocument)){
            keywordToDocumentsMap.putIfAbsent(s, new TreeSet<>());
            keywordToDocumentsMap.get(s).add(numeDocument);
        }
        return true;
    }

    @Override
    public boolean renameDocument(String numeDocument1, String numeDocument2) {
        SortedSet<String> set =documentToKeywordsMap.get(numeDocument1);
        documentToKeywordsMap.remove(numeDocument1);
        documentToKeywordsMap.put(numeDocument2, set);
        for(String s: set){
            keywordToDocumentsMap.get(s).remove(numeDocument1);
            keywordToDocumentsMap.get(s).add(numeDocument2);
        }
        return true;
    }

    @Override
    public Pereche<Integer, Integer> clear() {
        keywordToDocumentsMap.clear();
        documentToKeywordsMap.clear();
        inactiveDocumentToKeywordsMap.clear();
        return new Pereche<>(documentToKeywordsMap.size(),keywordToDocumentsMap.size());
    }

    @Override
    public boolean setShortDocName(String faraCaleCompleta) {
        showOnlyDocName = faraCaleCompleta.trim().equalsIgnoreCase("true");
        return showOnlyDocName;
    }

    public String getNameFromPath(String path){
        String[] lines = path.split("\\\\");
        return lines[lines.length-1];
    }
}
