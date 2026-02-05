package Nhom6.NGUYENHOANGANH2280600079.controllers;

import Nhom6.NGUYENHOANGANH2280600079.entities.Book;
import Nhom6.NGUYENHOANGANH2280600079.daos.Item;
import Nhom6.NGUYENHOANGANH2280600079.services.BookService;
import Nhom6.NGUYENHOANGANH2280600079.services.CartService;
import Nhom6.NGUYENHOANGANH2280600079.services.CategoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final CategoryService categoryService;
    private final CartService cartService;

    // Hàm lưu ảnh vào thư mục "uploads" ở gốc dự án
    private void saveImage(MultipartFile imageFile, Book book) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = imageFile.getOriginalFilename();
                // Sử dụng đường dẫn tương đối "uploads" (ngang hàng với pom.xml)
                Path uploadPath = Paths.get("uploads");
                
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                // Copy file và ghi đè nếu đã tồn tại
                Files.copy(imageFile.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                
                // Lưu tên file vào đối tượng Book
                book.setImage(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping
    public String showAllBooks(@NotNull Model model,
                               @RequestParam(defaultValue = "0") Integer pageNo,
                               @RequestParam(defaultValue = "20") Integer pageSize,
                               @RequestParam(defaultValue = "id") String sortBy) {
        model.addAttribute("books", bookService.getAllBooks(pageNo, pageSize, sortBy));
        model.addAttribute("currentPage", pageNo);
        // Kiểm tra size tránh lỗi chia cho 0
        int totalBooks = bookService.getAllBooks(pageNo, pageSize, sortBy).size();
        model.addAttribute("totalPages", pageSize > 0 ? totalBooks / pageSize : 0);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "book/list";
    }

    @GetMapping("/add")
    public String addBookForm(@NotNull Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "book/add";
    }

    @PostMapping("/add")
    public String addBook(@Valid @ModelAttribute("book") Book book,
                          @NotNull BindingResult bindingResult,
                          @RequestParam("imageFile") MultipartFile imageFile,
                          Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "book/add";
        }
        
        saveImage(imageFile, book); // Lưu ảnh
        bookService.addBook(book);
        return "redirect:/books";
    }

    // ==> ĐÃ SỬA: Logic thêm vào giỏ hàng sử dụng Item mới (có image)
    @PostMapping("/add-to-cart")
    public String addToCart(HttpSession session,
                            @RequestParam long id,
                            @RequestParam String name,
                            @RequestParam double price,
                            @RequestParam(defaultValue = "1") int quantity) {
        var cart = cartService.getCart(session);
        
        // Lấy thông tin sách từ DB để lấy tên file ảnh
        Book book = bookService.getBookById(id).orElse(null);
        String image = (book != null) ? book.getImage() : null;
        
        // Tạo Item với đầy đủ 5 tham số (ID, Name, Price, Quantity, Image)
        Item item = new Item(id, name, price, quantity, image);
        
        cart.addItems(item);
        cartService.updateCart(session, cart);
        return "redirect:/books";
    }

    @GetMapping("/edit/{id}")
    public String editBookForm(@NotNull Model model, @PathVariable long id) {
        var book = bookService.getBookById(id);
        model.addAttribute("book", book.orElseThrow(() -> new IllegalArgumentException("Book not found")));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "book/edit";
    }

    @PostMapping("/edit")
    public String editBook(@Valid @ModelAttribute("book") Book book,
                           @NotNull BindingResult bindingResult,
                           @RequestParam("imageFile") MultipartFile imageFile,
                           Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "book/edit";
        }

        // Logic quan trọng: Chỉ lưu ảnh mới nếu user có chọn file
        saveImage(imageFile, book); 
        
        bookService.updateBook(book);
        return "redirect:/books";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable long id) {
        bookService.getBookById(id)
                .ifPresentOrElse(
                        book -> bookService.deleteBookById(id),
                        () -> { throw new IllegalArgumentException("Book not found"); });
        return "redirect:/books";
    }

    @GetMapping("/search")
    public String searchBook(@NotNull Model model,
                             @RequestParam String keyword,
                             @RequestParam(defaultValue = "0") Integer pageNo,
                             @RequestParam(defaultValue = "20") Integer pageSize,
                             @RequestParam(defaultValue = "id") String sortBy) {
        model.addAttribute("books", bookService.searchBook(keyword));
        model.addAttribute("currentPage", pageNo);
        int totalBooks = bookService.getAllBooks(pageNo, pageSize, sortBy).size();
        model.addAttribute("totalPages", pageSize > 0 ? totalBooks / pageSize : 0);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "book/list";
    }
}