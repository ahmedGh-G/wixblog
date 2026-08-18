### 📝 WixBlog - Dynamic Web Publishing & Content Management Ecosystem

WixBlog is an enterprise-grade full-stack web application designed as a robust showcase of modern software engineering principles, decoupled multi-tier architecture, and reactive state management. The ecosystem provides an immersive digital workspace where authors can compose rich-text content using a block-based document model, while readers explore dynamically updated feeds, structured taxonomy systems, and interactive engagement mechanics. 

The primary objective of this repository is to demonstrate foundational software competencies including type-safe client-server coordination, stateless security controls, and clean code optimization within an industry-standard monolithic backend paired with an atomic, component-driven client architecture. 

### 🏗️ Architectural Matrix & System Topology

WixBlog utilizes a structured **Monorepo Pattern** to house both the user interface client and the server API runtime within a unified repository layout. This configuration ensures consistent type contracts while establishing strict operational boundaries between system layers. 

### Client Layer (UI Project)

Built on **Angular Standalone Architecture**, the frontend follows a strict **Domain-Driven Design (DDD)** folder model divided into Core, Feature, and Shared domains. State interactions are optimized via reactive primitive wrappers (Angular Signals) to avoid unnecessary change detection loops, and the application leverages lazy-loaded paths to guarantee fast Time-to-Interactive (TTI) scores. 

### Server Layer (Backend Project)

Built on **Spring Boot**, the backend uses a traditional **Monolithic N-Tier / Layered Architecture**. Technical concerns are cleanly separated into isolated horizontal boundaries: 

* **Presentation Layer**: Responsible for mapping incoming REST HTTP vectors and enforcing Data Transfer Object (DTO) validation.
* **Business Logic Layer**: Responsible for processing domain operations, executing programmatic validations, and coordinating service boundaries.
* **Data Access Layer**: Abstracted data persistence using repository patterns.

### 📁 Repository Directory Structure

text

wixblog/
├── frontend/                     # Angular 19+ Client Project Application
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/             # Centralized System Singletons
│   │   │   │   ├── auth/         # JWT parsing, storage, and global session Signals
│   │   │   │   ├── guards/       # Route activation security and entry middleware
│   │   │   │   ├── interceptors/ # Centralized HTTP request/response pipeline modifiers
│   │   │   │   └── models/       # Shared TypeScript model contracts
│   │   │   │
│   │   │   ├── feature/          # Domain-Specific Product Views (Lazy Loaded)
│   │   │   │   ├── onboarding/   # Multi-step welcome and category selection wizards
│   │   │   │   ├── auth/         # Self-contained Login, Signup, and Reset portals
│   │   │   │   ├── home/         # Tabbed content feeds (For You, Featured) and side panels
│   │   │   │   ├── write/        # Creative authoring rich-text canvas interface
│   │   │   │   ├── stories/      # Article layouts and cascading comment components
│   │   │   │   ├── explore-topics/ # Discover grids, Tag pathways, and Topic routers
│   │   │   │   ├── user/         # Public profiles, saved Bookmarks, and account configs
│   │   │   │   └── about/        # Static informational views and system landing pages
│   │   │   │
│   │   │   └── shared/           # Presentational Components & Pure Pipes
│   │   │       ├── loading/      # Loading shimmers, skeletons, and UX spinners
│   │   │       ├── navbar/       # Master top layout navigation utility
│   │   │       ├── footer/       # System disclosure and links bottom footer
│   │   │       ├── 404/          # Fallback structural route error destination
│   │   │       ├── sidebar/      # Collapsible responsive overlay layouts
│   │   │       ├── user/         # Reusable Author, Profile, and Follow layouts
│   │   │       ├── pipes/        # UI transformation elements (MinReadPipe, TimeAgoPipe)
│   │   │       └── interactive-story-bar/ # Reactive clapping and collection toggles
│   │   └── assets/               # Static icons, image assets, and fallback JSON mocks
│   ├── tailwind.config.js        # Utility-first style design tokens
│   └── angular.json              # Client workspace infrastructure manifest
│
├── backend/                      # Spring Boot Layered N-Tier Monolithic Server
│   ├── src/main/java/com/wixblog/
│   │   ├── controller/           # Presentation Tier (REST Controllers & Request mapping)
│   │   ├── service/              # Core Domain Logic Tier (Transactional Contexts & Validation)
│   │   ├── repository/           # Persistence Tier (Spring Data JPA abstraction contracts)
│   │   ├── model/                # Entity Tier (Database schemas & relation configurations)
│   │   ├── security/             # Security Context (JWT configuration filters & encoders)
│   │   └── config/               # Global configuration beans (CORS, Object Mapping)
│   ├── src/main/resources/
│   │   └── application.properties # Server environments, database connections, and pooling profiles
│   ├── pom.xml                   # Maven build automation configuration
│   └── mvnw                      # Script wrapper for cross-platform Maven operations
│
├── .gitignore                    # Universal runtime exclusion configuration
└── README.md                     # Technical project blueprint file (This file)

