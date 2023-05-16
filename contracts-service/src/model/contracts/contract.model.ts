import mongoose from "mongoose";

export enum ContractStatus {
    DRAFT = 'DRAFT',
    FINAL = 'FINAL'
}

export enum TransactionAction {
    BUY = "BUY",
    SELL = "SELL"
}

const contractSchema = new mongoose.Schema({
    companyId: {
        type: Number,
        required: true
    },
    agentId: {
        type: Number,
        required: true
    },
    status: {
        type: String,
        enum: ContractStatus,
        required: true,
        default: ContractStatus.DRAFT
    },
    createdDateTime: {
        type: Date,
        default: Date.now
    },
    modifiedDateTime: {
        type: Date,
        default: Date.now
    },
    referenceNumber: {
        type: String,
        required: true,
        unique: true
    },
    description: String,
    transactions: [{
        action: {
            type: String,
            enum: TransactionAction,
            required: true,
            default: TransactionAction.BUY
        },
        symbol: {
            type: String,
            required: true
        },
        quantity: {
            type: Number,
            required: true
        },
        price: {
            type: Number,
            required: true
        }
    }]
});

const Contract = mongoose.model('Contract', contractSchema);

export default Contract;


