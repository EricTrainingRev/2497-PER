# NFRs & Metrics

## Table of Contents

* [1. High-Level Overview](#1-high-level-overview)
* [2. Key NFR Concepts](#2-key-nfr-concepts)
* [3. SLA / SLO / SLI](#3-sla--slo--sli)
* [4. Throughput & TPS](#4-throughput--tps)
* [5. Response Time](#5-response-time)
* [6. Percentiles (90/95/99)](#6-percentiles-909599)
* [7. Apdex Score Basics](#7-apdex-score-basics)

***

## 1. High-Level Overview

**Non-Functional Requirements (NFRs)** define the quality attributes a system must meet beyond its functional behavior — how fast, how scalable, how reliable, and how available. NFRs are typically defined early in the project and validated through non-functional testing and monitoring.

To make NFRs measurable, we use specific metrics: service-level targets (SLA/SLO/SLI), throughput, response time, percentiles, and the Apdex score. This guide defines each and shows how they turn business expectations into measurable targets.

[↑ Back to Table of Contents](#table-of-contents)

***

*First, we establish the broad categories of non-functional requirements.*

## 2. Key NFR Concepts

| NFR | Definition |
| :--- | :--- |
| **Performance** | The system's ability to meet response time, throughput, and resource utilization targets under expected workloads. |
| **Scalability** | The capability to handle increased load by adding resources (horizontal or vertical scaling). |
| **Reliability** | The ability of the system to operate without failure over a specified period. |
| **Availability** | The proportion of time the system is operational and accessible when required for use. |
| **Security** | Protection of data and resources from unauthorized access and vulnerabilities. |
| **Maintainability** | How easily the system can be updated, fixed, or improved. |
| **Usability** | The ease with which users can interact with the system. |

[↑ Back to Table of Contents](#table-of-contents)

***

*NFRs are made concrete through service-level targets.*

## 3. SLA / SLO / SLI

| Term | Full Name | Definition | Example |
| :--- | :--- | :--- | :--- |
| **SLA** | Service Level Agreement | A formal contract between a service provider and its customer outlining specific performance and availability targets. | 99.9% uptime, or a response time under 500ms. |
| **SLO** | Service Level Objective | The measurable goals that underpin SLAs, often set more stringently to provide a buffer. | 99.95% of requests served in under 300ms. |
| **SLI** | Service Level Indicator | The actual metrics used to track and quantify the level of service provided. | The measured percentage of requests completed within a timeframe. |

> [!IMPORTANT]
> **The relationship:** An SLA might require 99.9% uptime. To support this, the SLO could be set at 99.95% uptime, and the SLI would be the actual measured uptime over a given period. SLOs are set more stringently than SLAs so that contractual obligations are consistently met even if occasional issues arise.

[↑ Back to Table of Contents](#table-of-contents)

***

*Now we define the core metrics used to measure performance against those targets.*

## 4. Throughput & TPS

**Throughput** is the number of transactions or requests processed per unit of time (e.g., requests per second). A common measurement used to track throughput is **Transactions per Second (TPS)**.

*   **Throughput** reflects the actual work done by the system, not just the number of requests sent.
*   Only successful (non-error) requests should be counted when interpreting throughput.
*   Throughput typically increases with load up to a point, then plateaus or drops as the system reaches its capacity limit.

> [!NOTE]
> If throughput plateaus at 1,000 requests/second while error rates and response times spike, the system's true capacity is around 1,000 requests/second under current conditions.

[↑ Back to Table of Contents](#table-of-contents)

***

*Next, the metric that captures how long users wait for a response.*

## 5. Response Time

**Response time** is the total time from sending a request to receiving a response, including network and processing delays. It is distinct from **latency**, which is the time taken to process a single request (usually in milliseconds) — though the two terms are often used interchangeably, with latency strictly meaning one-way delay.

*   Response time is what users actually experience.
*   It is a key input to SLAs and SLOs.
*   Response time is most meaningful when examined across a distribution (see percentiles), not as a single average.

[↑ Back to Table of Contents](#table-of-contents)

***

*Averages can mislead — percentiles reveal the real distribution of response times.*

## 6. Percentiles (90/95/99)

**Percentiles** are metrics that show the value below which a given percentage of observations fall. A percentile (such as the 90th, 95th, or 99th) indicates the distribution of response times and helps identify outliers.

| Percentile | Meaning |
| :--- | :--- |
| **50th (Median)** | Half of requests complete at or below this time. |
| **90th** | 90% of requests complete at or below this time. |
| **95th** | 95% of requests complete at or below this time. |
| **99th** | 99% of requests complete at or below this time (captures rare, severe slowdowns). |

> [!WARNING]
> Averages hide outliers and spikes. A 95th percentile response time of 400ms means 95% of requests completed in 400ms or less, while the slowest 5% took longer. A large gap between the median and higher percentiles signals occasional bottlenecks.

[↑ Back to Table of Contents](#table-of-contents)

***

*Finally, we summarize user satisfaction into a single score.*

## 7. Apdex Score Basics

The **Apdex Score** is a standardized metric for user satisfaction based on response-time thresholds. It ranges from **0 (no users satisfied)** to **1 (all users satisfied)**.


$$
\text{Apdex} = \frac{\text{Satisfied Requests} + (0.5 \times \text{Tolerating Requests})}{\text{Total Requests}}
$$


*   **Satisfied** requests: response time at or below the target threshold.
*   **Tolerating** requests: response time above the target but within the tolerance threshold (by convention, up to **4×** the target).
*   **Frustrated** requests: response time beyond the tolerance threshold (counted as 0).

> [!NOTE]
> **Example:** An application with a throughput of 2,000 TPS, a 95th percentile response time of 400ms, and an Apdex score of 0.92 meets its SLO for user experience.

[↑ Back to Table of Contents](#table-of-contents)
