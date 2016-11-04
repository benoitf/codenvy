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
 * Controller for a managing team details.
 *
 * @author Ann Shumilova
 */
export class TeamDetailsController {

  $route: ng.route.IRouteService;

  /**
   * Default constructor that is using resource injection
   * @ngInject for Dependency injection
   */
  constructor(codenvyTeam, $route: ng.route.IRouteService, $location) {
    this.codenvyTeam = codenvyTeam;
    this.teamName = $route.current.params.teamName;

    let page = $route.current.params.page;
    if (!page) {
      $location.path('/team/' + this.teamName);
    } else {
      this.selectedTabIndex = 0;
      switch (page) {
        case 'settings':
          this.selectedTabIndex = 0;
          break;
        case 'developers':
          this.selectedTabIndex = 1;
          break;
        case 'workspaces':
          this.selectedTabIndex = 2;
          break;
        default:
          $location.path('/team/' + this.teamName);
          break;
      }
    }

    this.fetchTeamDetails();
  }

  fetchTeamDetails() {
    this.team  = this.codenvyTeam.getTeamByName(this.teamName);

    if (!this.team) {
      this.codenvyTeam.fetchTeamByName(this.teamName).then((team) => {
        this.team = team;
      }, (error) => {
        this.invalidTeam = true;
      });
    }
  }

}
