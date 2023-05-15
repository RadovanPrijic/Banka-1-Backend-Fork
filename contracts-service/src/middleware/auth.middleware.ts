import e, {NextFunction, Request, Response} from "express";
import {ErrorMessages} from "../model/errors/error-messages";
import jwt from 'jsonwebtoken'


const secretKey = "SECRET_KEY";

export function authToken(req: Request, res: Response, next: NextFunction) {
    const authHeader = req.headers['authorization'];
    let token = authHeader && authHeader.split(' ')[1];
    console.log(token);

    //token = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbkBhZG1pbi5jb20iLCJyb2xlcyI6WyJST0xFX0FETUlOIl0sImV4cCI6MTY4NDIyNjU2NSwidXNlcklkIjoxLCJpYXQiOjE2ODQxOTA1NjV9.aiFyaobodYViebREmdZInNpC47SWCO1Ru0vEFJ6oylo'

    if(token == null){
        return res.status(401).json({message: ErrorMessages.userNotLoggedInError });
    }

    //TODO FIX JWT VERIFY
    //potencijalni problem u enkodingu prilikom potpisivanja tokena

    jwt.verify(token, secretKey, (err, payload) => {
        if(err){
            return res.status(403).json({message: ErrorMessages.unauthorizedAccessError });
        }

        req.params['token'] = JSON.stringify(payload);
        next();
    });
}

export function hasPermission(permission: string, req: Request): boolean{
    let jwt = req.params['token'];
    console.log(jwt);

    return true;
}
