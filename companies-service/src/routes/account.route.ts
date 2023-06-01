import express from 'express';
import {authToken} from "../middleware/auth.middleware";
import {ErrorMessages} from "../model/errors/error-messages";
import Account from "../model/companies/account.model";
import Contact from "../model/companies/contact.model";


const router = express.Router();


router.get('/list/:companyId', authToken, async (req, res) => {
    try {
        let companyId = req.params['companyId']
        const accounts = await Account.find({ companyId: companyId });
        res.json(accounts);
    } catch (error) {
        console.error(ErrorMessages.accountsGetError, error);
        res.status(500).send({ message: ErrorMessages.accountsGetError });
    }
});


router.post('/', authToken, async (req, res) => {
    try {
        const newAccount = new Account({
            companyId: req.body.companyId,
            accountNumber: req.body.accountNumber,
            bankName: req.body.bankName,
            type: req.body.type
        });

        const savedAccount = await newAccount.save();
        res.json(savedAccount);
    } catch (error) {
        console.error(ErrorMessages.accountsCreateError, error);
        res.status(500).send({ message: ErrorMessages.accountsCreateError });
    }
});


router.delete('/:accountId', authToken, async (req, res) => {
    try {
        let accountId = req.params['accountId'];

        const account = await Account.findById(accountId);
        if(account){
            await Account.deleteOne({ _id: accountId });
            res.status(200).send();
        }
    } catch (error) {
        console.error(ErrorMessages.accountsDeleteError, error);
        res.status(500).send({ message: ErrorMessages.accountsDeleteError });
    }
});


export default router;
