# JMeter: Correlation

## Table of Contents

* [1. High-Level Overview](#1-high-level-overview)
* [2. Regex Extractor](#2-regex-extractor)
* [3. JSON Extractor](#3-json-extractor)
* [4. Session Handling](#4-session-handling)
* [5. Token Management](#5-token-management)
* [6. Dynamic IDs](#6-dynamic-ids)

***

## 1. High-Level Overview

**Correlation** is the process of extracting dynamic values from one response and injecting them into later requests so a test script stays valid across runs. It is the difference between a script that works once (the recording session) and one that works every time.

This module covers the two most common extraction tools — the **Regex Extractor** and the **JSON Extractor** — and the three categories of dynamic data they are applied to: **sessions**, **tokens**, and **dynamic IDs**.

[↑ Back to Table of Contents](#table-of-contents)

***

*The workhorse of correlation is the post-processor; we start with pattern-based extraction.*

## 2. Regex Extractor

The **Regex Extractor** is a post-processor that captures dynamic values from server responses using **regular expressions**. It is the general-purpose tool for correlation when the value appears anywhere in the response (body, headers, URL).

### Key Concepts

| Concept | Detail |
| :--- | :--- |
| **Post-Processor** | Runs after a sampler executes, parsing the response |
| **Regular Expressions** | Patterns match and capture data from the response body, headers, or fields |
| **Variable Assignment** | Captured values are stored in variables for later use |
| **Multiple Matches** | Can capture single or multiple occurrences with indexed access |
| **Scope** | Applies to the main sample, sub-samples, or both |

### Typical Workflow

1.  **Identify Dynamic Data** — determine which value must be extracted (session ID, token).
2.  **Add Regex Extractor** — attach it as a child of the relevant sampler.
3.  **Configure the Expression** — define the regex pattern, template, and variable name.
4.  **Test Extraction** — verify with the View Results Tree listener.
5.  **Reference the Variable** — use `${session_id}` in subsequent requests or logic.

### Regex Basics for Extraction

*   Use a **capturing group** `( ... )` around the value you want. For example `"session_token":"(.*?)"` captures whatever is between the quotes.
*   `.*?` is a **non-greedy** match — it grabs as little as possible, which avoids over-matching multiple fields.
*   Set a **default value** so the variable is defined (e.g., to a sentinel) when the pattern does not match, preventing downstream failures or confusing `${undefined_var}`.

> [!WARNING]
> Regex is brittle: a small change in the response format silently breaks extraction. If the data is well-formed JSON, prefer the **JSON Extractor**; reserve regex for values nested in HTML, headers, or awkward text where JSONPath cannot reach.

**Best Practices:**

*   Write precise, unambiguous patterns to avoid capturing unintended data.
*   Test patterns against sample responses before large runs.
*   Handle "no match" gracefully with a default value.
*   Use a Debug Sampler to inspect extracted variables during development.
*   Comment complex regular expressions for maintainability.
*   Limit scope to where extraction is needed.

**Example:** To extract a session token from a login response, add a Regex Extractor to the login request, configure the pattern `"session_token":"(.*?)"`, and assign it to `session_token`. Reference `${session_token}` in the headers/body of following requests to maintain session continuity.

**Example Test Plan Structure:**

```
Test Plan
├── Thread Group
│   ├── HTTP Request (Login)
│   │   └── Regex Extractor (extract session_token)
│   ├── HTTP Request (Access Protected Resource using ${session_token})
│   └── Listener (View Results Tree)
```

[↑ Back to Table of Contents](#table-of-contents)

***

*For JSON responses, JSONPath is far more robust than regex.*

## 3. JSON Extractor

The **JSON Extractor** is a post-processor that extracts values from **JSON responses** using **JSONPath expressions**. It is essential for modern APIs and web applications that return JSON, enabling robust correlation of tokens, IDs, and other values.

| Concept | Detail |
| :--- | :--- |
| **Post-Processor** | Parses the JSON response after the sampler runs |
| **JSONPath Expressions** | Precise, structure-aware paths into the JSON (e.g., `$.auth.token`) |
| **Variable Assignment** | Extracted values stored in variables for later requests |
| **Multiple Matches** | Supports extracting single or multiple values with indexed access |
| **Scope** | Main sample, sub-samples, or both |

### Typical Workflow

1.  **Identify Dynamic Data** — determine which JSON value to extract.
2.  **Add JSON Extractor** — attach it as a child of the sampler.
3.  **Configure JSONPath** — define the expression and variable name.
4.  **Test Extraction** — verify with View Results Tree.
5.  **Reference the Variable** — use `${auth_token}` in subsequent requests or logic.

### JSONPath Examples

| JSONPath | Extracts |
| :--- | :--- |
| `$.token` | Top-level `token` field |
| `$.auth.token` | Nested `token` inside `auth` object |
| `$.data.items[0].id` | `id` of the first item in `data.items` array |
| `$..token` | First `token` found anywhere in the document |

> [!TIP]
> JSONPath is **structure-aware**, so it survives cosmetic changes (whitespace, field ordering) that would break a regex. Use it whenever the response is valid JSON.

**Best Practices:**

*   Write accurate JSONPath expressions that target exactly the data needed.
*   Test expressions against sample responses before large runs.
*   Handle missing data gracefully with a default value.
*   Use a Debug Sampler to inspect extracted variables.
*   Document complex expressions for maintainability.

**Example:** To extract an auth token from a JSON login response, add a JSON Extractor to the login request, set the JSONPath `$.auth.token`, assign it to `auth_token`, and reference `${auth_token}` in the headers of subsequent API calls.

**Example Test Plan Structure:**

```
Test Plan
├── Thread Group
│   ├── HTTP Request (Login)
│   │   └── JSON Extractor (extract auth_token)
│   ├── HTTP Request (Access Protected Resource using ${auth_token})
│   └── Listener (View Results Tree)
```

[↑ Back to Table of Contents](#table-of-contents)

***

*Now we apply these extractors to real-world dynamic data, starting with sessions.*

## 4. Session Handling

**Session handling** manages user sessions and stateful interactions during testing. Many applications use sessions to track authentication, preferences, and activity; each virtual user must maintain a **unique and valid session** throughout the test.

### Key Concepts

*   **Session State:** Continuity of a user's interaction, maintained via cookies, tokens, or URL parameters.
*   **Cookie Management:** JMeter's **HTTP Cookie Manager** automatically handles cookies per thread, simulating browser-like session behavior.
*   **Token-Based Sessions:** Modern APIs often use tokens (JWT, OAuth) requiring extraction and reuse.
*   **Correlation:** Extracting and injecting session identifiers (cookies, tokens, dynamic IDs) to maintain continuity.
*   **Thread Isolation:** Each thread must have an independent session to avoid collisions.

### Typical Workflow

1.  **Enable Cookie Manager** — add an HTTP Cookie Manager to handle cookies automatically per thread.
2.  **Extract Session Identifiers** — use extractors (Regex, JSON, XPath) to capture tokens/IDs when cookies are not used.
3.  **Parameterize Requests** — insert extracted session values into headers, parameters, or bodies.
4.  **Validate Session Continuity** — use assertions or listeners to confirm each user stays in a valid session.
5.  **Handle Logouts/Timeouts** — simulate session expiration or logout if required by the test objectives.

> [!TIP]
> **Use the Cookie Manager as your default session mechanism for cookie-based apps.** It stores and replays cookies per thread automatically, so you often need *no* manual session extraction at all — you only add extractors when the app uses token-based (header) sessions.

### Best Practices

*   **Use HTTP Cookie Manager** — always include it for cookie-based web apps.
*   **Isolate Sessions** — ensure each thread has its own session context.
*   **Extract and Reuse Tokens** — for token-based auth, extract after login and reuse in all subsequent requests.
*   **Monitor Session Validity** — check for redirects to login pages or error messages that signal a dropped session.
*   **Simulate Realistic Behavior** — include login, navigation, and logout.
*   **Document Session Strategy** — comment how sessions are managed in the test plan.

**Example Test Plan Structure:**

```
Test Plan
├── Thread Group
│   ├── HTTP Cookie Manager
│   ├── HTTP Request (Login)
│   │   └── JSON Extractor (extract auth_token)
│   ├── HTTP Header Manager (set Authorization: Bearer ${auth_token})
│   ├── HTTP Request (Access Protected Resource)
│   └── Listener (View Results Tree)
```

**Example:** Each user logs in, establishing a session via cookies or tokens. The Cookie Manager or extracted token maintains session continuity per thread, and subsequent requests use that context — accurately simulating real-world behavior and validating session management under load.

[↑ Back to Table of Contents](#table-of-contents)

***

*Closely related to sessions are the tokens that secure them.*

## 5. Token Management

**Token management** handles the various tokens used for authentication, authorization, and session continuity. Tokens are pervasive in modern apps and APIs, and correct handling ensures each virtual user can authenticate and act as intended.

| Token Type | Purpose |
| :--- | :--- |
| **Authentication Token** | Verifies identity (JWT, OAuth, SAML, API keys) |
| **Session Token** | Maintains session state across requests, often issued after login |
| **CSRF Token** | Protects against cross-site request forgery; unique per form/sensitive action |

### Key Concepts

*   **Dynamic Extraction:** Tokens are usually generated dynamically and must be extracted from responses.
*   **Token Renewal/Expiration:** Some tokens have limited lifespans and require refresh or re-authentication during longer tests.
*   **Secure Handling:** Tokens should be managed securely to avoid leaks during testing.

### Typical Workflow

1.  **Login or Initial Request** — user authenticates and receives a token (in JSON, headers, or cookies).
2.  **Extract Token** — use a JSON/Regex Extractor to capture the value.
3.  **Store in Variable** — assign it to a variable for reuse.
4.  **Reuse Token** — insert it into headers, parameters, or request bodies.
5.  **Handle Expiry** — implement refresh or re-authentication for expiring tokens.

**Best Practices:**

*   **Automate Extraction** — always extract tokens dynamically; never hardcode.
*   **Parameterize Usage** — reference token variables in all authenticated requests.
*   **Monitor Expiry** — be aware of token lifespan; implement refresh logic if necessary.
*   **Secure Storage** — avoid logging tokens in plain text or exposing them in reports.
*   **Test Token Scenarios** — simulate expiration, invalid tokens, and concurrent usage to validate robustness.
*   **Document Token Flow** — comment how tokens are managed and used.

> [!WARNING]
> **Token expiry under long tests:** if an access token expires mid-run (e.g., after 15 minutes) and your test runs an hour, every request after expiry fails with `401`. Plan for a refresh flow or re-authentication step, and set a realistic test duration relative to the token's lifetime.

**Example Test Plan Structure:**

```
Test Plan
├── Thread Group
│   ├── HTTP Request (Login)
│   │   └── JSON Extractor (extract access_token)
│   ├── HTTP Header Manager (set Authorization: Bearer ${access_token})
│   ├── HTTP Request (Perform Authenticated Action)
│   └── Listener (View Results Tree)
```

**Example:** The user logs in and receives an access token. It is extracted into `access_token`, and all subsequent requests include it in the Authorization header, ensuring authenticated access and session continuity.

[↑ Back to Table of Contents](#table-of-contents)

***

*Beyond full sessions and tokens, applications generate small dynamic identifiers that must be chained across requests.*

## 6. Dynamic IDs

**Dynamic IDs** are unique values generated per session, transaction, or action — order numbers, cart IDs, request IDs, workflow tokens — that later requests must reference to maintain workflow integrity.

### Key Concepts

*   **Uniqueness:** Generated by the server and change with each session or transaction.
*   **Correlation:** Extracting IDs from responses and injecting them into future requests is essential.
*   **Multiple Occurrences:** Some responses contain several IDs (e.g., lists), requiring indexed extraction and reuse.
*   **Chained Requests:** Dynamic IDs often link dependent requests (create order → update order → delete order).
*   **Variable Assignment:** Extracted IDs are stored in variables for later samplers or controllers.

### Typical Workflow

1.  **Identify Dynamic IDs** — determine which IDs are generated dynamically and needed later.
2.  **Extract IDs** — use a Regex, JSON, or XPath extractor on the relevant response.
3.  **Store in Variable** — assign the ID to a variable (e.g., `order_id`).
4.  **Reuse in Requests** — reference the variable in subsequent requests.
5.  **Handle Multiple IDs** — use indexed variables or loops to process each as needed.

### Best Practices

*   **Map Workflows** — map where dynamic IDs are generated and consumed.
*   **Automate Extraction** — always extract dynamically; never hardcode.
*   **Validate Extraction** — use listeners/assertions to confirm IDs are captured and used correctly.
*   **Handle Lists** — for responses with multiple IDs, use JMeter's variable indexing to iterate.
*   **Document ID Flow** — comment where and how dynamic IDs are managed.

> [!TIP]
> When a dynamic ID from a previous request is not extracted correctly, the next request typically fails with a **404** or a business-logic error (e.g., "no such order"). A cluster of 404s mid-flow is a strong fingerprint of a broken correlation on a dynamic ID, not a server fault.

**Example Test Plan Structure:**

```
Test Plan
├── Thread Group
│   ├── HTTP Request (Create Order)
│   │   └── JSON Extractor (extract order_id)
│   ├── HTTP Request (Update Order using ${order_id})
│   ├── HTTP Request (Delete Order using ${order_id})
│   └── Listener (View Results Tree)
```

**Example:** The user creates an order, and the server returns a unique `order_id`. It is extracted and stored in a variable, and the update and delete requests use `${order_id}` to reference the correct order — keeping the workflow consistent and accurately simulating real user actions.

[↑ Back to Table of Contents](#table-of-contents)
