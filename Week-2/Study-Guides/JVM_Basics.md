# JVM Basics

## Table of Contents

* [1. High-Level Overview](#1-high-level-overview)
* [2. Heap vs. Stack](#2-heap-vs-stack)
* [3. GC Fundamentals](#3-gc-fundamentals)
* [4. JVM Memory Regions](#4-jvm-memory-regions)
* [5. Object Lifecycle in JVM](#5-object-lifecycle-in-jvm)

***

## 1. High-Level Overview

The **Java Virtual Machine (JVM)** manages memory and execution for Java applications. Understanding how the JVM allocates, uses, and reclaims memory is essential for diagnosing performance issues during load testing—particularly **garbage collection (GC) pauses**, **heap pressure**, and **memory leaks**.

This module covers the two primary memory areas (**heap** and **stack**), the fundamentals of **garbage collection**, the distinct **memory regions** within the JVM, and the **lifecycle of objects** from creation to collection.

[↑ Back to Table of Contents](#table-of-contents)
***

*Let's begin with the two fundamental memory areas every Java developer must understand: the heap and the stack.*

## 2. Heap vs. Stack

The JVM divides memory into two primary areas with very different characteristics and purposes.

| Feature | Heap | Stack |
| :--- | :--- | :--- |
| **Contents** | Class instances and arrays | Method call frames, local variables |
| **Sharing** | Shared among all threads | One per thread |
| **Size** | Large | Small |
| **Managed By** | Garbage collector | Automatically on method exit |
| **Failure Mode** | `OutOfMemoryError` | `StackOverflowError` |

*   **Heap:** The heap is the runtime memory area from which all class instances and arrays are allocated. It is shared among all threads. The heap is managed by the JVM's garbage collector, which reclaims memory from objects that are no longer referenced. High heap usage can lead to increased garbage collection activity, which may cause application pauses (GC pauses) and impact response times.
*   **Stack:** Each thread in a Java application has its own stack, which stores method call frames, local variables, and partial results. The stack is much smaller than the heap and is used for short-lived data. Stack overflows occur when too many nested method calls exhaust the stack space, resulting in a `StackOverflowError`.

> [!WARNING]
> A `StackOverflowError` is almost always a **code defect** (e.g., unbounded recursion), not a load problem. An `OutOfMemoryError` on the heap, by contrast, is frequently a load-test finding—either a genuine leak or simply too much live data for the configured heap size.

**Example:** A web application with many active user sessions may see heap usage grow as more objects are created. If the heap becomes full, the JVM triggers garbage collection to free up space, potentially causing noticeable pauses. A recursive function without a proper base case can cause a stack overflow by consuming all available stack space.

[↑ Back to Table of Contents](#table-of-contents)
***

*With the memory areas defined, let's examine the mechanism that keeps the heap from overflowing: garbage collection.*

## 3. GC Fundamentals

**Garbage Collection (GC)** is the process by which the JVM automatically identifies and reclaims memory occupied by objects that are no longer in use. Different GC algorithms offer trade-offs between throughput, pause times, and resource usage.

*   **GC Algorithms:** Common collectors include **Serial**, **Parallel**, **G1**, and **Z**. Each balances throughput (work done) against pause times (application stalls).
*   **GC Pauses:** During GC, application threads may be paused (stop-the-world events), leading to increased response times or latency spikes. Frequent or long GC pauses can degrade application performance, especially under heavy load.
*   **Tuning GC:** JVM parameters (e.g., heap size, GC algorithm) can be tuned to balance throughput and pause times based on application needs.

| GC Algorithm | Primary Trade-off | Typical Use |
| :--- | :--- | :--- |
| **Serial** | Single-threaded, long pauses | Small, single-core apps |
| **Parallel** | High throughput, longer pauses | Batch/throughput-oriented workloads |
| **G1** | Balanced throughput and pause times | Default for most server apps |
| **Z** | Very low pause times | Large heaps, latency-sensitive apps |

> [!IMPORTANT]
> Frequent **full GCs** during a load test are a strong signal of heap pressure—either the heap is too small for the workload or objects are being retained longer than necessary (a potential leak). GC pause spikes often correlate directly with response-time spikes.

**Example:** During a load test, frequent full GCs cause noticeable pauses in request processing, indicating the need for heap or GC tuning.

[↑ Back to Table of Contents](#table-of-contents)
***

*To understand why GC behaves the way it does, we need to look at how the heap is structured into regions.*

## 4. JVM Memory Regions

The JVM heap is not a single undifferentiated block. It is divided into regions based on object age, which allows the collector to optimize for the fact that most objects die young.

| Region | Purpose | Collection Type |
| :--- | :--- | :--- |
| **Young Generation** | Where new objects are allocated | Minor GC (fast, frequent) |
| **Old Generation** | Long-lived objects promoted from young | Full GC (expensive, infrequent) |
| **Metaspace** | Class metadata and static info (native memory) | Grows as needed |

*   **Young Generation (Short Lived):** Where new objects are allocated. Most objects die young and are quickly collected in minor GCs. The young generation is further divided into **Eden** and **Survivor** spaces.
*   **Old Generation (Long Lived):** Objects that survive multiple GCs in the young generation are promoted to the old generation. Full GCs clean up this region and are typically more expensive.
*   **Metaspace (Java 8+):** Stores class metadata and static information. Metaspace is allocated in **native memory**, not the heap, and can grow as needed (limited by system memory or JVM settings).

> [!WARNING]
> **Metaspace is not part of the heap.** A large application with many classes or dynamic class loading may see high Metaspace usage, potentially leading to `OutOfMemoryError: Metaspace` if not sized appropriately. This error is distinct from a heap `OutOfMemoryError` and requires a different fix.

**Example:** A large application with many classes or dynamic class loading may see high Metaspace usage, potentially leading to `OutOfMemoryError: Metaspace` if not sized appropriately.

[↑ Back to Table of Contents](#table-of-contents)
***

*Now let's trace the full journey of an object from creation to collection.*

## 5. Object Lifecycle in JVM

Every object in the JVM follows a predictable lifecycle, from allocation to eventual reclamation by the garbage collector.

| Stage | Description |
| :--- | :--- |
| **Creation** | Object allocated on the heap via `new` or similar |
| **Usage** | Object referenced and manipulated by application code |
| **Garbage Collection** | Object becomes eligible when no references remain |
| **Finalization (Deprecated)** | `finalize()` cleanup, now deprecated |

*   **Creation:** Objects are created on the heap using the `new` keyword or similar mechanisms.
*   **Usage:** Objects are referenced and manipulated by application code. As long as references exist, the object remains reachable.
*   **Garbage Collection:** When no references to an object remain, it becomes eligible for garbage collection. The exact time of collection is determined by the JVM.
*   **Finalization (Deprecated):** The `finalize()` method was once used for cleanup before object removal, but is now deprecated in favor of more reliable resource management techniques (e.g., try-with-resources, explicit close methods).

> [!IMPORTANT]
> An object is only collectable when it is **unreachable**—no live references point to it. A memory leak occurs when references are retained unintentionally (e.g., a cache or session map that never evicts), keeping objects reachable and preventing collection. This is why leaks manifest as steadily growing heap usage.

**Example:** A cache object is created and used during a request. Once the request completes and no references remain, the cache object is eventually collected by the JVM.

**Example Scenario:** During a load test, heap usage steadily increases and frequent full GCs occur, causing response times to spike. Analysis reveals a memory leak in session management code, leading to objects being retained longer than necessary. After fixing the leak and tuning the heap size, GC pauses are reduced and application throughput improves.

[↑ Back to Table of Contents](#table-of-contents)
