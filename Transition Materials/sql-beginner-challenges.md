# SQL Beginner Challenges (Chinook Database)

Welcome! This is a **learning playground**, not a test. Each challenge is a
small, self-contained query you'll build one step at a time. They're ordered
from easiest to hardest, and each one layers a new SQL idea on top of the last —
so do them in order, take your time, and expect to get stuck. Getting stuck is
where the learning happens.

All of these run against the **Chinook** database: a fake digital music store
with artists, albums, tracks, customers, and invoices. The same database the
setup script builds. If you can query Chinook, you can query a real business —
the tables are shaped just like one.

## The database in one glance

Before you write anything, get comfortable with what you'll be querying. Eleven
tables, four you'll lean on constantly:

- `Artist` — the bands and solo artists (`ArtistId`, `Name`).
- `Album` — each release, pointing at its `Artist`.
- `Track` — each song, pointing at its `Album` (`Name`, `Milliseconds`,
  `Composer`, `UnitPrice`, ...).
- `Genre` — rock, jazz, metal, and friends (`GenreId`, `Name`).
- `Customer` — the people buying music (`FirstName`, `LastName`, `Country`, ...).
- `Invoice` — a purchase (`CustomerId`, `BillingCountry`, `Total`).
- `InvoiceLine` — one line of an invoice: a single track sold (`TrackId`,
  `UnitPrice`, `Quantity`).
- Plus `Employee`, `MediaType`, `Playlist`, `PlaylistTrack` for later.

Don't memorize this. You'll absorb it by querying it. When you're unsure what a
table contains, run `SELECT * FROM <table> LIMIT 5;` and look.

## How to use these challenges

- This document **assumes the database is already loaded** and you have a way
  to run queries — the `sqlite3` command line, or a GUI like DB Browser for
  SQLite / DBeaver. (If your tool has a *multiple statements* or *run selected
  query* mode, use it — see the note below.)
- Write each query, **run it, don't just write it.** Read the output. Does it
  look right? Fewer rows than you expected? Duplicates? Good — that's exactly
  what you're learning to notice.
- **Use the hints progressively.** Each challenge has hints that get more
  specific. Try it on your own first. Stuck for a while? Read *Hint 1*. Still
  stuck? *Hint 2* narrows it further. The point is to push yourself a little
  further each time, not to peek immediately.
- The **Stretch** line at the end is optional. Only attempt it once the base
  version works — it's a tougher, more satisfying version of the same idea.
- **Break it on purpose.** Drop the `WHERE` clause and run `SELECT *` — the
  whole table floods in, right? Understanding why shape changed *is* learning.

> [!NOTE] **One tip from the start:** many beginners reach for
> `SELECT *` and write everything in one giant query. Resist. Most challenges
> are best solved in pieces — run a small query to *see* the data, then build
> up. There's no prize for writing it all at once.

---

## Table of Contents

