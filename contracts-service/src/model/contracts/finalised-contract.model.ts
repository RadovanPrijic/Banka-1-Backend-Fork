import mongoose from "mongoose";

const finalisedContractSchema = new mongoose.Schema({
    contractId: {
        type: String,
        required: true,
        unique: true
    },
    referenceNumber: {
        type: String,
        required: true,
        unique: true
    },
    contractFile: {
        type: Buffer,
        required: true
    }
});

const FinalisedContract = mongoose.model('FinalisedContract', finalisedContractSchema);

export default FinalisedContract;