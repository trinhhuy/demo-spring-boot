TEMPO

{name=~".*/api/.*"}

{name =~ ".*/api/.*"} and http.status_code >= 400

{name =~ ".*/api/.*"} and http.status_code >= 500




PROMETHEUS
# Login attempts counter
auth_login_attempts_total

# Failed login counter
auth_login_failed_total

# Registration metrics with labels
auth_register_outcomes{success="true"}
auth_register_outcomes{success="false"}


# Active sessions
auth_sessions_active




# Summary of login duration
auth_login_duration_seconds_count  # Number of measurements
auth_login_duration_seconds_sum    # Total time of all measurements

# Percentiles
auth_login_duration_seconds{quantile="0.5"}   # Median (50th percentile)
auth_login_duration_seconds{quantile="0.95"}  # 95th percentile
auth_login_duration_seconds{quantile="0.99"}  # 99th percentile

# Histogram buckets (useful for heatmaps)
auth_login_duration_seconds_bucket{le="0.01"}  # Under 10ms
auth_login_duration_seconds_bucket{le="0.05"}  # Under 50ms
auth_login_duration_seconds_bucket{le="0.1"}   # Under 100ms



# Login success rate
1 - (auth_login_failed_total / auth_login_attempts_total)

# Average login duration (in seconds)
rate(auth_login_duration_seconds_sum[5m]) / rate(auth_login_duration_seconds_count[5m])

# Percentage of logins faster than 50ms
auth_login_duration_seconds_bucket{le="0.05"} / auth_login_duration_seconds_count



Auth Counter Metrics
Panel: Login Attempts (Counter)
Visualization: Time series
Title: "Login Attempts"
PromQL Query: rate(auth_login_attempts_total[5m])
Panel: Login Success vs Failures
Visualization: Pie chart
Title: "Login Outcomes"
PromQL Queries:
Success: sum(increase(auth_login_attempts_total[24h]) - increase(auth_login_failed_total[24h]))
Failed: sum(increase(auth_login_failed_total[24h]))
Panel: Success Rate Percentage
Visualization: Gauge
Title: "Login Success Rate"
PromQL Query: (1 - (rate(auth_login_failed_total[5m]) / rate(auth_login_attempts_total[5m]))) * 100
Unit: Percent (0-100)
Thresholds:
Red: 0-95
Yellow: 95-99
Green: 99-100
Auth Gauge Metrics
Panel: Active Sessions
Visualization: Stat or Gauge
Title: "Current Active Sessions"
PromQL Query: auth_sessions_active
Panel: Session History
Visualization: Time series
Title: "Active Sessions Over Time"
PromQL Query: auth_sessions_active
Auth Histogram/Timer Metrics
Panel: Login Duration Percentiles
Visualization: Graph
Title: "Login Duration Percentiles"
PromQL Queries:
50th: histogram_quantile(0.5, sum(rate(auth_login_duration_seconds_bucket[5m])) by (le))
95th: histogram_quantile(0.95, sum(rate(auth_login_duration_seconds_bucket[5m])) by (le))
99th: histogram_quantile(0.99, sum(rate(auth_login_duration_seconds_bucket[5m])) by (le))
Panel: Login Duration Distribution
Visualization: Heatmap
Title: "Login Duration Heatmap"
PromQL Query: sum(increase(auth_login_duration_seconds_bucket[5m])) by (le)
Format: Heatmap
Legend: false
Panel: Average Response Time
Visualization: Stat
Title: "Avg Login Response Time"
PromQL Query: rate(auth_login_duration_seconds_sum[5m]) / rate(auth_login_duration_seconds_count[5m])
Unit: Seconds
Decimals: 3 (for millisecond precision)