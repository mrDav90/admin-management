import { Injectable } from '@angular/core';
import { AbilityBuilder, createMongoAbility, MongoAbility } from '@casl/ability';
import { UserInfos } from '../../../auth/models/UserInfos';

type Action = string;
type Subject = string;
type AppAbility = MongoAbility<[Action, Subject]>;

@Injectable({
  providedIn: 'root'
})
export class CaslService {
  private ability: AppAbility | undefined;
  constructor() { }
  
  defineAbilityFor(user: UserInfos) {
    const { can , rules } = new AbilityBuilder<AppAbility>(createMongoAbility);
    user.permissions.forEach(permission => {
      const { rsname, scopes } = permission;
      scopes.forEach(scope => {
      can(scope, rsname);
      });
    });
    this.ability = createMongoAbility<AppAbility>(rules);
    return createMongoAbility(rules);
  }

  can(action: Action, subject: Subject): boolean {
    return this.ability?.can(action, subject) ?? false;
  }

  getAbility() {
    return this.ability;
  }
}
