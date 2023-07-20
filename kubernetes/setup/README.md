# Kubernetes setup

## Redosled instaliranja
1. rabbitmq
2. mariadb
3. redis
4. mongo

## Način instaliranja
### Sve sem mongo-db
kubectl apply -f PUTANJA_YAML_FAJLA -n NAMESPACE

### mongo
#### Instaliranje pomoću Helm-a
helm install mongo oci://registry-1.docker.io/bitnamicharts/mongodb-sharded -n NAMESPACE --set auth.enabled=true --set fullnameOverride=mongo
