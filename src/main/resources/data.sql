-- Cities (using ON CONFLICT to avoid duplicates on restart)
INSERT INTO cities (id, name, state, is_active) VALUES 
(1, 'Mumbai', 'Maharashtra', true),
(2, 'Delhi', 'Delhi', true),
(3, 'Bangalore', 'Karnataka', true),
(4, 'Hyderabad', 'Telangana', true),
(5, 'Chennai', 'Tamil Nadu', true),
(6, 'Kolkata', 'West Bengal', true),
(7, 'Pune', 'Maharashtra', true),
(8, 'Ahmedabad', 'Gujarat', true),
(9, 'Jaipur', 'Rajasthan', true),
(10, 'Lucknow', 'Uttar Pradesh', true)
ON CONFLICT (id) DO NOTHING;

-- Localities (Mumbai = 1, Delhi = 2, Bangalore = 3)
INSERT INTO localities (id, name, pincode, city_id, is_active) VALUES
(1, 'Andheri', '400058', 1, true),
(2, 'Bandra', '400050', 1, true),
(3, 'Dadar', '400014', 1, true),
(4, 'Connaught Place', '110001', 2, true),
(5, 'Lajpat Nagar', '110024', 2, true),
(6, 'Dwarka', '110075', 2, true),
(7, 'Koramangala', '560034', 3, true),
(8, 'Whitefield', '560066', 3, true),
(9, 'Indiranagar', '560038', 3, true)
ON CONFLICT (id) DO NOTHING;

-- Job Roles
INSERT INTO job_roles (id, name, category, is_active) VALUES
(1, 'Software Developer', 'IT', true),
(2, 'Data Entry Operator', 'Office', true),
(3, 'Sales Executive', 'Sales', true),
(4, 'Customer Support', 'Support', true),
(5, 'Delivery Partner', 'Logistics', true),
(6, 'Accountant', 'Finance', true),
(7, 'Teacher', 'Education', true),
(8, 'Graphic Designer', 'Design', true)
ON CONFLICT (id) DO NOTHING;

-- Skills (role_id: 1=IT, 2=Data Entry, 3=Sales, 4=Support)
INSERT INTO skills (id, name, role_id, is_active) VALUES
-- IT Skills (role_id = 1)
(1, 'Java', 1, true),
(2, 'JavaScript', 1, true),
(3, 'Python', 1, true),
(4, 'SQL', 1, true),
(5, 'React', 1, true),
(6, 'Spring Boot', 1, true),
-- Data Entry Skills (role_id = 2)
(7, 'MS Excel', 2, true),
(8, 'Typing Speed 40+ WPM', 2, true),
(9, 'Data Analysis', 2, true),
(10, 'English', 2, true),
-- Sales Skills (role_id = 3)
(11, 'Communication', 3, true),
(12, 'Negotiation', 3, true),
(13, 'Field Sales', 3, true),
(14, 'CRM Software', 3, true),
-- Customer Support Skills (role_id = 4)
(15, 'Phone Handling', 4, true),
(16, 'Email Support', 4, true),
(17, 'Problem Solving', 4, true),
(18, 'Hindi', 4, true)
ON CONFLICT (id) DO NOTHING;