
# Project 2: Sunshine Seashore Rental and Lesson System

## 1. Problem Description

Sunshine Seashore provides equipment rentals and optional instructor-led lessons for beach activities. Manual management of pricing, rental duration, equipment availability, and instructor scheduling frequently causes calculation errors, double bookings, unavailable equipment, and slow contract lookups. The company also lacks a login and authentication mechanism, leading to uncontrolled access to sensitive contract records.

The upgraded system must function as a complete rental and lesson management
platform and provide:

- Secure user login and authentication
- Customer profile management and rental history
- Equipment inventory tracking (quantity, condition, category)
- Rental contract creation, pricing, and lifecycle management
- Optional lesson package assignment with instructor scheduling
- Direct contract lookup through Random Access File I/O
- Search, sorting, filtering, and dashboard summaries
- Persistent storage using sequential and random-access files
- A fully interactive GUI for all workflows

---

## 2. Functional Requirements

### FR-0: Login and Authentication

- **FR-0.1:** The system shall provide a login screen requiring username and password.
- **FR-0.2:** The system shall validate login credentials against a stored user file.
- **FR-0.3:** The system shall support at least two roles (Admin, Staff).
- **FR-0.4:** The system shall restrict sensitive operations (deletion, file operations) to Admin users.
- **FR-0.5:** The system shall record successful logins and logouts.

---

### FR-1: Customer Management

- **FR-1.1:** The system shall allow creating, editing, and deleting customer profiles.
- **FR-1.2:** The system shall validate customer phone number and email format.
- **FR-1.3:** The system shall allow searching customers by name or phone number.
- **FR-1.4:** The system shall display a customer’s rental history.

---

### FR-2: Equipment Inventory Management

- **FR-2.1:** The system shall allow adding, updating, and removing equipment items.
- **FR-2.2:** The system shall track equipment condition (Good, Needs Repair, Out of Service).
- **FR-2.3:** The system shall maintain equipment availability by category and quantity.
- **FR-2.4:** The system shall prevent rentals if required equipment is unavailable.

---

### FR-3: Rental Contract Management

- **FR-3.1:** The system shall allow creating, viewing, updating, and deleting rental contracts.
- **FR-3.2:** The system shall validate the Contract Number as one uppercase letter followed by three digits.
- **FR-3.3:** The system shall validate rental duration between 60 and 7200 minutes.
- **FR-3.4:** The system shall calculate rental fees using:
  - $40 per full hour
  - $1 per extra minute (capped at 40 minutes)
- **FR-3.5:** The system shall support contract statuses (Draft, Active, Returned, Completed, Overdue).
- **FR-3.6:** The system shall auto-mark rentals as Overdue when past their return time.

---

### FR-4: Lesson Package and Instructor Scheduling

- **FR-4.1:** The system shall allow assigning optional lesson packages to a rental.
- **FR-4.2:** The system shall assign instructors to lesson packages.
- **FR-4.3:** The system shall prevent instructor schedule conflicts.
- **FR-4.4:** The system shall add lesson package charges to the rental’s total cost.

---

### FR-5: Dashboard, Search, and Direct Lookup

- **FR-5.1:** The system shall display all rentals in a sortable list (customer, status, price, duration).
- **FR-5.2:** The system shall filter rentals by status (Active, Completed, Overdue).
- **FR-5.3:** The system shall retrieve rental contracts using Random Access File lookup by Contract Number.
- **FR-5.4:** The system shall provide summary statistics (active rentals, overdue rentals, projected revenue).

---

### FR-6: Persistence and GUI Requirements

- **FR-6.1:** The system shall store customers, equipment, and lesson packages using Sequential File I/O.
- **FR-6.2:** The system shall store rental contracts using Random Access File I/O.
- **FR-6.3:** The system shall save login credentials in an encoded user file.
- **FR-6.4:** The GUI shall present tabs for Customers, Equipment, Rentals, Lessons, and Dashboard.
- **FR-6.5:** The GUI shall display pricing updates in real time.
- **FR-6.6:** The GUI shall display all validation errors via JOptionPane.
