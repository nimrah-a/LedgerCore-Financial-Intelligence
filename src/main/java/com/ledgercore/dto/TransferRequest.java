package com.ledgercore.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record TransferRequest(@NotNull Long fromAccountId,@NotNull Long toAccountId,@NotNull @DecimalMin("0.01") @Digits(integer=12,fraction=2) BigDecimal amount) {}
