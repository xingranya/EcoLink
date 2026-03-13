package com.ecolink.server.controller.admin;

import com.ecolink.server.common.ApiResponse;
import com.ecolink.server.domain.Category;
import com.ecolink.server.exception.BizException;
import com.ecolink.server.repository.CategoryRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台分类管理接口
 */
@RestController
@RequestMapping("/api/v1/admin/categories")
public class AdminCategoryController {
    private final CategoryRepository categoryRepository;

    public AdminCategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public ApiResponse<List<Category>> list() {
        return ApiResponse.ok(categoryRepository.findAllByOrderBySortAsc());
    }

    @PostMapping
    public ApiResponse<Category> create(@Valid @RequestBody CategoryReq req) {
        Category cat = new Category();
        cat.setName(req.name());
        cat.setSort(req.sort() != null ? req.sort() : 0);
        cat.setEnabled(req.enabled() != null ? req.enabled() : true);
        return ApiResponse.ok(categoryRepository.save(cat));
    }

    @PutMapping("/{id}")
    public ApiResponse<Category> update(@PathVariable long id, @Valid @RequestBody CategoryReq req) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new BizException(4040, "分类不存在"));
        cat.setName(req.name());
        if (req.sort() != null) cat.setSort(req.sort());
        if (req.enabled() != null) cat.setEnabled(req.enabled());
        return ApiResponse.ok(categoryRepository.save(cat));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable long id) {
        categoryRepository.deleteById(id);
        return ApiResponse.ok(null);
    }

    public record CategoryReq(
            @NotBlank String name,
            Integer sort,
            Boolean enabled
    ) {}
}
