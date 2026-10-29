# SQL Performance Fundamentals

## Table of Contents

* [1. High-Level Overview](#1-high-level-overview)
* [2. Query Execution Lifecycle](#2-query-execution-lifecycle)
* [3. Full Table Scan vs. Index Scan](#3-full-table-scan-vs-index-scan)
* [4. Joins vs. Subqueries Performance](#4-joins-vs-subqueries-performance)
* [5. Understanding Query Execution Plans](#5-understanding-query-execution-plans)
* [6. Identifying Slow Queries in Logs](#6-identifying-slow-queries-in-logs)

***

## 1. High-Level Overview

Understanding **SQL performance fundamentals** is crucial for diagnosing database bottlenecks and optimizing query efficiency during load testing. Well-designed queries and proper indexing can significantly improve application responsiveness and throughput.

This module traces a query from submission to result, contrasts **full table scans** with **index scans**, compares **joins** against **subqueries**, and explains how to read **execution plans** and mine **slow query logs** for optimization targets.

[↑ Back to Table of Contents](#table-of-contents)
***

*Let's start by tracing what happens to a query from the moment it is submitted.*

## 2. Query Execution Lifecycle

Every SQL statement passes through a predictable set of stages before returning results. Understanding these stages helps you know where optimization effort is best spent.

| Stage | Description |
| :--- | :--- |
| **Parsing** | Database parses the SQL, checks syntax, validates object references |
| **Optimization** | Query optimizer selects the most efficient execution plan |
| **Execution** | Database engine executes the plan, retrieving and processing data |
| **Result Return** | Results are sent back to the application or client |

> [!IMPORTANT]
> The **optimization** stage is where most performance is won or lost. The optimizer's choice of plan (index vs. scan, join order) determines how much work the execution stage must do. A missing index or stale statistics can force a poor plan.

**Example:** A SELECT query is parsed, optimized to use an index, executed, and the result set is returned to the user.

**Query Parsing Order of Operations:** SQL clauses are logically processed in a specific order, which differs from their written order:

```sql
FROM
WHERE
GROUP BY
HAVING
SELECT
WINDOW FUNCTION
ORDER BY
OFFSET / FETCH / LIMIT
```

[↑ Back to Table of Contents](#table-of-contents)
***

*The single biggest factor in query performance is how the database accesses rows. Let's compare the two fundamental access methods.*

## 3. Full Table Scan vs. Index Scan

How a database retrieves rows is the most consequential decision in query performance. The two extremes are scanning every row or using an index to jump directly to matches.

| Feature | Full Table Scan | Index Scan |
| :--- | :--- | :--- |
| **Rows Read** | Every row in the table | Only matching rows |
| **Resource Cost** | High (disk I/O, CPU) | Low |
| **Performance** | Slow on large tables | Fast, especially on large tables |
| **When Used** | No suitable index, or table is small | Suitable index exists |

*   **Full Table Scan:** The database reads every row in the table to find matching records. This is resource-intensive and slow for large tables, often causing high disk I/O and increased response times.
*   **Index:** An index is a data structure used by databases to quickly locate and access rows in a table based on the values of one or more columns. Indexes work like a book's table of contents, allowing the database to find data efficiently without scanning every row. Properly designed indexes greatly improve query performance, especially for large tables.
*   **Index Scan:** The database uses an index to quickly locate matching rows, reducing the number of reads and improving performance. Proper indexing is key to avoiding unnecessary full table scans.

> [!TIP]
> A full table scan is not always bad. On a small table, scanning every row may be faster than the overhead of using an index. The optimizer makes this call—but on large tables, an unexpected full scan is a red flag.

**Example:** Searching for a user by email in a table with an email index results in a fast index scan. Without the index, the database performs a full table scan, slowing down the query.

[↑ Back to Table of Contents](#table-of-contents)
***

*Beyond access method, the way you structure a query—as a join or a subquery—also affects performance.*

## 4. Joins vs. Subqueries Performance

**Joins** and **subqueries** are two ways to combine data from multiple tables, but they can differ significantly in performance.

| Feature | Joins | Subqueries |
| :--- | :--- | :--- |
| **Mechanism** | Combine rows from related tables | Nested queries that filter/aggregate |
| **Typical Performance** | More efficient with proper indexes | Can be less efficient if repeated |
| **Risk** | Complex joins increase CPU/memory | Correlated subqueries run per row |

*   **Joins:** Combine rows from two or more tables based on related columns. Joins are generally more efficient than subqueries, especially when proper indexes are present. However, complex joins can increase CPU and memory usage.
*   **Subqueries:** Nested queries used to filter or aggregate data. Subqueries can be less efficient, especially if executed repeatedly or if they return large result sets.

> [!WARNING]
> A **correlated subquery** runs the inner query once for *every row* of the outer query. This repeated work can devastate performance on large tables. Rewriting it as a join often yields a single-pass execution.

**Poorly optimized subquery** (runs the subquery for every row in the outer table, causing repeated work and slow performance):

```sql
SELECT o.id, o.amount
FROM orders o
WHERE o.customer_id IN (
  SELECT c.id
  FROM customers c
  WHERE c.status = 'active'
);
```

**Optimized join** (retrieves matching rows in a single pass, leveraging indexes and reducing redundant work):

```sql
SELECT o.id, o.amount
FROM orders o
INNER JOIN customers c ON o.customer_id = c.id
WHERE c.status = 'active';
```

[↑ Back to Table of Contents](#table-of-contents)
***

*To know which access method and join strategy the database actually chose, you must read the execution plan.*

## 5. Understanding Query Execution Plans

An **execution plan** is the database's detailed map of how it will execute a query. Reading it is the definitive way to identify inefficiencies.

*   **What It Shows:** Join methods, index usage, row access strategies, and estimated costs.
*   **Why It Matters:** It reveals *why* a query is slow—e.g., a missing index causing a full scan, or a poor join order.
*   **How to Use It:** Review the plan, spot the expensive operations, and adjust indexes or rewrite the query.

> [!IMPORTANT]
> The execution plan is the ground truth for query performance. If a query is slow, the plan tells you exactly where the time goes. Look for full table scans, missing index hints, and high-cost operations.

**Example:** An execution plan shows a sequential scan on a large table, indicating a missing index. Adding the index changes the plan to an index scan, improving performance.

[↑ Back to Table of Contents](#table-of-contents)
***

*Finally, let's see how to find problematic queries in the first place: the slow query log.*

## 6. Identifying Slow Queries in Logs

Databases typically log queries that exceed a configurable execution-time threshold. These **slow query logs** are the first place to look when diagnosing database bottlenecks.

*   **Slow Query Logs:** Databases often provide logs of queries that exceed a specified execution time threshold. Analyzing these logs helps pinpoint problematic queries and areas for optimization. The mechanism for this will differ between database management systems.
*   **Common Causes:** Slow queries are often caused by missing indexes, inefficient joins, large result sets, or poor query structure.

> [!TIP]
> Enable and tune the slow query log *before* load testing, not after. Set the threshold low enough to capture borderline queries, then use the log to build a prioritized list of queries to optimize.

**Example:** During a load test, the slow query log reveals several SELECT statements taking multiple seconds due to full table scans. Adding indexes and rewriting queries reduces execution times.

**Practical Tips:**
*   Use indexes to optimize query performance and avoid full table scans.
*   Review query execution plans regularly to identify bottlenecks and inefficiencies.
*   Monitor slow query logs to detect and address problematic SQL statements.
*   Optimize joins and subqueries for efficiency, especially in high-load scenarios.
*   Limit result set sizes and avoid unnecessary data retrieval.

**Example Scenario:** During a performance test, response times spike when a report query runs. Analysis of the execution plan shows a full table scan on a large transactions table. Adding an index on the relevant column and rewriting the query to use a JOIN reduces execution time from 10 seconds to under 1 second, improving overall system throughput.

[↑ Back to Table of Contents](#table-of-contents)
