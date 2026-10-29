-- ============================================================================
-- Example 1: FULL TABLE SCAN
-- ----------------------------------------------------------------------------
-- BillingCity has no index (and the leading wildcard would skip any index
-- anyway). SQLite must visit every row of Invoice.
--
-- Look for:  SCAN Invoice  |  (no "USING ... INDEX" / "SEARCH" keyword)
-- ============================================================================

SELECT InvoiceId, BillingCity, BillingCountry
FROM Invoice
WHERE BillingCity LIKE '%ondon%';

-- ----------------------------------------------------------------------------
-- Execution plan for the query above
-- Look for:  SCAN Invoice  |  (no "USING ... INDEX" / "SEARCH" keyword)
-- ----------------------------------------------------------------------------
EXPLAIN QUERY PLAN
SELECT InvoiceId, BillingCity, BillingCountry
FROM Invoice
WHERE BillingCity LIKE '%ondon%';
