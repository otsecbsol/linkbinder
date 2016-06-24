--
-----------------------
-- production
-----------------------
CREATE USER linkbinder
  IDENTIFIED BY linkbinder
  DEFAULT TABLESPACE linkbinder
;
GRANT
  CREATE session,
  ALTER  session,
  SELECT any dictionary,
  CREATE table,
  CREATE view,
  CREATE sequence,
  CREATE any synonym,
  CREATE procedure,
  CREATE any type,
  CREATE trigger,
  UNLIMITED TABLESPACE
TO linkbinder;
--
-----------------------
-- test
-----------------------
CREATE USER linkbinder_test
  IDENTIFIED BY linkbinder_test
  DEFAULT TABLESPACE linkbinder_test
;
GRANT
  CREATE session,
  ALTER  session,
  SELECT any dictionary,
  CREATE table,
  CREATE view,
  CREATE sequence,
  CREATE any synonym,
  CREATE procedure,
  CREATE any type,
  CREATE trigger,
  UNLIMITED TABLESPACE
TO linkbinder_test;
quit;
