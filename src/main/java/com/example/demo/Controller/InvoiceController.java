package com.example.demo.Controller;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.Invoice;
import com.example.demo.Service.InvoiceService;
import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {
	@Autowired
	private InvoiceService service;
	@PostMapping("/generate")
	public void generateInvoice(@RequestParam String dealerId, 
			@RequestParam String vehicleId, 
			@RequestParam String custName,
			@RequestParam BigDecimal price,
			HttpServletResponse response) throws IOException {
		Invoice invoice = service.generateInvoce(dealerId, vehicleId, custName, price);
		response.setContentType("Application/pdf");
		response.setHeader("Content Disposition ","Attachment Filename=invoice.pdf");
		
		PdfWriter writer= new PdfWriter(response.getOutputStream());
        PdfDocument document = new PdfDocument(writer);
        Document Docs= new Document(document);

        Paragraph title = new Paragraph("VEHICLE SALES INFO ")
                .setBold()
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        Docs.add(title);

        Table headerTable = new Table(2);
        headerTable.setWidthPercent(100);
        headerTable.addCell(new Cell().add(new Paragraph("Invoice NO: "+invoice.getInvoiceNumber()))
                .setBorder(Border.NO_BORDER));
        headerTable.addCell(new Cell().add(new Paragraph("Date: " + invoice.getTimeStamp()))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER));
        Docs.add(headerTable);
        Docs.add(new Paragraph("\n"));

        Table detailsTable = new Table(new float[]{1,2});
        detailsTable.setWidthPercent(100);
        detailsTable.addCell(new Cell().add(new Paragraph("Dealer Id")).setBold());
        detailsTable.addCell(new Cell().add(new Paragraph(invoice.getDealerId())));

        detailsTable.addCell(new Cell().add(new Paragraph("Vehicle  Id")).setBold());
        detailsTable.addCell(new Cell().add(new Paragraph(invoice.getVehicleId())));

        detailsTable.addCell(new Cell().add(new Paragraph("Customer Name")).setBold());
        detailsTable.addCell(new Cell().add(new Paragraph(invoice.getCustName())));
        Docs.add(detailsTable);
        Docs.add(new Paragraph("\n"));

        Table priceTable = new Table(new float[]{2,1});
        priceTable.addCell(new Cell().add(new Paragraph("Price ")).setBold());
        priceTable.addCell(new Cell().add(new Paragraph("$"+invoice.getPrice())));

        priceTable.addCell(new Cell().add(new Paragraph("Tax ")).setBold());
        priceTable.addCell(new Cell().add(new Paragraph("$"+invoice.getTax())));

        priceTable.addCell(new Cell().add(new Paragraph("Total Price")).setBold());
        priceTable.addCell(new Cell().add(new Paragraph("$"+invoice.getTotalPrice())));
        Docs.add(priceTable);
        Docs.add(new Paragraph("\n"));

        BarcodeQRCode qrCode = new BarcodeQRCode(invoice.getInvoiceNumber());
        Image qrImage = new Image(qrCode.createFormXObject(document))
                .setWidth(20)
                .setHeight(20);

        Paragraph qrTitle = new Paragraph("Transaction QR Code")
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20);

        Docs.add(qrTitle);
        Docs.add(qrImage.setAutoScale(true).setHorizontalAlignment(com.itextpdf
                .layout
                .property
                .HorizontalAlignment.CENTER));

        Docs.add(new Paragraph("Thank you for your Purchase Have a great ride")
                .setTextAlignment(TextAlignment.CENTER)
                .setItalic());
        Docs.close();
	}
}
