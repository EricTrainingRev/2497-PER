# Project Instructions: Todo Basic Performance Testing

## Overview
This document provides step-by-step instructions for conducting performance testing on the test Todo application using Apache JMeter. The process follows the Software Testing Life Cycle (STLC) and incorporates the provided user stories to ensure comprehensive coverage.

## User Stories Under Test
- As a new user I can register an account to start tracking my todo tasks
- As a new user I can log in and out to securely access my todo items
- As a user I can create and edit todo items to keep track of my work
- As a user I can create and edit subtask items to better organize my primary tasks

## General Order of Operations (STLC)
1. **Requirement Analysis**
	- Review user stories and application features.
	- Identify performance-critical scenarios (e.g., registration, login, todo CRUD operations).
2. **Test Planning**
	- Define performance goals (response time, throughput, error rate).
	- Select tools (Apache JMeter) and test environment.
3. **Test Case Design**
	- Design JMeter test plans through script recording.
	- Parameterize scripts for dynamic data (e.g., user credentials, todo items).
4. **Test Environment Setup**
	- Configure test environment (hardware, software, network).
	- Ensure application and dependencies are running.
5. **Test Execution**
	- Run JMeter scripts for different load profiles (baseline, load, stress).
	- Monitor system and application during tests.
6. **Result Analysis**
	- Collect and analyze JMeter results.
	- Identify bottlenecks and performance issues.
7. **Reporting**
	- Document findings, metrics, and recommendations.
	- Summarize lessons learned and next steps.

## Metrics to Observe and Record
- **Response Time** (average, min, max, percentiles)
- **Throughput** (requests per second)
- **Error Rate** (percentage of failed requests)
- **Concurrent Users** (load profile)
- **Transaction Success/Failure Counts**

## Additional Notes
- Save all JMeter test plans and result files for reference.
- Include screenshots or exports of JMeter graphs as needed.
- Document any deviations from the plan or unexpected findings.

---

## Presenting JMeter Test Findings: Mini Stakeholder Presentation

When presenting your JMeter test results to stakeholders, focus on clarity, key insights, and actionable recommendations. Organize your findings as a concise, visually supported mini-presentation. Use the following structure:

### 1. Executive Summary
Briefly state the purpose of the performance test and the main outcomes (e.g., "The purpose of this performance test was to evaluate the Todo application's responsiveness and stability under varying user loads. The main outcomes show that the application meets performance targets under expected load, but bottlenecks were observed during peak usage, particularly in the task creation workflow.").

### 2. Test Scope & Scenarios
- Summarize what was tested (user stories, endpoints, workflows).
- List the types of tests performed (baseline, load, stress).

### 3. Key Metrics & Visuals
- Present essential metrics: response times, throughput, error rates, and resource usage.
- Use charts, graphs, or screenshots from the JMeter HTML report to illustrate trends and highlight issues.

### 4. Findings & Bottlenecks
- Highlight any performance issues, slow transactions, or failure points.
- Reference specific scenarios or user stories where problems were observed.

### 5. Recommendations
- Provide clear, actionable suggestions for improvement (e.g., optimize database queries, increase server resources, fix specific endpoints).

### 6. Next Steps
- Outline follow-up actions, retesting plans, or areas for further investigation.

**Tips for Effective Stakeholder Presentations:**
- Keep slides or sections brief and focused.
- Use visuals (charts, tables, screenshots) to make data easy to understand.
- Summarize technical details in plain language for non-technical stakeholders.
- End with a Q&A session.
