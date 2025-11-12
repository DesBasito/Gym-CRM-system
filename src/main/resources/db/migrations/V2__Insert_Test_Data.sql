-- Insert Users
INSERT INTO users (first_name, last_name, username, password, is_active) VALUES
    ('John', 'Doe', 'John.Doe', 'password123', true),
    ('Jane', 'Smith', 'Jane.Smith', 'pass456', true),
    ('Mike', 'Johnson', 'Mike.Johnson', 'secure789', true),
    ('Alice', 'Brown', 'Alice.Brown', 'mypass111', true),
    ('Bob', 'Wilson', 'Bob.Wilson', 'secret222', false),
    ('Charlie', 'Davis', 'Charlie.Davis', 'pwd333', true),
    ('Sarah', 'Connor', 'Sarah.Connor', 'term444', true)
ON CONFLICT (username) DO NOTHING;

-- Insert Trainees
INSERT INTO trainees (user_id, date_of_birth, address)
SELECT u.id, '1990-05-15', '123 Main St' FROM users u WHERE u.username = 'Alice.Brown'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO trainees (user_id, date_of_birth, address)
SELECT u.id, '1995-08-20', '456 Oak Ave' FROM users u WHERE u.username = 'Bob.Wilson'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO trainees (user_id, date_of_birth, address)
SELECT u.id, '1992-03-10', '789 Pine Rd' FROM users u WHERE u.username = 'Charlie.Davis'
ON CONFLICT (user_id) DO NOTHING;

-- Insert Trainers
INSERT INTO trainers (user_id, specialization_id)
SELECT u.id, tt.id FROM users u, training_types tt
WHERE u.username = 'John.Doe' AND tt.training_type_name = 'Fitness'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO trainers (user_id, specialization_id)
SELECT u.id, tt.id FROM users u, training_types tt
WHERE u.username = 'Jane.Smith' AND tt.training_type_name = 'Yoga'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO trainers (user_id, specialization_id)
SELECT u.id, tt.id FROM users u, training_types tt
WHERE u.username = 'Mike.Johnson' AND tt.training_type_name = 'Cardio'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO trainers (user_id, specialization_id)
SELECT u.id, tt.id FROM users u, training_types tt
WHERE u.username = 'Sarah.Connor' AND tt.training_type_name = 'Boxing'
ON CONFLICT (user_id) DO NOTHING;

-- Insert Trainer-Trainee relationships
INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT t.id, tr.id FROM trainers t, trainees tr, users tu, users uu
WHERE t.user_id = tu.id AND tu.username = 'John.Doe'
  AND tr.user_id = uu.id AND uu.username = 'Alice.Brown'
ON CONFLICT (trainer_id, trainee_id) DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT t.id, tr.id FROM trainers t, trainees tr, users tu, users uu
WHERE t.user_id = tu.id AND tu.username = 'Jane.Smith'
  AND tr.user_id = uu.id AND uu.username = 'Bob.Wilson'
ON CONFLICT (trainer_id, trainee_id) DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT t.id, tr.id FROM trainers t, trainees tr, users tu, users uu
WHERE t.user_id = tu.id AND tu.username = 'Mike.Johnson'
  AND tr.user_id = uu.id AND uu.username = 'Alice.Brown'
ON CONFLICT (trainer_id, trainee_id) DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT t.id, tr.id FROM trainers t, trainees tr, users tu, users uu
WHERE t.user_id = tu.id AND tu.username = 'Sarah.Connor'
  AND tr.user_id = uu.id AND uu.username = 'Charlie.Davis'
ON CONFLICT (trainer_id, trainee_id) DO NOTHING;

-- Insert Trainings
INSERT INTO trainings (trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration)
SELECT tr.id, t.id, 'Morning Workout', tt.id, '2024-01-15', 60
FROM trainees tr, trainers t, training_types tt, users uu, users tu
WHERE tr.user_id = uu.id AND uu.username = 'Alice.Brown'
  AND t.user_id = tu.id AND tu.username = 'John.Doe'
  AND tt.training_type_name = 'Fitness'
ON CONFLICT DO NOTHING;

INSERT INTO trainings (trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration)
SELECT tr.id, t.id, 'Yoga Session', tt.id, '2024-01-16', 90
FROM trainees tr, trainers t, training_types tt, users uu, users tu
WHERE tr.user_id = uu.id AND uu.username = 'Bob.Wilson'
  AND t.user_id = tu.id AND tu.username = 'Jane.Smith'
  AND tt.training_type_name = 'Yoga'
ON CONFLICT DO NOTHING;

INSERT INTO trainings (trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration)
SELECT tr.id, t.id, 'Cardio Training', tt.id, '2024-01-17', 45
FROM trainees tr, trainers t, training_types tt, users uu, users tu
WHERE tr.user_id = uu.id AND uu.username = 'Alice.Brown'
  AND t.user_id = tu.id AND tu.username = 'Mike.Johnson'
  AND tt.training_type_name = 'Cardio'
ON CONFLICT DO NOTHING;

INSERT INTO trainings (trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration)
SELECT tr.id, t.id, 'Boxing Class', tt.id, '2024-01-18', 75
FROM trainees tr, trainers t, training_types tt, users uu, users tu
WHERE tr.user_id = uu.id AND uu.username = 'Charlie.Davis'
  AND t.user_id = tu.id AND tu.username = 'Sarah.Connor'
  AND tt.training_type_name = 'Boxing'
ON CONFLICT DO NOTHING;

INSERT INTO trainings (trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration)
SELECT tr.id, t.id, 'Evening Fitness', tt.id, '2024-01-19', 50
FROM trainees tr, trainers t, training_types tt, users uu, users tu
WHERE tr.user_id = uu.id AND uu.username = 'Charlie.Davis'
  AND t.user_id = tu.id AND tu.username = 'John.Doe'
  AND tt.training_type_name = 'Fitness'
ON CONFLICT DO NOTHING;