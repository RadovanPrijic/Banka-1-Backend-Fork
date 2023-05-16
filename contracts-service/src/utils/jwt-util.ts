import {Request} from "express";

export function getUserId(req: Request): number {
    let jwt = JSON.parse(req.params['token']);
    return jwt['userId'];
}

