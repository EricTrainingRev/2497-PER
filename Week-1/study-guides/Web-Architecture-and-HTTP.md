# Web Architecture & HTTP

## Table of Contents

* [1. High-Level Overview](#1-high-level-overview)
* [2. HTTP/HTTPS Deep Dive](#2-httphttps-deep-dive)
* [3. HTTP Methods](#3-http-methods)
* [4. Status Codes](#4-status-codes)
* [5. Headers & Cookies](#5-headers--cookies)
* [6. REST Architecture](#6-rest-architecture)
* [7. JSON Structure](#7-json-structure)
* [8. Session Management Concepts](#8-session-management-concepts)
* [9. Stateless vs Stateful Applications](#9-stateless-vs-stateful-applications)

***

## 1. High-Level Overview

Performance engineers must deeply understand the web architecture they are testing. This guide covers the **HTTP/HTTPS** protocols, **HTTP methods**, **status codes**, **headers and cookies**, the **REST** architectural style, **JSON** as a data format, and **session management** — including the distinction between **stateless** and **stateful** applications.

[↑ Back to Table of Contents](#table-of-contents)

***

*At the core of web communication is the HTTP protocol, and its secure variant HTTPS.*

## 2. HTTP/HTTPS Deep Dive

**HTTP (Hypertext Transfer Protocol)** is the foundational protocol for data communication on the web. It operates over TCP (usually port 80) and is **stateless** by design, meaning each request is independent and contains all necessary information for the server to process it.

*   **HTTP/1.1:** The most widely used version; supports persistent connections (keep-alive), pipelining, and chunked transfer encoding.
*   **HTTP/2:** Introduces multiplexing (multiple requests/responses in a single connection), header compression, and improved performance over HTTP/1.1. Uses binary framing instead of text-based messages.
*   **HTTP/3:** Based on QUIC (UDP instead of TCP), further reduces latency and improves connection reliability, especially on unreliable networks.
*   **Request/Response Model:** Clients send requests (with method, URI, headers, optional body), and servers respond with status codes, headers, and optional body.
*   **Statelessness:** Each request is processed in isolation; session state must be managed externally (e.g., via cookies or tokens).

**HTTPS (HTTP Secure)** is HTTP layered over TLS (Transport Layer Security, usually port 443), providing encryption, authentication, and data integrity for secure communication.

*   **TLS Handshake:** Establishes a secure session by negotiating encryption algorithms and exchanging keys.
*   **Certificates:** Digital certificates authenticate the server (and optionally the client) to prevent man-in-the-middle attacks.
*   **Data Encryption:** All data exchanged between client and server is encrypted, protecting against eavesdropping and tampering.

[↑ Back to Table of Contents](#table-of-contents)

***

*Within HTTP, methods define the action a client wants to perform.*

## 3. HTTP Methods

HTTP methods define the type of action a client wants to perform on a resource identified by a URL. They are fundamental to RESTful APIs and web communication.

| Method | Purpose |
| :--- | :--- |
| **GET** | Retrieve data from the server (read-only, no side effects). |
| **POST** | Submit data to the server (creates or updates resources). |
| **PUT** | Replace an existing resource or create it if it does not exist. |
| **PATCH** | Partially update an existing resource. |
| **DELETE** | Remove a resource from the server. |
| **HEAD, OPTIONS, TRACE, CONNECT** | Metadata retrieval, capability discovery, diagnostics, and tunneling. |

> [!NOTE]
> GET is idempotent and read-only; POST may create or update. PUT replaces, PATCH partially updates, DELETE removes. The remaining methods handle metadata, discovery, diagnostics, and connection tunneling.

[↑ Back to Table of Contents](#table-of-contents)

***

*Methods communicate intent; status codes communicate the outcome.*

## 4. Status Codes

| Class | Meaning | Examples |
| :--- | :--- | :--- |
| **1xx (Informational)** | Request received, continuing process. | 100 Continue |
| **2xx (Success)** | Request successfully received, understood, and accepted. | 200 OK, 201 Created |
| **3xx (Redirection)** | Further action needed to complete the request. | 301 Moved Permanently, 302 Found |
| **4xx (Client Error)** | The request contains bad syntax or cannot be fulfilled. | 400 Bad Request, 404 Not Found |
| **5xx (Server Error)** | The server failed to fulfill a valid request. | 500 Internal Server Error, 503 Service Unavailable |

> [!WARNING]
> In performance testing, a surge of **5xx** responses or **timeouts** under load usually signals resource exhaustion, thread-pool depletion, or backend saturation — not random failures. Correlate error spikes with load to find the breaking point.

[↑ Back to Table of Contents](#table-of-contents)

***

*Requests and responses carry metadata and state through headers and cookies.*

## 5. Headers & Cookies

**Headers** are key-value pairs sent with HTTP requests and responses, carrying metadata that controls how requests and responses are processed.

| Header | Purpose |
| :--- | :--- |
| **Content-Type** | Specifies the media type of the resource or data being sent (e.g., application/json). |
| **Accept** | Informs the server about the types of data the client can process. |
| **Authorization** | Contains credentials for authenticating the client with the server (e.g., Bearer tokens, Basic auth). |
| **User-Agent** | Identifies the client application, browser, or tool making the request. |
| **Cookie** | Sends stored cookies from the client to the server for session management or personalization. |
| **Set-Cookie** | Used by the server to send cookies to the client. |
| **Cache-Control** | Directs caching behavior for requests and responses (e.g., no-cache, max-age). |
| **Location** | Used in redirects to specify the new URL for the client to request. |
| **Host** | Specifies the domain name of the server (useful for virtual hosting). |
| **Referer** | Indicates the URL of the previous web page from which a request was made. |
| **X-Forwarded-For** | Identifies the originating IP address of a client connecting through a proxy or load balancer. |

**Cookies** are small pieces of data stored on the client and sent with requests to maintain state, track sessions, or store preferences. They are essential for enabling features like user authentication, shopping carts, and personalization.

*   **Session Cookies:** Temporary cookies deleted when the browser is closed; used for session management.
*   **Persistent Cookies:** Remain on the client for a specified duration; used for remembering preferences or login states.
*   **Secure:** Can only be transmitted over HTTPS connections, enhancing security.
*   **HttpOnly:** Cannot be accessed via JavaScript, reducing the risk of cross-site scripting (XSS) attacks.
*   **SameSite:** Restricts when cookies are sent with cross-site requests, helping prevent cross-site request forgery (CSRF) attacks.
*   **Domain/Path:** Control the scope of URLs to which the cookie is sent.

> [!TIP]
> In performance test scripts, always review recorded headers and cookies — replaying sessions correctly depends on matching them (e.g., Authorization headers and session cookies).

[↑ Back to Table of Contents](#table-of-contents)

***

*On top of HTTP sits the REST architectural style for designing APIs.*

## 6. REST Architecture

**REST (Representational State Transfer)** is an architectural style for designing networked applications. RESTful services exchange data in machine-friendly formats (such as JSON or XML), sending representations of objects rather than language-specific objects themselves. This enables interoperability between different systems by using standard formats for communication. REST relies on stateless communication, resource-based URIs, and standard HTTP methods.

| Principle | Description |
| :--- | :--- |
| **Statelessness** | Each request from client to server must contain all the information needed to understand and process it; the server does not store session state between requests. |
| **Client-Server Separation** | The client and server are independent; the client handles the UI and user experience, while the server manages data and business logic. |
| **Cacheability** | Responses must define themselves as cacheable or not to prevent clients from reusing stale or inappropriate data. |
| **Layered System** | The architecture can be composed of hierarchical layers, with each layer only interacting with adjacent ones, improving scalability and manageability. |
| **Uniform Interface** | A standardized way of interacting with resources, typically using HTTP methods and URIs, simplifies and decouples the architecture. |

[↑ Back to Table of Contents](#table-of-contents)

***

*REST services typically exchange data in the JSON format.*

## 7. JSON Structure

**JSON (JavaScript Object Notation)** is a lightweight, human-readable data format used for data interchange between clients and servers. It supports objects, arrays, strings, numbers, booleans, and null values.

```json
{
  "user": {
    "id": 1,
    "name": "Alice",
    "isActive": true,
    "roles": ["admin", "editor"]
  }
}
```

> [!NOTE]
> JSON's simplicity and language independence make it the default interchange format for modern REST APIs — and a common target for parameterization and correlation in performance test scripts.

[↑ Back to Table of Contents](#table-of-contents)

***

*Because HTTP is stateless, applications need a way to track users across requests.*

## 8. Session Management Concepts

A **session** is a series of interactions between a client and server, often requiring state to be maintained for purposes such as user authentication, shopping carts, personalization, or tracking user activity across multiple requests.

| Technique | Description |
| :--- | :--- |
| **Cookies** | Store session identifiers on the client and send them with each request; commonly used for web authentication and maintaining user state. |
| **Tokens (JWT)** | Self-contained tokens that encode user/session data and are sent with each request (often in headers); useful for stateless authentication in APIs and single-page applications. |
| **Server-side Session Stores** | The server maintains session data (e.g., in memory, databases, or distributed caches) and references it via a session ID sent to the client; useful for storing sensitive or large session data securely. |
| **URL Parameters** | Session identifiers are included in the URL query string; less secure and less common, but sometimes used when cookies are not available. |

[↑ Back to Table of Contents](#table-of-contents)

***

*Finally, we summarize the fundamental design choice of whether the server holds client state.*

## 9. Stateless vs Stateful Applications

| Aspect | Stateless | Stateful |
| :--- | :--- | :--- |
| **Request Independence** | Each request is independent; the server does not retain client context between requests. | The server maintains client context across multiple requests. |
| **Scalability** | Preferred for scalability and reliability. | Can simplify some workflows but reduces scalability. |

> [!TIP]
> Stateless designs are generally preferred for performance and scalability because any server can handle any request without needing shared session state. Stateful designs are used when the workflow genuinely requires server-side context.

[↑ Back to Table of Contents](#table-of-contents)
