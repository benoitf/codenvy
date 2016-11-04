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
  constructor(codenvyTeam, cheWorkspace) {
    this.codenvyTeam = codenvyTeam;
    this.cheWorkspace = cheWorkspace;

    this.workspaces = [];
    this.isInfoLoading = true;


    this.fetchWorkspaces();
  }

  fetchWorkspaces() {
    let promise = this.cheWorkspace.fetchWorkspaces();

    promise.then(() => {
        this.cheWorkspace.getWorkspaces();
      },
      (error) => {
        if (error.status === 304) {
          this.cheWorkspace.getWorkspaces();
        }
        this.state = 'error';
        this.isInfoLoading = false;
      });
  }
}
