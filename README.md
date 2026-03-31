# 🛠️ RemixLab Refactoring & Troubleshooting

This repository is a refactored version of the RemixLab backend project.

The goal of this repository is not only to improve the original codebase but also to document real backend development issues encountered during development and deployment. Through this repository, the project focuses on troubleshooting, architectural improvements, and documenting engineering decisions made during the refactoring process.

---

# ⭐️ Purpose

The original RemixLab project was developed as a team backend project and deployed on shared cloud infrastructure.

Because the infrastructure resources such as the cloud instance, database, and deployment pipeline are owned by the team environment, modifying the production environment or restructuring the system after the project ended was not feasible.

To address this limitation, this repository was created as an independent refactoring workspace.

The main purposes of this repository are:

- Independently refactor the original backend codebase
- Analyze real development issues encountered during the project
- Improve architecture and code maintainability
- Document troubleshooting and engineering decisions
- Build a personal backend engineering archive

---

# ⚙️ Tech Stack

### Backend

- Java
- Spring Boot
- Spring Data JPA
- QueryDSL

### Database

- MySQL
- Redis

### Infrastructure

- Docker

### Development Tools

- Gradle ( Build )
- JUnit ( Test )
- Mockito ( Test )

---

# ✅ Refactoring Goals

## 1. Introduce Automated Testing for Core Business Logic

The original project had very limited automated test coverage, which made it difficult to safely modify business logic during development.

This refactoring introduces a structured testing strategy to improve reliability and maintainability of the codebase.

Key improvements include:

- Writing unit tests for service-layer business logic
- Isolating domain logic to make it testable without infrastructure dependencies
- Introducing mock-based testing for repositories and external services
- Verifying edge cases and error handling scenarios
- Preventing regression during refactoring

The goal of this step is **to ensure that internal logic can be safely modified** while maintaining stable functionality.

---

## 2. Performance Improvements for External API Calls

The project integrates with several external AI generation APIs.

In the original implementation, external API calls were handled in a simple synchronous manner without much optimization. This approach can introduce unnecessary latency and inefficient request handling when external services are slow or unstable.

This refactoring focuses on improving the performance and reliability of these external integrations.

Key improvements include:

- Improving external API client structure
- Optimizing HTTP request handling
- Reducing unnecessary blocking operations
- Improving response parsing and error handling
- Introducing timeout and retry strategies
- Improving logging for external service failures

Expected results include reduced latency, **improved stability** when external services fail, and clearer separation between domain logic and external API clients.

---

## 3. Redesign of User Token Storage Strategy

The original implementation stored authentication tokens in a way that tightly coupled token management with the User domain.

This refactoring aims to redesign the token management strategy to improve security, scalability, and maintainability of the authentication system.

Key changes include:

- Redesigning the refresh token storage strategy
- Separating authentication concerns from the core User domain
- Improving refresh token lifecycle management
- Improving token validation and invalidation logic
- Introducing a clearer authentication flow

Possible improvements include **Redis-based token storage** and improved **session management strategies.**

Expected outcomes include a cleaner authentication architecture, improved security handling, and more maintainable authentication logic.


---

## 4. AI Integration Architecture Improvement

The RemixLab project relies heavily on external AI generation services to produce story content, images, and videos.

During the original implementation, interactions with AI services were handled through direct HTTP requests with manually constructed prompts and response parsing logic. While this approach was functional, it introduced several maintainability and reliability challenges.

Some of the main issues identified were:

* AI prompt logic mixed with service-layer business logic
* Manual JSON parsing of AI responses
* Lack of structured output validation
* Difficulty managing and reusing prompt templates
* Tight coupling between domain services and external AI APIs

This refactoring aims to introduce a more structured AI integration architecture by improving how AI requests, prompts, and responses are handled within the backend system.

Key improvements include:

* Introducing a dedicated **AI service layer** for external AI integrations
* Separating prompt generation logic from domain business logic
* Improving response handling through structured DTO mapping
* Designing a clearer AI pipeline for content generation
* Improving maintainability of prompt templates and AI request flows

The content generation pipeline of RemixLab can be summarized as follows:

```
User Prompt
     ↓
Plot Generation
     ↓
Scene Generation
     ↓
Image Generation
     ↓
Video Generation
```

Each step of this pipeline will be redesigned to ensure clearer separation of concerns between domain logic and AI integration logic.

The expected outcomes of this refactoring include:

* Improved maintainability of AI integration code
* Cleaner separation between business logic and AI services
* Reduced complexity in prompt and response handling
* A more scalable architecture for future AI features
* Easier experimentation with different AI models and services

---

# Troubleshooting Archive

One of the main purposes of this repository is to document real development issues encountered during the project.

This includes technical problems related to backend development, infrastructure configuration, and deployment environments.

Examples include:

- API design mistakes
- Authentication and token handling issues
- Unit Test and Integration Test

Each troubleshooting case will document:

- Problem description
- Root cause
- Debugging process
- Solution
- Lessons learned

Instead of storing these records in documentation files, troubleshooting cases are organized through **GitHub Issues** so that the debugging process, discussion, and resolution can be tracked chronologically.

This approach better reflects how issues are managed in real development environments and allows each problem to be documented with its full investigation process.

---

