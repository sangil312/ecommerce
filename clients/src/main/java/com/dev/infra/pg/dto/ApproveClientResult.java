package com.dev.infra.pg.dto;

public record ApproveClientResult(
        Boolean isSuccess,
        ApproveFail fail,
        ApproveSuccess success
) {
}
