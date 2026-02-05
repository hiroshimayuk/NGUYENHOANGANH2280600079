package Nhom6.NGUYENHOANGANH2280600079.validators;

import Nhom6.NGUYENHOANGANH2280600079.entities.Category;
import Nhom6.NGUYENHOANGANH2280600079.validators.annotations.ValidCategoryId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCategoryIdValidator implements ConstraintValidator<ValidCategoryId, Category> {
    @Override
    public boolean isValid(Category category, ConstraintValidatorContext context) {
        return category != null && category.getId() != null;
    }
}