import express from 'express';
import Contract, {ContractStatus, TransactionAction} from "../model/contracts/contract.model";
import {ErrorMessages} from "../model/errors/error-messages";
import {authToken, hasRole} from "../middleware/auth.middleware";
import {UserRoles} from "../model/users/user-roles";
import {getUserId, isAgent} from "../utils/jwt-util";
import axios from "axios";
import {Environment} from "../environment";

const router = express.Router();

const reserveTransactionsUrl = Environment.getReserveTransactionsUrl();


router.get('/', authToken, async (req, res) => {
    if(!hasRole(UserRoles.ROLE_SUPERVISOR, req)){
        res.status(403).send(ErrorMessages.unauthorizedAccessError);
    }
    else {
        try {
            const contracts = await Contract.find();
            res.json(contracts);
        } catch (error) {
            console.error(ErrorMessages.contractsGetError, error);
            res.status(500).send({message: ErrorMessages.contractsGetError});
        }
    }
});


router.get('/my-contracts', authToken, async (req, res) => {
    let agentId = getUserId(req);

    try {
        const contracts = await Contract.find({ agentId: agentId });
        res.json(contracts);
    } catch (error) {
        console.error(ErrorMessages.contractsGetError, error);
        res.status(500).send({message: ErrorMessages.contractsGetError});
    }
});


router.get('/:contractId', authToken, async (req, res) => {
    try {
        const contract = await Contract.findById(req.params['contractId']);
        res.json(contract);
    } catch (error) {
        console.error(ErrorMessages.contractsGetError, error);
        res.status(500).send({message: ErrorMessages.contractsGetError});
    }
});


router.get('/company-contracts/:companyId', authToken, async (req, res) => {
    try {
        const contracts = await Contract.find({ companyId: req.params['companyId'] });
        res.json(contracts);
    } catch (error) {
        console.error(ErrorMessages.contractsGetError, error);
        res.status(500).send({message: ErrorMessages.contractsGetError});
    }
});


router.post('/', authToken, async (req, res) => {
    try {
        let agentId = getUserId(req);

        const newContract = new Contract({
            companyId: req.body.companyId,
            agentId: agentId,
            status: ContractStatus.DRAFT,
            referenceNumber: req.body.referenceNumber,
            description: req.body.description,
            transactions: req.body.transactions
        });

        const savedContract = await newContract.save();

        try {
            let price = calculatePrice(savedContract.transactions);
            if(price > 0){
                let reserveTransactions = {
                    contractId: savedContract._id,
                    price: price
                }
                const response = await axios.post(reserveTransactionsUrl + '/reserve-assets', reserveTransactions, {
                    headers: {
                        Authorization: req.headers['authorization'] || ''
                    }
                });
            }

            res.json(savedContract);
        } catch (error) {
            console.error(ErrorMessages.contractsTransactionsError, error);
            res.status(500).send({message: ErrorMessages.contractsTransactionsError});
        }

    } catch (error) {
        console.error(ErrorMessages.contractsCreateError, error);
        res.status(500).send({message: ErrorMessages.contractsCreateError});
    }
});


router.put('/:contractId', authToken, async (req, res) => {
    try {
        let contractId = req.params['contractId'];
        const contract = await Contract.findById(contractId);
        if(contract){
            if(isAgent(req)){
                let agentId = getUserId(req);
                if(contract['agentId'] != agentId){
                    res.status(403).send({message: ErrorMessages.unauthorizedAccessError});
                    return;
                }
            }

            if(contract['status'] == ContractStatus.FINAL){
                res.status(403).send({message: ErrorMessages.contractsFinalisedUpdateError});
                return;
            }
            else {
                let update = {
                    companyId: req.body.companyId,
                    referenceNumber: req.body.referenceNumber,
                    description: req.body.description,
                    transactions: req.body.transactions,
                    modifiedDateTime: Date.now(),
                }

                await Contract.findByIdAndUpdate(contractId, update, { status: ContractStatus.DRAFT })

                try {
                    let price = calculatePrice(update.transactions);
                    if(price > 0){
                        let reserveTransactions = {
                            contractId: contractId,
                            price: price
                        }
                        const response = await axios.post(reserveTransactionsUrl + '/reserve-assets', reserveTransactions, {
                            headers: {
                                Authorization: req.headers['authorization'] || ''
                            }
                        });
                    }

                    res.status(200).send();
                } catch (error) {
                    console.error(ErrorMessages.contractsTransactionsError, error);
                    res.status(500).send({message: ErrorMessages.contractsTransactionsError});
                }
            }
        }
    } catch (error) {
        console.error(ErrorMessages.contractsUpdateError, error);
        res.status(500).send({message: ErrorMessages.contractsUpdateError});
    }
});


router.delete('/:contractId', authToken, async (req, res) => {
    if(!hasRole(UserRoles.ROLE_SUPERVISOR, req)){
        res.status(403).send({message: ErrorMessages.unauthorizedAccessError});
    }

    try {
        let contractId = req.params['contractId'];

        const contract = await Contract.findById(contractId);
        if(contract){
            if(contract['status'] == ContractStatus.FINAL){
                res.status(403).send({message: ErrorMessages.contractsFinalisedDeleteError});
                return;
            }
            else {
                await Contract.deleteOne({ _id: contractId });

                try {
                    const response = await axios.delete(reserveTransactionsUrl + '/' + contractId, {
                        headers: {
                            Authorization: req.headers['authorization'] || ''
                        }
                    });

                    res.status(200).send();
                } catch (error) {
                    console.error(ErrorMessages.contractsTransactionsError, error);
                    res.status(500).send({message: ErrorMessages.contractsTransactionsError});
                }
            }
        }
    } catch (error) {
        console.error(ErrorMessages.contractsDeleteError, error);
        res.status(500).send({message: ErrorMessages.contractsDeleteError});
    }
});


function calculatePrice(transactions): number{
    let price = 0;
    for(let transaction of transactions){
        if(transaction.action == TransactionAction.BUY){
            price += transaction.price * transaction.quantity;
        }
    }

    return price;
}

export default router;
