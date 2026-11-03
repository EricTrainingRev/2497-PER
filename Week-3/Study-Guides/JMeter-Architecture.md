# JMeter Architecture

## Table of Contents

* [1. High-Level Overview](#1-high-level-overview)
* [2. The Test Plan](#2-the-test-plan)
* [3. Thread Groups](#3-thread-groups)
* [4. Samplers vs Controllers](#4-samplers-vs-controllers)
* [5. Component Hierarchy](#5-component-hierarchy)

***

## 1. High-Level Overview

**JMeter** is an open-source performance testing tool that simulates virtual users against a system under test. Its power and flexibility come from its **tree-based architecture** — a Test Plan composed of nested elements (Thread Groups, Samplers, Controllers, Config Elements, Listeners) that together define what to test, how to test it, and how results are collected.

This module covers the top-level **Test Plan**, the **Thread Group** that models virtual users, the crucial distinction between **samplers and controllers**, and the overall **component hierarchy** that governs scope, inheritance, and execution order.

[↑ Back to Table of Contents](#table-of-contents)

***

*Everything in JMeter lives inside one container: the Test Plan.*

## 2. The Test Plan

A **Test Plan** in JMeter is the blueprint for your performance test. It defines what to test, how to test it, and the sequence of operations, organizing every element required to simulate user behavior and measure system performance.

### Key Concepts

*   **Container for Test Elements:** The Test Plan is the top-level container that holds Thread Groups, Samplers, Controllers, Listeners, and Config Elements.
*   **Execution Flow:** The structure and order of elements determine the execution flow of the test.
*   **Parameterization:** Test Plans can include variables and configuration elements to make tests reusable and flexible.
*   **Reusability:** Test Plans can be saved and reused (as `.jmx` files) for different scenarios or environments.

### Typical Workflow

1.  **Create a New Test Plan** in JMeter.
2.  **Add Thread Groups** — define user groups and concurrency settings.
3.  **Add Samplers** — specify the requests (HTTP, JDBC, etc.) to send.
4.  **Add Controllers** — manage request flow and branching.
5.  **Add Config Elements** — set up defaults, variables, and data sources.
6.  **Add Listeners** — configure result collection and reporting.
7.  **Parameterize and Correlate** — use variables and extractors for dynamic data.
8.  **Save and Run** — save the Test Plan and execute.

### Best Practices

*   **Organize Elements Logically** — group related requests and controllers for clarity.
*   **Use Comments** — describe the purpose of each section or element.
*   **Parameterize Data** — use variables and config elements instead of hardcoded values.
*   **Modularize** — use Test Fragments and Module Controllers for reusable components.
*   **Validate Before Scaling** — start small to verify correctness before large runs.

### Example Structure

```
Test Plan
├── Thread Group (e.g., 50 users)
│   ├── HTTP Request (Login)
│   ├── HTTP Request (Browse)
│   ├── HTTP Request (Purchase)
│   └── Listener (View Results Tree)
├── User Defined Variables
└── CSV Data Set Config (for parameterization)
```

**Example:** A Test Plan simulates 50 users logging in and purchasing. It includes a Thread Group for user simulation, HTTP Samplers for each action, a CSV Data Set Config for credentials, and Listeners for result analysis. Variables hold session tokens and extractors handle dynamic values, keeping the test realistic and maintainable.

[↑ Back to Table of Contents](#table-of-contents)

***

*A Test Plan is empty until we add virtual users — that is the job of the Thread Group.*

## 3. Thread Groups

A **Thread Group** represents a group of virtual users executing a set of test steps. It defines the number of users, the **ramp-up period**, and the **loop count**, controlling the load applied to the system under test.

| Setting | Purpose |
| :--- | :--- |
| **Number of Threads** | The number of virtual users (one thread = one user). |
| **Ramp-Up Period** | How long JMeter takes to start all threads, enabling gradual load increase. |
| **Loop Count** | How many times each thread executes the test steps. |

### Key Concepts

*   **User Simulation:** Each thread simulates a single user performing the defined actions.
*   **Concurrency Control:** The number of threads determines the level of concurrency.
*   **Lifecycle Management:** Thread Groups manage the start and end of user sessions.

### Typical Workflow

1.  **Add a Thread Group** to your Test Plan.
2.  **Configure Threads** — set the number of users, ramp-up period, and loop count.
3.  **Add Samplers and Controllers** — define the actions each user performs.
4.  **Parameterize Data** — use variables or data sources for user-specific data.
5.  **Monitor Execution** — use Listeners to observe thread activity and results.

### Best Practices

*   **Start Small** — validate test logic with a low thread count before scaling up.
*   **Use a Gradual Ramp-Up** — avoid sudden spikes that don't reflect real-world usage.
*   **Match Realistic User Behavior** — mirror expected think times and pacing.
*   **Use Multiple Thread Groups** — separate thread groups for distinct user roles or workflows.
*   **Monitor Resources** — watch JMeter and system resource usage to avoid the test machine becoming a bottleneck.

> [!TIP]
> On a single load generator, thousands of threads share the same machine's CPU, memory, and network. A **gradual ramp-up** both models reality better and avoids a startup spike that can distort your first few results.

### Example Structure

```
Test Plan
├── Thread Group (e.g., 100 users, 60s ramp-up, 5 loops)
│   ├── HTTP Request (Login)
│   ├── HTTP Request (Browse)
│   └── HTTP Request (Purchase)
└── Listener (Summary Report)
```

**Example:** A Thread Group is configured with 100 threads, a 60-second ramp-up, and 5 loops. Each thread logs in, browses, and purchases. The ramp-up adds users gradually, and the loop count lets each user repeat the scenario, providing a realistic load profile.

[↑ Back to Table of Contents](#table-of-contents)

***

*Within a Thread Group, elements play two distinct roles — samplers do, controllers decide.*

## 4. Samplers vs Controllers

**Samplers** and **Controllers** look similar but serve opposite purposes: samplers *perform* an action (send a request), while controllers *route* the flow of those actions.

| Aspect | Samplers | Controllers |
| :--- | :--- | :--- |
| **Role** | Perform a concrete action (e.g., send a request) | Control order and logic of execution |
| **Protocols** | HTTP, FTP, JDBC, JMS, TCP, etc. | N/A (logical, not protocol-specific) |
| **Types** | HTTP Request, JDBC Request, Debug Sampler | If, Loop, While, Transaction, Throughput |
| **Direct Child** | Thread Group or Controller | Thread Group or another Controller |

### Samplers

A **Sampler** sends a request to a server and waits for a response. It represents the actual action performed by a virtual user.

**Typical Workflow:**

1.  **Add a Sampler** (e.g., HTTP Request) to your Thread Group or Controller.
2.  **Configure Request Details** — target server, endpoint, method, parameters, payload.
3.  **Add Assertions** — optionally validate the response (status code, content).
4.  **Extract Dynamic Data** — use extractors (post-processors) to capture values for later requests.
5.  **Chain Samplers** — sequence multiple samplers to simulate a workflow.

**Best Practices:**

*   Use the right sampler for the protocol.
*   Parameterize requests; avoid hardcoded values.
*   Validate responses with assertions.
*   Handle dynamic data with extractors.
*   Reuse config elements (e.g., HTTP Request Defaults) to reduce repetition.

### Controllers

A **Controller** determines the order and logic by which samplers execute — enabling branching, looping, and conditional execution to simulate realistic user behavior.

**Typical Workflow:**

1.  **Add a Controller** (e.g., Loop, If, Transaction) to your Thread Group or under another Controller.
2.  **Configure Logic** — set conditions, loop counts, or expressions.
3.  **Add Samplers** — place elements inside the controller to define what it controls.
4.  **Combine Controllers** — nest or sequence multiple controllers for complex flows.
5.  **Test and Debug** — verify the logic behaves as intended.

**Best Practices:**

*   Keep logic clear; use comments and logical grouping.
*   Avoid deep nesting; modularize where possible.
*   Use Transaction Controllers to group multiple requests as one reported transaction.
*   Parameterize conditions with variables and functions.
*   Validate each controller's behavior in isolation before combining.

> [!IMPORTANT]
> **The distinction matters for debugging.** If a request never runs, the cause is usually a *controller* routing issue (a false condition, an empty loop). If a request runs but returns the wrong data, it is a *sampler* configuration or correlation problem. Knowing which layer to inspect saves time.

### Example Structure

```
Thread Group
├── Loop Controller (5 times)
│   └── HTTP Request (Browse)
├── If Controller (if loggedIn)
│   └── HTTP Request (Purchase)
└── Transaction Controller (Checkout Process)
    ├── HTTP Request (Add to Cart)
    └── HTTP Request (Payment)
```

**Example:** A Loop Controller repeats browsing five times; an If Controller only executes a purchase when the user is logged in; a Transaction Controller groups the checkout steps into a single reported transaction. Together they simulate realistic, conditional user journeys.

[↑ Back to Table of Contents](#table-of-contents)

***

*These elements do not float freely — their nesting defines what each element can see and when it runs.*

## 5. Component Hierarchy

The **component hierarchy** defines how elements are organized and interact within a Test Plan. Understanding it is essential for building maintainable, correct test scripts, because the hierarchy determines **scope**, **inheritance**, and **execution order**.

### Key Concepts

*   **Parent-Child Relationships:** Elements are nested, with the Test Plan at the top, followed by Thread Groups, Controllers, Samplers, and other components.
*   **Scope and Inheritance:** Configuration elements and variables apply to their children — placing a config higher up makes it available to more elements.
*   **Execution Order:** The hierarchy determines the order in which elements run during a test.
*   **Modularity:** Logical grouping and nesting enable reusable test designs.

### Typical Hierarchy Diagram

```
Test Plan
├── Thread Group(s)
│   ├── Logic Controller(s)
│   │   ├── Sampler(s)
│   │   └── Assertion(s)
│   ├── Config Element(s)
│   ├── Pre/Post-Processor(s)
│   └── Listener(s)
├── Config Element(s) (global)
└── Listener(s) (global)
```

### Hierarchy Rules of Thumb

*   **Samplers** must live under a Thread Group (or a Controller inside one) to run.
*   **Assertions and Post-Processors** are placed as children of a sampler (or a controller) to act on its response.
*   **Config Elements** (e.g., HTTP Request Defaults) placed at the Thread Group level apply to all samplers within it; placed at the Test Plan level, they apply globally.
*   **Listeners** can sit at any level and collect results for everything in their scope.

> [!TIP]
> **Global vs. local config:** Put shared, environment-level configuration (default server, common headers) at the **Test Plan** level. Put user-specific or role-specific configuration at the **Thread Group** level. This minimizes duplication and keeps test plans maintainable.

### Best Practices

*   **Organize for Clarity** — structure the plan to reflect real workflows and keep related elements together.
*   **Use Global vs. Local Configs** — place configs at the appropriate level (Test Plan for global, Thread Group for local).
*   **Minimize Duplication** — reuse components and variables where possible.
*   **Comment and Document** — explain complex hierarchies and logic.
*   **Test Incrementally** — build and validate the hierarchy step by step to catch issues early.

**Example:** A Test Plan has two Thread Groups — one for regular users and one for admins. Each Thread Group owns its controllers and samplers, but both share a global HTTP Request Defaults configuration. Listeners sit at both Thread Group and Test Plan levels to capture detailed results. This hierarchy separates user roles, reuses configuration, and provides comprehensive reporting.

[↑ Back to Table of Contents](#table-of-contents)
