# Gemini Code Agent

This file provides instructions for Gemini on how to best assist you with this project.

## About This Project

This is a modern Java application built with Maven. The project uses Java 21.

Key technologies:

- Java 21
- Maven
- Spock and Groovy for testing

## How to Help with this Project

### Dependencies

The project's dependencies are managed in the `pom.xml` file. When adding new dependencies, please add them to the `pom.xml` file.

### Testing

Tests are written using the Spock framework and are located in the `src/test/groovy` directory. Test files end with `Spec.groovy`. To run the tests, use the following command:

```bash
mvn test
```

### Code Style

Please follow the existing code style.

### Common Tasks

#### Adding a new feature

1.  Create a new branch for the feature.
2.  Add new classes and methods as needed.
3.  Add unit tests for the new feature.
4.  Run all tests to ensure that nothing is broken.
5.  Open a pull request.

#### Adding a new dependency


1.  Find the dependency on Maven Central.
2.  Add the dependency to the `pom.xml` file.
3.  Reload the Maven project.
