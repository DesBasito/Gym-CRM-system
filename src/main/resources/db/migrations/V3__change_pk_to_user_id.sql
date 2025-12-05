ALTER TABLE trainers_trainees DROP CONSTRAINT IF EXISTS fk_trainers_trainees_trainer;
ALTER TABLE trainers_trainees DROP CONSTRAINT IF EXISTS fk_trainers_trainees_trainee;
ALTER TABLE trainings DROP CONSTRAINT IF EXISTS fk_training_trainee;
ALTER TABLE trainings DROP CONSTRAINT IF EXISTS fk_training_trainer;

UPDATE trainers_trainees tt
SET trainer_id = (SELECT user_id FROM trainers WHERE id = tt.trainer_id);
UPDATE trainers_trainees tt
SET trainee_id = (SELECT user_id FROM trainees WHERE id = tt.trainee_id);


UPDATE trainings t
SET trainee_id = (SELECT user_id FROM trainees WHERE id = t.trainee_id);
UPDATE trainings t
SET trainer_id = (SELECT user_id FROM trainers WHERE id = t.trainer_id);

ALTER TABLE trainees DROP CONSTRAINT IF EXISTS trainees_pkey;
ALTER TABLE trainees DROP COLUMN IF EXISTS id;
ALTER TABLE trainees ADD PRIMARY KEY (user_id);

ALTER TABLE trainers DROP CONSTRAINT IF EXISTS trainers_pkey;
ALTER TABLE trainers DROP COLUMN IF EXISTS id;
ALTER TABLE trainers ADD PRIMARY KEY (user_id);

ALTER TABLE trainers_trainees
    ADD CONSTRAINT fk_trainers_trainees_trainer
    FOREIGN KEY (trainer_id) REFERENCES trainers(user_id) ON DELETE CASCADE;

ALTER TABLE trainers_trainees
    ADD CONSTRAINT fk_trainers_trainees_trainee
    FOREIGN KEY (trainee_id) REFERENCES trainees(user_id) ON DELETE CASCADE;

ALTER TABLE trainings
    ADD CONSTRAINT fk_training_trainee
    FOREIGN KEY (trainee_id) REFERENCES trainees(user_id) ON DELETE CASCADE;

ALTER TABLE trainings
    ADD CONSTRAINT fk_training_trainer
    FOREIGN KEY (trainer_id) REFERENCES trainers(user_id) ON DELETE CASCADE;