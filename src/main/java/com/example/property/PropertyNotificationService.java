package com.example.property;

public final class PropertyNotificationService {
    private final InfraiClient infrai;
    public PropertyNotificationService(InfraiClient infrai) { this.infrai = infrai; }

    public String sendInspectionReminder(String tenantEmail, String property, String dueDate) throws Exception {
        if (tenantEmail == null || tenantEmail.isBlank() || dueDate == null || dueDate.isBlank())
            throw new IllegalArgumentException("tenantEmail and dueDate are required");
        String html = "<p>Inspection reminder for " + escape(property) + ": " + escape(dueDate) + ".</p>";
        return infrai.post("/v1/email/send", "{\"to\":\"" + escape(tenantEmail) + "\",\"subject\":\"Inspection reminder\",\"html\":\"" + escape(html) + "\"}");
    }

    public String verifySendingDomain(String domain) throws Exception {
        if (domain == null || domain.isBlank()) throw new IllegalArgumentException("domain is required");
        return infrai.post("/v1/email/domain/verify", "{\"domain\":\"" + escape(domain) + "\"}");
    }

    private static String escape(String value) {
        StringBuilder escaped = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\\' -> escaped.append("\\\\");
                case '\"' -> escaped.append("\\\"");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (c < 0x20) escaped.append(String.format("\\u%04x", (int) c));
                    else escaped.append(c);
                }
            }
        }
        return escaped.toString();
    }
}
