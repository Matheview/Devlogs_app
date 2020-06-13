package utils;

import com.pdfcrowd.*;
import java.io.*;

public class PdfGenerator {

    /**
     * Metoda do generowania raportów PDF
     * @param url adres z którego ma być wygenerowany PDF
     * @param target nazwa pliku (oraz ew. ścieżka) do pliku wyjściowego. UWAGA! Musi mieć rozszerzenie .pdf
     * @throws IOException
     * @throws Pdfcrowd.Error
     */
    public static void generate(String url, String target) throws IOException, Pdfcrowd.Error {
        // create the API client instance
        Pdfcrowd.HtmlToPdfClient client =
                new Pdfcrowd.HtmlToPdfClient("devslog", "d81f481f1d8f1bd9567ebc5a178d7b77");

        client.setPageSize("A3");
        // run the conversion and write the result to a file
        client.convertUrlToFile(url, target);
    }
}
