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
 * @name teams.workspaces:ListTeamWorkspacesController
 * @description This class is handling the controller for the list of team's workspaces.
 * @author Ann Shumilova
 */
export class ListTeamWorkspacesController {

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor(codenvyTeam, cheWorkspace, $mdDialog, $q) {
    this.codenvyTeam = codenvyTeam;
    this.cheWorkspace = cheWorkspace;
    this.$mdDialog = $mdDialog;
    this.$q = $q;

    this.workspaces = [];
    this.isInfoLoading = true;

    this.workspaceFilter = {config: {name: ''}};
    this.workspacesSelectedStatus = {};
    this.isBulkChecked = false;
    this.isNoSelected = true;
    this.fetchWorkspaces();
  }

  fetchWorkspaces() {
    let promise = this.cheWorkspace.fetchWorkspaces();

    promise.then(() => {
        this.isInfoLoading = false;
        this.workspaces = this.cheWorkspace.getWorkspacesByNamespace(this.team.name);
      },
      (error) => {
        if (error.status === 304) {
          this.workspaces = this.cheWorkspace.getWorkspacesByNamespace(this.team.name);
        }
        this.state = 'error';
        this.isInfoLoading = false;
      });
  }

  /**
   * return true if all workspaces in list are checked
   * @returns {boolean}
   */
  isAllWorkspacesSelected() {
    return this.isAllSelected;
  }

  /**
   * returns true if all workspaces in list are not checked
   * @returns {boolean}
   */
  isNoWorkspacesSelected() {
    return this.isNoSelected;
  }

  /**
   * Check all workspaces in list
   */
  selectAllWorkspaces() {
    this.workspaces.forEach((workspace) => {
      this.workspacesSelectedStatus[workspace.id] = true;
    });
  }

  /**
   * Uncheck all workspaces in list
   */
  deselectAllWorkspaces() {
    this.workspaces.forEach((workspace) => {
      this.workspacesSelectedStatus[workspace.id] = false;
    });
  }

  /**
   * Change bulk selection value
   */
  changeBulkSelection() {
    if (this.isBulkChecked) {
      this.deselectAllWorkspaces();
      this.isBulkChecked = false;
    } else {
      this.selectAllWorkspaces();
      this.isBulkChecked = true;
    }
    this.updateSelectedStatus();
  }

  /**
   * Update workspace selected status
   */
  updateSelectedStatus() {
    this.isNoSelected = true;
    this.isAllSelected = true;

    Object.keys(this.workspacesSelectedStatus).forEach((key) => {
      if (this.workspacesSelectedStatus[key]) {
        this.isNoSelected = false;
      } else {
        this.isAllSelected = false;
      }
    });

    if (this.isNoSelected) {
      this.isBulkChecked = false;
      return;
    }

    if (this.isAllSelected) {
      this.isBulkChecked = true;
    }
  }

  /**
   * Delete all selected workspaces
   */
  deleteSelectedWorkspaces() {
    let workspacesSelectedStatusKeys = Object.keys(this.workspacesSelectedStatus);
    let checkedWorkspacesKeys = [];

    if (!workspacesSelectedStatusKeys.length) {
      this.cheNotification.showError('No such workspace.');
      return;
    }

    workspacesSelectedStatusKeys.forEach((key) => {
      if (this.workspacesSelectedStatus[key] === true) {
        checkedWorkspacesKeys.push(key);
      }
    });

    let queueLength = checkedWorkspacesKeys.length;
    if (!queueLength) {
      this.cheNotification.showError('No such workspace.');
      return;
    }

    let confirmationPromise = this.showDeleteWorkspacesConfirmation(queueLength);
    confirmationPromise.then(() => {
      let numberToDelete = queueLength;
      let isError = false;
      let deleteWorkspacePromises = [];
      let workspaceName;

      checkedWorkspacesKeys.forEach((workspaceId) => {
        this.workspacesSelectedStatus[workspaceId] = false;

        let workspace = this.cheWorkspace.getWorkspaceById(workspaceId);
        workspaceName = workspace.config.name;
        let stoppedStatusPromise = this.cheWorkspace.fetchStatusChange(workspaceId, 'STOPPED');

        // stop workspace if it's status is RUNNING
        if (workspace.status === 'RUNNING') {
          this.cheWorkspace.stopWorkspace(workspaceId);
        }

        // delete stopped workspace
        let promise = stoppedStatusPromise.then(() => {
          return this.cheWorkspace.deleteWorkspaceConfig(workspaceId);
        }).then(() => {
            queueLength--;
          },
          (error) => {
            isError = true;
            this.$log.error('Cannot delete workspace: ', error);
          });
        deleteWorkspacePromises.push(promise);
      });

      this.$q.all(deleteWorkspacePromises).finally(() => {
        this.fetchWorkspaces();
        this.updateSelectedStatus();
        if (isError) {
          this.cheNotification.showError('Delete failed.');
        }
        else {
          if (numberToDelete === 1) {
            this.cheNotification.showInfo(workspaceName + ' has been removed.');
          }
          else {
            this.cheNotification.showInfo('Selected workspaces have been removed.');
          }
        }
      });
    });
  }

  /**
   * Show confirmation popup before workspaces to delete
   * @param numberToDelete
   * @returns {*}
   */
  showDeleteWorkspacesConfirmation(numberToDelete) {
    let confirmTitle = 'Would you like to delete ';
    if (numberToDelete > 1) {
      confirmTitle += 'these ' + numberToDelete + ' workspaces?';
    } else {
      confirmTitle += 'this selected workspace?';
    }
    let confirm = this.$mdDialog.confirm()
      .title(confirmTitle)
      .ariaLabel('Remove workspaces')
      .ok('Delete!')
      .cancel('Cancel')
      .clickOutsideToClose(true);

    return this.$mdDialog.show(confirm);
  }
}
