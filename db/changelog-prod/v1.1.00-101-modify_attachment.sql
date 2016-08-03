--liquibase formatted sql
--changeset opentone:v1.1.00-101-modify_attachment

---------------------------
-- attachment
---------------------------
ALTER TABLE attachment ADD file_type NUMBER(1,0);
ALTER TABLE attachment ADD org_extracted_text CLOB;
ALTER TABLE attachment ADD extracted_text CLOB;
--
CREATE OR REPLACE VIEW v_attachment AS
  SELECT
    a.id                 AS id,
    a.no                 AS no,
    a.correspon_id       AS correspon_id,
    a.file_id            AS file_id,
    a.file_name          AS file_name,
    a.file_type          AS file_type,
    a.org_extracted_text AS org_extracted_text,
    a.extracted_text     AS extracted_text,
    a.created_by         AS created_by,
    a.created_at         AS created_at,
    a.updated_by         AS updated_by,
    a.updated_at         AS updated_at,
    a.delete_no          AS delete_no
  FROM (
         SELECT
           id,
           ROW_NUMBER() OVER (PARTITION BY correspon_id ORDER BY id) no,
           correspon_id,
           file_id,
           file_name,
           file_type,
           org_extracted_text,
           extracted_text,
           created_by,
           created_at,
           updated_by,
           updated_at,
           delete_no
         FROM attachment
         WHERE delete_no = 0
       ) a
;
