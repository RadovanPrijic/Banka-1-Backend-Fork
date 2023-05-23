export class Environment {

    private static isDev: boolean = false;

    public static getMongoUrl(){
        if(this.isDev) {
            return 'mongodb://banka1_mongodb:banka1_mongodb@localhost:27017';
        }
        else return 'mongodb://banka1_mongodb:banka1_mongodb@mongo-db:27017';
    }

}