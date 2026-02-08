DROP DATABASE IF EXISTS medical_booking_system;
CREATE DATABASE medical_booking_system;
USE medical_booking_system;

DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS invoices;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS doctor_services;
DROP TABLE IF EXISTS services;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       email VARCHAR(150) NOT NULL UNIQUE,
                       fiscal_code VARCHAR(50),
                       name VARCHAR(100),
                       surname VARCHAR(100),
                       specialization VARCHAR(100)
);

CREATE TABLE services (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(200) NOT NULL,
                          base_price DOUBLE NOT NULL
);

CREATE TABLE doctor_services (
                                 doctor_id BIGINT NOT NULL,
                                 service_id BIGINT NOT NULL,
                                 PRIMARY KEY (doctor_id, service_id),
                                 FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE,
                                 FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE
);

CREATE TABLE appointments (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              patient_id BIGINT NOT NULL,
                              doctor_id BIGINT NOT NULL,
                              service_id BIGINT NOT NULL,
                              requested_date DATE NOT NULL,
                              confirmed_time TIME,
                              status VARCHAR(50) NOT NULL,
                              FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
                              FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE,
                              FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE
);

CREATE TABLE invoices (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          appointment_id BIGINT NOT NULL,
                          amount DOUBLE NOT NULL,
                          payment_status VARCHAR(50) NOT NULL,
                          FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE
);

CREATE TABLE notifications (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               patient_id BIGINT NOT NULL,
                               appointment_id BIGINT NOT NULL,
                               message TEXT NOT NULL,
                               type VARCHAR(50) NOT NULL,
                               is_read BOOLEAN DEFAULT FALSE,
                               FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
                               FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE
);
CREATE USER 'guest' IDENTIFIED BY 'guest';
GRANT ALL PRIVILEGES TO 'guest';