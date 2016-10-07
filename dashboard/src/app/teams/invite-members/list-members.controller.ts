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
 * @name teams.invite.members:ListMembersController
 * @description This class is handling the controller for the list of invited members
 * @author Ann Shumilova
 */
export class ListMembersController {

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor($mdDialog, lodash) {
    this.$mdDialog = $mdDialog;
    this.lodash = lodash;

    this.isNoSelected = true;
    this.isBulkChecked = false;
    this.membersSelectedStatus = {};
    this.membersSelectedNumber = 0;
    this.membersOrderBy = 'name';
  }

  /**
   * Update members selected status
   */
  updateSelectedStatus() {
    debugger;
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

  addMember() {
 /*   let name = this.buildServerName(port);
    this.servers[name] = {'port': port, 'protocol': protocol};

    this.updateSelectedStatus();
    return this.serversOnChange().then(() => {this.buildServersList();});*/
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
      templateUrl: 'app/teams/invite-teams/add-member/add-member.html'
    });
  }

  /**
   * Removes selected members
   */
  deleteSelectedMembers() {
   /* this.showDeleteConfirmation(this.serversSelectedNumber).then(() => {
      this.lodash.forEach(this.serversSelectedStatus, (server, name) => {
        delete this.servers[name];
      });
      this.deselectAllPorts();
      this.isBulkChecked = false;
      this.serversOnChange().then(() => {this.buildServersList();});
    })*/
  }

  /**
   * Show confirmation popup before member to delete
   * @param numberToDelete
   * @returns {*}
   */
  showDeleteConfirmation(numberToDelete) {
 /*   let confirmTitle = 'Would you like to delete ';
    if (numberToDelete > 1) {
      confirmTitle += 'these ' + numberToDelete + ' ports?';
    }
    else {
      confirmTitle += 'this selected port?';
    }
    let confirm = this.$mdDialog.confirm()
      .title(confirmTitle)
      .ariaLabel('Remove port')
      .ok('Delete!')
      .cancel('Cancel')
      .clickOutsideToClose(true);

    return this.$mdDialog.show(confirm);*/
  }

}
