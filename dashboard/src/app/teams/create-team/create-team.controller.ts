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

/**
 * @ngdoc controller
 * @name teams.create.controller:CreateTeamController
 * @description This class is handling the controller for the new team creation.
 * @author Ann Shumilova
 */
export class CreateTeamController {

  codenvyTeam: CodenvyTeam;
  codenvyUser: CodenvyUser;

  /**
   * Default constructor
   * @ngInject for Dependency injection
   */
  constructor(codenvyTeam, codenvyUser, cheNotification, $location) {
    this.codenvyTeam = codenvyTeam;
    this.codenvyUser = codenvyUser;
    this.cheNotification = cheNotification;
    this.$location = $location;

    this.teamName = 'MyTeam';
    this.isLoading = true;
    this.members = [];
    this.members.push({'email' : 'user1', 'manageTeam' : true});
    this.members.push({'email' : 'user2', 'manageTeam' : false});



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
      this.codenvyTeam.fetchTeams();
      this.isLoading = false;
      this.$location.path('#/team/' + data.name);
    }, (error) => {
      this.isLoading = false;
      let message = error.data && error.data.message ? error.data.message : 'Failed to create team ' + this.teamName;
      this.cheNotification.showError(message);
    });
  }
}
