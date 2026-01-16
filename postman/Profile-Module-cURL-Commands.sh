# Profile Module API - cURL Commands Reference
# Base URL: http://localhost:8080
# User ID: 17 (Test User: +918808319836)

# ============================================
# AUTHENTICATION - Get Token First
# ============================================

# Step 1: Insert OTP into database (for testing)
docker exec sortout_db psql -U sortout -d sortout_jobs_db -c "DELETE FROM otps WHERE phone = '+918808319836'; INSERT INTO otps (phone, otp_code, expiry_time, verified) VALUES ('+918808319836', '123456', NOW() + INTERVAL '5 minutes', false);"

# Step 2: Verify OTP and get token
curl -X POST "http://localhost:8080/api/auth/phone/verify-otp" \
  -H "Content-Type: application/json" \
  -d '{"phone": "+918808319836", "otp": "123456"}'

# Save the accessToken from response and use it below
# TOKEN="your_access_token_here"

# ============================================
# FULL PROFILE
# ============================================

# Get Full Profile
curl -X GET "http://localhost:8080/api/profile/17" \
  -H "Authorization: Bearer $TOKEN"

# Update Profile
curl -X PUT "http://localhost:8080/api/profile/17" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "profilePicture": null
  }'

# ============================================
# PERSONAL DETAILS
# ============================================

# Get Personal Details
curl -X GET "http://localhost:8080/api/profile/17/personal-details" \
  -H "Authorization: Bearer $TOKEN"

# Update Personal Details
curl -X PUT "http://localhost:8080/api/profile/17/personal-details" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "dateOfBirth": "1995-05-15",
    "gender": "MALE",
    "maritalStatus": "SINGLE",
    "category": "GENERAL",
    "differentlyAbled": false,
    "careerBreak": false,
    "permanentAddress": "123 Main Street, City",
    "hometown": "Mumbai",
    "pincode": "400001",
    "languages": ["English", "Hindi"]
  }'

# ============================================
# HEADLINE & SUMMARY
# ============================================

# Update Resume Headline
curl -X PUT "http://localhost:8080/api/profile/17/headline" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '"Experienced Software Developer | Java, Spring Boot, React | 5+ Years Experience"'

# Update Profile Summary
curl -X PUT "http://localhost:8080/api/profile/17/summary" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '"Passionate software developer with 5+ years of experience in building scalable web applications."'

# ============================================
# EMPLOYMENTS
# ============================================

# Get All Employments
curl -X GET "http://localhost:8080/api/profile/17/employments" \
  -H "Authorization: Bearer $TOKEN"

# Add Employment
curl -X POST "http://localhost:8080/api/profile/17/employments" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "designation": "Software Engineer",
    "organization": "Tech Corporation",
    "isCurrentEmployment": true,
    "startDate": "2020-01-15",
    "endDate": null,
    "jobProfile": "Full stack development using Java, Spring Boot, and React",
    "noticePeriod": "30 days"
  }'

# Update Employment (replace {employmentId} with actual ID)
curl -X PUT "http://localhost:8080/api/profile/17/employments/{employmentId}" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "designation": "Senior Software Engineer",
    "organization": "Tech Corporation",
    "isCurrentEmployment": true,
    "startDate": "2020-01-15",
    "endDate": null,
    "jobProfile": "Leading full stack development team",
    "noticePeriod": "60 days"
  }'

# Delete Employment
curl -X DELETE "http://localhost:8080/api/profile/17/employments/{employmentId}" \
  -H "Authorization: Bearer $TOKEN"

# ============================================
# EDUCATIONS
# ============================================

# Get All Educations
curl -X GET "http://localhost:8080/api/profile/17/educations" \
  -H "Authorization: Bearer $TOKEN"

# Add Education
curl -X POST "http://localhost:8080/api/profile/17/educations" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "educationLevel": "GRADUATION",
    "course": "B.Tech",
    "specialization": "Computer Science",
    "university": "Indian Institute of Technology",
    "courseType": "FULL_TIME",
    "passingYear": 2018,
    "gradingSystem": "CGPA",
    "marks": "8.5"
  }'

# Update Education (replace {educationId} with actual ID)
curl -X PUT "http://localhost:8080/api/profile/17/educations/{educationId}" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "educationLevel": "GRADUATION",
    "course": "B.Tech",
    "specialization": "Computer Science",
    "university": "Indian Institute of Technology Delhi",
    "courseType": "FULL_TIME",
    "passingYear": 2018,
    "gradingSystem": "CGPA",
    "marks": "9.0"
  }'

# Delete Education
curl -X DELETE "http://localhost:8080/api/profile/17/educations/{educationId}" \
  -H "Authorization: Bearer $TOKEN"

# ============================================
# PROJECTS
# ============================================

# Get All Projects
curl -X GET "http://localhost:8080/api/profile/17/projects" \
  -H "Authorization: Bearer $TOKEN"

# Add Project
curl -X POST "http://localhost:8080/api/profile/17/projects" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "E-Commerce Platform",
    "description": "Built a full-stack e-commerce platform with microservices architecture",
    "status": "FINISHED",
    "startDate": "2021-01-01",
    "endDate": "2021-06-30",
    "projectUrl": "https://github.com/user/ecommerce-platform"
  }'

# Update Project (replace {projectId} with actual ID)
curl -X PUT "http://localhost:8080/api/profile/17/projects/{projectId}" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "title": "E-Commerce Platform v2",
    "description": "Enhanced e-commerce platform with advanced features",
    "status": "FINISHED",
    "startDate": "2021-01-01",
    "endDate": "2021-12-31",
    "projectUrl": "https://github.com/user/ecommerce-platform-v2"
  }'

# Delete Project
curl -X DELETE "http://localhost:8080/api/profile/17/projects/{projectId}" \
  -H "Authorization: Bearer $TOKEN"

# ============================================
# IT SKILLS
# ============================================

# Get All IT Skills
curl -X GET "http://localhost:8080/api/profile/17/it-skills" \
  -H "Authorization: Bearer $TOKEN"

# Add IT Skill
curl -X POST "http://localhost:8080/api/profile/17/it-skills" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "skillName": "Java",
    "version": "17",
    "lastUsed": "2024",
    "experienceMonths": 60
  }'

# Update IT Skill (replace {skillId} with actual ID)
curl -X PUT "http://localhost:8080/api/profile/17/it-skills/{skillId}" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "skillName": "Java",
    "version": "21",
    "lastUsed": "2025",
    "experienceMonths": 72
  }'

# Delete IT Skill
curl -X DELETE "http://localhost:8080/api/profile/17/it-skills/{skillId}" \
  -H "Authorization: Bearer $TOKEN"

# ============================================
# RESUME
# ============================================

# Upload Resume (multipart/form-data)
curl -X POST "http://localhost:8080/api/profile/17/resume" \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/resume.pdf"

# Delete Resume
curl -X DELETE "http://localhost:8080/api/profile/17/resume" \
  -H "Authorization: Bearer $TOKEN"

# ============================================
# PASSWORD & PHONE LINKING
# ============================================

# Change Password
curl -X POST "http://localhost:8080/api/profile/17/change-password" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "oldPassword123",
    "newPassword": "newSecurePassword456"
  }'

# Send Phone Link OTP
curl -X POST "http://localhost:8080/api/profile/17/link-phone/send-otp" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "+919876543210"
  }'

# Verify & Link Phone
curl -X POST "http://localhost:8080/api/profile/17/link-phone/verify" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "+919876543210",
    "otp": "123456"
  }'
