export interface UserResponse {
    id: string;
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    role: string;
    realmRoles: string[];
    enabled: boolean;
}