INSERT INTO users (user_name, user_password, user_role, user_email, user_phone_number) VALUES
('admin01', 'adminpass', 'Admin', 'admin@sunshine.com', '0900000001'),
('staff01', 'staffpass1', 'Staff', 'staff01@sunshine.com', '0900000002'),
('staff02', 'staffpass2', 'Staff', 'staff02@sunshine.com', '0900000003');

INSERT INTO log_history (log_status, user_id) VALUES
('login', 1), ('logout', 1),
('login', 2), ('logout', 2),
('login', 3), ('logout', 3),
('login', 2), ('logout', 2);

INSERT INTO customer (cus_name, cus_email, cus_phone_number) VALUES
('Alice Brown', 'alice@gmail.com', '0911000001'),
('Bob Smith', 'bob@gmail.com', '0911000002'),
('Charlie Lee', 'charlie@gmail.com', '0911000003'),
('Daisy Tran', 'daisy@gmail.com', '0911000004'),
('Ethan Nguyen', 'ethan@gmail.com', '0911000005'),
('Fiona Le', 'fiona@gmail.com', '0911000006'),
('George Hoang', 'george@gmail.com', '0911000007'),
('Hannah Pham', 'hannah@gmail.com', '0911000008'),
('Ivan Vu', 'ivan@gmail.com', '0911000009'),
('Julia Dang', 'julia@gmail.com', '0911000010');

INSERT INTO equipment (equipment_name, equipment_fee, equipment_number, equipment_available) VALUES
('Surfboard', 40, 10, 10),
('Kayak', 40, 10, 10),
('Paddleboard', 40, 10, 10),
('Snorkeling Gear', 40, 10, 10),
('Beach Bike', 40, 10, 10);

INSERT INTO items (item_name, equipment_id) VALUES
-- Surfboard
('Surfboard #1', 1), ('Surfboard #2', 1), ('Surfboard #3', 1), ('Surfboard #4', 1), ('Surfboard #5', 1),
('Surfboard #6', 1), ('Surfboard #7', 1), ('Surfboard #8', 1), ('Surfboard #9', 1), ('Surfboard #10', 1),

-- Kayak
('Kayak #1', 2), ('Kayak #2', 2), ('Kayak #3', 2), ('Kayak #4', 2), ('Kayak #5', 2),
('Kayak #6', 2), ('Kayak #7', 2), ('Kayak #8', 2), ('Kayak #9', 2), ('Kayak #10', 2),

-- Paddleboard
('Paddleboard #1', 3), ('Paddleboard #2', 3), ('Paddleboard #3', 3), ('Paddleboard #4', 3), ('Paddleboard #5', 3),
('Paddleboard #6', 3), ('Paddleboard #7', 3), ('Paddleboard #8', 3), ('Paddleboard #9', 3), ('Paddleboard #10', 3),

-- Snorkeling Gear
('Snorkeling Gear #1', 4), ('Snorkeling Gear #2', 4), ('Snorkeling Gear #3', 4), ('Snorkeling Gear #4', 4), ('Snorkeling Gear #5', 4),
('Snorkeling Gear #6', 4), ('Snorkeling Gear #7', 4), ('Snorkeling Gear #8', 4), ('Snorkeling Gear #9', 4), ('Snorkeling Gear #10', 4),

-- Beach Bike
('Beach Bike #1', 5), ('Beach Bike #2', 5), ('Beach Bike #3', 5), ('Beach Bike #4', 5), ('Beach Bike #5', 5),
('Beach Bike #6', 5), ('Beach Bike #7', 5), ('Beach Bike #8', 5), ('Beach Bike #9', 5), ('Beach Bike #10', 5);

INSERT INTO instructor (instructor_name, instructor_email, instructor_phone_number) VALUES
('Mark Ocean', 'mark@sunshine.com', '0922000001'),
('Luna Wave', 'luna@sunshine.com', '0922000002'),
('Jack Tide', 'jack@sunshine.com', '0922000003');

INSERT INTO lesson (lesson_name, lesson_fee, instructor_id) VALUES
('Surfboard Usage Instruction', 25, 1),
('Kayak Usage Instruction', 25, 2),
('Paddleboard Usage Instruction', 25, 3),
('Snorkeling Instruction', 20, 1),
('Beach Bike Safety Instruction', 15, 2);

INSERT INTO rental_contract (rental_duration, rental_fee, rental_status, lesson_id, customer_id, equipment_id) VALUES
(2, 80, 'Completed', 1, 1, 1),
(1, 40, 'Completed', NULL, 2, 2),
(3, 120, 'Active', 2, 3, 2),
(2, 80, 'Completed', NULL, 4, 3),
(4, 160, 'Overdue', 3, 5, 3),
(1, 40, 'Completed', NULL, 6, 4),
(2, 80, 'Completed', 4, 7, 4),
(3, 120, 'Active', NULL, 8, 5),
(1, 40, 'Completed', 5, 9, 5),
(2, 80, 'Completed', NULL, 10, 1),

(3, 120, 'Active', NULL, 1, 2),
(2, 80, 'Completed', 2, 2, 3),
(1, 40, 'Completed', NULL, 3, 4),
(2, 80, 'Completed', 4, 4, 5),
(3, 120, 'Overdue', NULL, 5, 1),
(1, 40, 'Completed', 1, 6, 2),
(2, 80, 'Completed', NULL, 7, 3),
(4, 160, 'Active', 3, 8, 4),
(2, 80, 'Completed', NULL, 9, 5),
(1, 40, 'Completed', 5, 10, 1),

(2, 80, 'Completed', NULL, 1, 3),
(3, 120, 'Active', 2, 2, 4),
(1, 40, 'Completed', NULL, 3, 5),
(2, 80, 'Completed', 4, 4, 1),
(3, 120, 'Overdue', NULL, 5, 2),
(1, 40, 'Completed', 1, 6, 3),
(2, 80, 'Completed', NULL, 7, 4),
(3, 120, 'Active', 5, 8, 5),
(1, 40, 'Completed', NULL, 9, 1),
(2, 80, 'Completed', 3, 10, 2);

