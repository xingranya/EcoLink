package com.ecolink.server.controller;

import com.ecolink.server.common.ApiResponse;
import com.ecolink.server.dto.address.AddressResponse;
import com.ecolink.server.dto.address.AddressUpsertRequest;
import com.ecolink.server.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ApiResponse<List<AddressResponse>> list() {
        return ApiResponse.ok(addressService.list());
    }

    @PostMapping
    public ApiResponse<AddressResponse> create(@Valid @RequestBody AddressUpsertRequest request) {
        return ApiResponse.ok(addressService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> update(@PathVariable Long id, @Valid @RequestBody AddressUpsertRequest request) {
        return ApiResponse.ok(addressService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return ApiResponse.okMessage("删除地址成功");
    }
}
