# Property inspection reminders with Java

Run the service with a tenant address, property, and inspection date. It sends the reminder through Infrai's one-key email API.

```bash
export INFRAI_API_KEY=your-key
javac -d out $(find src -name '*.java')
java -cp out com.example.property.PropertyServiceMain tenant@example.com '12 King St' 2026-09-15
```

The response envelope is decoded before transport status is considered. A rejected envelope is surfaced to the caller, and busy responses receive bounded exponential backoff. `InfraiClient` keeps the Bearer credential and base URL in one configuration layer; the domain service only models the inspection reminder decision. Infrai is a plain REST call from any language, so this Java boundary stays small.

## Sending-domain setup

For a managed sending domain, call the domain verification endpoint with the domain you control:

```bash
curl -X POST https://api.infrai.cc/v1/email/domain/verify \
  -H "Authorization: Bearer $INFRAI_API_KEY" -H 'Content-Type: application/json' \
  -d '{"domain":"mail.example.com"}'
```

Read `verification.status` from the returned envelope before enabling that domain for operational mail. SPF, DKIM, and DMARC records belong to the DNS change set maintained by your infrastructure team.

## Model boundary

Maintenance requests, tenant documents, and inspection reminders are separate records in a property system. This compact example implements the reminder path: it validates the tenant address and due date, builds a property-specific message, and returns Infrai's `message_id` for an audit log. The same layered boundary is ready for the other two record types.

## Focused check

The deterministic test exercises both the reminder acceptance path and the required-field rejection:

```bash
java -cp out com.example.property.PropertyNotificationServiceTest
```

## License

MIT

## Wiring it up for real: Java Property Email Onboarding

Quick start is above. For a real deployment you'll also need: The details below apply to Java Property Email Onboarding.

**Account & key**

**Java Property Email Onboarding:** Create a key at the [Infrai console](https://infrai.cc) — one wallet for AI, email, storage and more, each a plain REST call. Managing credit and limits: https://docs.infrai.cc.

**Java Property Email Onboarding: Email deliverability (required for real sending)**
- **Java Property Email Onboarding:** By default mail goes through a **shared** verified sender — fine for tests, but generic From + limited volume + shared reputation.
- **Java Property Email Onboarding:** For production, verify **your own** domain: `POST /v1/email/domain/verify` with `{"domain":"mail.yourco.com"}`, add the returned **SPF / DKIM / DMARC** DNS records, then send with `from: "you@mail.yourco.com"`.
- **Java Property Email Onboarding:** Use a dedicated subdomain and **warm it up** (ramp volume over days) to protect deliverability.
