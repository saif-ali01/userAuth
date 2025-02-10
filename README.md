# Authentication API

This project is a Spring Boot-based API that handles user authentication, OTP generation, and verification, as well as user registration and login.

## Features

- **User Registration**: Allows users to register by providing their details.
- **User Login**: Enables registered users to log in with their credentials.
- **Password Reset**: Provides OTP-based password reset functionality.
- **OTP Verification**: Verifies OTP for email verification or password reset.
- **Change Password**: Allows users to change their password using OTP verification.

## API Endpoints

### 1. Register a New User
**Endpoint**: `/auth/register`  
**Method**: `POST`

**Request Body**:
```json
{
  "name": "John Doe",
  "email": "johndoe@example.com",
  "password": "password123"
}
```
**Responses:**

**Success**: Returns user details.
**Error**:
If the email already exists and the user is verified, an error message will be returned.
If the email exists but the user is not verified, a verification prompt will be sent.

### 2. Login an Existing User
**Endpoint**: `/auth/login`  
**Method**: `POST`

**Request Body**:
```json
{
  "email": "johndoe@example.com",
  "password": "password123"
}
```
**Responses**:

**Success**: Returns user details if the login is successful and the user is verified.
**Error**: Returns an error message if login is unsuccessful or if the user is not verified.

### 3. Request OTP for Password Reset
**Endpoint**: /auth/forgetpassword
**Method**: POST

**Request Body**:

```json

{
  "email": "johndoe@example.com"
}
```
**Responses**:

**Success**: Sends OTP to the registered email.
**Error**: Returns 'USER NOT FOUND' if the email is not registered.

### 4. Generate OTP
**Endpoint**: /api/otp/generate
**Method**: POST

**Request Body**:

```json

{
  "email": "johndoe@example.com"
}
```
**Responses**:

**Success**: Sends OTP for email verification or password reset.
**Error**: Returns success message after OTP is sent.

### 5. Verify OTP
**Endpoint**: /api/otp/verify
**Method**: POST

**Request Body**:

```json

{
  "userId": "userId123",
  "otp": "123456"
}
```
**Responses**:

**Success**: Returns a success message if OTP is valid and not expired.
**Error**: Returns an error message if OTP is invalid or expired.

###6. Verify OTP for Changing Password
**Endpoint**: /api/otp/verifyOtpForChangePassword
**Method**: POST

**Request Body**:

```json

{
  "userId": "userId123",
  "otp": "123456",
  "password": "newpassword123"
}
```
**Responses**:

**Success**: Returns a success message if OTP is valid and the password is updated.
**Error**: Returns an error message if OTP is invalid or expired.

### Project Setup
**Prerequisites**
Java 17
Maven
### Installation Steps
**Clone this repository:**
git clone https://github.com/saif-ali01/userAuth.git
cd userAuth

**Build the project using Maven:**
-mvn clean install

**Run the application:**
mvn spring-boot:run
The API will be available at http://localhost:8080.

### Dependencies
The following dependencies are included in the project:

**Spring Boot Starter Actuator.**

**Spring Boot Starter Data MongoDB.**

**Spring Boot Starter Web.**

**Spring Boot Starter Mail.**

**Spring Boot Starter Test (for testing).**

