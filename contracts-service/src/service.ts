import express, {json, NextFunction, Request, Response} from 'express';
import {connect} from "./mongo-db";
import contractsRoute from './routes/contract.route';
import {authToken} from "./middleware/auth.middleware";


const app = express();
const port = 8082;


connect()
    .then(() => console.log('Connected to MongoDB.'))
    .catch(() => console.log('Connection to MongoDB failed.'));

app.use(express.json());
//app.use(authToken);
app.use('/api/contracts', contractsRoute);

app.listen(port, () => {
    console.log('Contracts service is running.');
});
