# API Testing Guide - Account Deletion & Registration Verification

## Quick Reference

### Base URL
```
http://localhost:8080
```

---

## 1. Registration Flow with Email Verification

### Step 1: Register Teacher
**Endpoint**: `POST /api/teacher/register`

**Request**:
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "teacherId": "123456",
  "password": "SecurePass@123"
}
```

**Response**:
```
Teacher registered successfully. Verification email sent.
```

**What happens**:
- Teacher entity created with `verified = false`
- `verificationToken` set to random UUID
- Email sent with verification link

---

### Step 2: Verify Email (Click Link in Email)
**Endpoint**: `GET /api/teacher/verify`

**Query Parameter**:
```
?token=<verification_token_from_email>
```

**Full URL Example**:
```
http://localhost:8080/api/teacher/verify?token=550e8400-e29b-41d4-a716-446655440000
```

**Response**:
```
Account verified successfully
```

**What happens**:
- Teacher `verified` set to `true`
- `verificationToken` cleared
- Teacher can now login

---

## 2. Account Deletion Flow

### Step 1: Request Deletion
**Endpoint**: `POST /api/teacher/request-delete`

**Request**:
```json
{
  "email": "john@example.com"
}
```

**Response**:
```
Verification link sent to your email. Click it to confirm account deletion.
```

**What happens**:
- `deleteToken` generated (random UUID)
- `deleteTokenExpiry` set to now + 24 hours
- Email sent with deletion link

---

### Step 2: Verify and Delete (Click Link in Email)
**Endpoint**: `GET /api/teacher/verify-delete`

**Query Parameter**:
```
?token=<delete_token_from_email>
```

**Full URL Example**:
```
http://localhost:8080/api/teacher/verify-delete?token=660e8400-e29b-41d4-a716-446655440001
```

**Response**:
```
Account deleted successfully
```

**What happens**:
1. Delete token validated
2. Check token not expired (24 hour window)
3. Find all students with teacher's classroom code
4. Delete attendance records for each student
5. Delete all students
6. Delete teacher account

---

## 3. Testing with cURL

### Register Teacher
```bash
curl -X POST http://localhost:8080/api/teacher/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane@test.com",
    "teacherId": "654321",
    "password": "TestPass@456"
  }'
```

### Request Account Deletion
```bash
curl -X POST http://localhost:8080/api/teacher/request-delete \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane@test.com"
  }'
```

### Verify Registration (Browser)
```
http://localhost:8080/api/teacher/verify?token=YOUR_TOKEN_HERE
```

### Verify and Delete (Browser)
```
http://localhost:8080/api/teacher/verify-delete?token=YOUR_TOKEN_HERE
```

---

## 4. JavaScript Frontend Testing

### Test Delete Account Button
```javascript
// In browser console on teacher dashboard
deleteAccount();
```

**Steps**:
1. Confirm button prompts confirmation
2. Prompts for email
3. Calls `/api/teacher/request-delete`
4. Shows response message

---

## 5. Database Check

### Verify Teacher Registration
```sql
-- Check if teacher created
SELECT id, name, email, teacherId, verified, verificationToken 
FROM teachers 
WHERE email = 'john@example.com';
```

### Verify Deletion
```sql
-- After clicking deletion link, should return no rows
SELECT * FROM teachers WHERE email = 'john@example.com';

-- All related students should be deleted
SELECT * FROM students WHERE classroom_code = '<teacher_classroom_code>';

-- All attendance records should be deleted (if cascaded properly)
SELECT * FROM attendance WHERE student_id IN (
  SELECT id FROM students WHERE classroom_code = '<teacher_classroom_code>'
);
```

---

## 6. Email Service Verification

### Check if EmailService is Called
Look for logs in stdout:
```
[INFO] Sending email to: john@example.com
[INFO] Subject: Verify your account
```

### Gmail Settings (if using Gmail SMTP)
1. Enable 2-Factor Authentication
2. Create App Password (not regular password)
3. Use App Password in `application.properties`:
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password-16-chars
```

---

## 7. Common Issues & Fixes

### Issue: "Email not found" on deletion request
**Cause**: Email doesn't exist in system
**Fix**: Make sure teacher is registered with exact email

### Issue: "Token expired" on deletion verification
**Cause**: More than 24 hours passed since deletion request
**Fix**: Request deletion again

### Issue: "Invalid token" 
**Cause**: Token doesn't match any teacher
**Fix**: Use link from actual email sent

### Issue: Verification email not received
**Cause**: Email service not configured
**Fix**: Check `application.properties` mail settings and console logs

### Issue: Students not deleted with account
**Cause**: Cascade delete not working
**Fix**: Check `verifyDelete()` method in controller - should call:
- `attendanceRepository.deleteByStudent(s)` for each student
- `studentRepository.deleteAll(students)`

---

## 8. Success Indicators

✅ Teacher can register and receive verification email
✅ Teacher can verify email and login
✅ Delete Account button visible in dashboard
✅ Clicking Delete Account prompts for confirmation and email
✅ Deletion request email received
✅ Clicking deletion link deletes account
✅ All students of deleted teacher are deleted
✅ All attendance records are cleaned up
✅ Deletion token expires after 24 hours

---

## 9. API Call Diagram

```
REGISTRATION FLOW:
Teacher → "Register" form
        → POST /api/teacher/register
        → Email sent (verification link)
        → Click link in email
        → GET /api/teacher/verify?token=xxx
        → Account verified, can login

DELETION FLOW:
Teacher → Dashboard
        → Click "Delete Account" button
        → Prompts for email
        → POST /api/teacher/request-delete
        → Email sent (deletion link)
        → Click link in email
        → GET /api/teacher/verify-delete?token=xxx
        → Account + all data deleted
```

---

## 10. Error Response Examples

### Invalid Email (Deletion)
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Email not found"
}
```

### Invalid Token
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid or expired deletion token"
}
```

### Token Expired
```
Token expired
```

---

**Last Updated**: February 7, 2026
**Implementation Complete**: ✅ All endpoints tested and validated
