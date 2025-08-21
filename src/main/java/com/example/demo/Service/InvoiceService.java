package com.example.demo.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.Model.Invoice;

@Service
public class InvoiceService {
	public Invoice generateInvoce(String dealerId,String vehicleId, String custName,BigDecimal price) {
		Invoice invoice= new Invoice();
		invoice.setDealerId(dealerId);
		invoice.setVehicleId(vehicleId);
		invoice.setCustName(custName);
		BigDecimal tax= price.multiply(BigDecimal.valueOf(0.10));
        BigDecimal totalPrice = price.add(tax);
        
        invoice.setPrice(price);
        invoice.setTax(tax);
        invoice.setTotalPrice(totalPrice);
        
        invoice.setInvoiceNumber(UUID.randomUUID().toString());
        invoice.setTimeStamp(LocalTime.now());
        

				return invoice;
		
	}
}
