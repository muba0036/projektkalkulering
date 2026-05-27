# Projektkalkulationsvaerktoej - Alpha Solutions

Projektkalkulationssystem til Alpha Solutions, der hjaelper med nedbrydning af projekter og beregning af kalkulationer til estimering.

## Funktioner

- Opret og vedligehold projekter, delprojekter og opgaver
- Summering af tidsforbrug pa projekter og delprojekter
- Beregning af arbejdsdage og estimeret afslutningsdato
- CO2-aftryk (ESG-rapportering)
- Bruger-login med roller (ADMIN, PROJECT_MANAGER, USER)

## Softwaremaessige forudsaetninger

- Java 17
- Maven 3.8+
- MySQL 8.0+
- (Til produktion) Azure account

## Anvendte teknologier

- Spring Boot 3.3.4
- Spring JDBC (IKKE JPA)
- MySQL Connector/J 8
- Thymeleaf
- H2 (kun til tests)
- JUnit 5 + Mockito
- GitHub Actions (CI/CD)
- Maven

## Saet op lokalt

```bash
# Klon repository
git clone https://github.com/<dit-team>/projectcalc.git
cd projectcalc

# Opret MySQL database (sker automatisk via createDatabaseIfNotExist)
# Default credentials i application-dev.properties: root/root

# Kor applikationen
mvn spring-boot:run

# Eller med eksplicit profil
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Applikationen koerer pa http://localhost:8080.

Default login: `admin` / `admin123`

## Kor tests

```bash
# Alle tests (unit + integration med H2)
mvn test

# Kun unit tests
mvn test -Dtest='*ServiceTest'

# Kun integration tests
mvn test -Dtest='*IntegrationTest'
```

## Profiler

| Profile | Database | Brug                       |
|---------|----------|----------------------------|
| `dev`   | Lokal MySQL | Udvikling                |
| `prod`  | Azure MySQL | Production deployment    |
| `test`  | H2 in-memory | Automatiserede tests    |

Aktiver med: `-Dspring.profiles.active=prod` eller environment variable `SPRING_PROFILES_ACTIVE=prod`.

## Deployment til Azure

Webapplikationen er deployet til Azure App Service. MySQL-databasen koerer pa Azure Database for MySQL.

Link til koerende applikation: _<indsaet Azure URL her efter deployment>_

CI/CD pipeline i `.github/workflows/ci-cd.yml`:
- **CI:** Automatisk build og test pa alle pull requests
- **CD:** Automatisk deploy til Azure ved push til main

## Mappestruktur

```
src/
  main/
    java/com/alphasolutions/projectcalc/
      controller/   # Spring MVC controllers (Thymeleaf)
      service/      # Forretningslogik
      repository/   # JDBC-baseret data adgang
      model/        # Domaene-modeller
    resources/
      templates/    # Thymeleaf HTML
      static/css/   # Stylesheets
      schema.sql    # Database schema
      data.sql      # Test data
  test/
    java/com/alphasolutions/projectcalc/
      service/      # Unit tests (Mockito)
      repository/   # Integration tests (H2)
```

## Bidrag

Se [CONTRIBUTING.md](CONTRIBUTING.md).
