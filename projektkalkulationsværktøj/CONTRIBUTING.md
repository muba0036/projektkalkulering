# Contributing Guide

Velkommen til projektet. Denne guide beskriver, hvad et nyt teammedlem skal vide for at bidrage til koden.

## Kom i gang

1. Klon repository: `git clone https://github.com/<dit-team>/projectcalc.git`
2. Installer Java 17, Maven 3.8+, og MySQL 8.0+
3. Start MySQL lokalt (default port 3306, bruger `root`/`root`)
4. Kor `mvn clean install` for at builde og koere tests
5. Start applikationen med `mvn spring-boot:run`

## Branching strategi

Vi anvender Git Flow:

- `main` - Production-ready kode. Deployes automatisk til Azure.
- `develop` - Integration branch for nye features.
- `feature/<navn>` - Nye features. Branched fra `develop`.
- `bugfix/<navn>` - Bugfixes. Branched fra `develop`.
- `hotfix/<navn>` - Akut fix til production. Branched fra `main`.

## Pull request flow

1. Opret feature branch: `git checkout -b feature/min-feature develop`
2. Skriv kode + unit tests
3. Kor `mvn test` lokalt - alle tests skal passere
4. Push branch og opret PR mod `develop`
5. CI pipeline koerer build + tests automatisk
6. Mindst ee teammedlem skal review og approve
7. Merge til `develop` (squash recommended)

## Kode-standarder

- Java 17 syntax
- Brug konstruktor-injection (ikke `@Autowired` pa felter)
- Brug `JdbcTemplate`, IKKE Spring Data JPA
- Service-laget validerer input - kast `IllegalArgumentException` ved ugyldigt input
- Controller-laget haandterer kun HTTP-mapping, ingen forretningslogik
- Pakke-struktur: `controller`, `service`, `repository`, `model`

## Test

- **Unit tests** - placeres i `src/test/java/.../service/`. Brug Mockito til at mocke afhaengigheder.
- **Integration tests** - placeres i `src/test/java/.../repository/`. Bruger H2 in-memory database via `@JdbcTest` og `@ActiveProfiles("test")`.
- Kor lokalt: `mvn test` for alle tests.

## Database aendringer

1. Opdater `src/main/resources/schema.sql`
2. Opdater `src/main/resources/data.sql` om noedvendigt
3. Test lokalt - drop database og lad Spring genoprette den
4. Dokumenter aendringen i PR-beskrivelsen

## Commit besked-format

```
<type>: <kort beskrivelse>

<laenger beskrivelse om noedvendigt>
```

Typer: `feat`, `fix`, `refactor`, `test`, `docs`, `style`, `chore`.

Eksempel: `feat: tilfoej beregning af CO2-aftryk pa delprojekt-niveau`

## Par-programmering

Vi opfordrer til par-programmering, isaer ved komplekse opgaver. Brug VS Code Live Share eller IntelliJ Code With Me.

## Sporgsmal?

Kontakt Scrum Master eller en af de oprindelige bidragydere.
