export class Environment {

    private static isDev: boolean = false;

    public static getMongoUrl(){
        if(this.isDev) {
            return 'mongodb://banka1_mongodb:banka1_mongodb@localhost:27017';
        }
        else return 'mongodb://banka1_mongodb:banka1_mongodb@mongo-db:27017';
    }

    public static getReserveTransactionsUrl(){
        if(this.isDev) {
            return 'http://localhost:8080/api/users-contracts';
        }
        else return 'http://user-service:8080/api/users-contracts';
    }

    public static getFinaliseUrl(){
        if(this.isDev) {
            return 'http://localhost:8080/api/users-contracts/finalize-contract';
        }
        else return 'http://user-service:8080/api/users-contracts/finalize-contract';
    }
}