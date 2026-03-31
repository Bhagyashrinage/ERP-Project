-- Add receiver_name column to job_work_receipts table
ALTER TABLE job_work_receipts 
ADD COLUMN receiver_name VARCHAR(255) NOT NULL DEFAULT '';

-- Update existing records to have a default receiver name if needed
UPDATE job_work_receipts 
SET receiver_name = 'Unknown Receiver' 
WHERE receiver_name = '' OR receiver_name IS NULL;
