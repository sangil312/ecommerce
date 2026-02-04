package com.dev.infra.pg.dto;

public record ApproveResult(
        Boolean isSuccess,
        ApproveFail fail,
        ApproveSuccess success
) {
}
