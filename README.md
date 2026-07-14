# kaizen-circuit-breaker

## Design Rationale

The configuration layer uses a split between raw bound properties and immutable
runtime config objects:

- `KaizenProperties` binds external configuration from Spring.
- `KaizenConfig` is the validated runtime value object.
- `KaizenConfigRegistry` resolves configs by name at runtime.
- `DefaultKaizenConfigRegistry` builds an immutable lookup map once at startup.

This is a registry pattern, not a direct "properties as factory" approach. I chose
it because it keeps responsibilities separate and keeps the runtime code simple:

- Configuration binding stays focused on reading YAML or properties.
- Validation stays close to the domain rules in `KaizenConfig`.
- Lookup is O(1) and does not require scanning or reconstructing objects.
- Adding or removing a breaker is data-only, so no Java code change is needed.

## Why This Is Cleaner

The previous shape mixed binding, validation, and conversion in one place. That
creates avoidable coupling and makes the code harder to evolve. The current
design follows the intent of SOLID:

- Single Responsibility: each class has one job.
- Open/Closed: new breaker definitions are added in config, not code.
- Liskov Substitution: `KaizenConfigRegistry` can be replaced with another
  implementation later, such as a remote or dynamic registry.
- Interface Segregation: consumers only depend on `get(name)` and `getAll()`.
- Dependency Inversion: application code depends on the registry abstraction,
  not on the concrete map-building logic.

This also improves failure behavior:

- Invalid numeric ranges fail fast.
- Duplicate breaker names fail fast.
- Missing named configs fail fast with `KaizenConfigException`.

## What We Build Next

The next useful pieces in Kaizen should be:

1. A circuit breaker engine that uses `KaizenConfig` and records outcomes.
2. A state machine for `CLOSED`, `OPEN`, and `HALF_OPEN`.
3. Sliding-window storage and failure-rate calculation.
4. An execution wrapper or annotation-based API for protecting calls.
5. Metrics and logging so breaker transitions are observable.

That sequence keeps the system layered:

- config and validation first,
- breaker state transitions second,
- request interception third,
- observability last.

## Current Model

Configuration is currently expressed as a list of named breakers. Example:

```yaml
kaizen:
  circuit-breakers:
    - name: inventory
      failure-rate-threshold: 60
      minimum-number-of-calls: 10
      sliding-window-size: 20
      sliding-window-type: COUNT_BASED
      wait-duration-in-open-state: 30s
      permitted-calls-in-half-open-state: 5
```

This keeps the config format easy to extend and keeps runtime resolution stable.

## Sliding Window

This project uses a sliding window to evaluate recent traffic instead of looking at
the entire history of requests. The idea is to keep the circuit breaker decisions
focused on what has happened lately, because old failures or successes may no longer
reflect the current state of the downstream service.

The sliding window is represented by `KaizenSlidingWindowType`, which supports two
modes:

- `COUNT_BASED`
- `TIME_BASED`

## How It Works

At a high level, the circuit breaker keeps a rolling set of recent requests and
their outcomes. As new requests come in, older data falls out of the window. The
breaker then uses the data inside that active window to calculate failure rates,
success rates, or other health metrics.

This gives the breaker a moving picture of service health instead of a stale
historical average.

Example sample:

- `F F F S S`
- The breaker counts the failures in that active window
- It then compares the failure percentage against the configured threshold

For example, if the failure threshold is set to `50%`, then a window with `3`
failures out of `5` requests has a `60%` failure rate, which would trip the
circuit breaker.

In other words, the breaker does not just look at the latest single request. It
looks at the recent pattern and decides based on the configured failure rate rule.

## Count-Based Sliding Window

In a count-based window, the system tracks a fixed number of the most recent
requests.

Example:

- Window size = 100 requests
- The breaker evaluates only the latest 100 calls
- When request 101 arrives, request 1 drops out of the window

This mode is useful when you want every request to have equal weight and you care
about the most recent sample of traffic, regardless of time.

## Time-Based Sliding Window

In a time-based window, the system tracks requests that happened within a fixed
time range.

Example:

- Window size = 1 minute
- The breaker evaluates only requests from the last 60 seconds
- As time moves forward, requests older than 60 seconds are removed

This mode is useful when request volume changes a lot. It tells you how the service
has behaved over a recent time period, even if traffic is bursty or sparse.

## When To Use Each Mode

- Use `COUNT_BASED` when request volume is fairly steady and you want a predictable
  sample size.
- Use `TIME_BASED` when traffic varies a lot and you want health decisions tied to
  a recent time interval.
- Implement both and understand them.

## In Short

The sliding window keeps circuit breaker decisions current by only considering a
recent slice of traffic. Count-based windows slice by request count. Time-based
windows slice by elapsed time.
