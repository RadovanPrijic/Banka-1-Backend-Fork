import {authToken} from "../middleware/auth.middleware";
import express from "express";

const router = express.Router();


router.get('/', authToken, async (req, res) => {
    //TODO get contracts
});

router.get('/:contractId', authToken, async (req, res) => {
    //TODO get contract
});

router.post('/:contractId', authToken, async (req, res) => {
    //TODO upload PDF
});

export default router;