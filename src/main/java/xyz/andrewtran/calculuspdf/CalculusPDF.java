package xyz.andrewtran.calculuspdf;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class CalculusPDF {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) {
        File inputFolder = new File("input");
        inputFolder.mkdirs();
        File outputFolder = new File("output");
        outputFolder.mkdirs();
        List<File> outputFiles = new ArrayList<>();
        //noinspection ConstantConditions
        for (File pdfFile : inputFolder.listFiles()) {
            File outputFile = new File(outputFolder, pdfFile.getName());
            try (PDDocument document = PDDocument.load(pdfFile)) {
                int pages = document.getNumberOfPages();
                // If odd amount of pages, add a blank page
                if (pages % 2 == 1) {
                    document.addPage(new PDPage());
                    document.save(outputFile);
                } else {
                    Files.copy(pdfFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                outputFiles.add(outputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File outputFile = new File("output.pdf");
        outputFile.delete();
        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName(outputFile.getAbsolutePath());
        for (File outputPDFFile : outputFiles) {
            try {
                merger.addSource(outputPDFFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        try {
            merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done: " + outputFile.getAbsolutePath());
    }
}
