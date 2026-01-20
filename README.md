# Travel Plans

This project is a Java web application built with Servlets, JSP, Maven and Tomcat. It allows users to create, list, group and update travel plans.

The application uses:
* Java 8
* Maven
* Apache Tomcat 9
* In-memory repositories (no database required)

## Features

* List all travel plans
* Create a new travel plan
* Update an existing travel plan
* Group compatible plans by type, origin and destination
* Automatic city creation and reuse (in-memory)
* Server-side validation with detailed error messages

## Project Structure (simplified)

```text
src/
 └── main/
     ├── java/
     │   └── com.ginamarieges.travelplans
     │       ├── domain
     │       ├── repository
     │       ├── service
     │       └── web
     └── webapp/
         └── WEB-INF/
             └── jsp/
```

## Requirements

* Java 8
* Maven 3.8+
* Apache Tomcat 9

Check versions:

```sh
java -version
mvn -version
```

## Build the project

From the project root:

```sh
mvn clean package
```

This will generate:

```
target/travel-plans.war
```

## Deploy to Tomcat

### 1. Stop Tomcat (if running)

```sh
brew services stop tomcat@9
```

Or manually:

```sh
/opt/homebrew/opt/tomcat@9/bin/shutdown.sh
```

### 2. Deploy the WAR

Copy the generated WAR to Tomcat:

```sh
cp target/travel-plans.war /opt/homebrew/opt/tomcat@9/libexec/webapps/
```

(Optional) Clean previous exploded app:

```sh
rm -rf /opt/homebrew/opt/tomcat@9/libexec/webapps/travel-plans
```

### 3. Start Tomcat

```sh
brew services start tomcat@9
```

Or manually:

```sh
/opt/homebrew/opt/tomcat@9/bin/startup.sh
```

## Access the application

Open your browser:

```
http://localhost:8080/travel-plans/plans
```

## Available Routes

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/plans` | List all plans |
| GET | `/plans?grouping=true` | Group compatible plans |
| GET | `/plans/new` | Create plan form |
| POST | `/plans` | Create plan |
| GET | `/plans/edit?id={id}` | Edit plan form |
| POST | `/plans/edit` | Update plan |

## Notes

* The application uses only annotations (`@WebServlet`), no servlet mappings in `web.xml`.
* All data is stored in memory, restarting Tomcat will reset the data.
* Cities are automatically resolved:
   * If a city already exists → reused
   * If not → created with a generated ID

## Author

Gina Marieges  
Technical Test – January 2026
