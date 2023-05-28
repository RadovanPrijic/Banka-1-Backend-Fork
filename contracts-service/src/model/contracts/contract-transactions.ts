import {ObjectId} from "mongodb";

export interface ContractTransactions {
    _id: ObjectId,
    agentId: number,
    transactions:
        {
            action: string,
            symbol: string,
            quantity: number,
            price: number
        }[]
}