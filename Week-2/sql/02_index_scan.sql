-- ============================================================================
-- Example 2: INDEX SCAN / SEEK
-- ----------------------------------------------------------------------------
-- Uses IFK_TrackAlbumId (the FK index on Track.AlbumId).
--
-- (a) Point lookup  ->  SEARCH Track USING INDEX IFK_TrackAlbumId (AlbumId=?)
-- (b) Range scan    ->  SEARCH Track USING INDEX ... (AlbumId>? AND AlbumId<?)
-- ============================================================================

-- (a) Index seek (point lookup)
SELECT t.TrackId, t.Name, t.Milliseconds
FROM Track t
WHERE t.AlbumId = 347;

-- (b) Index range scan
SELECT t.TrackId, t.Name, t.AlbumId 
FROM Track t
WHERE t.AlbumId BETWEEN 1 AND 20
ORDER BY t.AlbumId;

-- ----------------------------------------------------------------------------
-- Execution plans for the queries above
-- (a) -> SEARCH Track USING INDEX IFK_TrackAlbumId (AlbumId=?)
-- (b) -> SEARCH Track USING INDEX ... (AlbumId>? AND AlbumId<?)
-- ----------------------------------------------------------------------------
EXPLAIN QUERY PLAN
SELECT t.TrackId, t.Name, t.Milliseconds
FROM Track t
WHERE t.AlbumId = 347;

EXPLAIN QUERY PLAN
SELECT t.TrackId, t.Name, t.AlbumId 
FROM Track t
WHERE t.AlbumId BETWEEN 1 AND 20
ORDER BY t.AlbumId;
