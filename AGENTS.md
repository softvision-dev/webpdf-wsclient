# webpdf-wsclient — Agent Instructions

webpdf-wsclient is a Java client library (Apache 2.0) that provides a simplified, object-oriented interface to the webPDF server REST and SOAP APIs. It is published to Maven Central as `net.webpdf:webpdf-wsclient`. The library version tracks the webPDF server version it targets (e.g., 10.0.5 → webPDF Server 10.0.5).

- **Repository**: https://github.com/softvision-dev/webpdf-wsclient
- **Documentation**: https://github.com/softvision-dev/webpdf-wsclient/wiki
- **License**: Apache 2.0
- **Java**: 11+

---

## Repository Layout

| Path | Purpose |
|---|---|
| `src/main/java/net/webpdf/wsclient/` | Hand-written library source |
| `src/main/java/.../schema/stubs/` | **Generated** — JAXB from WSDL/XSD — do not edit |
| `src/main/java/.../openapi/` | **Generated** — OpenAPI generator — do not edit |
| `src/main/resources/openapi/` | OpenAPI spec and code-generation templates |
| `src/main/resources/wsdl/` | WSDL definitions for SOAP services |
| `src/main/resources/schemas/` | XML Schema definitions |
| `src/test/java/net/webpdf/wsclient/` | Unit and integration tests |
| `examples/` | Usage examples |
| `build/` | Maven build output — do not edit |
| `config/testConfig.sample.json` | Sample test configuration — committed, all values disabled by default |
| `config/testConfig.json` | Active test configuration — **gitignored**, never committed |
| `docker/` | Docker Compose setup for integration test infrastructure |
| `.ai-workspace/` | Agent workspace — gitignored, temporary use only |

---

## Build

Always use the Maven Wrapper. Never call `mvn` directly.

```
# Windows
mvnw.cmd clean install

# Linux / macOS / WSL
./mvnw clean install
```

Integration tests are controlled exclusively by `config/testConfig.json` (gitignored, not in this repository). If that file does not exist or has `integrationTests.enabled` set to `false`, all integration tests are skipped automatically — no server is required and the build passes cleanly.

Run the full test suite (unit tests only when no config file is present or integration is disabled):
```
# Windows
mvnw.cmd test

# Linux / macOS / WSL
./mvnw test
```

Integration tests require a running webPDF server instance. They are conditionally enabled per test method via `@IntegrationTest`, `@OAuthTest`, `@TLSTest`, `@ProxyTest`, and `@LdapTest` — each reads its enabled state from `config/testConfig.json`.

To run integration tests using the bundled Docker containers (requires Docker Desktop or Docker Engine), build the images once and then run the tests:
```
# Build Docker images (only required once, or after changes to docker/)
docker compose -f docker/docker-compose.yml build

# Windows
mvnw.cmd test -Dwebpdf.test.integration.enabled=true -Dwebpdf.test.useContainer=true

# Linux / macOS / WSL
./mvnw test -Dwebpdf.test.integration.enabled=true -Dwebpdf.test.useContainer=true
```

The Docker setup starts three containers: **webPDF** (ports 8080/8443), **LDAP** (port 10389), and **Proxy** (port 8888). TestContainers manages the full lifecycle — no manual `docker compose up` needed when using the Maven command above.

The system properties `-Dwebpdf.test.integration.enabled` and `-Dwebpdf.test.useContainer` take precedence over `config/testConfig.json`. A sample config documenting all available options is provided at `config/testConfig.sample.json`.

---

## Architecture

The library shares a common abstraction layer across two protocol implementations:

**Session layer** (`session/`)
- `SessionFactory` creates `Session` instances (REST or SOAP).
- A session manages authentication, HTTP/HTTPS connection, TLS, and proxy settings.
- Authentication providers: `AnonymousAuthProvider`, `UserAuthProvider`, `OAuth2Provider`.

**Web service layer** (`webservice/`)
- `WebServiceFactory` creates `WebService` instances from a session.
- Available services: Barcode, Converter, OCR, Pdfa, Signature, Toolbox, UrlConverter.
- Each service maps to a REST (`webservice/rest/`) and a SOAP (`webservice/soap/`) implementation.

**Protocol implementations**
- REST: Apache HttpComponents 5, JSON serialization via Jackson.
- SOAP: JAX-WS, JAXB-generated stubs from WSDL/XSD.

**Error model**: `ResultException` (and subtypes `ClientResultException`, `ServerResultException`, `AuthResultException`) is the library's unified error type. Do not swallow or replace it with unchecked alternatives.

---

## Code Standards

- Java 11 source compatibility — do not use language features beyond Java 11.
- Use JetBrains `@NotNull` / `@Nullable` annotations on all public API members.
- Prefer `@NotNull` contracts and early precondition checks over defensive null guards deep in internal code.
- All files and string literals use UTF-8. Do not use ASCII substitutes for non-ASCII characters (e.g., German umlauts) unless a protocol or format explicitly requires it.
- Follow existing naming conventions: interfaces without `I`-prefix, no Hungarian notation.
- Do not introduce new unchecked exception types. Wrap lower-level exceptions in the appropriate `ResultException` subtype.

---

## Documentation

- All public types, constructors, methods, and non-obvious fields carry Javadoc.
- Comments explain **why** — the intent, constraint, or invariant that would not be obvious from reading the code. Do not restate what the code does.
- Keep documentation aligned with implementation. Update Javadoc when behavior changes.

---

## Testing

- **Before changing any code**: verify that the affected code paths are covered by
  existing JUnit tests and that those tests pass. If coverage is missing, write the
  tests first, confirm they pass, then make the code change.
- **After every code change**: re-run the relevant tests to confirm no regressions
  were introduced. A change is not complete until the tests are green.
- Reuse existing test helpers, fixtures, and base classes before creating new ones.
- When a behavioral change is correct and test output changes as a result, update
  reference artifacts — do not weaken assertions to make tests pass.
- Do not modify files in `config/` — these are environment-specific and gitignored.
- Do not commit credentials, server addresses, or environment-specific values into
  test resources.

---

## Generated Code

The following directories contain generated sources and are rebuilt automatically during the Maven build. Do not edit them manually — changes will be overwritten.

- `src/main/java/net/webpdf/wsclient/schema/stubs/` — JAXB classes from WSDL/XSD
- `src/main/java/net/webpdf/wsclient/openapi/` — REST client from OpenAPI spec

To regenerate: `mvnw.cmd generate-sources` (or `./mvnw generate-sources`).

---

## Agent Workspace

Redirect high-volume output (logs, intermediate files, analysis dumps) to `.ai-workspace/temp/` rather than the repository root or source directories. Use collision-safe filenames that include a timestamp or run identifier. This directory is gitignored.

---

## Commits

- Subject line: ≤72 characters, imperative mood, no trailing period.
- Body: optional — explain *why*, not *what* (the diff shows what). Keep it concise.
- Do not include `Co-Authored-By` trailers.
- Do not name specific vulnerabilities or internal bug details in public commit messages; describe the fix category instead (e.g. "harden error handling" rather than "prevent NPE when getFaultInfo() returns null").

---

## Releases

The library is published to Maven Central. Version numbers are kept in sync with the webPDF server release they target. Do not modify version fields in `pom.xml` manually — version management is part of the release process.
