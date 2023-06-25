#!/usr/bin/env bash

set -euo pipefail

NAMESPACE="$1"
NEW_MONGODB_DB="$2"
NEW_MONGODB_USER="$3"
NEW_MONGODB_USER_PASSWORD="$4"

export MONGODB_ROOT_PASSWORD=$(kubectl get secret --namespace ${NAMESPACE} mongo-db -o jsonpath="{.data.mongodb-root-password}" | base64 -d)

echo "Creating mongo user ${NEW_MONGODB_USER} for database ${NEW_MONGODB_DB}..."

kubectl --namespace="${NAMESPACE}" exec $(kubectl --namespace="${NAMESPACE}" get pods -o name | grep -m1 mongo-db-mongos | cut -d'/' -f 2) -c mongos -- mongosh -u "root" -p "${MONGODB_ROOT_PASSWORD}" --authenticationDatabase admin --authenticationMechanism=SCRAM-SHA-256 --eval 'db.getSiblingDB("admin").createRole({ role: "SystemRole", privileges: [{resource:{db:"admin",collection:""},actions:["killop","inprog"]}, {resource:{db:"'"${NEW_MONGODB_DB}"'",collection:""},actions:["killop","inprog"]}], roles: [] });'
kubectl --namespace="${NAMESPACE}" exec $(kubectl --namespace="${NAMESPACE}" get pods -o name | grep -m1 mongo-db-mongos | cut -d'/' -f 2) -- mongosh -u "root" -p "${MONGODB_ROOT_PASSWORD}" --authenticationDatabase admin --authenticationMechanism=SCRAM-SHA-256 --eval 'db.getSiblingDB("admin").createUser({user:"'"${NEW_MONGODB_USER}"'",pwd:"'"${NEW_MONGODB_USER_PASSWORD}"'",roles:[{role:"readWrite",db:"'"${NEW_MONGODB_DB}"'"},{role:"SystemRole",db:"admin"},"clusterManager"]});'

echo "Enabling sharding for database ${NEW_MONGODB_DB}"

kubectl --namespace="${NAMESPACE}" exec $(kubectl --namespace="${NAMESPACE}" get pods -o name | grep -m1 mongo-db-mongos | cut -d'/' -f 2) -c mongos -- mongosh -u "root" -p "${MONGODB_ROOT_PASSWORD}" --authenticationDatabase admin --authenticationMechanism=SCRAM-SHA-256 --eval "sh.enableSharding('${NEW_MONGODB_DB}');"
