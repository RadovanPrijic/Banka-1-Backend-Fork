import express from 'express';
import {authToken} from "../middleware/auth.middleware";
import {ErrorMessages} from "../model/errors/error-messages";
import Contact from "../model/companies/contact.model";


const router = express.Router();


router.get('/list/:companyId', authToken, async (req, res) => {
    try {
        let companyId = req.params['companyId']
        const contacts = await Contact.find({ companyId: companyId });
        res.json(contacts);
    } catch (error) {
        console.error(ErrorMessages.contactsGetError, error);
        res.status(500).send({ message: ErrorMessages.contactsGetError });
    }
});


router.get('/:contactId', authToken, async (req, res) => {
    try {
        const contact = await Contact.findById(req.params['contactId']);
        res.json(contact);
    } catch (error) {
        console.error(ErrorMessages.contactsGetError, error);
        res.status(500).send({ message: ErrorMessages.contactsGetError });
    }
});


router.post('/', authToken, async (req, res) => {
    try {
        const newContact = new Contact({
            companyId: req.body.companyId,
            fullName: req.body.fullName,
            phoneNumber: req.body.phoneNumber,
            email: req.body.email,
            position: req.body.position,
            note: req.body.note
        });

        const savedContact = await newContact.save();
        res.json(savedContact);
    } catch (error) {
        console.error(ErrorMessages.contactsCreateError, error);
        res.status(500).send({ message: ErrorMessages.contactsCreateError });
    }
});


router.put('/:contactId', authToken, async (req, res) => {
    try {
        let contactId = req.params['contactId'];
        const contact = await Contact.findById(contactId);
        if(contact){
            const update = {
                fullName: req.body.fullName,
                phoneNumber: req.body.phoneNumber,
                email: req.body.email,
                position: req.body.position,
                note: req.body.note
            }

            await Contact.findByIdAndUpdate(contactId, update);
            res.status(200).send();
        }
    } catch (error) {
        console.error(ErrorMessages.contactsUpdateError, error);
        res.status(500).send({ message: ErrorMessages.contactsUpdateError });
    }
});


router.delete('/:contactId', authToken, async (req, res) => {
    try {
        let contactId = req.params['contactId'];

        const contact = await Contact.findById(contactId);
        if(contact){
            await Contact.deleteOne({ _id: contactId });
            res.status(200).send();
        }
    } catch (error) {
        console.error(ErrorMessages.contactsDeleteError, error);
        res.status(500).send({ message: ErrorMessages.contactsDeleteError });
    }
});


export default router;
