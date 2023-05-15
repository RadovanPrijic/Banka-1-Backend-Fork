import mongoose, { ConnectOptions } from 'mongoose';

const databaseUrl = 'mongodb://banka1_mongodb:banka1_mongodb@localhost:27017';
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