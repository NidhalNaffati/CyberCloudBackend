package tn.esprit.utils;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import tn.esprit.entity.Facture;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
public class PDFUtils {

    public static File generatePdf(Facture facture) throws Exception {
        File pdfFile = File.createTempFile("facture", ".pdf");

        PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Titre
        Paragraph title = new Paragraph("Détail des factures")
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(16);
        document.add(title);

        document.add(new Paragraph("\n"));

        // Table avec colonnes dynamiques
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 3, 2, 2, 2}));
        table.setWidth(UnitValue.createPercentValue(100));
        String nom=facture.getDoctor().getFirstName() + " " + facture.getDoctor().getLastName();
        // En-têtes
        table.addHeaderCell("Référence");
        table.addHeaderCell("Médecin");
        table.addHeaderCell("Date");
        table.addHeaderCell("Montant");
        table.addHeaderCell("Statut");
        table.addCell(facture.getReference());
        table.addCell(facture.getDoctor() != null ? nom : "N/A");
        table.addCell(facture.getDate().toString());
        table.addCell(facture.getMontant() + " TND");
        table.addCell(facture.getStatut());


        document.add(table);
        document.close();

        return pdfFile;
    }
}
