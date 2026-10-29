-- ============================================================================
-- Example 3: JOIN
-- ----------------------------------------------------------------------------
-- Each hop uses an index (FK indexes on Track.AlbumId / Album.ArtistId),
-- so the plan reads a small slice of each table instead of scanning.
--
-- Look for:  SEARCH ... USING INDEX ...  per hop
--            +  USE TEMP B-TREE FOR GROUP BY
-- ============================================================================

SELECT a.Title, ar.Name AS Artist, COUNT(t.TrackId) AS Tracks
FROM Track t
JOIN Album a  ON a.AlbumId    = t.AlbumId
JOIN Artist ar ON ar.ArtistId = a.ArtistId
GROUP BY a.AlbumId, a.Title, ar.Name
ORDER BY a.AlbumId;

-- ----------------------------------------------------------------------------
-- Execution plan for the query above
-- Look for:  SEARCH ... USING INDEX ...  per hop
--            +  USE TEMP B-TREE FOR GROUP BY
-- ----------------------------------------------------------------------------
EXPLAIN QUERY PLAN
SELECT a.Title, ar.Name AS Artist, COUNT(t.TrackId) AS Tracks
FROM Track t
JOIN Album a  ON a.AlbumId    = t.AlbumId
JOIN Artist ar ON ar.ArtistId = a.ArtistId
GROUP BY a.AlbumId, a.Title, ar.Name
ORDER BY a.AlbumId;
