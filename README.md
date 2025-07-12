# Spring Boot Blog Post Backend

A secure blog post backend application with JWT-based authentication and authorization.

## Features

- **JWT Authentication**: Secure token-based authentication
- **Role-based Authorization**: USER and ADMIN roles
- **Blog Posts**: CRUD operations with authorization
- **Comments**: CRUD operations with authorization
- **User Management**: Registration and login
- **PostgreSQL Database**: Persistent data storage

## Authentication Endpoints

### Register User

```
POST /api/v1/auth/register
Content-Type: application/json

{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "password123",
    "profileImageUrl": "https://example.com/image.jpg"
}
```

### Login

```
POST /api/v1/auth/login
Content-Type: application/json

{
    "username": "user",
    "password": "password123"
}
```

Response includes JWT token:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "user",
  "role": "USER",
  "message": "Authentication successful"
}
```

## Protected Endpoints

All endpoints except `/api/v1/auth/**` require authentication. Include the JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

### Posts Endpoints

- `GET /api/v1/posts` - Get all posts (public)
- `GET /api/v1/posts/{id}` - Get post by ID (public)
- `POST /api/v1/posts` - Create new post (authenticated)
- `PUT /api/v1/posts/{id}` - Update post (author or admin only)
- `DELETE /api/v1/posts/{id}` - Delete post (author or admin only)
- `GET /api/v1/posts/author/{authorId}` - Get posts by author (public)
- `GET /api/v1/posts/search?title=keyword` - Search posts by title (public)

### Comments Endpoints

- `GET /api/v1/comments/post/{postId}` - Get comments by post ID (public)
- `GET /api/v1/comments/{id}` - Get comment by ID (public)
- `POST /api/v1/comments/post/{postId}` - Create new comment (authenticated)
- `PUT /api/v1/comments/{id}` - Update comment (author or admin only)
- `DELETE /api/v1/comments/{id}` - Delete comment (author or admin only)
- `GET /api/v1/comments/author/{authorId}` - Get comments by author (public)

## Authorization Rules

- **Public Access**: Reading posts and comments
- **Authenticated Users**: Can create posts and comments
- **Post/Comment Authors**: Can update and delete their own content
- **Admin Users**: Can update and delete any content

## Default Users

The application creates two default users on startup:

1. **Admin User**:

   - Username: `admin`
   - Password: `admin123`
   - Role: `ADMIN`

2. **Regular User**:
   - Username: `user`
   - Password: `user123`
   - Role: `USER`

## Configuration

### JWT Configuration

```properties
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000
jwt.refresh-token.expiration=604800000
```

### Database Configuration

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/blogdb
spring.datasource.username=admin
spring.datasource.password=admin@123
```

## Running the Application

1. Ensure PostgreSQL is running with the configured database
2. Run the application: `./mvnw spring-boot:run`
3. The application will start on port 8080
4. Default users will be created automatically

## Security Features

- **Password Encryption**: BCrypt password hashing
- **JWT Token Validation**: Secure token-based authentication
- **Role-based Access Control**: Fine-grained authorization
- **Input Validation**: Request validation with proper error handling
- **CORS Support**: Cross-origin resource sharing enabled
- **Global Exception Handling**: Proper error responses

## API Testing

You can test the API using tools like Postman or curl:

1. Register a new user or login with existing credentials
2. Copy the JWT token from the response
3. Include the token in subsequent requests as `Authorization: Bearer <token>`
4. Test protected endpoints with proper authorization
