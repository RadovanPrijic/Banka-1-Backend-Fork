import express from 'express';
import Contract, { ContractStatus } from "../model/contracts/contract.model";
import {ErrorMessages} from "../model/errors/error-messages";
import {authToken, hasRole} from "../middleware/auth.middleware";
import {UserRoles} from "../model/users/user-roles";
import {getUserId, isAgent} from "../utils/jwt-util";

const router = express.Router();


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
            res.status(500).send(ErrorMessages.contractsGetError);
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
        res.status(500).send(ErrorMessages.contractsGetError);
    }
});

router.get('/:contractId', authToken, async (req, res) => {
    try {
        const contract = await Contract.findById(req.params['contractId']);
        res.json(contract);
    } catch (error) {
        console.error(ErrorMessages.contractsGetError, error);
        res.status(500).send(ErrorMessages.contractsGetError);
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
        res.json(savedContract);
    } catch (error) {
        console.error(ErrorMessages.contractsCreateError, error);
        res.status(500).send(ErrorMessages.contractsCreateError);
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
                    res.status(403).send(ErrorMessages.unauthorizedAccessError);
                    return;
                }
            }

            if(contract['status'] == ContractStatus.FINAL){
                res.status(403).send(ErrorMessages.contractsFinalisedUpdateError);
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
                res.status(200).send();
            }
        }
    } catch (error) {
        console.error(ErrorMessages.contractsUpdateError, error);
        res.status(500).send(ErrorMessages.contractsUpdateError);
    }
});

router.delete('/:contractId', authToken, async (req, res) => {
    try {
        let contractId = req.params['contractId'];

        const contract = await Contract.findById(contractId);
        if(contract){
            if(contract['status'] == ContractStatus.FINAL){
                res.status(403).send(ErrorMessages.contractsFinalisedDeleteError);
                return;
            }
            else {
                await Contract.deleteOne({ _id: contractId });
                res.status(200).send();
            }
        }
    } catch (error) {
        console.error(ErrorMessages.contractsDeleteError, error);
        res.status(500).send(ErrorMessages.contractsDeleteError);
    }
});

export default router;
