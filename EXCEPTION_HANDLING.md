# Exception Handling System

This document describes the comprehensive exception handling system implemented in the Spring Boot Blog Post Backend.

## Overview

The application uses a unified exception handling approach with a single `BlogException` class and a comprehensive `GlobalExceptionHandler` that provides consistent, user-friendly JSON error responses.

## Error Response Format

All error responses follow this consistent structure:

```json
{
  "statusCode": 400,
  "message": "All fields are required: title, content",
  "error": "Bad Request",
  "path": "/api/v1/posts",
  "timestamp": "2025-07-16T17:10:40.8690725Z"
}
```

## BlogException Class

The `BlogException` class is the main exception used throughout the application. It provides static factory methods for common error types:

### Available Factory Methods

```java
// 404 Not Found
BlogException.notFound("User not found")
BlogException.notFound("Post", 123L)
BlogException.notFound("Comment", 456L)

// 400 Bad Request
BlogException.badRequest("Invalid input data")
BlogException.badRequest("Email format is invalid")

// 401 Unauthorized
BlogException.unauthorized("Invalid credentials")
BlogException.unauthorized("Token expired")

// 403 Forbidden
BlogException.forbidden("You can only update your own posts")
BlogException.forbidden("Admin access required")

// 409 Conflict
BlogException.conflict("Email already exists")
BlogException.conflict("Username already taken")

// 500 Internal Server Error
BlogException.internalServerError("Database connection failed")
BlogException.internalServerError("External service unavailable")
```

## Usage Examples

### In Service Classes

Instead of throwing generic exceptions:

```java
// ❌ Don't do this
throw new RuntimeException("Post not found");

// ✅ Do this instead
throw BlogException.notFound("Post", id);
```

### In Controller Classes

Instead of returning ResponseEntity.notFound():

```java
// ❌ Don't do this
return ResponseEntity.notFound().build();

// ✅ Do this instead
throw BlogException.notFound("Post", id);
```

## GlobalExceptionHandler

The `GlobalExceptionHandler` automatically catches and processes all exceptions, converting them to the standard JSON format. It handles:

### Custom Exceptions

- `BlogException` - Main application exception
- `PostNotFoundException` - Legacy exception (backward compatibility)
- `UnauthorizedAccessException` - Legacy exception (backward compatibility)

### Spring Framework Exceptions

- `MethodArgumentNotValidException` - Validation errors
- `ConstraintViolationException` - Constraint violations
- `HttpMessageNotReadableException` - Invalid request body
- `MethodArgumentTypeMismatchException` - Invalid parameter types
- `NoHandlerFoundException` - 404 errors

### Security Exceptions

- `UsernameNotFoundException` - User not found
- `BadCredentialsException` - Invalid credentials
- `AccessDeniedException` - Access denied
- `AuthenticationException` - Authentication errors

### Generic Exceptions

- `RuntimeException` - Runtime errors
- `Exception` - All other exceptions

## Error Codes and Messages

| Status Code | Error Type            | Common Messages                           |
| ----------- | --------------------- | ----------------------------------------- |
| 400         | Bad Request           | "All fields are required: title, content" |
| 401         | Unauthorized          | "Invalid username or password"            |
| 403         | Forbidden             | "You can only update your own posts"      |
| 404         | Not Found             | "Post not found with id: 123"             |
| 409         | Conflict              | "Email already exists"                    |
| 500         | Internal Server Error | "An unexpected error occurred"            |

## Migration Guide

### For Existing Code

1. **Replace RuntimeException with BlogException:**

   ```java
   // Old
   throw new RuntimeException("Post not found");

   // New
   throw BlogException.notFound("Post", id);
   ```

2. **Replace ResponseEntity.notFound() with BlogException:**

   ```java
   // Old
   return ResponseEntity.notFound().build();

   // New
   throw BlogException.notFound("Post", id);
   ```

3. **Remove try-catch blocks in controllers:**

   ```java
   // Old
   try {
       service.method();
       return ResponseEntity.ok().build();
   } catch (RuntimeException e) {
       return ResponseEntity.badRequest().build();
   }

   // New
   service.method();
   return ResponseEntity.ok().build();
   ```

## Benefits

1. **Consistent Error Format**: All errors follow the same JSON structure
2. **User-Friendly Messages**: Clear, descriptive error messages
3. **Proper HTTP Status Codes**: Correct status codes for different error types
4. **Centralized Handling**: All exception handling in one place
5. **Easy to Use**: Simple factory methods for common error types
6. **Backward Compatible**: Legacy exceptions still work
7. **Comprehensive Coverage**: Handles all common error scenarios

## Testing

To test the exception handling, you can:

1. Try to access a non-existent post: `GET /api/v1/posts/999999`
2. Submit invalid data: `POST /api/v1/posts` with missing fields
3. Try to update another user's post
4. Access protected endpoints without authentication

All should return consistent JSON error responses with appropriate status codes.
