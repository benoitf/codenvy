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
 * @name teams.navbar.controller:NavbarTeamsController
 * @description This class is handling the controller for the teams section in navbar
 * @author Ann Shumilova
 */
export class NavbarTeamsController {

  codenvyTeam: CodenvyTeam;

  /**
   * Default constructor
   * @ngInject for Dependency injection
   */
  constructor(codenvyTeam) {
    this.codenvyTeam = codenvyTeam;
    this.fetchTeams();
  }

  fetchTeams() {
    this.codenvyTeam.fetchTeams();
  }

  getTeams() {
    return this.codenvyTeam.getTeams();
  }

}
