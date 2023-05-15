import express from 'express';
import Contract, { ContractStatus } from "../model/contract.model";
import {ErrorMessages} from "../model/errors/error-messages";
import {authToken} from "../middleware/auth.middleware";

const router = express.Router();


router.get('/', authToken, async (req, res) => {
    //TODO only supervisor
    console.log(req.params['token']);

    try {
        const contracts = await Contract.find();
        res.json(contracts);
    } catch (error) {
        console.error(ErrorMessages.contractsGetError, error);
        res.status(500).send(ErrorMessages.contractsGetError);
    }
});

router.get('/my-contracts', async (req, res) => {
    //TODO get agent id from token
    try {
        const contracts = await Contract.find({ agentId: 1 });
        res.json(contracts);
    } catch (error) {
        console.error(ErrorMessages.contractsGetError, error);
        res.status(500).send(ErrorMessages.contractsGetError);
    }
});

router.get('/:contractId', async (req, res) => {
    try {
        const contract = await Contract.findById(req.params['contractId']);
        res.json(contract);
    } catch (error) {
        console.error(ErrorMessages.contractsGetError, error);
        res.status(500).send(ErrorMessages.contractsGetError);
    }
});

router.post('/', async (req, res) => {
    //TODO get agentId from token
    try {
        const newContract = new Contract({
            companyId: req.body.companyId,
            status: ContractStatus.DRAFT,
            referenceNumber: req.body.referenceNumber,
            description: req.body.description,
        });

        const savedContract = await newContract.save();
        res.json(savedContract);
    } catch (error) {
        console.error(ErrorMessages.contractsCreateError, error);
        res.status(500).send(ErrorMessages.contractsCreateError);
    }
});

router.post('/finalise/:contractId', async (req, res) => {
    //TODO upload PDF
});

router.delete('/:contractId', async (req, res) => {
    try {
        let contractId = req.params['contractId'];

        const contract = await Contract.findById(contractId);
        if(contract){
            if(contract['status'] == ContractStatus.FINAL){
                res.status(403).send(ErrorMessages.contractsFinalisedDeleteError);
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
