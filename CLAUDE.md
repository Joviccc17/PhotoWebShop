# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project context

University coursework for Algebra (Java web programming, "PROJEKTNI ZADATAK" by Aleksander Radovan). The assignment is a photo e-commerce web shop. The full spec lives in `Specification` (Croatian) — read it before making architectural decisions, since several requirements are graded and cannot be substituted with idiomatic alternatives.

At time of writing the repo is a Spring Initializr scaffold: only `PhotoStoreApplication`, an empty `contextLoads` test, and empty `static/` and `templates/` directories exist. Treat new work as greenfield against the spec.

## Build / run / test (Windows, PowerShell)

Use the bundled Maven wrapper (`.cmd` variant on Windows):

- Run the app: `.\mvnw.cmd spring-boot:run`
- Build a runnable jar: `.\mvnw.cmd clean package` (output in `target/`)
- Run all tests: `.\mvnw.cmd test`
- Run a single test class: `.\mvnw.cmd test "-Dtest=PhotoStoreApplicationTests"`
- Run a single test method: `.\mvnw.cmd test "-Dtest=PhotoStoreApplicationTests#contextLoads"`
- Skip tests when packaging: `.\mvnw.cmd clean package -DskipTests`

PowerShell-specific: quote `-Dtest=...` arguments (as above) because `#` is parsed by PowerShell.

## Stack

- Spring Boot **4.0.6**, Java **21** (set in `pom.xml`).
- Starters wired in: `spring-boot-starter-webmvc`, `spring-boot-starter-thymeleaf`, `spring-boot-starter-security`, plus `thymeleaf-extras-springsecurity6` for `sec:` attributes in templates.
- `spring-boot-devtools` is on the classpath at runtime (auto-restart on classpath change).
- **Lombok** is enabled via `annotationProcessorPaths` in `maven-compiler-plugin`; the `spring-boot-maven-plugin` excludes Lombok from the repackaged jar. No extra IDE setup needed beyond enabling annotation processing.
- Test starters: `spring-boot-starter-webmvc-test`, `spring-boot-starter-thymeleaf-test`, `spring-boot-starter-security-test`.

Note Spring Boot 4.0.x uses the split starters (`-webmvc`, `-thymeleaf-test`, etc.) rather than the older monolithic `-web`/`-test` starters — if adding dependencies, follow that pattern.

## Graded constraints from the spec

These are non-negotiable requirements; design around them rather than retrofitting later:

- **Two roles**: anonymous/customer (`kupac`) and admin. Anonymous users can browse categories/products and manage a cart; authentication is only required for checkout and order history.
- **Spring MVC + Thymeleaf** are required (no swapping for WebFlux, JSP, or a JS SPA front-end).
- **Spring Security** must drive auth/authz (no custom auth scheme).
- **Servlet filter + listener**: the spec requires you to find at least one appropriate place to apply a security-related filter *and* a listener — these must exist as explicit `Filter`/`HttpSessionListener` (or similar) components, not just rely on Spring Security's built-in filter chain.
- **JWT (access + refresh)** must secure a REST API surface — separate from the MVC pages, which use session auth. Plan for both auth styles coexisting.
- **PayPal sandbox checkout** plus cash-on-delivery (`gotovina – pouzeće`). PayPal integration uses `developer.paypal.com` sandbox accounts.
- **Async** functionality must exist somewhere (e.g. `@Async`, `@EnableAsync`).
- **Login audit log**: admins must see who logged in, when, and from which IP — implement via the listener requirement above (e.g. `AuthenticationSuccessEvent` listener writing to a table).
- **Admin order history view** needs filters by customer and time period.
- **Class size cap: max 200 lines per class.** Split aggressively; do not let controllers/services balloon.
- **SonarQube must report zero errors** — keep code clean (no unused imports, no obvious smells, handle exceptions meaningfully).

## Layout conventions

- Base package: `hr.algebra.photostore` (groupId `hr.algebra`, artifactId `PhotoStore`).
- Thymeleaf templates go under `src/main/resources/templates/`, static assets under `src/main/resources/static/`. Bootstrap is suggested by the spec ("Do your best (hint: Bootstrap)").
- `application.properties` currently only sets `spring.application.name=PhotoStore` — datasource, security, JWT, and PayPal config still need to be added.

## Specification language

The `Specification` file is in Croatian. Key terms when reading it: `korisnik` = user, `kupac` = customer, `košarica` = cart, `kupnja` = purchase, `kategorija` = category, `proizvod` = product, `pouzeće` = cash on delivery, `pregled` = view/listing.