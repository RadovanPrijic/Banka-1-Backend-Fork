import express from 'express';
import {authToken} from "../middleware/auth.middleware";
import Company from "../model/companies/company.model";
import {ErrorMessages} from "../model/errors/error-messages";


const router = express.Router();


router.get('/', authToken, async (req, res) => {
    try {
        let name = req.query['name'] || "";
        let registrationNumber = req.query['registrationNumber'] || "";
        let taxNumber = req.query['taxNumber'] || "";

        let searchQuery = {
            name: new RegExp(String(name), 'i'),
            registrationNumber: new RegExp(String(registrationNumber), 'i'),
            taxNumber: new RegExp(String(taxNumber), 'i')
        }

        const companies = await Company.find(searchQuery);
        res.json(companies);
    } catch (error) {
        console.error(ErrorMessages.companiesGetError, error);
        res.status(500).send({ message: ErrorMessages.companiesGetError });
    }
});


router.get('/:companyId', authToken, async (req, res) => {
    try {
        const companies = await Company.findById(req.params['companyId']);
        res.json(companies);
    } catch (error) {
        console.error(ErrorMessages.companiesGetError, error);
        res.status(500).send({ message: ErrorMessages.companiesGetError });
    }
});


router.post('/', authToken, async (req, res) => {
    try {
        const newCompany = new Company({
            name: req.body.name,
            registrationNumber: req.body.registrationNumber,
            taxNumber: req.body.taxNumber,
            activityCode: req.body.activityCode,
            address: req.body.address
        });

        const savedContract = await newCompany.save();
        res.json(savedContract);
    } catch (error) {
        console.error(ErrorMessages.companiesCreateError, error);
        res.status(500).send({ message: ErrorMessages.companiesCreateError });
    }
});


router.put('/:companyId', authToken, async (req, res) => {
    try {
        let companyId = req.params['companyId'];
        const company = await Company.findById(companyId);
        if(company){
            const update = {
                name: req.body.name,
                activityCode: req.body.activityCode,
                address: req.body.address
            }

            await Company.findByIdAndUpdate(companyId, update);
            res.status(200).send();
        }
    } catch (error) {
        console.error(ErrorMessages.companiesUpdateError, error);
        res.status(500).send({ message: ErrorMessages.companiesUpdateError });
    }
});


router.delete('/:contractId', authToken, async (req, res) => {
    //TODO
});

export default router;
