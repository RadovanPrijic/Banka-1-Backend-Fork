import mongoose from "mongoose";

const finalisedContractSchema = new mongoose.Schema({
    contractId: { type: Number, required: true, unique: true },
    referenceNumber: { type: String, required: true, unique: true },

});

const Contract = mongoose.model('FinalisedContract', finalisedContractSchema);

export default Contract;