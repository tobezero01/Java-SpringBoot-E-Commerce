package com.eshop.client.dto.paypalDTO;

import java.time.Instant;
import java.util.Date;

public record PaypalCaptureResponse(
        boolean success,      // capture có thành công không
        String status,        // COMPLETED (kỳ vọng), hoặc khác
        String captureId,     // id của lần capture (để đối soát)
        String localOrderNumber,
        Float amount,         // số tiền capture
        String currency,      // đơn vị tiền
        Date capturedAt // thời điểm capture (ISO-8601)
) {}
