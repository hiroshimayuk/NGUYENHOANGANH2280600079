package Nhom6.NGUYENHOANGANH2280600079.services;

import Nhom6.NGUYENHOANGANH2280600079.entities.Invoice;
import Nhom6.NGUYENHOANGANH2280600079.repositories.IInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {
    private final IInvoiceRepository invoiceRepository;

    // Lấy tất cả hóa đơn (cho Admin)
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    // Lấy chi tiết hóa đơn theo ID
    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }
}