import e, {json, NextFunction, Request, Response} from "express";
import {ErrorMessages} from "../model/errors/error-messages";
import jwt from 'jsonwebtoken'


const secretKey = "SECRET_KEY";

export function authToken(req: Request, res: Response, next: NextFunction) {
    const authHeader = req.headers['authorization'];
    let token = authHeader && authHeader.split(' ')[1];

    if(token == null){
        return res.status(401).json({message: ErrorMessages.userNotLoggedInError });
    }

    jwt.verify(token, secretKey, (err, payload) => {
        if(err){
            return res.status(403).json({message: ErrorMessages.unauthorizedAccessError });
        }

        req.params['token'] = JSON.stringify(payload);
        next();
    });
}

