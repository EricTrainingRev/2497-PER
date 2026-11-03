# JMeter: Controllers, Timers & Parameterization

## Table of Contents

* [1. High-Level Overview](#1-high-level-overview)
* [2. Loop, If & Throughput Controllers](#2-loop-if--throughput-controllers)
* [3. Realistic User Behavior](#3-realistic-user-behavior)
* [4. CSV Data-Driven Testing](#4-csv-data-driven-testing)
* [5. Data Reuse vs One-Time Usage](#5-data-reuse-vs-one-time-usage)
* [6. Handling Large Datasets](#6-handling-large-datasets)
* [7. Preventing Data Collision](#7-preventing-data-collision)

***

## 1. High-Level Overview

Realistic performance tests do not hammer a server with identical requests. They vary **what** runs (through controllers), **when** it runs (through timers and pacing), and **what data** each user consumes (through parameterization). This module brings those levers together.

It covers the core **Loop, If, and Throughput controllers**, the elements of **realistic user behavior** (think times, randomness, pacing), **CSV data-driven testing**, the choice between **data reuse and one-time usage**, handling **large datasets**, and **preventing data collision**.

[↑ Back to Table of Contents](#table-of-contents)

***

*We begin with the controllers that shape when and how often requests execute.*

## 2. Loop, If & Throughput Controllers

Logic controllers determine the *flow* of a test. The **Loop**, **If**, and **Throughput** controllers are the three most commonly used to shape realistic request patterns.

### Loop Controller

The **Loop Controller** repeats its child elements a set number of times, simulating repeated behavior such as browsing multiple pages.

| Aspect | Detail |
| :--- | :--- |
| **Iteration Control** | Specifies how many times its children execute |
| **Nested Loops** | Can be nested for multi-level repetition |
| **Dynamic Counts** | Loop count can be a fixed number or a variable (`${loops}`) |
| **Scope** | Only children inside the controller are affected |

**Best Practices:**

*   Use for repeated actions (e.g., browsing several pages).
*   Parameterize the loop count with variables for flexibility.
*   Avoid excessive nesting — it hurts maintainability.
*   Add timers inside loops to simulate real pacing.
*   Validate with small counts before scaling.

**Example Structure:**

```
Thread Group
├── Loop Controller (5 times)
│   └── HTTP Request (Browse Page)
└── Listener (View Results Tree)
```

**Example:** A Loop Controller set to 5 iterations contains an HTTP Request that simulates browsing a page. Each virtual user executes the request five times in succession, mimicking a user visiting multiple pages.

### If Controller

The **If Controller** conditionally executes its children based on an expression, enabling decision-making logic such as "only purchase if logged in."

| Aspect | Detail |
| :--- | :--- |
| **Conditional Logic** | Runs children only when a condition evaluates to true |
| **Expression Language** | Uses JMeter's Expression Language, e.g., `"${loggedIn}".equals("true")` |
| **Negation** | Supports negating conditions to run when false |
| **Integration** | Combines with extractors, variables, and other controllers |

> [!TIP]
> A common ready-made condition is `${JMeterThread.last_sample_ok}`, which is true if the *previous* sampler succeeded — perfect for driving a failure-tolerant workflow without writing your own expression.

**Best Practices:**

*   Use it for real-world decision points.
*   Keep conditions simple and readable.
*   Drive conditions from variables and extractors.
*   Test with different data to verify behavior.

**Example Structure:**

```
Thread Group
├── HTTP Request (Login)
├── If Controller (${loggedIn} == 'true')
│   └── HTTP Request (Purchase)
└── Listener (View Results Tree)
```

**Example:** An If Controller after a login sampler runs the Purchase request only when the `loggedIn` variable is `true` (set by a post-processor on a successful login). Otherwise it is skipped, accurately modeling conditional behavior.

### Throughput Controller

The **Throughput Controller** controls how often its children execute, modeling scenarios where certain actions are less frequent than others (e.g., only 10% of users purchase).

| Aspect | Detail |
| :--- | :--- |
| **Execution Frequency** | Specifies a percentage or an absolute number of executions |
| **Modes** | **Percent Execution** (e.g., 20% of iterations) or **Total Executions** (e.g., run 10 times total) |
| **Scope** | Applies per user (thread) or across all users (test) |
| **Dynamic Control** | Throughput values can be parameterized |

**Best Practices:**

*   Model realistic traffic distributions (only a fraction of users purchase).
*   Parameterize throughput for different environments.
*   Nest with other controllers for complex patterns.
*   Validate the achieved distribution in results.

> [!WARNING]
> Despite the name, the **Throughput Controller does not target a requests-per-second rate** — it controls the *proportion or count* of executions, not the speed of the test. For true rate control you need pacing or a dedicated rate-limiting approach.

**Example Structure:**

```
Thread Group
├── HTTP Request (Browse)
├── Throughput Controller (10% execution)
│   └── HTTP Request (Purchase)
└── Listener (Summary Report)
```

**Example:** A Throughput Controller set to 10% wraps a Purchase request. All users browse, but only 10% of iterations execute a purchase — accurately simulating a scenario where purchases are less frequent than browsing.

[↑ Back to Table of Contents](#table-of-contents)

***

*Controllers shape *which* requests run; timers and pacing shape *when* and how realistically they run.*

## 3. Realistic User Behavior

Simulating **realistic user behavior** is what separates a meaningful test from a synthetic hammering. Real users pause, think, follow branches, and use distinct data — and a test that ignores this produces misleading results.

### Key Concepts

*   **Think Times:** Delays between actions, mimicking the time users spend reading or interacting with pages.
*   **Pacing:** Controlling the rate at which users perform actions, avoiding unrealistically rapid execution.
*   **Conditional Logic:** Using controllers (If, While, Switch) to simulate decision points and varied workflows.
*   **Randomization:** Adding randomness to delays, data, and flows to reflect diverse behavior.
*   **Data Parameterization:** Using variables and CSV data so each user has unique input.
*   **Session Management:** Simulating login, session persistence, and logout to mirror real sessions.

### Common Timers

| Timer | Behavior |
| :--- | :--- |
| **Constant Timer** | Adds a fixed delay between requests |
| **Uniform Random Timer** | Adds a delay uniformly distributed within a range |
| **Gaussian Random Timer** | Adds a delay with a normal (bell-curve) distribution around a mean |
| **Constant Throughput Timer** | Adjusts pacing to target a fixed requests-per-minute rate |

> [!TIP]
> Prefer **random** timers (Gaussian or Uniform) over a single constant think time. Real users do not pause for exactly the same duration every time; a distribution produces a far more believable load profile.

### Typical Workflow

1.  **Add Timers** — place a think-time timer between requests.
2.  **Parameterize Data** — use CSV Data Set Config or variables for unique per-user data.
3.  **Implement Conditional Flows** — use If/While/Switch controllers for branching.
4.  **Randomize Actions** — vary delays and data selection.
5.  **Simulate Sessions** — include login/logout and manage session variables.
6.  **Monitor and Adjust** — compare simulated behavior against real usage patterns.

### Best Practices

*   **Match Real Patterns** — base scenarios on analytics or production logs.
*   **Avoid Unrealistic Load** — use timers and pacing so users do not act faster than reality allows.
*   **Vary Data and Flows** — each virtual user should follow a unique path with unique data where possible.
*   **Combine Controllers and Timers** — use a mix for complex, lifelike scenarios.
*   **Validate with Stakeholders** — confirm realism with business/product owners.

**Example Structure:**

```
Thread Group
├── CSV Data Set Config (user data)
├── HTTP Request (Login)
├── Loop Controller (3 times)
│   ├── HTTP Request (Browse)
│   └── Constant Timer (think time)
├── If Controller (if addToCart)
│   └── HTTP Request (Add to Cart)
├── HTTP Request (Logout)
└── Listener (View Results Tree)
```

**Example:** Each user logs in with unique credentials from a CSV file, browses three times with think times, conditionally adds an item to the cart based on a variable, then logs out — closely mirroring a real user journey and yielding more accurate, actionable results.

[↑ Back to Table of Contents](#table-of-contents)

***

*Unique per-user data comes from external sources — most commonly a CSV file.*

## 4. CSV Data-Driven Testing

**CSV Data-Driven Testing** runs tests with dynamic input data, where each user or iteration uses unique values. It is implemented with the **CSV Data Set Config** element, which reads rows from a CSV file and assigns the columns to variables for use across the test plan.

### Key Concepts

*   **CSV Data Set Config:** A configuration element that reads data from a CSV file and exposes it as variables during execution.
*   **Parameterization:** Lets each virtual user or iteration use different data (usernames, passwords, product IDs).
*   **Variable Assignment:** Each CSV column maps to a variable name referenced in samplers and controllers.
*   **Data Scope:** Data can be shared across threads or kept unique per thread, depending on configuration.

### Typical Workflow

1.  **Prepare CSV File** — create the file with the required test data.
2.  **Add CSV Data Set Config** — insert it into the Test Plan or Thread Group.
3.  **Configure File and Variables** — set the path, delimiter, and variable names for the columns.
4.  **Reference Variables** — use `${username}`, `${password}`, etc., in samplers, controllers, or assertions.
5.  **Run the Test** — each thread/iteration consumes a new row.

### Best Practices

*   **Unique Data Per User** — ensure enough unique rows to avoid collisions.
*   **Consistent Formatting** — no stray spaces or missing values.
*   **Parameterize All Inputs** — use variables wherever dynamic data is needed.
*   **Handle End-of-File** — configure recycle/stop behavior appropriately.
*   **Secure Sensitive Data** — avoid plain-text secrets in CSV files when possible.

**Example Structure:**

```
Test Plan
├── Thread Group
│   ├── CSV Data Set Config (user credentials)
│   ├── HTTP Request (Login using ${username}, ${password})
│   └── Listener (View Results Tree)
```

> [!NOTE]
> The **CSV header row** (if the file includes one) is not automatically consumed — either set "Ignore first line" where supported, or ensure your column list aligns with the data rows.

**Example:** A CSV file `users.csv` holds username and password columns. The test plan adds a CSV Data Set Config pointing to it and defines `username` and `password` variables. Each virtual user logs in with different credentials, enabling realistic, scalable data-driven testing.

[↑ Back to Table of Contents](#table-of-contents)

***

*Once data is loaded, a key decision is whether users may share it or must each consume it once.*

## 5. Data Reuse vs One-Time Usage

Deciding whether to **reuse** data across users or consume each row **only once** depends on your test objectives and the behavior of the system under test.

| Aspect | Data Reuse | One-Time Usage |
| :--- | :--- | :--- |
| **What happens** | The same row is used by multiple threads/iterations | Each row is consumed exactly once |
| **Suitable for** | Shared accounts, stateless ops, general load | Unique users, registrations, orders |
| **CSV settings** | "Recycle on EOF" = true | "Recycle on EOF" = false, "Stop thread on EOF" = true |
| **Risk if misconfigured** | Duplicate usage may be harmless or may collide | Running out of rows stops threads |

### When to Reuse Data

*   **Limited Data Availability** — the dataset is small or sharing is acceptable.
*   **Testing General Behavior** — load/stress tests where unique data is not critical.
*   **Stateless Operations** — e.g., browsing a public page.

### When to Use Data Only Once

*   **Unique User Simulation** — registrations, unique logins, order placement.
*   **Avoiding Data Collisions** — preventing duplicate entries, session clashes, or integrity issues.
*   **Realistic Workflows** — each virtual user represents a distinct entity.

### Best Practices

*   **Match Test Goals** — choose based on whether you're testing concurrency vs. uniqueness.
*   **Configure CSV Appropriately** — one-time usage needs "Recycle on EOF" = false and "Stop thread on EOF" = true.
*   **Monitor Data Consumption** — ensure enough rows for all users/iterations.
*   **Document Data Strategy** — comment the approach in the test plan for maintainability.

> [!IMPORTANT]
> For **one-time usage**, the CSV file must contain at least as many rows as there are thread iterations. If it runs out, threads stop early (with "Stop thread on EOF") — which silently reduces your achieved load. Always size the dataset to the test.

**Example Scenarios** — *Reuse:* 100 users logging in with any of 10 shared test accounts to simulate general load. *One-time:* 1,000 users each registering a unique email to test account creation at scale.

[↑ Back to Table of Contents](#table-of-contents)

***

*Large datasets introduce their own challenges: memory, I/O, and access speed.*

## 6. Handling Large Datasets

With **large datasets**, efficient data management is essential for accurate, scalable, and performant execution. Very large CSV files can strain memory, I/O, and data integrity across threads.

### Key Concepts

*   **Efficient Data Access:** JMeter streams CSV files line by line, but very large files can still impact performance if mismanaged.
*   **Memory Management:** Avoid loading entire datasets into memory; rely on JMeter's streaming behavior.
*   **Data Partitioning:** Split large datasets into smaller files/segments, especially for distributed testing.
*   **Thread Safety:** Ensure each thread accesses data correctly to prevent collisions.
*   **File Location:** Store large CSV files on fast, local storage to minimize I/O bottlenecks.

### Best Practices

*   **Use Streaming** — let CSV Data Set Config read data as needed rather than loading the whole file.
*   **Limit Variable Scope** — read only the columns you need.
*   **Monitor Resources** — track memory and CPU during the run.
*   **Partition for Distributed Tests** — split data across load generators to balance load and avoid contention.
*   **Preprocess Data** — clean large datasets before the test (remove unneeded rows/columns).
*   **Avoid Network Shares** — place CSV files on each load generator's local disk.
*   **Handle EOF Gracefully** — configure EOF behavior for when the dataset is too small.

**Example Scenario:** To simulate 10,000 unique logins with a 10,000-row CSV, place the file on each load generator's local disk, configure CSV Data Set Config to consume one row per thread, and monitor resources. For distributed runs, split the file into chunks and assign each to a different server to avoid collisions and balance load.

### Troubleshooting Tips

*   **Slow Execution** — check for I/O bottlenecks or network latency with remote files.
*   **Out of Memory** — ensure scripts/beanshell are not loading the whole file into memory.
*   **Data Collisions** — confirm CSV settings and sufficient unique rows.

> [!WARNING]
> Animations of process-driven "streaming" aside, a beanshell/JSR223 processor that reads *the entire CSV into memory* bypasses JMeter's streaming and can blow the heap on large files. Let CSV Data Set Config do the reading.

[↑ Back to Table of Contents](#table-of-contents)

***

*When threads collide on shared data, test results become invalid — here's how to prevent it.*

## 7. Preventing Data Collision

**Data collision** occurs when multiple virtual users attempt to use the same data simultaneously, leading to duplicate entries, session overwrites, or failed transactions — and false test results.

### Potential Causes

*   **Insufficient Unique Data** — not enough unique rows for the users/iterations.
*   **Data Reuse Settings** — CSV config recycle mode or sharing mode misconfigured.
*   **Concurrent Access** — multiple threads reaching the same row at once.
*   **Improper Variable Scope** — user-specific variables shared unintentionally across threads.
*   **Distributed Testing Issues** — data files not partitioned or synchronized across load generators.

### How to Avoid It

*   **Ensure Sufficient Unique Data** — enough rows for all users and iterations.
*   **Configure CSV Properly** — "Recycle on EOF" = false and "Stop thread on EOF" = true for one-time usage; set **Sharing Mode** to *Current thread* to isolate data per user.
*   **Partition for Distributed Tests** — give each load generator a unique data subset.
*   **Isolate Variables** — use thread-local variables; avoid global/shared variables for user-specific data.
*   **Monitor for Collisions** — add assertions or checks to detect duplicate/reused data during execution.
*   **Document Data Usage** — comment your data-management strategy in the test plan.

> [!TIP]
> The **Sharing Mode** on CSV Data Set Config is `All threads` by default. Set it to **`Current thread`** (or `Current thread group`) so each virtual user walks through the file independently with its own pointer — the single most common fix for subtle cross-user data collisions.

**Example Scenario:** A registration workflow requires a unique email per user. If the CSV has fewer emails than users, or data is recycled, multiple users attempt to register with the same email. To prevent this, size the CSV to the user count, configure CSV Data Set Config for one-time usage (per-thread sharing), and monitor results for duplicate registration attempts.

[↑ Back to Table of Contents](#table-of-contents)
