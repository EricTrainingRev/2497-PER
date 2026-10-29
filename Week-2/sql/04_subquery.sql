-- ============================================================================
-- Example 4: SUBQUERY (correlated scalar subquery)
-- ----------------------------------------------------------------------------
-- Same logical result as Example 3, written as a correlated subquery that
-- runs once per Album row.
--
-- Look for:  CORRELATED SCALAR SUBQUERY  (a per-row probe)
--
-- NOTE: SQLite often rewrites a correlated scalar subquery
-- into the same join plan, so this may show an identical execution plan to
-- Example 3.
-- ============================================================================

SELECT a.Title,
       ar.Name AS Artist,
       (SELECT COUNT(*) FROM Track t WHERE t.AlbumId = a.AlbumId) AS Tracks
FROM Album a
JOIN Artist ar ON ar.ArtistId = a.ArtistId
ORDER BY a.AlbumId;

-- ----------------------------------------------------------------------------
-- Execution plan for the query above
-- Look for:  CORRELATED SCALAR SUBQUERY  (a per-row probe)
-- ----------------------------------------------------------------------------
EXPLAIN QUERY PLAN
SELECT a.Title,
       ar.Name AS Artist,
       (SELECT COUNT(*) FROM Track t WHERE t.AlbumId = a.AlbumId) AS Tracks
FROM Album a
JOIN Artist ar ON ar.ArtistId = a.ArtistId
ORDER BY a.AlbumId;
