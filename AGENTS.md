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
| `src/main/java/.../schema/stubs/` | Hand-maintained JAX-WS service interfaces and fault types of the SOAP services — update them by hand when a WSDL changes |
| `src/main/java/.../openapi/RestOperationData.java` | Hand-written REST operation container; the REST models of the same package are generated |
| `src/main/resources/openapi/` | OpenAPI spec and code-generation templates |
| `src/main/resources/wsdl/` | WSDL definitions for SOAP services |
| `src/main/resources/schemas/` | XML Schema definitions |
| `src/test/java/net/webpdf/wsclient/` | Unit and integration tests |
| `examples/` | Usage examples |
| `build/` | Maven build output — do not edit |
| `build/generated-sources/` | **Generated** during the build — REST models from the OpenAPI spec and JAXB classes from the XML schemas; never edit |
| `config/testConfig.sample.json` | Sample test configuration — committed, all values disabled by default |
| `config/testConfig.json` | Active test configuration — **gitignored**, never committed |
| `docker/` | Docker Compose setup for integration test infrastructure |
| `.ai-workspace/` | Agent workspace — gitignored except the `.gitkeep` files of its skeleton directories, temporary use only |

---

## Build

Always use the Maven Wrapper. Never call `mvn` directly.

```
# Windows
mvnw.cmd clean install

# Linux / macOS / WSL
./mvnw clean install
```

Integration tests are controlled by `config/testConfig.json` (gitignored, not in this repository) and by the system properties described below, which take precedence. If neither enables them, all integration tests are skipped automatically — no server is required and the build passes cleanly. A configuration file that exists but cannot be read fails the test run instead of silently skipping the integration tests.

Run the full test suite (unit tests only when no config file is present or integration is disabled):
```
# Windows
mvnw.cmd test

# Linux / macOS / WSL
./mvnw test
```

Integration tests require a running webPDF server instance. They are conditionally enabled per test method via `@IntegrationTest`, `@OAuthTest`, `@TLSTest`, `@ProxyTest`, and `@LdapTest` — each reads its enabled state from `config/testConfig.json` or the matching system property.

To run integration tests using the bundled Docker containers (requires Docker Desktop or Docker Engine with the `docker compose` plugin), run the tests below. Missing images are built on the first run; rebuild them after changes to `docker/` or to pick up a newer webPDF base image:
```
# Rebuild the Docker images (pulls the current base images)
docker compose -f docker/docker-compose.yml build --pull

# Windows
mvnw.cmd test -Dwebpdf.test.integration.enabled=true -Dwebpdf.test.useContainer=true

# Linux / macOS / WSL
./mvnw test -Dwebpdf.test.integration.enabled=true -Dwebpdf.test.useContainer=true
```

The Docker setup starts three compose services: `webpdf` (ports 8080/8443), `ldap` (port 10389), and `proxy` (port 8888). TestContainers manages the full lifecycle through the local `docker compose` — no manual `docker compose up` needed when using the Maven command above. TestContainers addresses the services by their compose service names, so the compose file must not set `container_name`. Without `config/testConfig.json`, the local server settings of `config/testConfig.sample.json` apply (`http://localhost:8080/webPDF`, `admin`/`admin`, `user`/`user`).

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

Generated sources are written to `build/generated-sources/` during every Maven build and are not committed. Change their inputs instead:

- `build/generated-sources/openapi/` — REST models (package `net.webpdf.wsclient.openapi`) from `src/main/resources/openapi/openapi.json`, shaped by the templates in `src/main/resources/openapi/templates/` and the generator configuration in `pom.xml`
- `build/generated-sources/xjc-operation/` and `xjc-extraction/` — JAXB classes from `src/main/resources/schemas/` and their `*.xjb` bindings

`openapi.json` and the XML schemas are copies of the webPDF server release the library targets; replace them as a whole, never edit them. The SOAP stubs in `src/main/java/net/webpdf/wsclient/schema/stubs/` are not generated and are maintained by hand.

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
