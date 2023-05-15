import mongoose from "mongoose";

export enum ContractStatus{
    DRAFT = 'DRAFT',
    FINAL = 'FINAL'
}

const contractSchema = new mongoose.Schema({
    companyId: { type: Number, required: true },
    agentId: { type: Number, required: true },
    status: { type: String, enum: ContractStatus, required: true , default: ContractStatus.DRAFT},
    createdDateTime: { type: Date, default: Date.now },
    modifiedDateTime: { type: Date, default: Date.now },
    referenceNumber: { type: String, required: true },
    description: String,
});

const Contract = mongoose.model('Contract', contractSchema);

export default Contract;


