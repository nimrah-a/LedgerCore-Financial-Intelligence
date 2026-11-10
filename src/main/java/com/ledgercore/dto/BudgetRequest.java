package com.ledgercore.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record BudgetRequest(@NotBlank String category,@NotNull @DecimalMin("0.01") @Digits(integer=12,fraction=2) BigDecimal monthlyLimit) {}
