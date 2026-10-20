# Performance Testing Fundamentals

## Table of Contents

* [1. High-Level Overview](#1-high-level-overview)
* [2. Functional vs Non-Functional Testing](#2-functional-vs-non-functional-testing)
* [3. Types of Performance Tests](#3-types-of-performance-tests)
* [4. Real-World Failure Modes](#4-real-world-failure-modes)
* [5. Performance Metrics Vocabulary](#5-performance-metrics-vocabulary)

***

## 1. High-Level Overview

**Performance testing** is a key category of **non-functional testing** that evaluates how a system behaves under load. It determines whether a system is fast, stable, and scalable enough to meet its performance targets.

This guide covers the difference between functional and non-functional testing, the four core performance test types (load, stress, spike, endurance), the real-world failure modes those tests are designed to prevent, and the fundamental metrics used to describe system performance.

[↑ Back to Table of Contents](#table-of-contents)

***

*Before we examine test types, we distinguish the two broad categories of testing they belong to.*

## 2. Functional vs Non-Functional Testing

**Functional testing** ensures that a system operates according to its requirements, producing correct outputs for given inputs. This process is focused on **verification**: confirming that the software performs as specified and meets the documented expectations.

**Non-functional testing**, on the other hand, evaluates broader attributes such as performance, scalability, reliability, and security. Performance testing is a key aspect of this category. The emphasis here is on **validation**, making sure the system not only works correctly but also fulfills user needs and quality standards beyond basic functionality.

| Aspect | Functional Testing | Non-Functional Testing |
| :--- | :--- | :--- |
| **Question** | "Are we building the product right?" | "Are we building the right product?" |
| **Focus** | Correct behavior and outputs | Performance, scalability, reliability, security |
| **Verification / Validation** | Verification and validation are a separate axis from functional/non-functional — both categories can be either. |
| **Example** | Login returns the expected page | Login responds within 500ms under 1,000 users |

> [!TIP]
> **Verification vs. Validation:** Verification asks "Are we building the product right?" (conformance to spec); validation asks "Are we building the right product?" (fits user needs). These are a separate axis from functional vs. non-functional — both categories can be verified or validated.

[↑ Back to Table of Contents](#table-of-contents)

***

*Now let's examine the specific types of performance tests used to validate system behavior.*

## 3. Types of Performance Tests

| Test Type | What It Measures | Example |
| :--- | :--- | :--- |
| **Load Testing** | System behavior under expected user loads; identifies throughput, response times, and resource utilization. | An e-commerce site is load tested with 10,000 concurrent users. |
| **Stress Testing** | System stability and error handling under extreme conditions. | The same site is stress tested to 50,000 users. |
| **Spike Testing** | System response to sudden, sharp increases in load. | Traffic bursts are simulated to test sudden spikes. |
| **Endurance (Soak) Testing** | System performance and resource usage over extended periods to detect memory leaks or degradation. | The site is endurance tested over 24 hours. |

> [!NOTE]
> **Example scenario:** An e-commerce site is load tested with 10,000 concurrent users, stress tested to 50,000 users, spike tested with sudden traffic bursts, and endurance tested over 24 hours.

[↑ Back to Table of Contents](#table-of-contents)

***

*These test types exist for a reason — each one prevents a specific real-world failure.*

## 4. Real-World Failure Modes

Performance testing exists to prevent real-world failures. Each failure mode below is a risk that, if left unaddressed, can cause outages, degraded user experience, or financial loss. The table maps each failure possibility to the test type that surfaces it and the symptom to watch for.

| Failure Mode | Test Type That Surfaces It | Symptom to Watch For |
| :--- | :--- | :--- |
| **Peak-load crash / outage** | Load Testing | System cannot handle expected concurrency; requests fail or time out at target load. |
| **Sudden traffic spike overwhelms capacity** | Spike Testing | Response times spike sharply and errors appear when load jumps (e.g., flash sale, launch day). |
| **Slow degradation over time / memory leak** | Endurance (Soak) Testing | Memory usage climbs steadily and never releases; response times worsen over hours. |
| **Database bottleneck from missing index or full table scan** | Load Testing + Bottleneck Analysis | High response times with low app-server CPU; slow query logs show full table scans (DB host CPU is typically high). |
| **Thread / connection pool exhaustion under concurrency** | Stress Testing | New requests queue or are rejected as all threads/connections become blocked. |
| **Third-party API / external dependency latency** | Load Testing + Monitoring | End-to-end latency spikes while internal CPU stays low; external call times dominate. |
| **Capacity underestimation → cost overruns or downtime at scale** | Workload Modeling + Capacity Planning | Throughput plateaus and errors rise before the expected target load is reached. |

> [!WARNING]
> A fast response with an error is not a success. High error rates under load invalidate throughput and response-time metrics — only successful requests count toward real capacity. Always validate that reported metrics exclude failed requests.

[↑ Back to Table of Contents](#table-of-contents)

***

*To interpret test results, we need a shared vocabulary of performance metrics.*

## 5. Performance Metrics Vocabulary

| Metric | Definition | Example |
| :--- | :--- | :--- |
| **Latency** | The time taken to process a single request (usually in milliseconds); often used interchangeably with response time, though strictly it refers to one-way delay. | A request processes in 200ms. |
| **Response Time** | The total time from sending a request to receiving a response, including network and processing delays. | End-to-end response of 350ms. |
| **Throughput** | The number of transactions or requests processed per unit of time (e.g., requests per second). | 1,000 transactions per second. |
| **Concurrency** | The number of simultaneous users or transactions the system can handle at once. | 5,000 concurrent users. |
| **Utilization** | The percentage of system resources (CPU, memory, disk, network) in use during testing. | 70% CPU utilization. |
| **Error Rate** | The percentage of requests that result in errors (e.g., HTTP 4xx/5xx) during a test. | Error rate below 0.5%. |

> [!NOTE]
> **Example scenario:** During a load test, the system shows an average latency of 200ms, a 95th percentile response time of 350ms, supports 5,000 concurrent users, achieves a throughput of 1,000 transactions per second at 70% CPU utilization, and maintains an Apdex score of 0.95 with an error rate below 0.5%.

[↑ Back to Table of Contents](#table-of-contents)
