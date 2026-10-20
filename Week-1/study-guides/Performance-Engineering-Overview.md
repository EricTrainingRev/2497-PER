# Performance Engineering Overview

## Table of Contents

* [1. High-Level Overview](#1-high-level-overview)
* [2. Performance Engineering vs Performance Testing](#2-performance-engineering-vs-performance-testing)
* [3. SDLC vs STLC](#3-sdlc-vs-stlc)
    * [3.1 SDLC](#31-sdlc)
    * [3.2 STLC](#32-stlc)
    * [3.3 SDLC vs STLC Comparison](#33-sdlc-vs-stlc-comparison)
* [4. Performance Across the SDLC (Shift-Left)](#4-performance-across-the-sdlc-shift-left)
* [5. Performance Test Engineer vs Performance Engineer](#5-performance-test-engineer-vs-performance-engine)

***

## 1. High-Level Overview

**Performance engineering** is the discipline of designing, developing, and testing software with **performance** as a core quality attribute throughout the entire Software Development Life Cycle (SDLC). It goes beyond traditional performance testing by proactively identifying and addressing performance risks from the earliest stages of a project.

This guide covers how performance engineering differs from performance testing, the lifecycles that frame them, how performance is integrated across the SDLC through the **shift-left approach**, and the distinct roles of the **Performance Test Engineer** and the **Performance Engineer**.

[↑ Back to Table of Contents](#table-of-contents)

***

*Before we compare roles, we must distinguish the discipline of performance engineering from the activity of performance testing.*

## 2. Performance Engineering vs Performance Testing

**Performance engineering** is the discipline of designing, developing, and testing software with performance as a core quality attribute throughout the entire SDLC. It goes beyond traditional performance testing by proactively identifying and addressing performance risks from the earliest stages of a project.

**Performance testing** is the subset of that discipline focused on executing tests (load, stress, spike, endurance) to measure and validate system behavior against defined requirements. It is the measurement and validation arm of performance engineering.

| Aspect | Performance Engineering | Performance Testing |
| :--- | :--- | :--- |
| **Scope** | A discipline applied across the whole SDLC | A specific activity (test execution) |
| **Timing** | From requirements through production | Primarily the testing phase |
| **Approach** | Proactive — design for performance and prevent issues | Reactive — measure and validate against targets |
| **Output** | Strategy, NFRs, architecture decisions, tuning | Test results, pass/fail reports against NFRs |

[↑ Back to Table of Contents](#table-of-contents)

***

*Now that we distinguish the discipline from the activity, let's look at the lifecycles these activities operate within.*

## 3. SDLC vs STLC

### 3.1 SDLC

The **Software Development Life Cycle (SDLC)** is the overall process of developing software, from requirements gathering to maintenance.

*   **Requirements Gathering:** Define what the system should do.
*   **Design:** Architect the system, selecting technologies and environments to be used.
*   **Implementation (Coding):** Develop the software according to design specifications.
*   **Testing:** Validate functional and non-functional requirements.
*   **Deployment:** Release the software to production.
*   **Maintenance:** Monitor, optimize, and update the system to maintain and improve it over time.

### 3.2 STLC

The **Software Testing Life Cycle (STLC)** is the subset of the SDLC focused on testing activities, including planning, design, execution, and closure.

*   **Requirement Analysis:** Review and analyze requirements to identify testable aspects.
*   **Test Planning:** Define the scope, objectives, resources, schedule, and approach for testing.
*   **Test Case Design:** Create detailed test cases and test data based on requirements and design documents.
*   **Test Environment Setup:** Prepare the hardware, software, and network configurations needed for testing.
*   **Test Execution:** Run the test cases, record results, and log defects as needed.
*   **Test Closure:** Evaluate test completion criteria, document findings, and archive test artifacts for future reference.

### 3.3 SDLC vs STLC Comparison

| Aspect | SDLC | STLC |
| :--- | :--- | :--- |
| **Scope** | The entire software development process | Testing activities only |
| **Focus** | Building the product | Verifying the product |
| **Phases** | Requirements → Design → Coding → Testing → Deployment → Maintenance | Analysis → Planning → Design → Setup → Execution → Closure |
| **Goal** | Deliver a working system | Ensure the system meets quality standards |

> [!NOTE]
> The SDLC provides the overall framework for software development, while the STLC ensures that testing is systematically planned and executed within that framework. Together, they help deliver high-quality software by integrating testing activities throughout the development process.

[↑ Back to Table of Contents](#table-of-contents)

***

*With the lifecycles in place, we now see how performance is integrated early in development rather than deferred to the end.*

## 4. Performance Across the SDLC (Shift-Left)

The **shift-left approach** in performance engineering means integrating performance considerations early in the SDLC, starting from requirements, design, and development, rather than waiting until testing or deployment. This helps catch potential bottlenecks and inefficiencies before they become costly to fix.

**Benefits in the SDLC:**

*   Early detection of performance issues reduces remediation costs and project delays.
*   Ensures that performance requirements are considered alongside functional requirements.
*   Improves user satisfaction by delivering faster, more reliable applications.
*   Supports scalability and reliability goals from the outset.
*   Reduces risk of production outages or slowdowns by validating performance continuously.

> [!TIP]
> The earlier a performance risk is found, the cheaper it is to fix. Shift-left means performance is a continuous concern — not a final checkpoint before release.

[↑ Back to Table of Contents](#table-of-contents)

***

*Finally, we clarify the two distinct roles that carry out performance work.*

## 5. Performance Test Engineer vs Performance Engineer

While the two roles are closely related and often overlap, they differ in scope, focus, and where they operate in the SDLC.

| Aspect | Performance Test Engineer | Performance Engineer |
| :--- | :--- | :--- |
| **Primary Focus** | Executing and validating performance tests | Owning performance as a quality attribute across the whole SDLC |
| **Where They Operate** | Primarily the testing phase | Requirements through design, development, testing, and production |
| **Key Activities** | Building scripts, running load/stress/spike/endurance tests, analyzing results against NFRs | Workload modeling, capacity planning, architecture review, tuning, shift-left strategy |
| **Typical Deliverables** | Test plans, scripts, test results, pass/fail reports | Performance strategy, NFRs, capacity models, tuning recommendations |
| **Mindset** | Reactive — measure and validate against targets | Proactive — design for performance and prevent issues before they occur |

> [!TIP]
> The two roles work together: the **Performance Engineer** sets the strategy, defines the NFRs, and designs the workload model, while the **Performance Test Engineer** executes the tests and validates the system against those targets. A single person often wears both hats, but the distinction matters for scope and responsibility.

[↑ Back to Table of Contents](#table-of-contents)
