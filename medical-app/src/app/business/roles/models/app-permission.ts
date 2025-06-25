import { PermissionItem } from "./permission-item";

export interface AppPermission {
    resourceName : string;
    resourceDisplayName : string;
    permissions : PermissionItem[];
}