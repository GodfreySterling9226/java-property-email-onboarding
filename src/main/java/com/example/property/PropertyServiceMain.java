package com.example.property;

public final class PropertyServiceMain {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) throw new IllegalArgumentException("usage: PropertyServiceMain <tenant-email> <property> <due-date>");
        var service = new PropertyNotificationService(new InfraiClient(InfraiConfig.fromEnvironment()));
        System.out.println(service.sendInspectionReminder(args[0], args[1], args[2]));
    }
}
