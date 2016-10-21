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
import {CodenvyTeamRoles} from '../../../components/api/codenvy-team-roles';

/**
 * @ngdoc controller
 * @name teams.create.controller:CreateTeamController
 * @description This class is handling the controller for the new team creation.
 * @author Ann Shumilova
 */
export class CreateTeamController {

  codenvyTeam: CodenvyTeam;
  codenvyUser: CodenvyUser;
  codenvyPersmissions: CodenvyPersmissions;

  /**
   * Default constructor
   * @ngInject for Dependency injection
   */
  constructor(codenvyTeam, codenvyUser, codenvyPermissions, cheNotification, $location, $q) {
    this.codenvyTeam = codenvyTeam;
    this.codenvyUser = codenvyUser;
    this.codenvyPermissions = codenvyPermissions;
    this.cheNotification = cheNotification;
    this.$location = $location;
    this.$q = $q;

    this.teamName = 'MyTeam';
    this.isLoading = true;
    this.members = [];

    if (codenvyUser.getUser()) {
      this.owner = codenvyUser.getUser().email;
      this.isLoading = false;
    } else {
      codenvyUser.fetchUser().then(() => {
        this.owner = codenvyUser.getUser().email;
        this.isLoading = false;
      }, (error) => {
        if (error.status === 304) {
          this.owner = codenvyUser.getUser().email;
          this.isLoading = false;
        } else {
          //TODO
        }
      });
    }
  }

  createTeam() {
    this.isLoading = true;
    this.codenvyTeam.createTeam(this.teamName).then((data) => {
      this.addPermissions(data, this.members);
      this.codenvyTeam.fetchTeams();
    }, (error) => {
      this.isLoading = false;
      let message = error.data && error.data.message ? error.data.message : 'Failed to create team ' + this.teamName;
      this.cheNotification.showError(message);
    });
  }

  addPermissions(data, members) {
    let promises = [];

    members.forEach(member => {
      let permissions = {};
      permissions.instanceId = data.id;
      permissions.userId = member.id;
      permissions.domainId = 'organization'; //TODO
      permissions.actions = this.codenvyTeam.getActionsFromRoles(member.roles);

      let promise = this.codenvyPermissions.storePermissions(permissions);
      promises.push(promise);
    });

     this.$q.all(promises).then(() => {
       this.isLoading = false;
       this.$location.path('#/team/' + data.name);
     }, (error) => {
       this.isLoading = false;
       console.log(error);
     });
  }
}
