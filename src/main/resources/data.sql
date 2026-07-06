-- Judge 1 (id esplicito 1)
INSERT INTO staff_members (id, name, email, hackathon_id) VALUES (1, 'Judge Test', 'judge@test.com', NULL);
INSERT INTO judges (id) VALUES (1);

-- Mentor 1 (id esplicito 2)
INSERT INTO staff_members (id, name, email, hackathon_id) VALUES (2, 'Mentor Test', 'mentor@test.com', NULL);
INSERT INTO mentors (id) VALUES (2);

-- Organizer (id esplicito 3)
INSERT INTO staff_members (id, name, email, hackathon_id) VALUES (3, 'Organizer Test', 'organizer@test.com', NULL);
INSERT INTO organizers (id) VALUES (3);

-- Judge 2
INSERT INTO staff_members (id, name, email, hackathon_id) VALUES (4, 'Judge Two', 'judge2@test.com', NULL);
INSERT INTO judges (id) VALUES (4);

-- Mentor 2 (id esplicito 5)
INSERT INTO staff_members (id, name, email, hackathon_id) VALUES (5, 'Mentor Two', 'mentor2@test.com', NULL);
INSERT INTO mentors (id) VALUES (5);

-- Riallinea il contatore AUTO_INCREMENT: il prossimo staff member creato via API avrà id 6
ALTER TABLE staff_members ALTER COLUMN id RESTART WITH 6;

-- Hackathon (organizer_id=3, judge_id=1)
INSERT INTO hackathons (name_hackathon, rulebook, registration_deadline, start_date, end_date,
                        location, prize, state_name, max_team_size, organizer_id, judge_id)
VALUES ('HackHub Test', 'Regolamento di prova', '2026-08-01', '2026-08-10', '2026-08-12',
        'Camerino', '1000€', 'RegistrationState', 5, 3, 1);
ALTER TABLE hackathons ALTER COLUMN id RESTART WITH 2;
UPDATE staff_members SET hackathon_id = 1 WHERE id = 2;

-- Team
INSERT INTO teams (team_name, hackathon_id) VALUES ('Team Test', 1);

ALTER TABLE teams ALTER COLUMN id RESTART WITH 2;

-- User
INSERT INTO users (name, email) VALUES ('Luigi', 'nuovo.luigi@test.com');
INSERT INTO users (name, email) VALUES ('Pippo', 'nuovo.pippo@test.com');
INSERT INTO users (name, email) VALUES ('Caio', 'nuovo.caio@test.com');
INSERT INTO users (name, email) VALUES ('Cesare', 'nuovo.cesare@test.com');
INSERT INTO users (name, email) VALUES ('Paolo', 'nuovo.paolo@test.com');
