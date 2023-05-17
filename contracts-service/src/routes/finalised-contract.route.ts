import {authToken, hasRole} from "../middleware/auth.middleware";
import express from "express";
import {ErrorMessages} from "../model/errors/error-messages";
import FinalisedContract from "../model/contracts/finalised-contract.model";
import multer from "multer";
import Contract, {ContractStatus} from "../model/contracts/contract.model";
import {UserRoles} from "../model/users/user-roles";

const router = express.Router();

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
        await Contract.findByIdAndUpdate(req.body.contractId, { status: ContractStatus.FINAL });
        res.status(201).send();
    } catch (error) {
        console.error(ErrorMessages.contractsFinaliseError, error);
        res.status(500).send(ErrorMessages.contractsFinaliseError);
    }
});

export default router;