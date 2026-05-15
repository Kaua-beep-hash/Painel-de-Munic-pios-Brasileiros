#!/bin/sh
# mvnw — wrapper simples caso o Maven Wrapper não esteja presente
# Em produção o Dockerfile usa 'mvn' diretamente se este falhar
exec mvn "$@"
