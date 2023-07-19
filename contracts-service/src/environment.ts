export class Environment {

    private static isDev: boolean = false;

    public static getMongoUrl(){
        if(this.isDev) {
            return 'mongodb://root:Z2ZPY1HLzM@mongodb:27017/';
        }
        else return 'mongodb://' + process.env.MONGODB_USER + ':' + process.env.MONGODB_PASSWORD  +'@mongo-db:27017';
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