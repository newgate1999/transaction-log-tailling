-- Create App User
CREATE USER INVENTORY IDENTIFIED BY dbz;
GRANT CONNECT TO INVENTORY;
GRANT DBA TO INVENTORY;


-- Create LogMiner Users/TBS

CREATE TABLESPACE LOGMINER_TBS DATAFILE '/u01/app/oracle/oradata/XE/logminer_tbs.dbf' SIZE 200M REUSE AUTOEXTEND ON MAXSIZE UNLIMITED;

CREATE USER c##dbzuser IDENTIFIED BY dbz DEFAULT TABLESPACE LOGMINER_TBS QUOTA UNLIMITED ON LOGMINER_TBS;
CREATE USER c##logminer IDENTIFIED BY dbz DEFAULT TABLESPACE LOGMINER_TBS QUOTA UNLIMITED ON LOGMINER_TBS;

GRANT CREATE SESSION TO c##dbzuser ;
GRANT CREATE SESSION TO c##logminer;

GRANT SELECT ON V_$DATABASE TO c##dbzuser;
GRANT FLASHBACK ANY TABLE TO c##dbzuser ;
GRANT SELECT ANY TABLE TO c##dbzuser ;
GRANT SELECT_CATALOG_ROLE TO c##dbzuser ;
GRANT EXECUTE_CATALOG_ROLE TO c##dbzuser ;
GRANT SELECT ANY TRANSACTION TO c##dbzuser ;
GRANT SELECT ANY DICTIONARY TO c##dbzuser ;

-- GRANT LOGMINING TO c##dbzuser ;
GRANT CREATE TABLE TO c##dbzuser ;
GRANT ALTER ANY TABLE TO c##dbzuser ;
GRANT LOCK ANY TABLE TO c##dbzuser ;
GRANT CREATE SEQUENCE TO c##dbzuser ;

-- GRANT LOGMINING TO c##logminer

GRANT CREATE SESSION TO c##logminer;
GRANT SELECT ON V_$DATABASE to c##logminer;
GRANT FLASHBACK ANY TABLE TO c##logminer;
GRANT LOCK ANY TABLE TO c##logminer;
GRANT CREATE TABLE TO c##logminer;
GRANT CREATE SEQUENCE TO c##logminer;
GRANT SELECT ON V_$LOG TO c##logminer;

-- GRANT LOGMINING TO c##logminer;
GRANT EXECUTE ON DBMS_LOGMNR TO c##logminer;
GRANT EXECUTE ON DBMS_LOGMNR_D TO c##logminer;
GRANT EXECUTE_CATALOG_ROLE TO c##logminer;
GRANT SELECT_CATALOG_ROLE TO c##logminer;
GRANT SELECT ANY TRANSACTION TO c##logminer;
GRANT SELECT ANY DICTIONARY TO c##logminer;

GRANT SELECT ON V_$LOGMNR_LOGS TO c##logminer;
GRANT SELECT ON V_$LOGMNR_CONTENTS TO c##logminer;
GRANT SELECT ON V_$LOGFILE TO c##logminer;
GRANT SELECT ON V_$ARCHIVED_LOG TO c##logminer;
GRANT SELECT ON V_$ARCHIVE_DEST_STATUS TO c##logminer;


GRANT INSERT ANY TABLE TO c##logminer; 
GRANT SELECT ANY TABLE TO c##logminer;
GRANT UPDATE ANY TABLE TO c##logminer;
GRANT DELETE ANY TABLE TO c##logminer;


CREATE USER debezium IDENTIFIED BY dbz;
  GRANT CONNECT TO debezium;
  GRANT CREATE SESSION TO debezium;
  GRANT CREATE TABLE TO debezium;
  GRANT CREATE SEQUENCE to debezium;
  ALTER USER debezium QUOTA 100M on users;

-- add REDO size and number

SET PAGES 100
COL STATUS FORMAT a8

SELECT a.group#, b.bytes/1024/1024, b.status
FROM v$logfile a, v$log b
WHERE a.group#=b.group#;


ALTER DATABASE ADD LOGFILE GROUP 3 SIZE 200M;
ALTER DATABASE ADD LOGFILE GROUP 4 SIZE 200M;
ALTER DATABASE ADD LOGFILE GROUP 5 SIZE 200M;



ALTER DATABASE ADD LOGFILE MEMBER '/u01/app/oracle/fast_recovery_area/XE/onlinelog/02_mf_3_j1t13w5g_.log' TO GROUP 3;
ALTER DATABASE ADD LOGFILE MEMBER '/u01/app/oracle/fast_recovery_area/XE/onlinelog/02_mf_4_j1t13wk0_.log' TO GROUP 4;
ALTER DATABASE ADD LOGFILE MEMBER '/u01/app/oracle/fast_recovery_area/XE/onlinelog/02_mf_5_j1t13x3f_.log' TO GROUP 5;


SELECT a.group#, b.bytes/1024/1024, b.status
FROM v$logfile a, v$log b
WHERE a.group#=b.group#;


ALTER SYSTEM SWITCH LOGFILE;
ALTER SYSTEM SWITCH LOGFILE;
ALTER SYSTEM SWITCH LOGFILE;
ALTER SYSTEM SWITCH LOGFILE;

SELECT a.group#, b.bytes/1024/1024, b.status
FROM v$logfile a, v$log b
WHERE a.group#=b.group#;

ALTER SYSTEM SWITCH LOGFILE;
ALTER SYSTEM SWITCH LOGFILE;



SELECT a.group#, b.bytes/1024/1024, b.status
FROM v$logfile a, v$log b
WHERE a.group#=b.group#;


-- Enable ARCHIVELOG mode

SHUTDOWN IMMEDIATE;
STARTUP MOUNT
ALTER DATABASE ARCHIVELOG;
ALTER DATABASE OPEN;
ARCHIVE LOG LIST;

-- Supplemental Logging
ALTER DATABASE ADD SUPPLEMENTAL LOG DATA (ALL) COLUMNS;
ALTER PROFILE DEFAULT LIMIT FAILED_LOGIN_ATTEMPTS UNLIMITED;

set pages 100
col member format a69
col status format a8

select a.group#, b.bytes/1024/1024, b.status
from v$logfile a, v$log b
where a.group#=b.group#;

-- Temporary DBA to forget about this in test
GRANT DBA TO c##logminer;
GRANT DBA TO DEBEZIUM;
GRANT DBA TO c##dbzuser;

-- Recreate REDO GROUPS 1 and 2

ALTER DATABASE DROP LOGFILE GROUP 1;
ALTER DATABASE DROP LOGFILE GROUP 2;

ALTER DATABASE ADD LOGFILE GROUP 1 SIZE 200M;
ALTER DATABASE ADD LOGFILE GROUP 2 SIZE 200M;


ALTER DATABASE ADD LOGFILE MEMBER '/u01/app/oracle/fast_recovery_area/XE/onlinelog/02_mf_1_.log' TO GROUP 1;
ALTER DATABASE ADD LOGFILE MEMBER '/u01/app/oracle/fast_recovery_area/XE/onlinelog/02_mf_2_.log' TO GROUP 2;
exit;