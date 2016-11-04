/*
 *  [2015] - [2016] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
'use strict';

import {CodenvyTeamRoles} from './codenvy-team-roles';

/**
 * This class is handling the interactions with
 * Team management API.
 *
 * @author Ann Shumilova
 */
export class CodenvyTeam {

  teamsMap : Map<string, any> = new Map();
  teams : any = [];

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor($resource, lodash, cheNamespaceRegistry) {
    this.lodash = lodash;
    this.cheNamespaceRegistry = cheNamespaceRegistry;

    this.remoteTeamAPI = $resource('/api/organization', {}, {
      getTeams: {method: 'GET', url: '/api/organization', isArray: true},
      createTeam: {method: 'POST', url: '/api/organization'},
      findTeam: {method: 'GET', url: '/api/organization/find?name=:teamName'}
    });
  }

  /**
   *
   * @returns {any}
   */
  fetchTeams() {
    let promise = this.remoteTeamAPI.getTeams().$promise;

    let resultPromise = promise.then((teams) => {
      this.teamsMap = new Map();
      this.teams = [];
      this.cheNamespaceRegistry.getNamespaces().length = 0;

      teams.forEach((team) => {
        this.teamsMap.set(team.id, team);
        this.teams.push(team);
        this.cheNamespaceRegistry.getNamespaces().push(team.name);
      });
    });

    return resultPromise;
  }

  getTeams() {
    return this.teams;
  }

  fetchTeamByName(name) {
    let promise = this.remoteTeamAPI.findTeam({'teamName' : name}).$promise;
    return promise;
  }

  getTeamByName(name) {
    for (let i = 0; i < this.teams.length; i++) {
      if (this.teams[i].name === name) {
        return this.teams[i];
      }
    };

    return null;
  }

  createTeam(name) {
    let data = {'name' : name};
    let promise = this.remoteTeamAPI.createTeam(data).$promise;
    return promise;
  }

  getRolesFromActions(actions) {
    let roles = [];
    let teamRoles = CodenvyTeamRoles.getValues();
    teamRoles.forEach((role) => {
      if (this.lodash.difference(role.actions, actions).length === 0) {
        roles.push(role);
      }
    });
    return roles;
  }

  getActionsFromRoles(roles) {
    let actions = [];
    roles.forEach(role => {
      actions = actions.concat(role.actions);
    });

    return actions;
  }
}
