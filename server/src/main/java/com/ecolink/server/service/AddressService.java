package com.ecolink.server.service;

import com.ecolink.server.domain.Address;
import com.ecolink.server.domain.User;
import com.ecolink.server.dto.address.AddressResponse;
import com.ecolink.server.dto.address.AddressUpsertRequest;
import com.ecolink.server.exception.BizException;
import com.ecolink.server.repository.AddressRepository;
import com.ecolink.server.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final AuthService authService;

    public AddressService(AddressRepository addressRepository, AuthService authService) {
        this.addressRepository = addressRepository;
        this.authService = authService;
    }

    public List<AddressResponse> list() {
        Long userId = SecurityUtils.currentUserId();
        return addressRepository.findByUserIdOrderByIsDefaultDescUpdatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public AddressResponse create(AddressUpsertRequest request) {
        User user = authService.getCurrentUserEntity();
        if (Boolean.TRUE.equals(request.isDefault())) {
            clearDefault(user.getId());
        }
        Address address = new Address();
        address.setUser(user);
        address.setReceiverName(request.receiverName());
        address.setReceiverPhone(request.receiverPhone());
        address.setDetail(request.detail());
        address.setIsDefault(Boolean.TRUE.equals(request.isDefault()));
        addressRepository.save(address);
        return toResponse(address);
    }

    @Transactional
    public AddressResponse update(Long id, AddressUpsertRequest request) {
        Long userId = SecurityUtils.currentUserId();
        Address address = addressRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new BizException(4042, "地址不存在"));
        if (Boolean.TRUE.equals(request.isDefault())) {
            clearDefault(userId);
        }
        address.setReceiverName(request.receiverName());
        address.setReceiverPhone(request.receiverPhone());
        address.setDetail(request.detail());
        address.setIsDefault(Boolean.TRUE.equals(request.isDefault()));
        addressRepository.save(address);
        return toResponse(address);
    }

    @Transactional
    public void delete(Long id) {
        Long userId = SecurityUtils.currentUserId();
        Address address = addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BizException(4042, "地址不存在"));
        addressRepository.delete(Objects.requireNonNull(address));
    }

    public Address findByIdForCurrentUser(Long id) {
        Long userId = SecurityUtils.currentUserId();
        return addressRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new BizException(4042, "地址不存在"));
    }

    private void clearDefault(Long userId) {
        List<Address> list = addressRepository.findByUserIdOrderByIsDefaultDescUpdatedAtDesc(userId);
        for (Address item : list) {
            if (Boolean.TRUE.equals(item.getIsDefault())) {
                item.setIsDefault(false);
                addressRepository.save(item);
            }
        }
    }

    private AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getReceiverName(),
                address.getReceiverPhone(),
                address.getDetail(),
                address.getIsDefault()
        );
    }
}