1. [Hello, Chinook!](#1-hello-chinook)
2. [Filter the Music](#2-filter-the-music)
3. [Sort It Out](#3-sort-it-out)
4. [Counting Tunes](#4-counting-tunes)
5. [Genres on the Billboard](#5-genres-on-the-billboard)
6. [Connecting the Dots](#6-connecting-the-dots)
7. [Three Degrees of Separation](#7-three-degrees-of-separation)
8. [The Sales Floor](#8-the-sales-floor)
9. [Follow the Money](#9-follow-the-money)
10. [One of Each Kind](#10-one-of-each-kind)

---

## 1. Hello, Chinook!

Your very first query. Ask the database for the names of all the artists. It
feels trivial — and it is! — but it teaches you the machinery every query on
this list depends on: choosing a table, picking columns, and running the
thing.

### The task

- Show the `Name` of **every** artist in the `Artist` table.
- One row per artist, no extras.

### Getting started

- The most basic shape of any query is
  `SELECT <columns> FROM <table>;`.
- The `Artist` table has two columns: `ArtistId` and `Name`. You want `Name`.
- End your query with a semicolon (`;`) — it's how the database knows your
  statement is finished.

### Hints

> [!TIP] **Hint 1** — To see everything a table holds, `SELECT *` grabs every
> column: `SELECT * FROM Artist;`. Run that first — you're *looking*, not
> writing the answer yet.

> [!NOTE] **Hint 2** — Now narrow it to just the name:
> `SELECT Name FROM Artist;`. Compare the two outputs and note what changed.

> [!WARNING] **Hint 3** — If your tool reports a "near ... syntax error",
> it's almost always a missing semicolon, a typo like `FROMM`, or a table name
> that isn't matching. Check those three before anything else.

### You'll learn here

- The `SELECT ... FROM ...` shape and when you need the `;`.
- Picking a single column versus `*`.
- Running a query and reading its output.

### Stretch challenge

Add the numbers: `SELECT ArtistId, Name FROM Artist;`. You now have a two-column
result — the beginning of thinking about results as **rows** with **fields**.

[↑ Back to Table of Contents](#table-of-contents)

---

## 2. Filter the Music

Now you'll stop grabbing *everything* and start asking for just what you want.
The whole point of a database is returning the *right* rows, and that power
comes from `WHERE`. You'll find all the tracks shorter than two minutes.

### The task

- Find every track whose length is **under two minutes**.
- Show its `Name` and its length.

### Getting started

- The `Track` table stores length in a column called `Milliseconds` (it's an
  integer — the number of milliseconds, so a 3-minute song is about `180000`).
- `WHERE` goes right after `FROM`. It keeps only rows that match a condition.
- Two minutes = 120,000 milliseconds. "Under" means `<`.

> [!IMPORTANT] **The trap is mixing up your units.** `Milliseconds` is already
> in milliseconds, so "two minutes" is `120000` — not `2`. Getting the units
> wrong quietly returns either nothing or the whole table, and the query "runs
> fine" either way. Always sanity-check against the data.

### Hints

> [!TIP] **Hint 1** — The condition goes in a `WHERE` clause after the table:
> `SELECT Name, Milliseconds FROM Track WHERE Milliseconds < 120000;`

> [!NOTE] **Hint 2** — Upside down is a great sanity check: run
> `... WHERE Milliseconds > 590000;` (any track longer than ~10 minutes). If
> that returns a handful of long songs, your `Track` table and the `Milliseconds`
> column are behaving — so your short-song query is probably right too.

### You'll learn here

- The `WHERE` clause and comparison operators (`<`, `>`, `=`, `>=`, `<=`).
- Selecting multiple columns in one query.
- Checking units against real data.

### Stretch challenge

Combine two conditions with `AND` to find tracks **between 1 and 2 minutes**
long (`Milliseconds >= 60000 AND Milliseconds < 120000`).

[↑ Back to Table of Contents](#table-of-contents)

---

## 3. Sort It Out

Two tiny tools that make results readable: ordering (`ORDER BY`) and pattern
matching (`LIKE`). Together they'll turn raw rows into something you can
actually scan. You'll put both to work.

### The task

- List all albums, ordered alphabetically by `Title`.
- Find the artists whose names **start with** the letter `B`.

### Getting started

- `ORDER BY` sorts results, and goes **last** in a query — after `WHERE`.
- `LIKE` does fuzzy text matching. `%` is a wildcard meaning "anything here,"
  so `'B%'` means "starts with B," and `'%a%'` means "contains an a."
- You'll use `LIKE` in a `WHERE` clause: `WHERE Name LIKE 'B%'`.

### Hints

> [!TIP] **Hint 1** — Sort the albums: `SELECT Title FROM Album
> ORDER BY Title;`. That's it — alphabetical comes free.

> [!NOTE] **Hint 2** — For the artists: `SELECT Name FROM Artist
> WHERE Name LIKE 'B%';`. The `%` matches any number of characters after the B.

> [!WARNING] **Hint 3** — `%` is a **wildcard**, the `%` character means
> "any run of characters" — not "the letter at a position." `'B%'` can't become
> `'B' || '%'`; the whole pattern `B%` is one unit. If you're not getting hits,
> check your quotes — strings go in single quotes, `'B%'`, not double.

### You'll learn here

- `ORDER BY` for sorting, and `ASC`/`DESC` (ascending/descending) directions.
- `LIKE` wildcard matching with `%`.
- Where each clause sits in the query order: `SELECT → FROM → WHERE → ORDER BY`.

### Stretch challenge

Flip the sort (`ORDER BY Title DESC`) — albums Z to A. Then try a wider search:
find every **track** whose name contains `love` anywhere,
`WHERE Name LIKE '%love%'`.

[↑ Back to Table of Contents](#table-of-contents)

---

## 4. Counting Tunes

Stop fetching rows — start answering questions *about* them. Aggregate
functions like `COUNT`, `AVG`, and `SUM` collapse many rows into one number.
This is the shift that turns SQL from "show me the list" into "tell me the
answer."

### The task

- How many tracks are in the database?
- What's the **average** track length, in **seconds** (not milliseconds)?

### Getting started

- `COUNT(*)` counts rows. `COUNT(column)` counts non-`NULL` values in that
  column.
- Aggregates go in the `SELECT` list in place of column names.
- To convert milliseconds to seconds, divide by `1000.0`. Note the `.0` — it
  matters (see below).
- Give results readable names with an alias: `... AS avg_seconds`.

> [!IMPORTANT] **Keep an eye on integer division.** `Milliseconds / 1000`
> divides two integers, and SQL will happily chop off the fraction — so
> `60999 / 1000` is `60`, not `60.999`. Write `/ 1000.0` to force a decimal
> and keep the precision. This exact gotcha costs people a lot of head-scratching.

### Hints

> [!TIP] **Hint 1** — `SELECT COUNT(*) FROM Track;` gives you the first answer
> on its own.

> [!NOTE] **Hint 2** — The average in seconds, aliased:
> `SELECT AVG(Milliseconds) / 1000.0 AS avg_seconds FROM Track;`

> [!NOTE] **Hint 3** — A nice touch: `ROUND(AVG(Milliseconds) / 1000.0, 1)`
> rounds to one decimal place so the answer isn't a wall of digits.

### You'll learn here

- Aggregates: `COUNT`, `AVG`, `SUM`.
- Column aliases with `AS`.
- `ROUND` for tidy output, and the integer-division gotcha.

### Stretch challenge

`COUNT(DISTINCT column)` counts distinct values. Find how many **different**
genres appear in the `Track` table:
`SELECT COUNT(DISTINCT GenreId) FROM Track;`

[↑ Back to Table of Contents](#table-of-contents)

---

## 5. Genres on the Billboard

Now the real power-up: `GROUP BY`. It splits your rows into buckets and runs an
aggregate *per bucket* — answering "how many per genre" instead of just "how
many." This is the most common real-world pattern in analytics, and the biggest
jump in feel so far.

### The task

- Show how many tracks fall in each **genre**.
- Sort them by count, **most first**.
- Show only genres with **at least 10** tracks.

### Getting started

- `GROUP BY GenreId` buckets the `Track` rows by genre, then `COUNT(*)` counts
  within each bucket.
- The golden rule: after `GROUP BY`, your `SELECT` list may only contain the
  grouped column(s) and aggregates — nothing else.
- `HAVING` filters the *groups*, after grouping. Don't confuse it with `WHERE`.

### Hints

> [!TIP] **Hint 1** — Start simple, one bucket:
> `SELECT GenreId, COUNT(*) FROM Track GROUP BY GenreId;` Just counts per genre
> id — no fancy sorting yet.

> [!NOTE] **Hint 2** — Sort high-to-low with `ORDER BY` tied to *the count*, not
> `GenreId`: `ORDER BY COUNT(*) DESC`. Yes, you can `ORDER BY` an aggregate in
> the same query.

> [!WARNING] **Hint 3** — `WHERE` filters **rows, before** grouping. `HAVING`
> filters **groups, after** grouping. So "at least 10 tracks" is a *group*
> condition: `HAVING COUNT(*) >= 10`. If someone tells you "I can't filter
> aggregated results," `HAVING` is what they forgot.

### You'll learn here

- `GROUP BY` and per-group aggregates.
- The difference between `WHERE` (rows) and `HAVING` (groups).
- Ordering by an aggregate.

### Stretch challenge

You probably saw `GenreId` (numbers), not genre *names* — that's the next
challenge's trick. For now, try a grouping twist: how many **distinct albums**
does each genre appear in?
`SELECT GenreId, COUNT(DISTINCT AlbumId) FROM Track GROUP BY GenreId;`

[↑ Back to Table of Contents](#table-of-contents)

---

## 6. Connecting the Dots

Time for the single most important skill in SQL: the **join**. Tables are
separate, but they reference each other through ids — `Track.AlbumId` points at
`Album.AlbumId`. A join stitches them back together so you can read a track
*and* its album title in one result.

### The task

- Show every track's `Name` together with the `Title` of the album it's on.

### Getting started

- Without a join, `Track` only has an `AlbumId` number — not the album *title*.
  The title lives in `Album`. You'll connect the two.
- The join tells the database how the tables relate:
  `Track.AlbumId = Album.AlbumId`.
- Once two tables are joined, you must be explicit about shared column names —
  write `t.Name` / `a.Title`, not just `Name` (see the note below).

> [!IMPORTANT] **Qualified names and aliases.** Both `Track` and `Album` have
> their own ids, and both could plausibly have a column called `Name`... in fact
> it gets messy fast. Short aliases make it clean: `FROM Track t JOIN Album a
> ON t.AlbumId = a.AlbumId`. Now `t.Name` and `a.Title` are unambiguous, and
> you'll type far less. `.` reads as "of": `t.Name` = "the Name of album-row t."

### Hints

> [!TIP] **Hint 1** — The full shape:
> `SELECT t.Name, a.Title FROM Track t JOIN Album a ON t.AlbumId = a.AlbumId;`
> `t` and `a` here are just short nicknames for the two tables.

> [!NOTE] **Hint 2** — Read the `ON` clause out loud: "a track row `t` matches
> an album row `a` when their `AlbumId`s are equal." That's the whole idea of a
> join — matching rows by a shared value.

> [!WARNING] **Hint 3** — If the result is *way* bigger than the number of
> tracks, you probably joined on the wrong columns (the `ON` is wrong) and are
> matching every track against too many albums. Fix the `ON`, not the `SELECT`.

### You'll learn here

- `INNER JOIN` and the `ON` condition.
- Table aliases and qualified (`table.column`) names.
- How ids link tables.

### Stretch challenge

Now that you can connect the tables, count how many tracks each **album** has —
by joining and grouping:
`SELECT a.Title, COUNT(t.TrackId) ...` (group by album) — a sneak preview of
challenge 8.

[↑ Back to Table of Contents](#table-of-contents)

---

## 7. Three Degrees of Separation

One join is nice; now chain **two** of them through the data model. The chain
here is `Track → Album → Artist`: a track sits on an album, and that album was
made by an artist. You'll pull a name from each hop into one result.

### The task

- Show the track `Name`, its album `Title`, and the artist's `Name`.
  One row per track, no omissions.

### Getting started

- You'll write two joins: `Track → Album` (from challenge 6) and then
  `Album → Artist` on top of that.
- The second join uses a *different* pair of ids: `Album.ArtistId` and
  `Artist.ArtistId`.
- Each `ON` clause needs aliases that are unambiguous — this is exactly why `t`,
  `a`, and (now) `ar` aliases earn their keep.

### Hints

> [!TIP] **Hint 1** — Extend challenge 6 line by line:
> `FROM Track t JOIN Album a ON t.AlbumId = a.AlbumId JOIN Artist ar ON
> a.ArtistId = ar.ArtistId`. Each `JOIN ... ON` is one hop.

> [!NOTE] **Hint 2** — Read the whole thing as a story: "track `t` is on album
> `a`, which is by artist `ar`." The `SELECT` just picks the columns you want
> from any of the three: `SELECT t.Name, a.Title, ar.Name ...`

> [!WARNING] **Hint 3** — Getting an "ambiguous column name" error? It means
> *both* a joined table has a column with the same name (here, both `Track` and
> `Artist` have a `Name`!). You must qualify at least one — write `ar.Name`
> (or `t.Name`) so the database knows which you mean.

### You'll learn here

- Chaining multiple `JOIN`s down a `FOREIGN KEY` chain.
- Why `table.column` becomes mandatory once names collide.
- Building up a query step by step instead of all at once.

### Stretch challenge

Add a filter: show track, album, artist for **one specific artist's work** —
e.g. everything by `Queen` (`WHERE ar.Name = 'Queen'`). This combines two joins
*and* a `WHERE` — a skill that's about to pay off.

[↑ Back to Table of Contents](#table-of-contents)

---

## 8. The Sales Floor

Now you'll put joins **and** aggregation together — the pattern that powers most
real business reports. Every album sale leaves a row in `InvoiceLine` with a
`TrackId` and a `Quantity`. You'll total up how many of each track have sold.

### The task

- Show each track's name and the **total number sold** of it.
- Order from best-seller (`Quantity` summed, highest) to least.

### Getting started

- Sales live in `InvoiceLine` (`TrackId`, `Quantity`, `UnitPrice`).
- The track *name* lives in `Track` — so you need a join to attach names, then
  `SUM(Quantity)` grouped by track.
- Each `InvoiceLine` row is one line of an invoice; its `Quantity` says how
  many copies were sold in that one line.

### Hints

> [!TIP] **Hint 1** — First just see the raw sales without naming:
> `SELECT TrackId, SUM(Quantity) FROM InvoiceLine GROUP BY TrackId;` — totals
> per track, as ids. Verify a couple of rows make sense.

> [!NOTE] **Hint 2** — Join `Track` onto that to replace ids with names, by
> extending the `FROM`:
> `FROM InvoiceLine il JOIN Track t ON il.TrackId = t.TrackId` and then
> `GROUP BY t.TrackId, t.Name`. Grouping by both keeps the name with its count.

> [!WARNING] **Hint 3** — One line of an invoice can sell **several** units
> (`Quantity > 1`). If you counted *lines* instead of summing `Quantity`, you'd
> under-report. `SUM(Quantity)` is the correct total; this is a classic
> count-vs-sum mistake.

### You'll learn here

- Joining for display names, then aggregating — the report pattern.
- `SUM` across grouped rows.
- Count vs. sum: knowing which one the question is really asking.

### Stretch challenge

You already have `Quantity` *and* `UnitPrice`. Compute each track's actual
**revenue** sold: `SUM(UnitPrice * Quantity)` — the money-makers, descending.

[↑ Back to Table of Contents](#table-of-contents)

---

## 9. Follow the Money

Bring it home with the store's income statement: total revenue by country. This
challenge needs almost no joins — it's a chance to combine everything cleanly —
but it *does* introduce `LIMIT`, the tool for "top N" questions.

### The task

- Total revenue (`SUM` of `Invoice.Total`) for each `BillingCountry`.
- Sorted from biggest to smallest.
- Show only the **top 10** countries.

### Getting started

- `Invoice` already stores where the bill went in `BillingCountry` and the total
  in `Total` — no join needed.
- It's the challenge 5 pattern again: `GROUP BY` + `SUM`, then `ORDER BY` the
  aggregate descending.
- `LIMIT 10` keeps just the first ten rows of the sorted result.

### Hints

> [!TIP] **Hint 1** — Build it without `LIMIT` first:
> `SELECT BillingCountry, SUM(Total) FROM Invoice GROUP BY BillingCountry
> ORDER BY SUM(Total) DESC;` — check the whole ranked list before trimming it.

> [!NOTE] **Hint 2** — Then append `LIMIT 10` to the end. `LIMIT` just chops
> the *number of rows returned*; it never changes the order or the math.

> [!WARNING] **Hint 3** — If `LIMIT` grabs the wrong rows, the usual cause is
> sorting *after* truncating in your head — `LIMIT` always applies to the
> already-`ORDER BY`ed result. Put `ORDER BY` before `LIMIT` and you're safe.

> [!NOTE] **Dialect note:** `LIMIT` is not part of the ANSI core standard, but
> it's supported by SQLite, PostgreSQL, and MySQL (older versions call it
> `TOP` in SQL Server / `ROWNUM` syndrome in Oracle). If you're not in SQLite,
> your tool has an equivalent to look up.

### You'll learn here

- `LIMIT` for top-N results.
- Pulling a grouped, sorted aggregate report end-to-end.
- Note that `WHERE` filters rows *before* grouping — it can't touch `SUM` results.

### Stretch challenge

Same report, but per **customer** instead of country — join `Customer` to
`Invoice` so you can show `FirstName`/`LastName` next to what they spent, ordered
high to low.

[↑ Back to Table of Contents](#table-of-contents)

---

## 10. One of Each Kind

The capstone: **top-per-group**. For *each* genre, find its longest track. Naive
SQL can find the longest track overall (`ORDER BY ... LIMIT 1`), but doing it
*inside* every genre needs a feature called a **window function** — the most
powerful, and most often misnamed as "scary," tool in SQL.

### The task

- For **every genre**, show the genre, the track title, and the length of its
  **longest** track.
- Each genre appears exactly once, showing its single longest track.

### Getting started

- `ROW_NUMBER() OVER (...) AS rn` assigns a number to each track within a group,
  and `PARTITION BY GenreId` defines "within each genre" as the group.
- Order the numbering by length, longest first, and pick `rn = 1`.
- Because window functions assign a number *per track*, you'll wrap the whole
  thing in a subquery and filter for `rn = 1` in the outer query.

### Hints

> [!TIP] **Hint 1** — A normal `GROUP BY` would squash a genre to one row but
> lose *which track* won. `ROW_NUMBER()` numbers each track individually *while
> keeping every row*, which is why it's right for this problem.

> [!NOTE] **Hint 2** — The window you want:
> `ROW_NUMBER() OVER (PARTITION BY GenreId ORDER BY Milliseconds DESC)`. In
> plain English: "number the tracks, restarting at 1 for each new genre, longest
> first." The `1` belongs to each genre's longest track.

> [!NOTE] **Hint 3** — Wrap it to filter the numbered result:
>
> ```sql
> SELECT GenreId, Name, Milliseconds
> FROM (
>   SELECT GenreId, Name, Milliseconds,
>          ROW_NUMBER() OVER (PARTITION BY GenreId
>                             ORDER BY Milliseconds DESC) AS rn
>   FROM Track
> )
> WHERE rn = 1;
> ```
>
> The subquery does the numbering; the outer one keeps only the winners.

> [!WARNING] **Dialect/compat note:** window functions need SQLite 3.25+ (2018)
> and are standard in PostgreSQL, MySQL 8+, and modern SQL Server — but not old
> MySQL 5.x. If yours errors, the classic fallback is a correlated subquery:
> match a track to the genre's `MAX(Milliseconds)` (a `WHERE` with `= (SELECT
> MAX(...) FROM Track t2 WHERE t2.GenreId = t.GenreId)`). Windows are cleaner —
> worth upgrading your engine if you can.

### You'll learn here

- Window functions: `OVER` and `PARTITION BY`.
- Subqueries (a `SELECT` inside a `SELECT`).
- Why window functions beat `GROUP BY` when you need per-row detail.

### Stretch challenge

Swap `ROW_NUMBER()` for `RANK()`. Now ties share a number — run it and find
every genre with **two or more** tracks sharing the exact same longest length.
Spot the difference between "row number" and "rank".

[↑ Back to Table of Contents](#table-of-contents)

---

## Where to go next

You've walked the full arc: read a table → filter it → sort it → count it →
group it → join tables → aggregate across joins → rank with window functions.
That's the exact toolkit behind most real SQL work. If this felt good, go back
and re-run the earlier challenges *without* the hints, then try the Stretches
you skipped. If the joins or window functions felt shaky, that's expected —
head to [SQLBolt](https://sqlbolt.com/), [SQLZoo](https://sqlzoo.net/), or your
database's own tutorial and drill joins specifically. They're the one skill
every real query leans on.
