package com.ecolink.server.dto.address;

public record AddressResponse(
        Long id,
        String receiverName,
        String receiverPhone,
        String detail,
        Boolean isDefault
) {}
