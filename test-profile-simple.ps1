# Simple Profile API Test Script
$ErrorActionPreference = "Continue"

Write-Host "=== ProfileController API Test ===" -ForegroundColor Cyan

# Get token
docker exec sortout_db psql -U sortout -d sortout_jobs_db -q -c "DELETE FROM otps WHERE phone = '+918808319836'; INSERT INTO otps (phone, otp_code, expiry_time, verified) VALUES ('+918808319836', '123456', NOW() + INTERVAL '5 minutes', false);" 2>$null

$r = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/phone/verify-otp" -Method POST -ContentType "application/json" -Body '{"phone": "+918808319836", "otp": "123456"}'
$TOKEN = $r.data.accessToken
$USER_ID = 17
$h = @{"Authorization"="Bearer $TOKEN"; "Content-Type"="application/json"}

Write-Host "Token obtained for User ID: $USER_ID" -ForegroundColor Green
Write-Host ""

$tests = @()

# Test function
function Test-API($name, $method, $url, $body=$null) {
    try {
        if ($body) {
            $r = Invoke-RestMethod -Uri $url -Method $method -Headers $h -Body $body
        } else {
            $r = Invoke-RestMethod -Uri $url -Method $method -Headers $h
        }
        Write-Host "[PASS] $name" -ForegroundColor Green
        return @{success=$true; data=$r.data; message=$r.message}
    } catch {
        Write-Host "[FAIL] $name - $($_.Exception.Message)" -ForegroundColor Red
        return @{success=$false}
    }
}

$BASE = "http://localhost:8080/api/profile"

# 1. Full Profile
Test-API "GET Full Profile" "GET" "$BASE/$USER_ID"

# 2. Personal Details
Test-API "GET Personal Details" "GET" "$BASE/$USER_ID/personal-details"
Test-API "PUT Personal Details" "PUT" "$BASE/$USER_ID/personal-details" '{"dateOfBirth":"1995-05-15","gender":"MALE","maritalStatus":"SINGLE"}'

# 3. Headline
Test-API "PUT Headline" "PUT" "$BASE/$USER_ID/headline" '"Senior Software Developer"'

# 4. Summary  
Test-API "PUT Summary" "PUT" "$BASE/$USER_ID/summary" '"Experienced developer with 5+ years in Java and React"'

# 5. Employments
Test-API "GET Employments" "GET" "$BASE/$USER_ID/employments"
$emp = Test-API "POST Employment" "POST" "$BASE/$USER_ID/employments" '{"designation":"Developer","organization":"TechCorp","isCurrentEmployment":true,"startDate":"2020-01-01"}'
if ($emp.success -and $emp.data.id) {
    Test-API "PUT Employment" "PUT" "$BASE/$USER_ID/employments/$($emp.data.id)" "{`"id`":$($emp.data.id),`"designation`":`"Sr Developer`",`"organization`":`"TechCorp`",`"isCurrentEmployment`":true,`"startDate`":`"2020-01-01`"}"
    Test-API "DELETE Employment" "DELETE" "$BASE/$USER_ID/employments/$($emp.data.id)"
}

# 6. Educations
Test-API "GET Educations" "GET" "$BASE/$USER_ID/educations"
$edu = Test-API "POST Education" "POST" "$BASE/$USER_ID/educations" '{"educationLevel":"GRADUATION","course":"B.Tech","university":"IIT","passingYear":2018}'
if ($edu.success -and $edu.data.id) {
    Test-API "PUT Education" "PUT" "$BASE/$USER_ID/educations/$($edu.data.id)" "{`"id`":$($edu.data.id),`"educationLevel`":`"GRADUATION`",`"course`":`"B.Tech`",`"university`":`"IIT Delhi`",`"passingYear`":2018}"
    Test-API "DELETE Education" "DELETE" "$BASE/$USER_ID/educations/$($edu.data.id)"
}

# 7. Projects
Test-API "GET Projects" "GET" "$BASE/$USER_ID/projects"
$proj = Test-API "POST Project" "POST" "$BASE/$USER_ID/projects" '{"title":"Portfolio","description":"Personal website","status":"FINISHED"}'
if ($proj.success -and $proj.data.id) {
    Test-API "PUT Project" "PUT" "$BASE/$USER_ID/projects/$($proj.data.id)" "{`"id`":$($proj.data.id),`"title`":`"Portfolio v2`",`"description`":`"Updated`",`"status`":`"FINISHED`"}"
    Test-API "DELETE Project" "DELETE" "$BASE/$USER_ID/projects/$($proj.data.id)"
}

# 8. IT Skills
Test-API "GET IT Skills" "GET" "$BASE/$USER_ID/it-skills"
$skill = Test-API "POST IT Skill" "POST" "$BASE/$USER_ID/it-skills" '{"skillName":"Python","experienceMonths":24}'
if ($skill.success -and $skill.data.id) {
    Test-API "PUT IT Skill" "PUT" "$BASE/$USER_ID/it-skills/$($skill.data.id)" "{`"id`":$($skill.data.id),`"skillName`":`"Python`",`"experienceMonths`":36}"
    Test-API "DELETE IT Skill" "DELETE" "$BASE/$USER_ID/it-skills/$($skill.data.id)"
}

Write-Host ""
Write-Host "=== Test Complete ===" -ForegroundColor Cyan
