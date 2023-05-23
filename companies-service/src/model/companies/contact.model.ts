import mongoose from "mongoose";

const contactSchema = new mongoose.Schema({
    companyId: {
        type: String,
        required: true
    },
    fullName: {
        type: String,
        required: true
    },
    phoneNumber: {
        type: String,
        required: true
    },
    email: {
        type: String,
        required: true
    },
    position: {
        type: String,
        required: true
    },
    note: {
        type: String,
        required: false
    }
});

const Contact = mongoose.model('Contact', contactSchema);

export default Contact;