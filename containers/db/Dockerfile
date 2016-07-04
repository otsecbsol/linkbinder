FROM alexeiled/docker-oracle-xe-11g

MAINTAINER Tetsuo Aoyagi <aoyagi@opentone.co.jp>

ENV ORACLE_HOME /u01/app/oracle/product/11.2.0/xe
ENV PATH $ORACLE_HOME/bin:$PATH
ENV ORACLE_SID XE

EXPOSE 1521
EXPOSE 8080

CMD chown -R oracle:dba /u01/app/oracle/extdata && /start.sh

