# Cloudinary Setup Guide

This guide explains how to set up Cloudinary for image uploads in the Spring Boot Blog Post Backend.

## Prerequisites

1. Create a Cloudinary account at [https://cloudinary.com/](https://cloudinary.com/)
2. Get your Cloud Name, API Key, and API Secret from your Cloudinary dashboard

## Configuration

### 1. Update Application Properties

Edit `src/main/resources/application.properties` and replace the placeholder values with your actual Cloudinary credentials:

```properties
# Cloudinary Configuration
cloudinary.cloud-name=your-actual-cloud-name
cloudinary.api-key=your-actual-api-key
cloudinary.api-secret=your-actual-api-secret
```

### 2. Environment Variables (Recommended)

For production, use environment variables instead of hardcoding credentials:

```properties
# Cloudinary Configuration
cloudinary.cloud-name=${CLOUDINARY_CLOUD_NAME}
cloudinary.api-key=${CLOUDINARY_API_KEY}
cloudinary.api-secret=${CLOUDINARY_API_SECRET}
```

Then set the environment variables:

```bash
export CLOUDINARY_CLOUD_NAME=your-cloud-name
export CLOUDINARY_API_KEY=your-api-key
export CLOUDINARY_API_SECRET=your-api-secret
```

## API Endpoints

### Upload Post Thumbnail

- **URL**: `POST /api/images/upload/post-thumbnail`
- **Authentication**: Required
- **Content-Type**: `multipart/form-data`
- **Parameter**: `image` (file)
- **Response**:

```json
{
  "imageUrl": "https://res.cloudinary.com/your-cloud/image/upload/v1234567890/blog-post-thumbnails/filename.jpg",
  "message": "Post thumbnail uploaded successfully"
}
```

### Upload Profile Image

- **URL**: `POST /api/images/upload/profile-image`
- **Authentication**: Required
- **Content-Type**: `multipart/form-data`
- **Parameter**: `image` (file)
- **Response**:

```json
{
  "imageUrl": "https://res.cloudinary.com/your-cloud/image/upload/v1234567890/user-profile-images/filename.jpg",
  "message": "Profile image uploaded successfully"
}
```

## Usage Examples

### Frontend Integration

#### Upload Post Thumbnail

```javascript
const formData = new FormData()
formData.append('image', file)

const response = await fetch('/api/images/upload/post-thumbnail', {
  method: 'POST',
  headers: {
    Authorization: `Bearer ${token}`,
  },
  body: formData,
})

const result = await response.json()
const imageUrl = result.imageUrl

// Use the imageUrl when creating/updating a post
const postData = {
  title: 'My Post',
  content: 'Post content',
  thumbnailUrl: imageUrl,
}
```

#### Upload Profile Image

```javascript
const formData = new FormData()
formData.append('image', file)

const response = await fetch('/api/images/upload/profile-image', {
  method: 'POST',
  headers: {
    Authorization: `Bearer ${token}`,
  },
  body: formData,
})

const result = await response.json()
const imageUrl = result.imageUrl

// Update user profile with the new image URL
const profileUpdate = {
  profileImageUrl: imageUrl,
}
```

## Features

- **Automatic Folder Organization**: Images are organized in Cloudinary folders:
  - Post thumbnails: `blog-post-thumbnails/`
  - Profile images: `user-profile-images/`
- **Secure URLs**: All uploaded images get secure HTTPS URLs
- **Error Handling**: Comprehensive error handling with meaningful messages
- **Authentication**: All upload endpoints require authentication
- **File Type Support**: Supports all common image formats (JPEG, PNG, GIF, etc.)

## Security Considerations

1. **Authentication Required**: All image upload endpoints require valid JWT authentication
2. **File Validation**: Consider adding file size and type validation in production
3. **Environment Variables**: Use environment variables for sensitive credentials
4. **CORS Configuration**: Ensure proper CORS configuration for your frontend domain

## Troubleshooting

### Common Issues

1. **"Cloudinary configuration not found"**: Check that all Cloudinary properties are set correctly
2. **"Unauthorized"**: Ensure you're sending a valid JWT token in the Authorization header
3. **"File upload failed"**: Check file size and format, ensure the file is not corrupted

### Logs

Check the application logs for detailed error messages:

```bash
tail -f logs/application.log
```
