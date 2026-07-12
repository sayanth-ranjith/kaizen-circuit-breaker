# kaizen-circuit-breaker

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
