import mongoose, { ConnectOptions } from 'mongoose';
import {Environment} from "./environment";


const databaseUrl = Environment.getMongoUrl();
const databaseName = 'mongo-db';


export async function connect() {
    try {
        await mongoose.connect(databaseUrl);
    } catch (error) {
        console.error('Error connecting to MongoDB:', error);
        throw error;
    }
}

export default mongoose;