export interface UserInfos {
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    role : string;
    permissions: Permission[];
}


interface Permission {
    rsid : string;
    rsname : string;
    scopes: string[];
}