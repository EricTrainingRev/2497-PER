# Threads & Concurrency

## Table of Contents

* [1. High-Level Overview](#1-high-level-overview)
* [2. Threads and Concurrency Fundamentals](#2-threads-and-concurrency-fundamentals)
* [3. Thread Pools](#3-thread-pools)
* [4. Blocking Behavior](#4-blocking-behavior)
* [5. Thread Exhaustion](#5-thread-exhaustion)
* [6. Context Switching](#6-context-switching)
* [7. Synchronous vs. Asynchronous Processing](#7-synchronous-vs-asynchronous-processing)

***

## 1. High-Level Overview

**Threads** and **concurrency** are central to how modern applications handle load. A thread is the smallest unit of execution within a process, and how threads are created, pooled, blocked, and switched directly determines an application's throughput and responsiveness under stress.

This module explores the fundamentals of threading and concurrency, then examines the specific failure modes—**blocking**, **thread exhaustion**, and excessive **context switching**—that surface during load testing, and finally contrasts **synchronous** and **asynchronous** processing models.

[↑ Back to Table of Contents](#table-of-contents)
***

*Let's start by establishing the core concepts of threads and concurrency.*

## 2. Threads and Concurrency Fundamentals

*   **Threads:** A thread is the smallest unit of execution within a process. Each thread runs independently but shares the same memory and resources as other threads in the same process. Threads enable concurrent execution of tasks, allowing programs to perform multiple operations at once (e.g., handling user input while processing data in the background). Effective use of threads can improve application responsiveness and throughput, but poor management can lead to issues like contention, deadlocks, or excessive context switching.
    *   **Contention:** Contention occurs when multiple threads compete for the same shared resource, such as a lock, CPU, or memory. High contention can lead to threads waiting or blocking, reducing overall system throughput and increasing response times. Effective synchronization and minimizing shared resource usage help reduce contention.
    *   **Deadlock:** A deadlock occurs when two or more threads are each waiting for resources held by the other, causing all of them to be stuck indefinitely. This situation typically arises from improper locking order or resource acquisition patterns. Deadlocks can halt progress in part of an application and are often difficult to detect and resolve.
*   **Concurrency:** Concurrency is the ability of a system to manage multiple tasks at the same time, even if they are not literally running simultaneously. It involves structuring programs so that many operations can be in progress, with the system switching between them as needed. Concurrency can be achieved with threads, event loops, or other mechanisms, and is about organizing work to maximize responsiveness and resource utilization.

> [!IMPORTANT]
> **Concurrency is not parallelism.** Concurrency is about *handling* many tasks at once (interleaving them), whereas parallelism is about *executing* tasks at the same time on multiple cores. A single-core system can be concurrent but not parallel.

[↑ Back to Table of Contents](#table-of-contents)
***

*With the fundamentals established, let's look at how threads are managed at scale: thread pools.*

## 3. Thread Pools

A **thread pool** is a collection of pre-instantiated, reusable threads managed by the system or application. Instead of creating a new thread for each task, tasks are assigned to available threads in the pool, reducing overhead and improving scalability.

*   **Reduced Overhead:** Creating and destroying threads is expensive. Reusing threads avoids this cost.
*   **Bounded Resources:** A fixed pool size caps the number of concurrent threads, preventing resource exhaustion.
*   **Queuing:** When all pool threads are busy, new tasks wait in a queue until a thread frees up.

> [!TIP]
> A thread pool's size is a key tuning lever. Too small and tasks queue up; too large and you risk excessive context switching and memory pressure. The "right" size depends on whether tasks are CPU-bound or I/O-bound.

**Example:** A web server uses a thread pool to handle incoming HTTP requests, reusing threads for each new connection instead of creating and destroying threads repeatedly.

[↑ Back to Table of Contents](#table-of-contents)
***

*Thread pools are only effective if threads are doing useful work. Let's examine what happens when they are not.*

## 4. Blocking Behavior

**Blocking** occurs when a thread waits for a resource or event (such as I/O completion, a lock, or data availability) before it can proceed. Excessive blocking can reduce system throughput and increase response times, as threads spend time idle rather than doing useful work.

*   **I/O Blocking:** A thread waits for a disk, network, or database operation to complete.
*   **Lock Blocking:** A thread waits to acquire a lock held by another thread (contention).
*   **Impact:** Blocked threads occupy pool slots without producing output, effectively reducing the pool's capacity.

> [!WARNING]
> Blocking is the root cause of many load-test failures. A pool full of threads *waiting on slow I/O* looks "busy" but is actually doing nothing useful—and new requests pile up behind them.

**Example:** A thread waiting for a database query to complete is blocked and cannot process other tasks until the query returns.

[↑ Back to Table of Contents](#table-of-contents)
***

*When blocking is severe, the pool can run out of available threads entirely. This is thread exhaustion.*

## 5. Thread Exhaustion

**Thread exhaustion** happens when all threads in a pool are busy or blocked, and no threads are available to handle new tasks. This can lead to request queuing, increased latency, or even application failures if the queue overflows.

*   **Cause:** Usually a combination of a fixed pool size and threads blocked on slow dependencies (database, external APIs).
*   **Symptoms:** New requests wait in a queue, response times climb sharply, and eventually requests may be rejected or time out.
*   **Detection:** Rising queue depth, thread-pool saturation metrics, and a sharp latency cliff.

> [!IMPORTANT]
> Thread exhaustion often presents as a **sudden** degradation rather than a gradual one. The system handles load fine until the pool fills, then response times spike dramatically. This "cliff" is a hallmark of pool exhaustion.

**Example:** During a load test, a fixed-size thread pool becomes fully occupied, causing new requests to wait in a queue or be rejected.

[↑ Back to Table of Contents](#table-of-contents)
***

*Even when threads are not blocked, the act of switching between them carries a cost.*

## 6. Context Switching

**Context switching** is the process by which the operating system saves the state of a currently running thread and loads the state of another thread. While necessary for multitasking, frequent context switches add overhead and can degrade performance, especially if there are many threads competing for CPU time.

*   **Overhead:** Each switch requires saving and restoring thread state, consuming CPU cycles that could otherwise do useful work.
*   **Trigger:** A large number of active threads competing for limited CPU cores.
*   **Signal:** High context-switch rates combined with high CPU usage and reduced efficiency.

> [!TIP]
> If an application has hundreds of active threads but CPU efficiency is poor, excessive context switching is a likely culprit. Reducing thread count (e.g., via a smaller pool or async processing) often improves throughput.

**Example:** An application with hundreds of active threads may experience high context switch rates, leading to increased CPU usage and reduced efficiency.

[↑ Back to Table of Contents](#table-of-contents)
***

*Finally, let's compare the two processing models that determine how threads are used: synchronous and asynchronous.*

## 7. Synchronous vs. Asynchronous Processing

The choice between **synchronous** and **asynchronous** processing fundamentally shapes how threads are utilized and how the system behaves under load.

| Feature | Synchronous | Asynchronous |
| :--- | :--- | :--- |
| **Execution** | Tasks run one after another | Tasks initiated, then completed independently |
| **Thread Behavior** | Thread waits for each operation | Thread starts a task and moves on |
| **Resource Utilization** | Under-utilized (threads idle while waiting) | High (threads stay busy) |
| **Best For** | Simple, CPU-bound, low-concurrency logic | I/O-bound operations (DB, network, files) |

*   **Synchronous Processing:** Tasks are performed one after another; a thread waits for each operation to complete before moving to the next. This can lead to blocking and under-utilization of resources.
*   **Asynchronous Processing:** Tasks can be initiated and then completed independently; a thread can start a task and move on to other work while waiting for the result. Asynchronous processing improves resource utilization and responsiveness, especially for I/O-bound operations.

> [!IMPORTANT]
> Asynchronous processing is the standard remedy for **I/O-bound** bottlenecks. When threads are blocked waiting on slow database or network calls, switching to async lets a single thread handle many in-flight requests, dramatically increasing throughput without adding threads.

**Example:** In a synchronous model, a web server thread waits for a file read to finish before handling another request. In an asynchronous model, the thread initiates the file read and immediately processes other requests, handling the file result when it becomes available.

**Example Scenario:** During a stress test, a server with a fixed thread pool size of 50 becomes saturated as all threads are blocked waiting for slow database responses. New incoming requests are queued, causing response times to increase sharply. Switching to asynchronous database calls allows threads to handle more requests concurrently, reducing wait times and improving overall throughput.

[↑ Back to Table of Contents](#table-of-contents)
