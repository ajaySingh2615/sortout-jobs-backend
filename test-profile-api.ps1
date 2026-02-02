# ProfileController API Testing Script
# Test user: Phone +918808319836, User ID: 17

$BASE_URL = "http://localhost:8080/api"
$USER_ID = 17

Write-Host "=== ProfileController API Testing ===" -ForegroundColor Cyan
Write-Host ""

# Step 1: Get a fresh token
Write-Host "1. Getting authentication token..." -ForegroundColor Yellow
docker exec sortout_db psql -U sortout -d sortout_jobs_db -q -c "DELETE FROM otps WHERE phone = '+918808319836'; INSERT INTO otps (phone, otp_code, expiry_time, verified) VALUES ('+918808319836', '123456', NOW() + INTERVAL '5 minutes', false);" 2>$null

$body = '{"phone": "+918808319836", "otp": "123456"}'
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/auth/phone/verify-otp" -Method POST -ContentType "application/json" -Body $body
    $TOKEN = $response.data.accessToken
    $USER_ID = $response.data.userId
    Write-Host "   Token obtained. User ID: $USER_ID" -ForegroundColor Green
} catch {
    Write-Host "   FAILED to get token: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

$headers = @{
    "Authorization" = "Bearer $TOKEN"
    "Content-Type" = "application/json"
}

# Helper function for API calls
function Test-Endpoint {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [string]$Body = $null
    )
    
    Write-Host ""
    Write-Host "Testing: $Name" -ForegroundColor Yellow
    Write-Host "   $Method $Url" -ForegroundColor Gray
    
    try {
        if ($Body) {
            $result = Invoke-RestMethod -Uri $Url -Method $Method -Headers $headers -Body $Body
        } else {
            $result = Invoke-RestMethod -Uri $Url -Method $Method -Headers $headers
        }
        Write-Host "   SUCCESS: $($result.message)" -ForegroundColor Green
        if ($result.data) {
            $json = $result.data | ConvertTo-Json -Depth 5 -Compress
            if ($json.Length -gt 200) { $json = $json.Substring(0, 200) + "..." }
            Write-Host "   Data: $json" -ForegroundColor DarkGray
        }
        return $result
    } catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        Write-Host "   FAILED (HTTP $statusCode): $($_.Exception.Message)" -ForegroundColor Red
        try {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $reader.BaseStream.Position = 0
            $errorBody = $reader.ReadToEnd()
            Write-Host "   Error: $errorBody" -ForegroundColor DarkRed
        } catch {}
        return $null
    }
}

Write-Host ""
Write-Host "==================== FULL PROFILE ====================" -ForegroundColor Cyan

# 2. GET Full Profile
Test-Endpoint -Name "Get Full Profile" -Method "GET" -Url "$BASE_URL/profile/$USER_ID"

Write-Host ""
Write-Host "==================== PERSONAL DETAILS ====================" -ForegroundColor Cyan

# 3. GET Personal Details
Test-Endpoint -Name "Get Personal Details" -Method "GET" -Url "$BASE_URL/profile/$USER_ID/personal-details"

# 4. PUT Personal Details
$personalDetailsBody = @{
    dateOfBirth = "1995-05-15"
    gender = "MALE"
    maritalStatus = "SINGLE"
    category = "GENERAL"
    differentlyAbled = $false
    careerBreak = $false
    permanentAddress = "123 Main St, City"
    hometown = "Mumbai"
    pincode = "400001"
    languages = @("English", "Hindi")
} | ConvertTo-Json

Test-Endpoint -Name "Update Personal Details" -Method "PUT" -Url "$BASE_URL/profile/$USER_ID/personal-details" -Body $personalDetailsBody

Write-Host ""
Write-Host "==================== HEADLINE & SUMMARY ====================" -ForegroundColor Cyan

# 5. Update Headline
$headlineBody = '"Experienced Software Developer | Java, Spring Boot, React"'
Test-Endpoint -Name "Update Headline" -Method "PUT" -Url "$BASE_URL/profile/$USER_ID/headline" -Body $headlineBody

# 6. Update Summary
$summaryBody = '"Passionate software developer with 5+ years of experience in building scalable applications."'
Test-Endpoint -Name "Update Summary" -Method "PUT" -Url "$BASE_URL/profile/$USER_ID/summary" -Body $summaryBody

Write-Host ""
Write-Host "==================== EMPLOYMENTS ====================" -ForegroundColor Cyan

# 7. GET Employments
Test-Endpoint -Name "Get Employments" -Method "GET" -Url "$BASE_URL/profile/$USER_ID/employments"

# 8. POST Employment (Add)
$employmentBody = @{
    designation = "Software Engineer"
    organization = "Tech Corp"
    isCurrentEmployment = $true
    startDate = "2020-01-01"
    endDate = $null
    jobProfile = "Full stack development"
    noticePeriod = "30 days"
} | ConvertTo-Json

$empResult = Test-Endpoint -Name "Add Employment" -Method "POST" -Url "$BASE_URL/profile/$USER_ID/employments" -Body $employmentBody

# 9. PUT Employment (Update) - if we got an ID
if ($empResult -and $empResult.data -and $empResult.data.id) {
    $empId = $empResult.data.id
    $updateEmpBody = @{
        id = $empId
        designation = "Senior Software Engineer"
        organization = "Tech Corp"
        isCurrentEmployment = $true
        startDate = "2020-01-01"
        endDate = $null
        jobProfile = "Leading full stack development team"
        noticePeriod = "60 days"
    } | ConvertTo-Json
    
    Test-Endpoint -Name "Update Employment ($empId)" -Method "PUT" -Url "$BASE_URL/profile/$USER_ID/employments/$empId" -Body $updateEmpBody
}

