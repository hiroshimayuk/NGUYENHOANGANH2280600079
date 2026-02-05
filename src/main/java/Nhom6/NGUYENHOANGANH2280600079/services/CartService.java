package Nhom6.NGUYENHOANGANH2280600079.services;

import Nhom6.NGUYENHOANGANH2280600079.daos.Cart;
import Nhom6.NGUYENHOANGANH2280600079.daos.Item;
import Nhom6.NGUYENHOANGANH2280600079.entities.Invoice;
import Nhom6.NGUYENHOANGANH2280600079.entities.ItemInvoice;
import Nhom6.NGUYENHOANGANH2280600079.entities.Book;
import Nhom6.NGUYENHOANGANH2280600079.repositories.IBookRepository;
import Nhom6.NGUYENHOANGANH2280600079.repositories.IInvoiceRepository;
import Nhom6.NGUYENHOANGANH2280600079.repositories.IItemInvoiceRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
public class CartService {
    private static final String CART_SESSION_KEY = "cart";
    private final IInvoiceRepository invoiceRepository;
    private final IItemInvoiceRepository itemInvoiceRepository;
    private final IBookRepository bookRepository;

    public Cart getCart(@NotNull HttpSession session) {
        return Optional.ofNullable((Cart) session.getAttribute(CART_SESSION_KEY))
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    session.setAttribute(CART_SESSION_KEY, cart);
                    return cart;
                });
    }

    public void updateCart(@NotNull HttpSession session, Cart cart) {
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void removeCart(@NotNull HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }

    public int getSumQuantity(@NotNull HttpSession session) {
        return getCart(session).getCartItems().stream().mapToInt(Item::getQuantity).sum();
    }

    public double getSumPrice(@NotNull HttpSession session) {
        return getCart(session).getCartItems().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }

    public void saveCart(@NotNull HttpSession session) {
        var cart = getCart(session);
        if (cart.getCartItems().isEmpty()) return;

        var invoice = new Invoice();
        invoice.setInvoiceDate(new Date());
        invoice.setPrice(getSumPrice(session));
        
        Invoice savedInvoice = invoiceRepository.save(invoice);

        cart.getCartItems().forEach(item -> {
            var itemInvoice = new ItemInvoice();
            itemInvoice.setInvoice(savedInvoice);
            itemInvoice.setQuantity(item.getQuantity());
            itemInvoice.setBook(bookRepository.findById(item.getBookId()).orElseThrow());
            itemInvoiceRepository.save(itemInvoice);
        });

        removeCart(session);
    }
}