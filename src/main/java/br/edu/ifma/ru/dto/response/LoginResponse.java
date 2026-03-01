package br.edu.ifma.ru.dto.response;

public record LoginResponse(
    String accessToken,
    AdminResponse admin
) {}
