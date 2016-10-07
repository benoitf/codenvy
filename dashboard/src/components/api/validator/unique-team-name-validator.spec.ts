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
 * Test the team name uniqueness directive
 * @author Ann Shumilova
 */

describe('unique-team-name-validator', function() {
  var $scope, form, $compiler;

  /**
   * Team API
   */
  var factoryTeam;

  /**
   * API builder.
   */
  var apiBuilder;

  /**
   * Backend for handling http operations
   */
  var httpBackend;

  /**
   * Che backend
   */
  var cheBackend;


  beforeEach(angular.mock.module('userDashboard'));


  beforeEach(inject(function($compile, $rootScope, codenvyTeam, cheAPIBuilder, cheHttpBackend, $document) {
    $scope = $rootScope;
    $compiler = $compile;
    factoryTeam = codenvyTeam;
    apiBuilder = cheAPIBuilder;
    cheBackend = cheHttpBackend;
    httpBackend = cheHttpBackend.getHttpBackend();
    this.$document = $document;

  }));

  describe('Validate Team Name', function() {

    it('team already exists', function() {
      let teams = [];
      // setup tests objects
      var idStack1 = 'idStack1';
      var nameStack1 = 'stack1';
      var stack1 = apiBuilder.getStackBuilder().withName(nameStack1).withId(idStack1).build();
      stacks.push(stack1);

      var idStack2 = 'idStack2';
      var stack2 = apiBuilder.getStackBuilder().withId(idStack2).build();
      stacks.push(stack2);

      // add into backend
      cheBackend.addStacks(stacks);
      cheBackend.setup();

      factoryStack.fetchStacks();

      // flush HTTP backend
      httpBackend.flush();

      $scope.model = {stackName: null};

      var element = angular.element(
        '<form name="form">' +
        '<input ng-model="model.stackName" name="name" unique-stack-name="stack2.name" />' +
        '</form>'
      );
      $compiler(element)($scope);
      form = $scope.form;

      form.name.$setViewValue(nameStack1);

      // check form (expect invalid)
      expect(form.name.$invalid).toBe(true);
      expect(form.name.$valid).toBe(false);
    });

    it('stack not yet defined', function() {

      // setup tests objects
      var idStack1 = 'idStack1';
      var nameStack1 = 'stack1';
      var stack1 = apiBuilder.getStackBuilder().withName(nameStack1).withId(idStack1).build();

      var idStack2 = 'idStack2';
      var nameStack2 = 'stack2';
      var stack2 = apiBuilder.getStackBuilder().withName('').withId(idStack2).build();

      factoryStack.fetchStacks();

      // add into backend
      cheBackend.addStacks([stack1, stack2]);

      // setup backend
      cheBackend.setup();

      // flush HTTP backend
      httpBackend.flush();

      $scope.model = { stackName: null };

      var element = angular.element(
        '<form name="form">' +
        '<input ng-model="model.stackName" name="name" unique-stack-name="stack2.name" />' +
        '</form>'
      );
      $compiler(element)($scope);
      form = $scope.form;

      form.name.$setViewValue(nameStack2);

      // check form valid
      expect(form.name.$invalid).toBe(false);
      expect(form.name.$valid).toBe(true);

    });
  });
});
