import {authToken, hasRole} from "../middleware/auth.middleware";
import express from "express";
import {ErrorMessages} from "../model/errors/error-messages";
import FinalisedContract from "../model/contracts/finalised-contract.model";
import multer from "multer";
import Contract, {ContractStatus, TransactionAction} from "../model/contracts/contract.model";
import {UserRoles} from "../model/users/user-roles";
import {Environment} from "../environment";
import axios from "axios";
import ContractModel from "../model/contracts/contract.model";
import {ContractTransactions} from "../model/contracts/contract-transactions";

const router = express.Router();

const finaliseUrl = Environment.getFinaliseUrl();

const storage = multer.memoryStorage();
const upload = multer({
    storage,
    fileFilter: (req, file, cb) => {
        if (file.mimetype !== 'application/pdf') {
            cb(new Error(ErrorMessages.contractsFinalisedFileFormat));
        }
        else {
            cb(null, true);
        }
    }
});


//download contract
router.get('/:contractId', authToken, async (req, res) => {
    try {
        let contractId = req.params['contractId'];
        const finalisedContract = await FinalisedContract.findOne({ contractId: contractId });

        if (!finalisedContract) {
            res.status(400).send(ErrorMessages.contractNotFound);
            return;
        }


        res.set('Content-Type', 'application/pdf');
        res.set('Content-Disposition', `attachment; filename="${finalisedContract['referenceNumber']}.pdf"`);

        res.send(finalisedContract['contractFile']);
    } catch (error) {
        console.error(ErrorMessages.contractsGetError, error);
        res.status(500).send(ErrorMessages.contractsGetError);
    }
});


router.post('/', authToken, upload.single('contractFile'), async (req, res) => {
    if(!hasRole(UserRoles.ROLE_SUPERVISOR, req)){
        res.status(403).send(ErrorMessages.unauthorizedAccessError);
        return;
    }

    try {
        const contractFile = req.file?.buffer;

        if (!contractFile) {
            res.status(400).send(ErrorMessages.contractsFinalisedFileMissing);
            return;
        }

        const finalisedContract = new FinalisedContract({
            contractId: req.body.contractId,
            referenceNumber: req.body.referenceNumber,
            contractFile: contractFile
        });

        await finalisedContract.save();
        const updatedContract = await Contract.findByIdAndUpdate(req.body.contractId, { status: ContractStatus.FINAL });
        const contract = updatedContract as ContractTransactions;

        let finaliseTransactions = {
            contractId: finalisedContract.contractId,
            sellPrice: calculateSellPrice(contract.transactions),
            stocks: getStocks(contract.transactions),
            userId: contract.agentId
        }

        try {
            const response = await axios.post(finaliseUrl, finaliseTransactions, {
                headers: {
                    Authorization: req.headers['authorization'] || ''
                }
            });

            res.status(201).send();
        } catch (error) {
            console.error(ErrorMessages.contractsTransactionsFinaliseError, error);
            res.status(500).send(ErrorMessages.contractsTransactionsFinaliseError);
        }

        /*
        * if (err) {
                res.status(500).send(ErrorMessages.contractsFinaliseError);
                return;
            } else {
                let finaliseTransactions = {
                    contractId: finalisedContract.contractId,
                    sellPrice: calculateSellPrice(result.transactions),
                    stocks: getStocks(result.transactions),
                    userId: result.agentId
                }

                try {
                    const response = await axios.post(finaliseUrl, finaliseTransactions, {
                        headers: {
                            Authorization: req.headers['authorization'] || ''
                        }
                    });

                    res.status(201).send();
                } catch (error) {
                    console.error(ErrorMessages.contractsTransactionsFinaliseError, error);
                    res.status(500).send(ErrorMessages.contractsTransactionsFinaliseError);
                }
            }*/

    } catch (error) {
        console.error(ErrorMessages.contractsFinaliseError, error);
        res.status(500).send(ErrorMessages.contractsFinaliseError);
    }
});


function calculateSellPrice(transactions: any){
    let sellPrice = 0;
    for(let transaction of transactions){
        if(transaction.action == TransactionAction.SELL){
            sellPrice += transaction.price * transaction.quantity;
        }
    }

    return sellPrice;
}

function getStocksByAction(transactions: any, transactionAction: TransactionAction){
    let stocks: any = [];
    for(let transaction of transactions){
        if(transaction.action == transactionAction){
            let stock = {
                symbol: transaction.symbol,
                quantity: transaction.quantity
            }
            stocks.push(stock);
        }
    }

    return stocks;
}

function getStocks(transactions: any){
    let stocks: any = [];
    for(let transaction of transactions){
        let stock = {
            symbol: transaction.symbol,
            quantity: transaction.quantity,
            transactionType: transaction.action
        }
        stocks.push(stock);
    }

    return stocks;
}


export default router;