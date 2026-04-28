package middleware

import (
	"strconv"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promauto"
)

var (
	httpRequestsTotal = promauto.NewCounterVec(
		prometheus.CounterOpts{
			Name: "http_requests_total",
			Help: "Total number of HTTP requests",
		},
		[]string{"method", "endpoint", "status"},
	)

	httpRequestDuration = promauto.NewHistogramVec(
		prometheus.HistogramOpts{
			Name:    "http_request_duration_seconds",
			Help:    "HTTP request latency in seconds",
			Buckets: prometheus.DefBuckets,
		},
		[]string{"method", "endpoint", "status"},
	)

	accountCreationTotal = promauto.NewCounter(
		prometheus.CounterOpts{
			Name: "account_creation_total",
			Help: "Total number of accounts created",
		},
	)

	transactionTotal = promauto.NewCounterVec(
		prometheus.CounterOpts{
			Name: "transaction_total",
			Help: "Total number of transactions",
		},
		[]string{"type", "status"},
	)

	transactionAmount = promauto.NewHistogramVec(
		prometheus.HistogramOpts{
			Name:    "transaction_amount",
			Help:    "Transaction amounts",
			Buckets: []float64{10, 50, 100, 500, 1000, 5000, 10000},
		},
		[]string{"type"},
	)

	optimisticLockRetries = promauto.NewCounter(
		prometheus.CounterOpts{
			Name: "optimistic_lock_retries_total",
			Help: "Total number of optimistic lock retries",
		},
	)
)

func PrometheusMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		start := time.Now()
		path := c.FullPath()
		if path == "" {
			path = c.Request.URL.Path
		}

		c.Next()

		duration := time.Since(start).Seconds()
		status := strconv.Itoa(c.Writer.Status())

		httpRequestsTotal.WithLabelValues(c.Request.Method, path, status).Inc()
		httpRequestDuration.WithLabelValues(c.Request.Method, path, status).Observe(duration)
	}
}

func RecordAccountCreation() {
	accountCreationTotal.Inc()
}

func RecordTransaction(txType string, status string, amount float64) {
	transactionTotal.WithLabelValues(txType, status).Inc()
	transactionAmount.WithLabelValues(txType).Observe(amount)
}

func RecordOptimisticLockRetry() {
	optimisticLockRetries.Inc()
}
