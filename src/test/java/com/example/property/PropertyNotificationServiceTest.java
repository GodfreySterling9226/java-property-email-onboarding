package com.example.property;

public final class PropertyNotificationServiceTest {
    public static void main(String[] args) throws Exception {
        var fake = new InfraiClient(new InfraiConfig("test", "http://127.0.0.1")) {
            @Override public String post(String path, String json) { if (!path.equals("/v1/email/send") || !json.contains("Inspection reminder")) throw new AssertionError(); return "{\"ok\":true,\"data\":{\"message_id\":\"m-1\"}}"; }
        };
        String result = new PropertyNotificationService(fake).sendInspectionReminder("tenant@example.com", "12 King St", "2026-09-15");
        if (!result.contains("message_id")) throw new AssertionError("message id missing");
        try { new PropertyNotificationService(fake).sendInspectionReminder("", "12 King St", "2026-09-15"); throw new AssertionError("blank email accepted"); }
        catch (IllegalArgumentException expected) { }
        System.out.println("inspection reminder decision: PASS");
    }
}
