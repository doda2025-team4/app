package frontend.ctrl;

public record ValidationErrorResponse(
    String code,
    String message
) {}
