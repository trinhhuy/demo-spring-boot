import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://localhost:8080/api/test';

// # Test Circuit Breaker
// k6 run --env PATTERN=circuitBreaker circuit-breaker-test.js
//
// # Test Bulkhead
// k6 run --env PATTERN=bulkhead circuit-breaker-test.js
//
// # Test Rate Limiter
// k6 run --env PATTERN=rateLimiter circuit-breaker-test.js
//
// # Test Timeout
// k6 run --env PATTERN=timeout circuit-breaker-test.js

// Cấu hình cho từng pattern riêng biệt
const patternConfigs = {
  // Circuit Breaker Pattern:
  // - Mục đích: Test khả năng ngắt mạch khi có nhiều lỗi liên tiếp
  // - Cách hoạt động: 
  //   1. Tăng dần users để tạo lỗi
  //   2. Khi đủ số lỗi, circuit breaker sẽ OPEN
  //   3. Giảm tải để circuit breaker có thể HALF_OPEN và recovery
  circuitBreaker: {
    executor: 'ramping-vus',
    startVUs: 0,
    stages: [
      { duration: '20s', target: 10 },   // Khởi động với 10 users
      { duration: '30s', target: 30 },   // Tăng cao để tạo nhiều lỗi -> OPEN
      { duration: '15s', target: 0 },    // Dừng hoàn toàn để chờ HALF_OPEN
      { duration: '15s', target: 1 },    // Chỉ dùng 1 user để gửi request thành công
      { duration: '20s', target: 5 },    // Tăng nhẹ tải sau khi CLOSED
      { duration: '20s', target: 0 },    // Kết thúc test
    ]
  },

  // Bulkhead Pattern:
  // - Mục đích: Test khả năng giới hạn số lượng concurrent calls
  // - Cấu hình trong application.yml: maxConcurrentCalls: 10
  // - Kỳ vọng: Khi vượt quá 10 concurrent calls sẽ nhận fallback response
  bulkhead: {
    executor: 'constant-arrival-rate',
    rate: 50,                 // Giảm xuống 50 requests/giây
    timeUnit: '1s',
    preAllocatedVUs: 50,     // Giảm số VUs
    duration: '1m'
  },

  // Rate Limiter Pattern:
  // - Mục đích: Test giới hạn số lượng requests trong một khoảng thời gian
  // - Cấu hình trong application.yml: 
  //   + limitForPeriod: 10 (số requests cho phép)
  //   + limitRefreshPeriod: 1s (thời gian refresh limit)
  rateLimiter: {
    executor: 'per-vu-iterations',
    vus: 20,                  // 20 users đồng thời
    iterations: 50,           // Mỗi user gửi 50 requests
    maxDuration: '2m'         // Tối đa 2 phút
  },

  // Timeout Pattern:
  // - Mục đích: Test xử lý timeout của service
  // - Cấu hình trong application.yml: timeoutDuration: 2s
  // - Kỳ vọng: Requests quá 2s sẽ bị timeout và trả về fallback
  timeout: {
    executor: 'constant-vus',
    vus: 15,                  // 15 users đồng thời
    duration: '1m'            // Chạy trong 1 phút
  }
};

// Lấy pattern từ environment variable hoặc mặc định là circuitBreaker
const pattern = __ENV.PATTERN || 'circuitBreaker';

// Export options dựa trên pattern được chọn
export const options = {
  scenarios: {
    [pattern]: patternConfigs[pattern]
  }
};

