# Kubernetes deployment

## Redosled pokretanja
1. rabbitmq
2. mysql-db
3. redis
4. mongo-db
5. user-service
6. flask-api-service
7. exchange-service
8. contracts-service
9. companies-service
10. frontend
11. ingress-dev (ili ingress-production sa podešenim sertifikatom)

## Način pokretanja
### Sve sem mongo-db
kubectl apply -f PUTANJA_YAML_FAJLA -n NAMESPACE

### mongo-db
#### Instaliranje pomoću Helm-a
helm install mongo-db oci://registry-1.docker.io/bitnamicharts/mongodb-sharded -n NAMESPACE --set auth.enabled=true --set fullnameOverride=mongo-db

Skripta mongo-create-db.sh služi za kreiranje i podešavanje baze podataka

#### Pokrenuti skriptu na sledeći način:
1. chmod +x mongo-create-db.sh
2. ./mongo-create-db.sh NAMESPACE IME_MONGO_BAZE IME_KORISNIKA ŠIFRA_KORISNIKA
