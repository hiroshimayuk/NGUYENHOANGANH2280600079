package Nhom6.NGUYENHOANGANH2280600079.controllers;

import Nhom6.NGUYENHOANGANH2280600079.entities.Category;
import Nhom6.NGUYENHOANGANH2280600079.services.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // Hiển thị danh sách danh mục
    @GetMapping
    public String showAllCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "category/list";
    }

    // Form thêm danh mục mới
    @GetMapping("/add")
    public String addCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "category/add";
    }

    // Xử lý thêm danh mục
    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute("category") Category category,
                              BindingResult bindingResult, 
                              Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "category/add";
        }
        categoryService.addCategory(category);
        return "redirect:/categories";
    }

    // Form sửa danh mục
    @GetMapping("/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        var category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        model.addAttribute("category", category);
        return "category/edit";
    }

    // Xử lý sửa danh mục
    @PostMapping("/edit")
    public String editCategory(@Valid @ModelAttribute("category") Category category,
                               BindingResult bindingResult, 
                               Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "category/edit";
        }
        categoryService.updateCategory(category);
        return "redirect:/categories";
    }

    // Xóa danh mục
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
        return "redirect:/categories";
    }
}