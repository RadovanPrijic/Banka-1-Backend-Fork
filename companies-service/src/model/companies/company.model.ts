import mongoose from "mongoose";

const companySchema = new mongoose.Schema({
    name: {
        type: String,
        required: true
    },
    registrationNumber: {
        type: String,
        required: true
    },
    taxNumber: {
        type: String,
        required: true
    },
    activityCode: {
        type: String,
        required: true
    },
    address: {
        type: String,
        required: true
    }
});

const Company = mongoose.model('Company', companySchema);

export default Company;