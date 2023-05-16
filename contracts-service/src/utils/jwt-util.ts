import {Request} from "express";
import {UserRoles} from "../model/users/user-roles";

export function getUserId(req: Request): number {
    let jwt = JSON.parse(req.params['token']);
    return jwt['userId'];
}

export function isAgent(req: Request): boolean {
    let jwt = JSON.parse(req.params['token']);

    if(jwt['roles'].includes(UserRoles.ROLE_AGENT) || !jwt['roles']){
        return true;
    }
    return false;
}