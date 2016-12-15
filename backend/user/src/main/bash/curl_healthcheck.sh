#!/bin/sh
url="https://fidorp.syruppay.co.kr/healthcheck.jsp"
content="$(curl "$url")"
if [ "$content" = "skp_healthcheck_ok" ]
then
    echo "checkCommandSuccess"
else
    echo "invalid url"
fi
