-- Исправляем NULL значения click_count для существующих записей
UPDATE links
SET click_count = 0
WHERE click_count IS NULL;

-- На всякий случай добавляем NOT NULL constraint с дефолтным значением
ALTER TABLE links
    ALTER COLUMN click_count SET DEFAULT 0,
ALTER COLUMN click_count SET NOT NULL;