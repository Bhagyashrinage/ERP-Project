-- Fix job_work_receipts table to remove unique constraint on challan_id
-- This allows multiple receipts per challan

USE Vinar;

-- Step 1: Create a backup of existing data
CREATE TABLE job_work_receipts_backup AS SELECT * FROM job_work_receipts;

-- Step 2: Drop the existing table
DROP TABLE job_work_receipts;

-- Step 3: Recreate the table without unique constraint on challan_id
CREATE TABLE job_work_receipts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    actual_weight DOUBLE NOT NULL,
    approved_at DATE DEFAULT NULL,
    approved_by VARCHAR(255) DEFAULT NULL,
    challan_no VARCHAR(255) NOT NULL,
    difference DOUBLE NOT NULL,
    job_charge DOUBLE NOT NULL,
    kata_weight DOUBLE NOT NULL,
    qty_received DOUBLE NOT NULL,
    receipt_date DATE NOT NULL,
    remarks VARCHAR(255) DEFAULT NULL,
    status VARCHAR(255) NOT NULL,
    challan_id BIGINT NOT NULL,
    CONSTRAINT FK_receipt_challan FOREIGN KEY (challan_id) REFERENCES job_work_challans(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Step 4: Restore the data
INSERT INTO job_work_receipts (id, actual_weight, approved_at, approved_by, challan_no, difference, job_charge, kata_weight, qty_received, receipt_date, remarks, status, challan_id)
SELECT id, actual_weight, approved_at, approved_by, challan_no, difference, job_charge, kata_weight, qty_received, receipt_date, remarks, status, challan_id
FROM job_work_receipts_backup;

-- Step 5: Clean up backup table
DROP TABLE job_work_receipts_backup;

-- Verify the table structure
SHOW CREATE TABLE job_work_receipts;
