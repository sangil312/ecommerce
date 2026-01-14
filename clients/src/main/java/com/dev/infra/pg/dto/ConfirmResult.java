package com.dev.infra.pg.dto;

public record ConfirmResult(
        Boolean isSuccess,
        ConfirmFail fail,
        ConfirmSuccess success
) {
}
