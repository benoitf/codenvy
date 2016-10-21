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
 * @name teams.invite.members:ListMembersController
 * @description This class is handling the controller for the list of invited members
 * @author Ann Shumilova
 */
export class ListMembersController {

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor($mdDialog, lodash, codenvyTeam) {
    this.$mdDialog = $mdDialog;
    this.lodash = lodash;
    this.codenvyTeam = codenvyTeam;

    this.isNoSelected = true;
    this.isBulkChecked = false;
    this.membersSelectedStatus = {};
    this.membersSelectedNumber = 0;
    this.membersOrderBy = 'email';
    this.memberRoles = {};
  }

  buildMembersList() {
    this.memberRoles = {};
    this.members.forEach(member => {
      let roles = {};
      roles.isManageTeam = member.roles.indexOf(CodenvyTeamRoles.MANAGE_TEAM) >= 0;
      roles.isManageResources = member.roles.indexOf(CodenvyTeamRoles.MANAGE_RESOURCES) >= 0;
      roles.isManageWorkspaces = member.roles.indexOf(CodenvyTeamRoles.MANAGE_WORKSPACES) >= 0;
      roles.isCreateWorkspaces = member.roles.indexOf(CodenvyTeamRoles.CREATE_WORKSPACES) >= 0;
      this.memberRoles[member.email] = roles;
    });
  }

  onRolesChanged(member) {
    let roles = [];
    let rolesInfo = this.memberRoles[member.email];
    if (rolesInfo.isManageTeam) {
      roles.push(CodenvyTeamRoles.MANAGE_TEAM);
    }

    if (rolesInfo.isManageResources) {
      roles.push(CodenvyTeamRoles.MANAGE_RESOURCES);
    }

    if (rolesInfo.isManageWorkspaces) {
      roles.push(CodenvyTeamRoles.MANAGE_WORKSPACES);
    }

    if (rolesInfo.isCreateWorkspaces) {
      roles.push(CodenvyTeamRoles.CREATE_WORKSPACES);
    }
    member.roles = roles;
  }

  /**
   * Update members selected status
   */
  updateSelectedStatus() {
    this.membersSelectedNumber = 0;
    this.isBulkChecked = !!this.members.length;
    this.members.forEach((member) => {
      if (this.membersSelectedStatus[member.email]) {
        this.membersSelectedNumber++;
      } else {
        this.isBulkChecked = false;
      }
    });
  }

  changeMemberSelection(email) {
    this.membersSelectedStatus[email] = !this.membersSelectedStatus[email];
    this.updateSelectedStatus();
  }

  /**
   * Change bulk selection value
   */
  changeBulkSelection() {
    if (this.isBulkChecked) {
      this.deselectAllMembers();
      this.isBulkChecked = false;
      return;
    }
    this.selectAllMembers();
    this.isBulkChecked = true;
  }

  /**
   * Check all members in list
   */
  selectAllMembers() {
    this.membersSelectedNumber = this.members.length;
    this.members.forEach((member) => {
      this.membersSelectedStatus[member.email] = true;
    })
  }

  /**
   * Uncheck all members in list
   */
  deselectAllMembers() {
    this.membersSelectedStatus = {};
    this.membersSelectedNumber = 0;
  }

  addMember(user, roles) {
    user.roles = roles;
    this.members.push(user);
    this.buildMembersList();
  }

  /**
   * Show dialog to add new port
   * @param $event
   */
  showAddDialog($event) {
    this.$mdDialog.show({
      targetEvent: $event,
      controller: 'AddMemberController',
      controllerAs: 'addMemberController',
      bindToController: true,
      clickOutsideToClose: true,
      locals: {
        members: this.members,
        callbackController: this
      },
      templateUrl: 'app/teams/invite-members/add-member/add-member.html'
    });
  }

  /**
   * Removes selected members
   */
  removeSelectedMembers() {
    this.lodash.remove(this.members, (member) => {
      return this.membersSelectedStatus[member.email];
    });
    this.buildMembersList();
    this.deselectAllMembers();
    this.isBulkChecked = false;
  }
}
