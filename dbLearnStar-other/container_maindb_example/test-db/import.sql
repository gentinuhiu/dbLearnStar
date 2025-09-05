-- === STUDENTS / COURSES / ENROLLMENTS ===

-- Students
CREATE TABLE student (
  id           SERIAL PRIMARY KEY,
  first_name   TEXT NOT NULL,
  last_name    TEXT NOT NULL,
  email        TEXT UNIQUE NOT NULL,
  created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Courses
CREATE TABLE course (
  id           SERIAL PRIMARY KEY,
  code         TEXT UNIQUE NOT NULL,     -- e.g., CS101
  title        TEXT NOT NULL,
  credits      INT  NOT NULL CHECK (credits BETWEEN 1 AND 10),
  created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Enrollment (many-to-many)
CREATE TABLE enrollment (
  id           SERIAL PRIMARY KEY,
  student_id   INT NOT NULL REFERENCES student(id) ON DELETE CASCADE,
  course_id    INT NOT NULL REFERENCES course(id)  ON DELETE CASCADE,
  enrolled_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  status       TEXT NOT NULL DEFAULT 'enrolled' CHECK (status IN ('enrolled','dropped','completed')),
  grade        SMALLINT CHECK (grade BETWEEN 0 AND 100),
  -- prevent duplicate enrollments
  UNIQUE (student_id, course_id)
);

-- Helpful index for lookups (optional)
CREATE INDEX idx_enrollment_course ON enrollment(course_id);
CREATE INDEX idx_enrollment_student ON enrollment(student_id);

-- === SAMPLE DATA ===

-- Students
INSERT INTO student (first_name, last_name, email) VALUES
 ('Ana',   'Petrova',   'ana.petrova@example.com'),
 ('Boris', 'Trajkov',   'boris.trajkov@example.com'),
 ('Elena', 'Stojanova', 'elena.stojanova@example.com'),
 ('Goran', 'Ivanov',    'goran.ivanov@example.com');

-- Courses
INSERT INTO course (code, title, credits) VALUES
 ('CS101', 'Intro to Computer Science', 6),
 ('DB201', 'Databases & SQL',           5),
 ('ALG150','Discrete Math & Algorithms',5);

-- Enrollments
INSERT INTO enrollment (student_id, course_id, status) VALUES
 ((SELECT id FROM student WHERE email='ana.petrova@example.com'),   (SELECT id FROM course WHERE code='CS101'), 'enrolled'),
 ((SELECT id FROM student WHERE email='ana.petrova@example.com'),   (SELECT id FROM course WHERE code='DB201'), 'enrolled'),
 ((SELECT id FROM student WHERE email='boris.trajkov@example.com'), (SELECT id FROM course WHERE code='CS101'), 'enrolled'),
 ((SELECT id FROM student WHERE email='elena.stojanova@example.com'),(SELECT id FROM course WHERE code='ALG150'),'enrolled'),
 ((SELECT id FROM student WHERE email='goran.ivanov@example.com'),  (SELECT id FROM course WHERE code='DB201'), 'enrolled');

-- Add some grades to simulate completion
UPDATE enrollment e
SET status='completed', grade=92
WHERE e.student_id = (SELECT id FROM student WHERE email='ana.petrova@example.com')
  AND e.course_id  = (SELECT id FROM course WHERE code='CS101');

UPDATE enrollment e
SET status='completed', grade=81
WHERE e.student_id = (SELECT id FROM student WHERE email='boris.trajkov@example.com')
  AND e.course_id  = (SELECT id FROM course WHERE code='CS101');

-- Example: a student dropped a course
UPDATE enrollment e
SET status='dropped'
WHERE e.student_id = (SELECT id FROM student WHERE email='goran.ivanov@example.com')
  AND e.course_id  = (SELECT id FROM course WHERE code='DB201');

-- === QUICK CHECKS ===

-- 1) Who’s in which course?
SELECT s.first_name || ' ' || s.last_name AS student,
       c.code, c.title, e.status, e.grade
FROM enrollment e
JOIN student s ON s.id = e.student_id
JOIN course  c ON c.id = e.course_id
ORDER BY c.code, student;

-- 2) Course roster counts
SELECT c.code, c.title,
       COUNT(*) FILTER (WHERE e.status='enrolled')  AS currently_enrolled,
       COUNT(*) FILTER (WHERE e.status='completed') AS completed,
       COUNT(*) FILTER (WHERE e.status='dropped')   AS dropped
FROM course c
LEFT JOIN enrollment e ON e.course_id = c.id
GROUP BY c.id
ORDER BY c.code;

-- 3) A student’s transcript-style view
SELECT s.first_name || ' ' || s.last_name AS student,
       c.code, c.title, e.status, e.grade
FROM student s
JOIN enrollment e ON e.student_id = s.id
JOIN course c     ON c.id = e.course_id
WHERE s.email = 'ana.petrova@example.com'
ORDER BY c.code;

-- 4) Prevent bad data demo (will fail due to UNIQUE):
-- INSERT INTO enrollment (student_id, course_id) VALUES
-- ((SELECT id FROM student WHERE email='ana.petrova@example.com'),
--  (SELECT id FROM course  WHERE code='CS101'));
