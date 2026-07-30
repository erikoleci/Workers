#!/usr/bin/env bash
# Generates the RSA keypair used for signing/verifying JWTs.
# Run this once per environment (local dev, staging, prod) — the keys are
# gitignored on purpose and must never be committed to source control.
#
# Usage: ./generate-jwt-keys.sh   (run from buildcrew-backend/)

set -e
cd "$(dirname "$0")/src/main/resources"

openssl genrsa -out privateKey.pem 2048
openssl rsa -in privateKey.pem -pubout -out publicKey.pem

echo "✅ privateKey.pem / publicKey.pem generated in src/main/resources"
echo "⚠️  These are local dev keys. For staging/production, generate a separate"
echo "    pair and inject them via your deployment secret store, not git."
