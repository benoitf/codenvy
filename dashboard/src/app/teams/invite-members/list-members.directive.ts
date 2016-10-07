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
 * @ngdoc directive
 * @name teams.invite.members:ListMembers
 * @restrict E
 * @element
 *
 * @description
 * `<list-members members="ctrl.members"></list-members>` for displaying list of members
 *
 * @usage
 *   <list-members members="ctrl.members"></list-members>
 *
 * @author Ann Shumilova
 */
export class ListMembers implements ng.IDirective {

  restrict = 'E';
  templateUrl = 'app/teams/invite-members/list-members.html';

  controller = 'ListMembersController';
  controllerAs = 'listMembersController';
  bindToController = true;

  scope = {
    members: '='
  };

  constructor () {
  }
}