Write-Host ""
Write-Host "==================== EDUCATIONS ====================" -ForegroundColor Cyan

# 10. GET Educations
Test-Endpoint -Name "Get Educations" -Method "GET" -Url "$BASE_URL/profile/$USER_ID/educations"

# 11. POST Education (Add)
$educationBody = @{
    educationLevel = "GRADUATION"
    course = "B.Tech"
    specialization = "Computer Science"
    university = "IIT Delhi"
    courseType = "FULL_TIME"
    passingYear = 2018
    gradingSystem = "CGPA"
    marks = "8.5"
} | ConvertTo-Json

$eduResult = Test-Endpoint -Name "Add Education" -Method "POST" -Url "$BASE_URL/profile/$USER_ID/educations" -Body $educationBody

# 12. PUT Education (Update)
if ($eduResult -and $eduResult.data -and $eduResult.data.id) {
    $eduId = $eduResult.data.id
    $updateEduBody = @{
        id = $eduId
        educationLevel = "GRADUATION"
        course = "B.Tech"
        specialization = "Computer Science"
        university = "IIT Delhi"
        courseType = "FULL_TIME"
        passingYear = 2018
        gradingSystem = "CGPA"
        marks = "9.0"
    } | ConvertTo-Json
    
    Test-Endpoint -Name "Update Education ($eduId)" -Method "PUT" -Url "$BASE_URL/profile/$USER_ID/educations/$eduId" -Body $updateEduBody
}

Write-Host ""
Write-Host "==================== PROJECTS ====================" -ForegroundColor Cyan

# 13. GET Projects
Test-Endpoint -Name "Get Projects" -Method "GET" -Url "$BASE_URL/profile/$USER_ID/projects"

# 14. POST Project (Add)
$projectBody = @{
    title = "E-Commerce Platform"
    description = "Built a full-stack e-commerce platform"
    status = "FINISHED"
    startDate = "2021-01-01"
    endDate = "2021-06-01"
    projectUrl = "https://github.com/user/ecommerce"
} | ConvertTo-Json

$projResult = Test-Endpoint -Name "Add Project" -Method "POST" -Url "$BASE_URL/profile/$USER_ID/projects" -Body $projectBody

# 15. PUT Project (Update)
if ($projResult -and $projResult.data -and $projResult.data.id) {
    $projId = $projResult.data.id
    $updateProjBody = @{
        id = $projId
        title = "E-Commerce Platform v2"
        description = "Enhanced e-commerce platform with microservices"
        status = "FINISHED"
        startDate = "2021-01-01"
        endDate = "2021-12-01"
        projectUrl = "https://github.com/user/ecommerce-v2"
    } | ConvertTo-Json
    
    Test-Endpoint -Name "Update Project ($projId)" -Method "PUT" -Url "$BASE_URL/profile/$USER_ID/projects/$projId" -Body $updateProjBody
}

Write-Host ""
Write-Host "==================== IT SKILLS ====================" -ForegroundColor Cyan

# 16. GET IT Skills
Test-Endpoint -Name "Get IT Skills" -Method "GET" -Url "$BASE_URL/profile/$USER_ID/it-skills"

# 17. POST IT Skill (Add)
$skillBody = @{
    skillName = "Java"
    version = "17"
    lastUsed = "2024"
    experienceMonths = 60
} | ConvertTo-Json

$skillResult = Test-Endpoint -Name "Add IT Skill" -Method "POST" -Url "$BASE_URL/profile/$USER_ID/it-skills" -Body $skillBody

# 18. PUT IT Skill (Update)
if ($skillResult -and $skillResult.data -and $skillResult.data.id) {
    $skillId = $skillResult.data.id
    $updateSkillBody = @{
        id = $skillId
        skillName = "Java"
        version = "21"
        lastUsed = "2025"
        experienceMonths = 72
    } | ConvertTo-Json
    
    Test-Endpoint -Name "Update IT Skill ($skillId)" -Method "PUT" -Url "$BASE_URL/profile/$USER_ID/it-skills/$skillId" -Body $updateSkillBody
}

Write-Host ""
Write-Host "==================== FINAL PROFILE CHECK ====================" -ForegroundColor Cyan

# Final check - get full profile to see all data
Test-Endpoint -Name "Get Full Profile (Final)" -Method "GET" -Url "$BASE_URL/profile/$USER_ID"

Write-Host ""
Write-Host "==================== CLEANUP (DELETE TESTS) ====================" -ForegroundColor Cyan

# Delete tests (optional, uncomment to enable)
# if ($skillResult -and $skillResult.data.id) { Test-Endpoint -Name "Delete IT Skill" -Method "DELETE" -Url "$BASE_URL/profile/$USER_ID/it-skills/$($skillResult.data.id)" }
# if ($projResult -and $projResult.data.id) { Test-Endpoint -Name "Delete Project" -Method "DELETE" -Url "$BASE_URL/profile/$USER_ID/projects/$($projResult.data.id)" }
# if ($eduResult -and $eduResult.data.id) { Test-Endpoint -Name "Delete Education" -Method "DELETE" -Url "$BASE_URL/profile/$USER_ID/educations/$($eduResult.data.id)" }
# if ($empResult -and $empResult.data.id) { Test-Endpoint -Name "Delete Employment" -Method "DELETE" -Url "$BASE_URL/profile/$USER_ID/employments/$($empResult.data.id)" }

Write-Host ""
Write-Host "=== Testing Complete ===" -ForegroundColor Cyan
