-- COMPLETE DATABASE FIX FOR DISPATCH, PACKING, AND JOB WORK
-- Run these SQL commands in MySQL Workbench or MySQL command line

USE Vinar;

-- =============================================
-- JOB WORK MODULE TABLES
-- =============================================

-- Drop existing job work tables if they exist
DROP TABLE IF EXISTS job_work_line;
DROP TABLE IF EXISTS job_work_receipt;
DROP TABLE IF EXISTS job_work_challan;
DROP TABLE IF EXISTS job_work_order;
DROP TABLE IF EXISTS job_work_vendor;

-- Create job work vendor table
CREATE TABLE job_work_vendor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vendor_id VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    gstin VARCHAR(20),
    pan VARCHAR(20),
    bank_name VARCHAR(255),
    account_number VARCHAR(50),
    ifsc_code VARCHAR(20),
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create job work order table
CREATE TABLE job_work_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_work_id VARCHAR(50) UNIQUE NOT NULL,
    po_id VARCHAR(50),
    vendor_id BIGINT NOT NULL,
    operation_type ENUM('cutting', 'machining', 'coating', 'heat_treatment', 'welding', 'other') NOT NULL,
    material_description TEXT,
    material_sent_qty DOUBLE NOT NULL,
    material_sent_uom VARCHAR(20) DEFAULT 'MT',
    material_received_qty DOUBLE DEFAULT 0,
    date_sent DATE NOT NULL,
    expected_return_date DATE,
    date_received DATE,
    billing_amount DECIMAL(15, 2) DEFAULT 0,
    billing_status ENUM('pending', 'paid') DEFAULT 'pending',
    status ENUM('sent', 'in_progress', 'received', 'completed') DEFAULT 'sent',
    remarks TEXT,
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (vendor_id) REFERENCES job_work_vendor(id) ON DELETE RESTRICT
);

-- Create job work challan table
CREATE TABLE job_work_challan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    challan_no VARCHAR(50) UNIQUE NOT NULL,
    challan_date DATE NOT NULL,
    job_work_id BIGINT NOT NULL,
    vehicle_no VARCHAR(50),
    driver_name VARCHAR(100),
    driver_contact VARCHAR(20),
    status ENUM('issued', 'pending_return', 'returned', 'completed') DEFAULT 'issued',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (job_work_id) REFERENCES job_work_order(id) ON DELETE CASCADE
);

-- Create job work line items table
CREATE TABLE job_work_line (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    challan_id BIGINT NOT NULL,
    item_code VARCHAR(50) NOT NULL,
    description TEXT,
    qty_issued DOUBLE NOT NULL,
    qty_received DOUBLE DEFAULT 0,
    weight DOUBLE,
    uom VARCHAR(20) DEFAULT 'MT',
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (challan_id) REFERENCES job_work_challan(id) ON DELETE CASCADE
);

-- Create job work receipt table
CREATE TABLE job_work_receipt (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receipt_no VARCHAR(50) UNIQUE NOT NULL,
    challan_id BIGINT NOT NULL,
    receipt_date DATE NOT NULL,
    qty_received DOUBLE NOT NULL,
    actual_weight DOUBLE NOT NULL,
    kata_weight DOUBLE NOT NULL,
    difference DOUBLE GENERATED ALWAYS AS (actual_weight - kata_weight) STORED,
    job_charge DECIMAL(15, 2) DEFAULT 0,
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    approved_by VARCHAR(100),
    approved_at TIMESTAMP NULL,
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (challan_id) REFERENCES job_work_challan(id) ON DELETE CASCADE
);

-- =============================================
-- SAMPLE DATA FOR JOB WORK MODULE
-- =============================================

-- Insert sample vendors
INSERT INTO job_work_vendor (vendor_id, name, contact_person, phone, email, address, gstin, pan, status) VALUES
('VEND001', 'Precision Engineering Works', 'Rajesh Kumar', '9876543210', 'precision@example.com', '123 Industrial Area, Mumbai', '22AAAAA0000A1Z5', 'AAAPL1234C', 'active'),
('VEND002', 'Metallurgical Solutions', 'Vikram Singh', '9876543211', 'metallurgical@example.com', '456 Tech Park, Pune', '33BBBBB0000B2Z5', 'BBBPQ5678D', 'active'),
('VEND003', 'Quality Coatings Ltd.', 'Priya Sharma', '9876543212', 'quality@example.com', '789 MIDC, Nashik', '44CCCCC0000C3Z5', 'CCCRS9012E', 'active');

