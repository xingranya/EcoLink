package com.ecolink.server.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressUpsertRequest(
        @NotBlank(message = "收件人不能为空")
        @Size(max = 50, message = "收件人长度不能超过 50")
        String receiverName,
        @NotBlank(message = "联系电话不能为空")
        @Size(max = 20, message = "联系电话长度不能超过 20")
        String receiverPhone,
        @NotBlank(message = "详细地址不能为空")
        @Size(max = 500, message = "详细地址长度不能超过 500")
        String detail,
        Boolean isDefault
) {}
