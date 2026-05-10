#!/usr/bin/env sh
set -eu

if docker compose version >/dev/null 2>&1; then
  COMPOSE="docker compose"
elif command -v docker-compose >/dev/null 2>&1; then
  COMPOSE="docker-compose"
else
  echo "Docker Compose nao encontrado. Instale o plugin 'docker compose' ou o binario 'docker-compose'."
  exit 1
fi

echo "Derrubando conteineres e removendo volumes..."
$COMPOSE down -v --remove-orphans

echo "Subindo conteineres novamente..."
$COMPOSE up -d

echo "Pronto."
