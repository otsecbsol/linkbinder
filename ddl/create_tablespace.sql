-- production
CREATE TABLESPACE linkbinder DATAFILE '/u01/app/oracle/extdata/tbs_linkbinder01.dbf' SIZE 100M AUTOEXTEND ON NEXT 100M;
CREATE TABLESPACE linkbinder_ix DATAFILE '/u01/app/oracle/extdata/tbs_linkbinder_ix01.dbf' SIZE 100M AUTOEXTEND ON NEXT 100M;
-- test
CREATE TABLESPACE linkbinder_test DATAFILE '/u01/app/oracle/extdata/tbs_linkbinder_test01.dbf' SIZE 100M AUTOEXTEND ON NEXT 100M;
CREATE TABLESPACE linkbinder_test_ix DATAFILE '/u01/app/oracle/extdata/tbs_linkbinder_test_ix01.dbf' SIZE 100M AUTOEXTEND ON NEXT 100M;
quit;
