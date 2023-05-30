FROM openjdk:11-jre-slim-buster

RUN set -x && apt-get update && DEBIAN_FRONTEND=nointeractive apt-get install -y --no-install-recommends \
  openssl dumb-init procps wget htop joe ldap-utils

ARG LDAP_SERVER_ID

ENV PORT 10389

RUN echo "> LDAP-ID: $LDAP_SERVER_ID"

RUN mkdir /ldap
WORKDIR /ldap

ADD $LDAP_SERVER_ID /ldap
ADD resources /ldap
ADD certificates /ldap

RUN cd /ldap
RUN chmod +x start.sh

EXPOSE $PORT

CMD ["/ldap/start.sh"]
