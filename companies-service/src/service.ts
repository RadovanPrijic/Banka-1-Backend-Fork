import express, {json, NextFunction, Request, Response} from 'express';
import companyRoute from "./routes/company.route";
import contactRoute from "./routes/contact.route";
import accountRoute from "./routes/account.route";
import {connect} from "./mongo-db";
import cors from 'cors'


const app = express();
const port = 8083;


connect()
    .then(() => console.log('Connected to MongoDB.'))
    .catch(() => console.log('Connection to MongoDB failed.'));

app.use(express.json());
app.use(cors());

app.use('/api/companies', companyRoute);
app.use('/api/contacts', contactRoute);
app.use('/api/accounts', accountRoute);

app.listen(port, () => {
    console.log('Contracts service is running.');
});