-- Insert sample job work orders
INSERT INTO job_work_order (job_work_id, po_id, vendor_id, operation_type, material_description, material_sent_qty, date_sent, expected_return_date, billing_amount, status, created_by) VALUES
('JW20231114001', 'PO12345', 1, 'machining', 'Steel Rods - 25mm Diameter', 5.25, '2023-11-01', '2023-11-15', 12500.00, 'sent', 'admin'),
('JW20231114002', 'PO12346', 2, 'heat_treatment', 'Alloy Steel Components', 2.75, '2023-11-05', '2023-11-20', 18500.00, 'in_progress', 'admin'),
('JW20231114003', 'PO12347', 3, 'coating', 'MS Plates - 10mm Thick', 8.50, '2023-11-10', '2023-11-25', 9500.00, 'sent', 'admin');

-- Insert sample job work challans
INSERT INTO job_work_challan (challan_no, challan_date, job_work_id, vehicle_no, driver_name, driver_contact, status) VALUES
('JWC20231114001', '2023-11-01', 1, 'MH12AB1234', 'Ramesh Patel', '9876543220', 'issued'),
('JWC20231114002', '2023-11-05', 2, 'MH12CD5678', 'Suresh Kumar', '9876543221', 'pending_return'),
('JWC20231114003', '2023-11-10', 3, 'MH12EF9012', 'Amit Singh', '9876543222', 'issued');

-- Insert sample job work line items
INSERT INTO job_work_line (challan_id, item_code, description, qty_issued, weight, uom, remarks) VALUES
(1, 'SR-25-1000', 'Steel Rod 25mm x 1m', 100, 5.25, 'MT', 'Precision machining required'),
(2, 'ASC-101', 'Alloy Steel Components', 50, 2.75, 'MT', 'Heat treatment as per spec'),
(3, 'MSP-10-2000', 'MS Plate 10mm x 2m', 25, 8.50, 'MT', 'Powder coating - RAL 3000');

-- Insert sample job work receipt
INSERT INTO job_work_receipt (receipt_no, challan_id, receipt_date, qty_received, actual_weight, kata_weight, job_charge, status, remarks) VALUES
('JWR20231115001', 1, '2023-11-15', 100, 5.20, 5.25, 12500.00, 'approved', 'Received in good condition');

-- Update job work order status for received items
UPDATE job_work_order SET 
    material_received_qty = 5.20,
    date_received = '2023-11-15',
    status = 'received'
WHERE id = 1;

-- =============================================
-- FIX: REMOVE UNIQUE CONSTRAINT FROM RECEIPTS
-- =============================================

-- Drop the unique constraint on challan_id in job_work_receipts table
-- This allows multiple receipts per challan
ALTER TABLE job_work_receipts DROP INDEX UK_m4ce2qgya8mi9b0x4he3bm99a;

-- =============================================
-- EXISTING DISPATCH AND PACKING TABLES
-- =============================================

-- Step 1: Drop existing tables to start fresh
DROP TABLE IF EXISTS packing;
DROP TABLE IF EXISTS dispatch;

-- Step 2: Create dispatch table with ALL required columns
CREATE TABLE dispatch (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dispatch_id VARCHAR(255),
    invoice_no VARCHAR(255),
    invoice_date DATE,
    section VARCHAR(255),
    customer VARCHAR(255),
    qty_issued DOUBLE,
    vehicle_no VARCHAR(255),
    jobwork_no VARCHAR(255),
    challan_no VARCHAR(255),
    po_no VARCHAR(255),
    po_order_date DATE,
    from_location VARCHAR(255),
    destination VARCHAR(255),
    status VARCHAR(255),
    dispatch_date DATE
);

-- Step 3: Create packing table with ALL required columns
CREATE TABLE packing (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sr_no INT,
    po_no VARCHAR(255),
    grade VARCHAR(255),
    colour_code VARCHAR(255),
    grade_section VARCHAR(255),
    section_wt VARCHAR(255),
    length DOUBLE,
    no_of_pcs INT,
    qty_in_mt DOUBLE,
    heat_no VARCHAR(255),
    challan_qty DOUBLE,
    challan_no VARCHAR(255),
    customer VARCHAR(255),
    doc_no VARCHAR(255),
    packing_date DATE,
    lorry_no VARCHAR(255),
    status VARCHAR(50),
    dispatch_id BIGINT,
    FOREIGN KEY (dispatch_id) REFERENCES dispatch(id) ON DELETE SET NULL
);

-- Step 4: Verify the tables were created correctly
DESCRIBE dispatch;
DESCRIBE packing;

-- Step 5: Show all tables to confirm
SHOW TABLES;