Use code with caution.

### 🛠️ Technology Stack & Core System Components

### Frontend Engine (UI Project)

* **Framework**: Angular 19+ (Signals State Management, Functional Routers, Standalone Components)
* **Styling**: Tailwind CSS & Structured SCSS Pre-processors
* **Rich Text Engine**: Editor.js (Generates and parses safe, clean JSON content block outputs)
* **Rendering**: Angular SSR (Server-Side Rendering) configured for dynamic runtime OpenGraph metadata injection

### Backend Engine (Server Project)

* **Framework**: Spring Boot 3+ (Java 21 Enterprise Environment)
* **Security Configuration**: Spring Boot Security (Stateless filter chains processing JWT authorizations)
* **Data Tier**: Spring Data JPA / Hibernate ORM
* **Database Management**: Relational Database Engine (PostgreSQL / MySQL) map-optimized via strict database constraint variables

### 🚀 Installation & Local Environment Setup

### Prerequisites

To compile and instantiate both frontend and backend projects locally, prepare your operating environment with: 

* **Node.js**: v20.x or higher
* **Angular CLI**: v19.x or higher
* **Java Development Kit (JDK)**: v21 Standard Edition
* **Database Target**: An active instance of PostgreSQL or MySQL

### Launch Instructions

1. **Clone the Architecture Source** 

bash

git clone https://github.com/YOUR_USERNAME/wixblog.git
cd wixblog

Use code with caution.
2. **Initialize the Frontend Client** 

bash

cd frontend
npm install
ng serve

Use code with caution.

The user interface mounts locally and serves live updates at http://localhost:4200.
3. **Initialize the Backend Server** 

  * Navigate to backend/src/main/resources/application.properties.
  * Configure your active database connection credentials and driver parameters.

bash

cd ../backend
./mvnw spring-boot:run

Use code with caution.

The REST API server maps endpoints onto port http://localhost:8080.

### 🌿 Git & Deployment Lifecycle (GitFlow Specification)

This project manages code integrations via an **Agile iteration framework** guided by a structural **GitFlow branching pattern** to preserve branch stability and facilitate peer technical reviews. 

### Branch Ecosystem Matrix

* main: Holds official compilation states ready for deployment. Protected against direct commits.
* develop: The target integration branch for technical contributions. All features merge here first.
* feature/*: Feature sandbox environments allocated to isolate distinct project user stories.
* release/*: Isolation streams created to finalize dependency audits and deployment configs before entering production.
* hotfix/*: Elite path branches spun off main to repair high-priority anomalies noticed live.

### Lifecycle Integration Cycle

1. **Synchronize and Allocate Isolation Branch**
Align your local engine with the staging context before isolating features: 

bash

git checkout develop
git pull origin develop
git checkout -b feature/implement-story-cards

Use code with caution.
2. **Semantic Commit Structure**
Record structural iterations cleanly using declarative scope variables: 

bash

git commit -m "feat(ui): design scalable vertical and horizontal story cards under shared module"

Use code with caution.
3. **GitHub Code Integration Review**
Deploy branches to the cloud tracking infrastructure: 

bash

git push origin feature/implement-story-cards

Use code with caution.

  * Open your project dashboard interface inside GitHub.
  * Issue a new **Pull Request (PR)** targeting the develop baseline tracking branch.
  * Once architectural code checks pass and structural builds validate, the feature merges smoothly to develop.