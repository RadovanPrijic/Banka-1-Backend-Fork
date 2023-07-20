export class Environment {

    private static isDev: boolean = false;

    public static getMongoUrl(){
        if(this.isDev) {
            return 'mongodb://banka1_mongodb:banka1_mongodb@localhost:27017';
        }
        else return 'mongodb://' + process.env.MONGODB_USER + ':' + process.env.MONGODB_PASSWORD  +'@mongo:27017';
    }

}