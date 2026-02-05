package Nhom6.NGUYENHOANGANH2280600079.services;

import Nhom6.NGUYENHOANGANH2280600079.entities.Book;
import Nhom6.NGUYENHOANGANH2280600079.repositories.IBookRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final IBookRepository bookRepository;

    // Hàm getAllBooks cũ của bạn (Bài 4)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Hàm bổ sung cho Bài 5 (Phân trang)
    public List<Book> getAllBooks(Integer pageNo, Integer pageSize, String sortBy) {
        return bookRepository.findAll(PageRequest.of(pageNo, pageSize, Sort.by(sortBy))).getContent();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public void addBook(Book book) {
        bookRepository.save(book);
    }

    // ==> ĐÃ SỬA PHƯƠNG THỨC NÀY <==
    public void updateBook(@NotNull Book book) {
        Book existingBook = bookRepository.findById(book.getId()).orElse(null);
        
        // Kiểm tra tồn tại để tránh lỗi NullPointerException
        if (existingBook != null) {
            existingBook.setTitle(book.getTitle());
            existingBook.setAuthor(book.getAuthor());
            existingBook.setPrice(book.getPrice());
            existingBook.setCategory(book.getCategory());
            
            // Logic quan trọng: Chỉ cập nhật ảnh nếu user có upload ảnh mới (book.getImage() khác null)
            // Nếu không upload, giá trị này null, ta giữ nguyên ảnh cũ (existingBook.image)
            if (book.getImage() != null) {
                existingBook.setImage(book.getImage());
            }
            
            bookRepository.save(existingBook);
        }
    }

    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> searchBook(String keyword) {
        return bookRepository.searchBook(keyword);
    }
}