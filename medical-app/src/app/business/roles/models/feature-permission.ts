import { Permission } from "./permission";

export interface FeaturePermission {
  feature_key: string;
  feature_name: string;
  permissions: Permission[];
}