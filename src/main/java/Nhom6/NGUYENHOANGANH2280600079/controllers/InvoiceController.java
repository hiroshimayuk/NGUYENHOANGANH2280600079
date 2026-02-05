package Nhom6.NGUYENHOANGANH2280600079.controllers;

import Nhom6.NGUYENHOANGANH2280600079.entities.Invoice;
import Nhom6.NGUYENHOANGANH2280600079.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    // Hiển thị danh sách hóa đơn
    @GetMapping
    public String listInvoices(Model model) {
        model.addAttribute("invoices", invoiceService.getAllInvoices());
        return "invoice/list";
    }

    // Xem chi tiết hóa đơn
    @GetMapping("/{id}")
    public String viewInvoiceDetail(@PathVariable Long id, Model model) {
        Invoice invoice = invoiceService.getInvoiceById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid invoice Id:" + id));
        model.addAttribute("invoice", invoice);
        return "invoice/detail";
    }
}