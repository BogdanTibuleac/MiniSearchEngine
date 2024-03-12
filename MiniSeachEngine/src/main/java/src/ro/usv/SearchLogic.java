package src.ro.usv;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class SearchLogic {
    private final TreeMap<String, SortedSet<String>> keywordToDocumentsMap;
    private final TreeMap<String, SortedSet<String>> documentToKeywordsMap;
    private final boolean showOnlyDocName;

    // Constructor
    public SearchLogic(TreeMap<String, SortedSet<String>> keywordToDocumentsMap,
                       TreeMap<String, SortedSet<String>> documentToKeywordsMap,
                       boolean showOnlyDocName) {
        this.keywordToDocumentsMap = keywordToDocumentsMap;
        this.documentToKeywordsMap = documentToKeywordsMap;
        this.showOnlyDocName = showOnlyDocName;
    }

    // Main search method
    /*
    *  1. Example: If set has elements A, B, C, and set2 has elements B, C, D,
    *  then after set.retainAll(set2), set will contain only elements B and C.
    *
    *  2.Example: If set has elements A, B, C, and set2 has elements B, C, D,
    *  then after set.addAll(set2), set will contain elements A, B, C, D.
    *
    *  3. Example: If set has elements A, B, C, and set2 has elements B, C, D,
    *  then after set.removeAll(set2), set will contain only element A.
    *
    * */
    public SortedSet<String> search(String interogare) {
        SortedSet<String> set;
        String[] interogationsWords = interogare.split(" ");

        // Adjust the array length to exclude the first element
        interogationsWords = adjustArrayLength(interogationsWords);

        if (interogationsWords.length == 0) {
            return new TreeSet<>();
        }

        if (interogationsWords.length == 1) {
            set = handleSingleWordQuery(interogationsWords[0]);
        } else {
            set = handleMultipleWordsQuery(interogationsWords, interogare);
        }
        processResults(set);

        return set;
    }

    // Utility method to adjust array length
    private String[] adjustArrayLength(String[] words) {
        for (int i = 0; i < words.length - 1; ++i) {
            words[i] = words[i + 1];
        }
        return Arrays.copyOf(words, words.length - 1);
    }

    // Handle a single-word query
    private SortedSet<String> handleSingleWordQuery(String word) {
        SortedSet<String> set;

        if (!word.contains("-")) {
            // Retrieve documents associated with the keyword
            set = keywordToDocumentsMap.get(word);
            if (set == null) {
                return new TreeSet<>();
            }
            set = new TreeSet<>(set);
        } else {
            // Handle a query with a minus prefix (exclude documents)
            set = handleMinusPrefix(word);
        }

        return set;
    }

    // Handle a multi-word query
    private SortedSet<String> handleMultipleWordsQuery(String[] words, String interogare) {
        SortedSet<String> set;

        if (!interogare.contains("+") && !interogare.contains("-")) {
            // Handle an intersection query (LOGIC AND)
            set = handleIntersection(words);
        } else {
            // Handle a query with plus and minus prefixes
            set = handlePlusMinusPrefix(words, interogare);
        }

        return set;
    }

    // Handle a query with a minus prefix
    private SortedSet<String> handleMinusPrefix(String word) {
        SortedSet<String> set;
        // Get all document names
        set = (SortedSet<String>) documentToKeywordsMap.keySet();
        // Get the set of documents to remove (exclude) based on the minus prefix
        SortedSet<String> setToRemove = keywordToDocumentsMap.get(word.substring(1));
        if (setToRemove != null) {
            // Exclude documents from the result set
            set = new TreeSet<>(set);
            set.removeAll(setToRemove);
        }
        return set;
    }

    // Handle an intersection query (LOGIC AND)
    private SortedSet<String> handleIntersection(String[] words) {
        SortedSet<String> set;
        // Get the initial set based on the first keyword
        set = keywordToDocumentsMap.get(words[0]);
        if (set == null) {
            return new TreeSet<>();
        }
        set = new TreeSet<>(set);

        // Iterate over the remaining keywords and retain documents that appear in all sets
        for (int i = 1; i < words.length; i++) {
            SortedSet<String> set2 = keywordToDocumentsMap.get(words[i]);
            if (set2 != null) {
                set.retainAll(set2);
            } else {
                // If a keyword has no associated documents, clear the result set
                set.clear();
                break;
            }
        }
        return set;
    }

    // Handle a query with plus and minus prefixes
    private SortedSet<String> handlePlusMinusPrefix(String[] words, String interogare) {
        SortedSet<String> set;

        if (!interogare.contains("+") && !interogare.contains("-")) {
            // Handle a query without plus or minus operators (LOGIC AND)
            set = keywordToDocumentsMap.get(words[0]);
            if (set == null) {
                return new TreeSet<>();
            }
            set = new TreeSet<>(set);

            // Iterate over the remaining keywords and retain documents that appear in all sets
            for (int i = 1; i < words.length; i++) {
                SortedSet<String> set2 = keywordToDocumentsMap.get(words[i]);
                if (set2 != null) {
                    set.retainAll(set2);
                } else {
                    // If a keyword has no associated documents, clear the result set
                    set.clear();
                    break;
                }
            }
        } else {
            // Handle a query with plus and minus operators
            set = handlePlusMinusOperators(words);
        }
        return set;
    }

    // Handle plus, minus, and intersection operators
    private SortedSet<String> handlePlusMinusOperators(String[] words) {
        SortedSet<String> set = keywordToDocumentsMap.get(words[0]);

        if (set == null) {
            return new TreeSet<>();
        }
        set = new TreeSet<>(set);

        // Iterate over the keywords and apply plus, minus, and intersection operations
        for (int i = 1; i < words.length; i++) {
            SortedSet<String> set2;

            if (words[i].contains("+")) {
                // Handle the plus operator (include documents)
                set2 = keywordToDocumentsMap.get(words[i].substring(1));
                if (set2 != null) {
                    set.addAll(set2);
                }
            } else if (words[i].contains("-")) {
                // Handle the minus operator (exclude documents)
                set2 = keywordToDocumentsMap.get(words[i].substring(1));
                if (set2 != null) {
                    set.removeAll(set2);
                }
            } else {
                // Handle a keyword without plus or minus operators (LOGIC AND)
                set2 = keywordToDocumentsMap.get(words[i]);
                if (set2 != null) {
                    set.retainAll(set2);
                } else {
                    // If a keyword has no associated documents, clear the result set
                    set.clear();
                    break;
                }
            }
        }
        return set;
    }

    // Process the results (optional)
    private void processResults(SortedSet<String> set) {
        // If specified, further process the results (e.g., display document names)
        if (!showOnlyDocName) {
            set.forEach(s -> getNameFromPath(s));
        }
    }

    // Extract the document name from the path (optional)
    private String getNameFromPath(String path) {
        // Split the path and return the last component (document name)
        String[] lines = path.split("\\\\");
        return lines[lines.length - 1];
    }
}
