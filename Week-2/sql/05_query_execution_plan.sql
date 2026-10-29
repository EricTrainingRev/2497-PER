-- ============================================================================
-- Example 5: QUERY EXECUTION PLAN  (SQLite)
-- ----------------------------------------------------------------------------
-- SQLite doesn't render a graphical plan; use EXPLAIN QUERY PLAN to print the
-- optimizer's strategy as text. Run these two and compare the operator lines:
--
--   03_join.sql     ->  index seeks per hop, joined in a nested loop
--   04_subquery.sql ->  a CORRELATED SCALAR SUBQUERY line (or the same joins
--                       if SQLite flattens the subquery into the join)
--
-- The point: the join form and the subquery form *should* produce the same
-- result; reading the plan shows whether they share the same access path.
-- ============================================================================

-- Plan for the JOIN query
EXPLAIN QUERY PLAN
SELECT a.Title, ar.Name AS Artist, COUNT(t.TrackId) AS Tracks
FROM Track t
JOIN Album a  ON a.AlbumId    = t.AlbumId
JOIN Artist ar ON ar.ArtistId = a.ArtistId
GROUP BY a.AlbumId, a.Title, ar.Name
ORDER BY a.AlbumId;

-- Plan for the SUBQUERY query
EXPLAIN QUERY PLAN
SELECT a.Title,
       ar.Name AS Artist,
       (SELECT COUNT(*) FROM Track t WHERE t.AlbumId = a.AlbumId) AS Tracks
FROM Album a
JOIN Artist ar ON ar.ArtistId = a.ArtistId
ORDER BY a.AlbumId;
