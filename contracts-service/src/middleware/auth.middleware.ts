import e, {json, NextFunction, Request, Response} from "express";
import {ErrorMessages} from "../model/errors/error-messages";
import jwt from 'jsonwebtoken'
import {UserRoles} from "../model/user-roles";


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

export function hasRole(role: string, req: Request): boolean {
    let jwt = JSON.parse(req.params['token']);

    if(jwt['roles'].includes(UserRoles.ROLE_ADMIN)){
        return true;
    }

    return !!jwt['roles'].includes(role);
}
