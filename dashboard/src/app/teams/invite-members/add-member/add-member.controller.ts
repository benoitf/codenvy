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

import {CodenvyTeamRoles} from '../../../../components/api/codenvy-team-roles';

/**
 * @ngdoc controller
 * @name teams.invite.members:AddMemberController
 * @description This class is handling the controller for adding members dialog.
 * @author Ann Shumilova
 */
export class AddMemberController {

  $mdDialog: angular.material.IDialogService;

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor($mdDialog, codenvyTeam, codenvyUser) {
    this.$mdDialog = $mdDialog;
    this.codenvyTeam = codenvyTeam;
    this.codenvyUser = codenvyUser;

    this.roles = [];
    this.roles.push({'role' : CodenvyTeamRoles.MANAGE_WORKSPACES, 'allowed' : true});
    this.roles.push({'role' : CodenvyTeamRoles.MANAGE_TEAM, 'allowed' : false});
    this.roles.push({'role' : CodenvyTeamRoles.CREATE_WORKSPACES, 'allowed' : true});
    this.roles.push({'role' : CodenvyTeamRoles.MANAGE_RESOURCES, 'allowed' : false});
  }

  /**
   * Hides the add member dialog.
   */
  hide() {
    this.$mdDialog.hide();
  }

  /**
   * Adds new port
   */
  addMember() {
    let userRoles = [];
    this.roles.forEach(roleInfo => {
      if (roleInfo.allowed) {
        userRoles.push(roleInfo.role);
      }
    });

    let user = {};
    user.email = this.email;
    let findUser = this.codenvyUser.fetchUserByAlias(this.email).then(() => {
      user = this.codenvyUser.getUserByAlias(this.email);
      this.finishAdding(user, userRoles);
    }, (error) => {
      this.finishAdding(user, userRoles);
    });


    this.hide();
  }

  finishAdding(user, roles) {
    this.callbackController.addMember(user, roles);
    this.hide();
  }

}
