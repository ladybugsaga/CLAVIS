# Code Style Guide

## General Principles
- Follow **SOLID** principles
- Keep methods short (<30 lines)
- Classes should have a single responsibility
- Prefer composition over inheritance

## Java Conventions
- **Java 17+** features allowed (records, sealed classes, switch expressions)
- Use `final` for fields that shouldn't change
- Use `Objects.requireNonNull()` for parameter validation
- Prefer `List.of()` / `List.copyOf()` for immutable collections

## Naming
| Element | Convention | Example |
|---------|-----------|---------|
| Classes | PascalCase | `PubMedClient` |
| Methods | camelCase | `searchPapers()` |
| Constants | SCREAMING_SNAKE | `MAX_RESULTS` |
| Packages | lowercase | `io.clavis.pubmed` |
| Test classes | ClassNameTest | `PubMedClientTest` |

## Documentation
- All public classes and methods must have Javadoc
- Include `@param`, `@return`, `@throws` tags
- Add `@author`, `@version`, `@since` to classes

## Formatting
- **Indentation**: 4 spaces (no tabs)
- **Line length**: 120 characters max
- **Braces**: K&R style (opening brace on same line)
- **Imports**: No wildcards, sorted alphabetically

## Testing
- Use JUnit 5 with `@DisplayName` annotations
- One assertion per test method (when practical)
- Use Mockito for external dependencies
- Naming: `testMethodName_condition_expectedResult`

## Error Handling
- Use `ClavisException` hierarchy for checked exceptions
- Use `ValidationException` for input validation (unchecked)
- Never swallow exceptions silently
- Include the cause when wrapping exceptions

## Logging
- Use SLF4J via `StructuredLogger` or standard `LoggerFactory`
- **TRACE**: Fine-grained debugging
- **DEBUG**: Diagnostic information
- **INFO**: API requests and server lifecycle events
- **WARN**: Recoverable issues (rate limits, retries)
- **ERROR**: Unrecoverable failures
