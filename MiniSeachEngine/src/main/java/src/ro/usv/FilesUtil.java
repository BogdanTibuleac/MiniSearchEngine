package src.ro.usv;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.presentation.*;

import java.io.*;
import java.io.File;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilesUtil {

    public static String extractContent(String fileName) throws Exception {
        if (isPdfFile(fileName)) {
            return extractTextFromPDF(fileName);
        } else if (isDocxFile(fileName)){
            return  extractTextFromDOCX(fileName);
        } else if (isPptxFile(fileName)){
            return  extractTextFromPPTX(fileName);
        } else {
            return extractTextFromFile(fileName);
        }
    }

    private static boolean isPdfFile(String fileName) {
        return fileName.toLowerCase().endsWith(".pdf");
    }
    private static boolean isDocxFile(String fileName) {
        return fileName.toLowerCase().endsWith(".docx");
    }
    private static boolean isPptxFile(String fileName) { return fileName.toLowerCase().endsWith(".pptx"); }
    private static String extractTextFromFile(String fileName) throws IOException {
        try {
            Path filePath = Paths.get(fileName);
            byte[] fileBytes = Files.readAllBytes(filePath);
            return new String(fileBytes);
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
            return "";
        }
    }
    public static String extractTextFromPPTX(String pptxFilePath) throws Exception {
        Presentation presentation = new Presentation();

        try {
            // Load the PowerPoint document
            presentation.loadFromFile(pptxFilePath);

            // Create a StringBuilder instance to store the extracted text
            StringBuilder buffer = new StringBuilder();

            // Iterate through slides and extract text
            for (Object slide : presentation.getSlides()) {
                for (Object shape : ((ISlide) slide).getShapes()) {
                    if (shape instanceof IAutoShape) {
                        for (Object tp : ((IAutoShape) shape).getTextFrame().getParagraphs()) {
                            buffer.append(((ParagraphEx) tp).getText()).append("\n");
                        }
                    }
                }
            }
            return buffer.toString();
        } finally {
            // Ensure that the presentation is closed to free resources
            presentation.dispose();
        }
    }

    private static String extractTextFromPDF(String pdfFileName) throws IOException {
        try {
            PdfReader reader = new PdfReader(pdfFileName);
            int pages = reader.getNumberOfPages();
            StringBuilder text = new StringBuilder();
            for (int i = 1; i <= pages; i++) {
                text.append(PdfTextExtractor.getTextFromPage(reader, i));
            }
            reader.close();
            return text.toString();
        } catch (IOException e) {
            System.out.println("An error occurred while extracting text from PDF using iTextPDF: " + e.getMessage());
            return "";
        }
    }

    private static String extractTextFromDOCX(String docxFileName) throws IOException {
        try {
            // Initialize an instance of the Document class
            Document document = new Document();

            // Load a Word document
            document.loadFromFile(docxFileName, FileFormat.Docx);

            // Get text from the whole document
            StringBuilder text = new StringBuilder(document.getText());

            return text.toString();
        } catch (Exception e) {
            System.out.println("An error occurred while extracting text from .docx file: " + e.getMessage());
            return "";
        }
    }
}
