import mongoose from "mongoose";

export enum AccountType {
    DINARSKI = "DINARSKI",
    DEVIZNI = "DEVIZNI"
}

const accountSchema = new mongoose.Schema({
    companyId: {
        type: String,
        required: true
    },
    accountNumber: {
        type: String,
        required: true
    },
    bankName: {
        type: String,
        required: true
    },
    type: {
        type: String,
        required: true,
        enum: AccountType,
        default: AccountType.DEVIZNI
    }
});

const Account = mongoose.model('Account', accountSchema);

export default Account;