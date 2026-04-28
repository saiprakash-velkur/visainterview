package handler

import (
	"net/http"
	"runtime"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/prometheus/client_golang/prometheus/promhttp"
)

type MetricsHandler struct {
	startTime time.Time
}

func NewMetricsHandler() *MetricsHandler {
	return &MetricsHandler{
		startTime: time.Now(),
	}
}

func (h *MetricsHandler) PrometheusMetrics(c *gin.Context) {
	promhttp.Handler().ServeHTTP(c.Writer, c.Request)
}

func (h *MetricsHandler) RuntimeStats(c *gin.Context) {
	var m runtime.MemStats
	runtime.ReadMemStats(&m)

	stats := gin.H{
		"goroutines": runtime.NumGoroutine(),
		"uptime_seconds": time.Since(h.startTime).Seconds(),
		"memory": gin.H{
			"alloc_mb":       m.Alloc / 1024 / 1024,
			"total_alloc_mb": m.TotalAlloc / 1024 / 1024,
			"sys_mb":         m.Sys / 1024 / 1024,
			"num_gc":         m.NumGC,
		},
		"cpu": gin.H{
			"num_cpu":     runtime.NumCPU(),
			"num_cgo_call": runtime.NumCgoCall(),
		},
	}

	c.JSON(http.StatusOK, stats)
}
