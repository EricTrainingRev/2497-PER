# Workload Modeling

## Table of Contents

* [1. High-Level Overview](#1-high-level-overview)
* [2. Think Time vs Pacing](#2-think-time-vs-pacing)
* [3. Arrival Rate Modeling](#3-arrival-rate-modeling)
* [4. User Distribution](#4-user-distribution)
* [5. Concurrency vs Arrival Rate](#5-concurrency-vs-arrival-rate)

***

## 1. High-Level Overview

**Workload modeling** is the process of designing realistic test scenarios that accurately represent how users interact with a system. Effective workload models help ensure that performance tests provide meaningful results and uncover potential bottlenecks before production.

A workload model defines how much load to apply, how quickly users arrive, what actions they perform, and how they are distributed across the system. This guide covers the four core concepts: **think time vs pacing**, **arrival rate modeling**, **user distribution**, and the **concurrency vs arrival rate** distinction.

[↑ Back to Table of Contents](#table-of-contents)

***

*First, we distinguish the two timing controls that pace user behavior.*

## 2. Think Time vs Pacing

| Concept | Definition |
| :--- | :--- |
| **Think Time** | The delay between a user receiving a response and sending the next request (simulates real user behavior). |
| **Pacing** | The interval between the start of one iteration and the start of the next, controlling the rate of test execution. |

*   **Think time** simulates realistic human pauses within a single user journey — reading a page, deciding what to do next, filling a form.
*   **Pacing** controls how often a user repeats the whole scenario, governing the overall rate of load.

> [!TIP]
> Without think time and pacing, virtual users fire requests back-to-back in unrealistic "machine-gun" patterns that can overwhelm the server and skew results.

[↑ Back to Table of Contents](#table-of-contents)

***

*Next, we model how new users enter the system over time.*

## 3. Arrival Rate Modeling

**Arrival rate modeling** defines how frequently new users or requests arrive at the system (e.g., users per second). This helps simulate real-world traffic patterns, such as bursts or gradual ramp-ups.

*   Arrival rate controls the **flow of new work** entering the system.
*   It enables realistic patterns like gradual ramp-ups, steady state, or sudden bursts.
*   Arrival rate is expressed in users or requests **per unit of time** (e.g., 10 users per minute).

> [!NOTE]
> Arrival rate and concurrency are different: arrival rate is the *rate* at which users enter, while concurrency is the *number* of users active at once (see Section 5).

[↑ Back to Table of Contents](#table-of-contents)

***

*Beyond arrival, we must represent the mix of user behaviors.*

## 4. User Distribution

**User distribution** specifies how different types of users or actions are represented in the test (e.g., 70% browsing, 20% purchasing, 10% support queries).

*   Reflects the real mix of business processes executed by users.
*   Guides how test scripts and virtual users are allocated across workflows.
*   Often implemented with controllers that apply a percentage of executions to different actions.

> [!NOTE]
> **Example:** In an e-commerce test, 80% of actions are browsing, 15% are adding to cart, and 5% are checking out — matching observed production traffic.

[↑ Back to Table of Contents](#table-of-contents)

***

*Finally, we clarify the often-confused distinction between concurrency and arrival rate.*

## 5. Concurrency vs Arrival Rate

| Concept | Definition |
| :--- | :--- |
| **Concurrency** | The number of users or sessions active at the same time. |
| **Arrival Rate** | The rate at which new users or requests enter the system, which may result in varying concurrency depending on session length and think time. |

The same arrival rate can produce very different concurrency depending on how long each user stays active. Longer sessions and higher think times mean more users are active at once for a given arrival rate.

> [!NOTE]
> **Worked example — testing an e-commerce site:**
> *   100 virtual users ramp up over 10 minutes.
> *   Each user spends 5 seconds (think time) between actions.
> *   80% of actions are browsing, 15% are adding to cart, 5% are checking out.
> *   Arrival rate is set to 10 new users per minute.

[↑ Back to Table of Contents](#table-of-contents)
