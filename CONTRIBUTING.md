# Contributing to CLAVIS

Thank you for your interest in contributing to CLAVIS! This document provides guidelines for contributing.

## How to Contribute

### Reporting Bugs

1. Check if the bug has already been reported in [Issues](https://github.com/ladybugsaga/CLAVIS/issues)
2. Create a new issue using the bug report template
3. Include steps to reproduce, expected behavior, and actual behavior

### Suggesting Features

1. Open a new issue using the feature request template
2. Describe the use case and proposed solution
3. Tag the issue with `enhancement`

### Adding a New MCP Server

See [docs/contributing/adding-new-mcp.md](docs/contributing/adding-new-mcp.md) for the step-by-step guide.

## Development Setup

```bash
git clone https://github.com/ladybugsaga/CLAVIS.git
cd CLAVIS
cp .env.example .env
mvn clean install
```

## Code Standards

- **Java 17** features are welcome
- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- All public APIs must have **Javadoc**
- Minimum **80% test coverage**
- Use **SLF4J** for logging (never `System.out.println`)
- Follow **Conventional Commits** for commit messages

## Pull Request Process

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Write tests first, then implement
4. Ensure all tests pass: `mvn test`
5. Commit with conventional messages: `feat(pubmed): add full-text search`
6. Push and create a Pull Request against `develop`

## Code Review

All PRs require at least one review. Reviewers check:

- Code quality and style
- Test coverage (≥ 80%)
- Documentation completeness
- Error handling
- No hardcoded credentials

## License

By contributing, you agree that your contributions will be licensed under the MIT License.
