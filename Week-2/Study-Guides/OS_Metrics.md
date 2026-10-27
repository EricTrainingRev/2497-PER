# OS Metrics

## Table of Contents

* [1. High-Level Overview](#1-high-level-overview)
* [2. Key OS Metrics](#2-key-os-metrics)
    * [2.1 CPU Utilization](#21-cpu-utilization)
    * [2.2 Memory Usage](#22-memory-usage)
    * [2.3 Disk I/O](#23-disk-io)
    * [2.4 Network Utilization](#24-network-utilization)
* [3. CPU Utilization vs. CPU Load Average](#3-cpu-utilization-vs-cpu-load-average)
    * [3.1 Virtualized CPU Cores](#31-virtualized-cpu-cores)
* [4. Memory Consumption vs. Memory Leak Indicators](#4-memory-consumption-vs-memory-leak-indicators)
* [5. Saturation Patterns](#5-saturation-patterns)
* [6. Monitoring Tools Overview](#6-monitoring-tools-overview)

***

## 1. High-Level Overview

Understanding **operating system (OS) metrics** is fundamental for diagnosing system performance and identifying bottlenecks during load testing. These metrics provide insight into how system resources are being utilized and where potential issues may arise.

By monitoring the four core resource families—**CPU**, **memory**, **disk**, and **network**—you can determine whether a system is healthy, approaching saturation, or already degraded. The goal is to correlate these metrics with observed symptoms (e.g., rising response times) to pinpoint the true bottleneck.

[↑ Back to Table of Contents](#table-of-contents)
***

*Now that we have established the high-level context, let's dive into the four core resource families that every load test should track.*

## 2. Key OS Metrics

The four primary OS resource categories form the foundation of any performance investigation. Each behaves differently under load and signals distinct failure modes.

| Resource | What It Measures | Saturation Signal |
| :--- | :--- | :--- |
| **CPU** | Percentage of processing capacity in use | Consistently high utilization (>85%) |
| **Memory** | Amount of RAM in use by system and applications | Swapping/paging, memory pressure |
| **Disk** | Rate of read/write operations | High queue length, ~100% utilization |
| **Network** | Data transmitted/received over interfaces | Packet loss, retransmissions, latency |

### 2.1 CPU Utilization

**CPU Utilization** is the percentage of CPU capacity currently in use. High CPU utilization may indicate that the system is processing a large number of tasks, but consistently high values (e.g., >85%) can signal **CPU saturation** and potential performance degradation.

> [!IMPORTANT]
> A single high CPU reading is not necessarily a problem. It is the *consistency* of high utilization combined with rising response times that indicates saturation. A system running at 90% CPU with stable response times may simply be well-utilized.

### 2.2 Memory Usage

**Memory Usage** is the amount of RAM currently in use by the system and applications. High memory usage can lead to **swapping or paging**, which significantly slows down performance.

*   **Swapping and Paging:** When available physical memory is exhausted, the operating system moves data to and from disk (swap space or page file) to free up RAM. This process is much slower than accessing RAM directly and can cause severe performance degradation, often observed as increased disk activity and high I/O wait times.
*   **Memory Pressure:** Occurs when the system is consistently close to full memory utilization, forcing the OS to aggressively reclaim or swap memory. Symptoms include slow application response, increased latency, and sometimes application crashes if memory cannot be allocated.
*   **Memory Leak Indicators:** A gradual increase in memory usage over time without release, often leading to eventual exhaustion and system instability. Leaks are typically caused by applications not freeing memory after use.

> [!WARNING]
> A steadily growing memory footprint that never decreases during a load test is a classic **memory leak** signature. If memory climbs to near-full and triggers swapping, response times degrade for *all* users, not just the ones driving the leak.

### 2.3 Disk I/O

**Disk I/O** measures the rate of read/write operations to disk. High disk I/O can become a bottleneck, especially if the disk is not able to keep up with demand, leading to increased response times.

*   **Saturation:** Occurs when the disk subsystem cannot process incoming read/write requests as quickly as they arrive, resulting in a backlog (queue) of pending operations. This is often observed as high disk queue lengths, increased wait times, and consistently high disk utilization (close to 100%).
*   **Impact:** When saturation is present, even small increases in workload can cause disproportionate increases in response time and system latency. Monitoring tools may show disk latency spikes and long I/O wait times as key indicators.

### 2.4 Network Utilization

**Network Utilization** is the amount of data being transmitted and received over network interfaces. High network usage can cause **packet loss**, **retransmissions**, and increased **latency**.

> [!TIP]
> Network saturation is often the hidden bottleneck in distributed systems. When response times rise but CPU, memory, and disk all look healthy, check the network interfaces for saturation before assuming the problem is in the application.

[↑ Back to Table of Contents](#table-of-contents)
***

*With the four resource families covered, let's clarify two pairs of metrics that are frequently confused: CPU utilization vs. load average, and memory consumption vs. leak indicators.*

## 3. CPU Utilization vs. CPU Load Average

**CPU Utilization** and **CPU Load Average** are related but distinct metrics. Confusing them is a common source of misdiagnosis.

| Feature | CPU Utilization | CPU Load Average |
| :--- | :--- | :--- |
| **What It Measures** | Percentage of CPU capacity in use | Number of processes waiting to be executed |
| **Timeframe** | Instantaneous snapshot | Averaged over 1, 5, 15 minutes |
| **Interpretation** | High value = CPU busy | Value > core count = overloaded |
| **Best For** | Spotting immediate saturation | Spotting sustained overload trends |

*   **CPU Utilization:** The percentage of CPU capacity currently in use. High utilization may indicate heavy processing, but consistently high values (>85%) can signal saturation.
*   **CPU Load Average:** Represents the average number of processes waiting to be executed over a period of time (e.g., 1, 5, 15 minutes). A load average higher than the number of CPU cores suggests the system is overloaded.
*   **CPU Cores:** A core is an individual processing unit within a CPU capable of executing its own thread of instructions. The total number of cores determines how many processes or threads can be executed simultaneously without waiting.

> [!IMPORTANT]
> **Load average must be compared to core count, not to a fixed number.** A load average of 6.0 is fine on a 16-core machine but indicates overload on a 4-core machine. Always normalize load average against the number of cores — and in virtualized environments, against the **allocated vCPU count** (see 3.1).

**Example:** On a 4-core system, a load average of 6.0 means there are more processes waiting than the CPU can handle efficiently.

### 3.1 Virtualized CPU Cores

In virtualized and cloud environments, the cores an OS sees are not necessarily physical cores. This changes how you interpret both utilization and load average.

| Concept | Meaning | Performance Implication |
| :--- | :--- | :--- |
| **vCPU** | A virtual CPU allocated to a VM/container | Maps to a physical core or hyperthread on the host; the guest counts vCPUs as "cores" |
| **Hyperthreading** | 1 physical core exposes 2 logical threads | Load average should be compared to *logical* threads, not physical cores |
| **CPU Steal Time** | Time the guest waits for the hypervisor to schedule it on a physical core | High steal = host contention; the guest's CPU looks busy but is actually waiting |
| **Noisy Neighbor** | Other VMs/containers on the same host consuming CPU | Causes steal time and unpredictable latency you cannot see from inside the guest |

> [!WARNING]
> **Load average must be compared to the *allocated* vCPU count, not the host's physical cores.** A 4-vCPU VM on a busy host can show a load average of 6.0 even though the host has 32 physical cores — the guest is overloaded relative to its allocation. Always check **CPU steal time** (`%st` in `top`/`vmstat`) when load is high but the guest's own CPU looks under-utilized; high steal points to host contention, not an application problem.

[↑ Back to Table of Contents](#table-of-contents)
***

## 4. Memory Consumption vs. Memory Leak Indicators

**Memory Consumption** describes how much memory a system is currently using, while **Memory Leak Indicators** describe the *trend* of that consumption over time. The distinction matters because a high-but-stable memory footprint is very different from a steadily growing one.

| Feature | Memory Consumption | Memory Leak Indicator |
| :--- | :--- | :--- |
| **What It Measures** | Current RAM in use | Change in memory usage over time |
| **Snapshot vs. Trend** | Snapshot at a point in time | Trend across the test duration |
| **Healthy Sign** | High but stable | Flat or returning to baseline |
| **Problem Sign** | Near-full (triggers swapping) | Steady, unreleased growth |

*   **Memory Consumption:** The amount of RAM currently in use. High consumption is only a problem when it approaches capacity and forces swapping.
*   **Memory Leak Indicators:** A gradual increase in memory usage over time without release, often leading to eventual exhaustion and system instability. Leaks are typically caused by applications not freeing memory after use.

> [!WARNING]
> A **soak test** (sustained load over hours) is the definitive way to expose memory leaks. A short spike test may never reveal a slow leak, because the memory footprint has not had time to grow noticeably.

**Example:** A web server's memory usage grows steadily during a load test, never decreasing, indicating a possible memory leak. In another scenario, a spike in active users causes memory usage to reach 95%, triggering swapping and resulting in much slower response times for all users.

[↑ Back to Table of Contents](#table-of-contents)
***

*Now that we understand the individual metrics, let's examine the pattern that ties them together: saturation.*

## 5. Saturation Patterns

**Saturation** occurs when a resource cannot process incoming requests as quickly as they arrive, resulting in a backlog of pending work. It is the point at which a system transitions from healthy to degraded.

*   **Backlog Formation:** Pending operations queue up because the resource cannot keep up with demand.
*   **Disproportionate Impact:** Once saturated, even small increases in workload cause large increases in response time and latency.
*   **Resource-Specific Signals:**
    *   **CPU:** Consistently high utilization (>85%) with rising response times.
    *   **Memory:** Swapping/paging activity as physical RAM is exhausted.
    *   **Disk:** High queue lengths, ~100% utilization, latency spikes.
    *   **Network:** Packet loss, retransmissions, timeouts.

> [!IMPORTANT]
> Saturation is a **threshold phenomenon**. Before the threshold, adding load increases throughput proportionally. After the threshold, throughput plateaus (or drops) while response time climbs sharply. Identifying this "knee" in the curve is a primary goal of load testing.

**Example:** During a database load test, disk utilization reaches 100%, the average disk queue length grows, and response times spike as the disk cannot keep up with the volume of requests.

[↑ Back to Table of Contents](#table-of-contents)
***

*Finally, let's review the tools used to observe all of these metrics in practice.*

## 6. Monitoring Tools Overview

A variety of tools exist to observe OS metrics, ranging from simple graphical dashboards to advanced command-line utilities.

| Tool | Platform | Primary Use |
| :--- | :--- | :--- |
| **Task Manager** | Windows | Graphical overview of CPU, memory, disk, and network |
| **top / htop** | Linux | Real-time command-line monitoring of resource usage |
| **perfmon** | Windows | Advanced tracking of detailed performance counters |
| **iostat** | Linux | Disk I/O statistics |
| **vmstat** | Linux | Memory, CPU, and process statistics |
| **sar** | Linux | Historical system activity reporting |

> [!NOTE]
> In virtualized environments, `top` and `vmstat` also expose **CPU steal time** (`%st` / `st`). A non-zero steal value means the host is contending for CPU on your behalf — see [3.1 Virtualized CPU Cores](#31-virtualized-cpu-cores).

> [!TIP]
> For load testing, prefer tools that can **log metrics over time** (e.g., `sar`, `perfmon` counters) rather than only showing a live snapshot. A live view tells you the current state; a time series tells you *when* and *how* the system degraded.

**Example Scenario:** During a load test, CPU utilization averages 70%, but memory usage steadily increases, eventually causing the system to swap and response times to degrade. Disk I/O remains low, indicating the bottleneck is memory, not CPU or disk.

[↑ Back to Table of Contents](#table-of-contents)
