# MiniSearchEngine
# Objective
The goal of this project is to develop a Mini Search Engine that allows you to search for files on your computer based on specific words or queries following certain rules. While the underlying principles and data structures used in building the search engine enable easy extension to web search, the focus of this project is limited to files on your local computer. The program was created in IntelliJ IDEA. The program is designed to read a set of commands either from a file or directly from the user input.
<br> What can be done next? An interface for the users, as the console is not very user-friendly. </br>


# Overview
The Mini Search Engine is designed to search for information in the local file system and perform two crucial operations of a search engine:

- Indexing: Loading information into the engine by:

  - Reading all files with a specific extension (e.g., txt, java, pdf, docx) from a directory structure starting from a root directory.
  - Extracting lexical units (words) from each file, retaining only distinct words (keywords).
  - Creating a mapping between each keyword and a data structure containing the names of all files (with paths) where that keyword appears.
- Searching: Allowing users to input various search queries and obtaining files (documents) that match those queries.

# Testing
The search engine will be tested by a web-based automated testing application for files with the following extensions:
"txt", "cpp", "java", "c", "xml", "csv", "html", "css", "js", "py", "docx", "pdf"

# Categories of Methods
The two main operations of the engine are implemented through the following methods:
- Indexing: build() and addDocument()
- Searching: search()
Other methods are categorized as follows:
- Methods providing information about the engine's state: getNumberOfKeywords(), getNumberOfDocuments(), getDocumentsOfKeyword(), getKeywordsOfDocument(), printDetails().
- Initialization methods: clear() – mainly used for testing and verification.
- Methods setting the display mode of document names: setShortDocName().
A crucial set of methods simulate updating (synchronizing) engine information with file operations in the file system:
- Deleting a file from the computer will remove it from the engine using deleteDocument().
- If a file is restored in the file system (e.g., through undo delete in Windows), it can be reintroduced to the engine using undeleteDocument().
- If a file is only renamed in the file system without modification, the engine should be updated using rename().

# Query Interpretation

Example of a Simplified Set (Stored in a File named setSimplu1.txt)
```
D:/sd/curs/c7/cursmap.txt
Interfata Map asociaza fiecarei chei o valoare. Clase care o implementeaza: TreeMap, HashMap...
src/ro/usv/DemoMap.java
class DemoMap { private Map<Long, String> nomenclator = new TreeMap<>(); }
```

Queries are interpreted following these rules:

- If a query contains a single word, the result is the set of document names associated with that keyword.

  - Example: search("map") -> [D:/sd/curs/c7/cursmap.txt, src/ro/usv/DemoMap.java]
- If a query contains multiple words without prefixes (+ or -), the result is the intersection of sets of document names associated with those keywords (interpreted as AND).
  - Example: search("interface map") -> [D:/sd/curs/c7/cursmap.txt]
- If a word is prefixed with +, the result is obtained by uniting the result obtained up to that word and the set associated with the word prefixed with +.
  - Example: search("interface +map") -> [D:/sd/curs/c7/cursmap.txt, src/ro/usv/DemoMap.java]
- If a word is prefixed with - (minus), the result is the set of document names where this word does not appear.
  - Example: search("-class") -> [D:/sd/curs/c7/cursmap.txt]
- If a word prefixed with - (minus) appears after other words, the result is the intersection of the result obtained up to the word prefixed with - and the set of document names where the word prefixed with - does not appear.
  - Example: search("interface -map") -> [] (since the word "interface" is associated with the set [D:/sd/curs/c7/cursmap.txt], and the word prefixed with minus, "-map," is associated with the empty set, intersecting them results in the empty set).
- Queries are processed from left to right, and there is no priority for operators.
  - Example: search("interface -map +new") -> [src/ro/usv/DemoMap.java] (the first two words result in an empty set, [], followed by the union with the set associated with the keyword "new").
