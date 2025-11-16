CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS trainees (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    date_of_birth DATE,
    address VARCHAR(255),
    CONSTRAINT fk_trainee_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS training_types (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    training_type_name VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO training_types (training_type_name) VALUES
    ('FITNESS'),
    ('YOGA'),
    ('CARDIO'),
    ('BOXING'),
    ('PILATES'),
    ('CROSSFIT'),
    ('SWIMMING');

CREATE TABLE IF NOT EXISTS trainers (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    specialization_id BIGINT NOT NULL,
    CONSTRAINT fk_trainer_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_trainer_specialization FOREIGN KEY (specialization_id) REFERENCES training_types(id)
);

CREATE TABLE IF NOT EXISTS trainers_trainees (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    trainer_id BIGINT NOT NULL,
    trainee_id BIGINT NOT NULL,
    CONSTRAINT fk_trainers_trainees_trainer FOREIGN KEY (trainer_id) REFERENCES trainers(id) ON DELETE CASCADE,
    CONSTRAINT fk_trainers_trainees_trainee FOREIGN KEY (trainee_id) REFERENCES trainees(id) ON DELETE CASCADE,
    CONSTRAINT uk_trainer_trainee UNIQUE (trainer_id, trainee_id)
);

CREATE TABLE IF NOT EXISTS trainings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    trainee_id BIGINT NOT NULL,
    trainer_id BIGINT NOT NULL,
    training_name VARCHAR(255) NOT NULL,
    training_type_id BIGINT NOT NULL,
    training_date DATE NOT NULL,
    training_duration BIGINT NOT NULL,
    CONSTRAINT fk_training_trainee FOREIGN KEY (trainee_id) REFERENCES trainees(id) ON DELETE CASCADE,
    CONSTRAINT fk_training_trainer FOREIGN KEY (trainer_id) REFERENCES trainers(id) ON DELETE CASCADE,
    CONSTRAINT fk_training_type FOREIGN KEY (training_type_id) REFERENCES training_types(id)
);

CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_trainee_user_id ON trainees(user_id);
CREATE INDEX idx_training_trainee_id ON trainings(trainee_id);
CREATE INDEX idx_training_trainer_id ON trainings(trainer_id);
