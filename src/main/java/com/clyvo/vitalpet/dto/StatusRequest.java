package com.clyvo.vitalpet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StatusRequest(
        @NotBlank @Size(max = 30) String status
) { }