// Các hàm test pattern
const patternTests = {
  // Test Circuit Breaker:
  // - Gọi endpoint failure để tạo lỗi
  // - Kiểm tra response và message
  // - Monitor trạng thái của circuit breaker
  circuitBreaker: function() {
    let response;
    const currentState = getCurrentCircuitBreakerState();
    
    if (currentState === 'HALF_OPEN') {
      // Khi ở trạng thái HALF_OPEN, gửi request success để chuyển sang CLOSED
      console.log('HALF_OPEN state detected - Sending success request to recover');
      response = http.get(`${BASE_URL}/test-circuit-breaker/success`);
    } else if (currentState === 'CLOSED' && __VU <= 5) {
      // Khi đã CLOSED và số VU thấp, tiếp tục gửi success để duy trì trạng thái
      console.log('CLOSED state - Maintaining healthy state with success requests');
      response = http.get(`${BASE_URL}/test-circuit-breaker/success`);
    } else {
      // Các trường hợp còn lại gửi request failure
      console.log('Sending failure request');
      response = http.get(`${BASE_URL}/test-circuit-breaker/failure`);
    }
    
    check(response, {
      'Status is 200': (r) => r.status === 200,
      'Response contains expected message': (r) => {
        const body = r.body.toString();
        return body.includes('Đã xảy ra lỗi') || body.includes('Success');
      },
      'Response time < 2s': (r) => r.timings.duration < 2000,
    });
    
    monitorCircuitBreakerState();
    sleep(1);
  },

  // Test Bulkhead:
  // - Gọi endpoint success với tải cao
  // - Kiểm tra response khi vượt quá concurrent calls
  // - Log chi tiết khi có lỗi
  bulkhead: function() {
    const response = http.get(`${BASE_URL}/test-circuit-breaker/success`);
    
    check(response, {
      'Status is 200': (r) => r.status === 200,
      'Valid response received': (r) => {
        const body = r.body.toString();
        return body.includes('Success response') || 
               body.includes('Hệ thống đang quá tải') ||
               body.includes('CircuitBreaker is OPEN');
      }
    });
    
    sleep(0.1);
    
    if (response.status !== 200) {
      console.warn(`[${new Date().toISOString()}] Bulkhead test response:`, {
        status: response.status,
        body: response.body,
        timings: response.timings
      });
    }
  },

  // Test Rate Limiter:
  // - Gọi endpoint success nhiều lần
  // - Kiểm tra response khi vượt quá rate limit
  // - Log các requests bị rate limit
  rateLimiter: function() {
    const tags = { testType: 'ratelimit' };
    const response = http.get(`${BASE_URL}/test-circuit-breaker/success`, { tags });
    
    check(response, {
      'Response is valid': (r) => {
        const body = r.body.toString();
        return body.includes('Success response') || 
               body.includes('Đã vượt quá số lượng yêu cầu cho phép');
      }
    });
    
    if (response.body.includes('Đã vượt quá số lượng yêu cầu')) {
      console.warn(`[${new Date().toISOString()}] Request rate limited`);
    }
  },

  // Test Timeout:
  // - Gọi endpoint timeout (có delay 5s)
  // - Kiểm tra xử lý timeout và fallback
  // - Log thời gian phản hồi
  timeout: function() {
    const tags = { testType: 'timeout' };
    const response = http.get(`${BASE_URL}/test-circuit-breaker/timeout`, { tags });
    
    check(response, {
      'Response handled correctly': (r) => {
        const body = r.body.toString();
        return body.includes('Delayed response') || 
               body.includes('Yêu cầu đã hết thời gian chờ');
      },
      'Response time is tracked': (r) => {
        console.log(`Timeout test response time: ${r.timings.duration}ms`);
        return true;
      }
    });
  }
};

// Thêm hàm mới để lấy trạng thái hiện tại của Circuit Breaker
function getCurrentCircuitBreakerState() {
  const response = http.get(`${BASE_URL}/test-circuit-breaker/status`);
  return response.body.toString();
}

function monitorCircuitBreakerState() {
  const state = getCurrentCircuitBreakerState();
  const timestamp = new Date().toISOString();
  
  console.log(`[${timestamp}] Circuit Breaker State: ${state}`);
  
  if (state.includes('OPEN')) {
    console.log('Circuit Breaker opened - Service is failing');
  } else if (state.includes('HALF_OPEN')) {
    console.log('Circuit Breaker attempting recovery - Allowing test requests');
  } else if (state.includes('CLOSED')) {
    console.log('Circuit Breaker closed - Service is healthy');
  }
}

// Hàm chính để chạy test
// - Chọn và chạy test dựa trên pattern được chỉ định
// Cách chạy: k6 run --env PATTERN=<pattern_name> circuit-breaker-test.js
// Ví dụ: k6 run --env PATTERN=circuitBreaker circuit-breaker-test.js
export default function() {
  patternTests[pattern]();
} 