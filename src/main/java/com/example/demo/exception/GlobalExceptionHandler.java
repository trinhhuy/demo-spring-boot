import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import com.example.demo.dto.ApiResponse;
import com.example.demo.enums.ResponseCode;

@ControllerAdvice
public class GlobalExceptionHandler {

//     @ExceptionHandler(RuntimeException.class)
//     public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
//         ApiResponse<String> response = ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, ex.getMessage());
//         return ResponseEntity
//             .status(HttpStatus.INTERNAL_SERVER_ERROR)
//             .body(response);
//     }

//     @ExceptionHandler(CallNotPermittedException.class)
//     @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
//     public ResponseEntity<ApiResponse<String>> handleCircuitBreakerException(CallNotPermittedException ex) {
//         return ResponseEntity
//             .status(HttpStatus.SERVICE_UNAVAILABLE)
//             .body(ApiResponse.error(ResponseCode.CIRCUIT_BREAKER_OPEN, "Service is temporarily unavailable"));
//     }

//     @ExceptionHandler(BulkheadFullException.class)
//     @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
//     public ResponseEntity<ApiResponse<String>> handleBulkheadException(BulkheadFullException ex) {
//         return ResponseEntity
//             .status(HttpStatus.TOO_MANY_REQUESTS)
//             .body(ApiResponse.error(ResponseCode.BULKHEAD_FULL, "Too many concurrent requests"));
//     }

//     @ExceptionHandler(RequestNotPermitted.class)
//     @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
//     public ResponseEntity<ApiResponse<String>> handleRateLimitException(RequestNotPermitted ex) {
//         return ResponseEntity
//             .status(HttpStatus.TOO_MANY_REQUESTS)
//             .body(ApiResponse.error(ResponseCode.RATE_LIMIT_EXCEEDED, "Rate limit exceeded"));
//     }
} 