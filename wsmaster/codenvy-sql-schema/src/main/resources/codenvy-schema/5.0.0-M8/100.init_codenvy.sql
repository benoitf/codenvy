--
--  [2012] - [2016] Codenvy, S.A.
--  All Rights Reserved.
--
-- NOTICE:  All information contained herein is, and remains
-- the property of Codenvy S.A. and its suppliers,
-- if any.  The intellectual and technical concepts contained
-- herein are proprietary to Codenvy S.A.
-- and its suppliers and may be covered by U.S. and Foreign Patents,
-- patents in process, and are protected by trade secret or copyright law.
-- Dissemination of this information or reproduction of this material
-- is strictly forbidden unless prior written permission is obtained
-- from Codenvy S.A..
--

-- Factory Button --------------------------------------------------------------
CREATE TABLE BUTTON (
    ID          BIGINT      NOT NULL,
    TYPE        VARCHAR,
    COLOR       VARCHAR,
    COUNTER     BOOLEAN,
    LOGO        VARCHAR,
    STYLE       VARCHAR,

    PRIMARY KEY (ID)
);
--------------------------------------------------------------------------------


-- Action ----------------------------------------------------------------------
CREATE TABLE ACTION (
    ENTITYID        BIGINT      NOT NULL,
    ID              VARCHAR,

    PRIMARY KEY (ENTITYID)
);
--------------------------------------------------------------------------------


-- Action properties -----------------------------------------------------------
CREATE TABLE Action_PROPERTIES (
    Action_ENTITYID         BIGINT,
    PROPERTIES              VARCHAR,
    PROPERTIES_KEY          VARCHAR
);
-- constraints
ALTER TABLE Action_PROPERTIES ADD CONSTRAINT FK_Action_PROPERTIES_Action_ENTITYID FOREIGN KEY (Action_ENTITYID) REFERENCES ACTION (ENTITYID);
--------------------------------------------------------------------------------


-- On app closed action --------------------------------------------------------
CREATE TABLE ONAPPCLOSED (
    ID      BIGINT      NOT NULL,

    PRIMARY KEY (ID)
);
--------------------------------------------------------------------------------


-- On projects loaded action ---------------------------------------------------
CREATE TABLE ONPROJECTSLOADED (
    ID      BIGINT      NOT NULL,

    PRIMARY KEY (ID)
);
--------------------------------------------------------------------------------


-- On app loaded action --------------------------------------------------------
CREATE TABLE ONAPPLOADED (
    ID      BIGINT      NOT NULL,

    PRIMARY KEY (ID)
);
--------------------------------------------------------------------------------


-- On app closed action --------------------------------------------------------
CREATE TABLE ONAPPCLOSED_ACTION (
    OnAppClosed_ID      BIGINT      NOT NULL,
    actions_ENTITYID    BIGINT      NOT NULL,

    PRIMARY KEY (OnAppClosed_ID, actions_ENTITYID)
);
-- constraints
ALTER TABLE ONAPPCLOSED_ACTION ADD CONSTRAINT FK_ONAPPCLOSED_ACTION_actions_ENTITYID FOREIGN KEY (actions_ENTITYID) REFERENCES ACTION (ENTITYID);
ALTER TABLE ONAPPCLOSED_ACTION ADD CONSTRAINT FK_ONAPPCLOSED_ACTION_OnAppClosed_ID FOREIGN KEY (OnAppClosed_ID) REFERENCES ONAPPCLOSED (ID);
--------------------------------------------------------------------------------


-- On project loaded action ----------------------------------------------------
CREATE TABLE ONPROJECTSLOADED_ACTION (
    OnProjectsLoaded_ID         BIGINT      NOT NULL,
    actions_ENTITYID            BIGINT      NOT NULL,

    PRIMARY KEY (OnProjectsLoaded_ID, actions_ENTITYID)
);
-- constraints
ALTER TABLE ONPROJECTSLOADED_ACTION ADD CONSTRAINT FK_ONPROJECTSLOADED_ACTION_OnProjectsLoaded_ID FOREIGN KEY (OnProjectsLoaded_ID) REFERENCES ONPROJECTSLOADED (ID);
ALTER TABLE ONPROJECTSLOADED_ACTION ADD CONSTRAINT FK_ONPROJECTSLOADED_ACTION_actions_ENTITYID FOREIGN KEY (actions_ENTITYID) REFERENCES ACTION (ENTITYID);
--------------------------------------------------------------------------------


-- On app loaded action --------------------------------------------------------
CREATE TABLE ONAPPLOADED_ACTION (
    OnAppLoaded_ID          BIGINT      NOT NULL,
    actions_ENTITYID        BIGINT      NOT NULL,

    PRIMARY KEY (OnAppLoaded_ID, actions_ENTITYID)
);
-- constraints
ALTER TABLE ONAPPLOADED_ACTION ADD CONSTRAINT FK_ONAPPLOADED_ACTION_actions_ENTITYID FOREIGN KEY (actions_ENTITYID) REFERENCES ACTION (ENTITYID);
ALTER TABLE ONAPPLOADED_ACTION ADD CONSTRAINT FK_ONAPPLOADED_ACTION_OnAppLoaded_ID FOREIGN KEY (OnAppLoaded_ID) REFERENCES ONAPPLOADED (ID);
--------------------------------------------------------------------------------


-- Ide -------------------------------------------------------------------------
CREATE TABLE IDE (
    ID                      BIGINT      NOT NULL,
    ONAPPCLOSED_ID          BIGINT,
    ONAPPLOADED_ID          BIGINT,
    ONPROJECTSLOADED_ID     BIGINT,

    PRIMARY KEY (ID)
);
-- constraints
ALTER TABLE IDE ADD CONSTRAINT FK_IDE_ONAPPCLOSED_ID FOREIGN KEY (ONAPPCLOSED_ID) REFERENCES ONAPPCLOSED (ID);
ALTER TABLE IDE ADD CONSTRAINT FK_IDE_ONPROJECTSLOADED_ID FOREIGN KEY (ONPROJECTSLOADED_ID) REFERENCES ONPROJECTSLOADED (ID);
ALTER TABLE IDE ADD CONSTRAINT FK_IDE_ONAPPLOADED_ID FOREIGN KEY (ONAPPLOADED_ID) REFERENCES ONAPPLOADED (ID);
--------------------------------------------------------------------------------


-- Factory ---------------------------------------------------------------------
CREATE TABLE FACTORY (
    ID                  VARCHAR         NOT NULL,
    NAME                VARCHAR,
    VERSION             VARCHAR         NOT NULL,
    CREATED             BIGINT,
    USERID              VARCHAR,
    creation_strategy   VARCHAR,
    match_reopen        VARCHAR,
    REFERER             VARCHAR,
    SINCE               BIGINT,
    UNTIL               BIGINT,
    BUTTON_ID           BIGINT,
    IDE_ID              BIGINT,
    WORKSPACE_ID        BIGINT,

    PRIMARY KEY (ID)
);
-- constraints
ALTER TABLE FACTORY ADD CONSTRAINT FK_FACTORY_userId FOREIGN KEY (userId) REFERENCES USR (ID);
ALTER TABLE FACTORY ADD CONSTRAINT FK_FACTORY_IDE_ID FOREIGN KEY (IDE_ID) REFERENCES IDE (ID);
ALTER TABLE FACTORY ADD CONSTRAINT FK_FACTORY_BUTTON_ID FOREIGN KEY (BUTTON_ID) REFERENCES BUTTON (ID);
ALTER TABLE FACTORY ADD CONSTRAINT FK_FACTORY_WORKSPACE_ID FOREIGN KEY (WORKSPACE_ID) REFERENCES WORKSPACECONFIG (ID);
--------------------------------------------------------------------------------


-- Factory Images --------------------------------------------------------------
CREATE TABLE Factory_IMAGES (
    IMAGEDATA   BYTEA,
    MEDIATYPE   VARCHAR,
    NAME        VARCHAR,
    Factory_ID  VARCHAR
);
-- constraints
ALTER TABLE Factory_IMAGES ADD CONSTRAINT FK_Factory_IMAGES_Factory_ID FOREIGN KEY (Factory_ID) REFERENCES FACTORY (ID);
--------------------------------------------------------------------------------


-- System permissions ----------------------------------------------------------
CREATE TABLE SYSTEMPERMISSIONS (
    ID      VARCHAR         NOT NULL,
    USERID  VARCHAR,

    PRIMARY KEY (ID)
);
-- indexes
CREATE UNIQUE INDEX INDEX_SYSTEMPERMISSIONS_userId ON SYSTEMPERMISSIONS (userId);
-- constraints
ALTER TABLE SYSTEMPERMISSIONS ADD CONSTRAINT FK_SYSTEMPERMISSIONS_USERID FOREIGN KEY (USERID) REFERENCES USR (ID);
--------------------------------------------------------------------------------


-- System permissions actions --------------------------------------------------
CREATE TABLE SystemPermissions_ACTIONS (
    SystemPermissions_ID VARCHAR,
    actions VARCHAR
);
-- indexes
CREATE INDEX INDEX_SystemPermissions_ACTIONS_actions ON SystemPermissions_ACTIONS (actions);
-- constraints
ALTER TABLE SystemPermissions_ACTIONS ADD CONSTRAINT FK_SystemPermissions_ACTIONS_SystemPermissions_ID FOREIGN KEY (SystemPermissions_ID) REFERENCES SYSTEMPERMISSIONS (ID);
--------------------------------------------------------------------------------


-- Workspace workers -----------------------------------------------------------
CREATE TABLE WORKER (
    ID              VARCHAR         NOT NULL,
    USERID          VARCHAR,
    WORKSPACEID     VARCHAR,

    PRIMARY KEY (ID)
);
-- indexes
CREATE UNIQUE INDEX INDEX_WORKER_userId_workspaceId ON WORKER (userId, workspaceId);
CREATE INDEX INDEX_WORKER_workspaceId ON WORKER (workspaceId);
-- constraints
ALTER TABLE WORKER ADD CONSTRAINT FK_WORKER_USERID FOREIGN KEY (USERID) REFERENCES USR (ID);
ALTER TABLE WORKER ADD CONSTRAINT FK_WORKER_WORKSPACEID FOREIGN KEY (WORKSPACEID) REFERENCES WORKSPACE (ID);
--------------------------------------------------------------------------------


-- Worker actions --------------------------------------------------------------
CREATE TABLE Worker_ACTIONS (
    Worker_ID       VARCHAR,
    actions         VARCHAR
);
-- indexes
CREATE INDEX INDEX_Worker_ACTIONS_actions ON Worker_ACTIONS (actions);
-- constraints
ALTER TABLE Worker_ACTIONS ADD CONSTRAINT FK_Worker_ACTIONS_Worker_ID FOREIGN KEY (Worker_ID) REFERENCES WORKER (ID);
--------------------------------------------------------------------------------


-- Stack permissions -----------------------------------------------------------
CREATE TABLE STACKPERMISSIONS (
    ID          VARCHAR         NOT NULL,
    STACKID     VARCHAR,
    USERID      VARCHAR,

    PRIMARY KEY (ID)
);
-- indexes
CREATE UNIQUE INDEX INDEX_STACKPERMISSIONS_userId_stackId ON STACKPERMISSIONS (userId, stackId);
CREATE INDEX INDEX_STACKPERMISSIONS_stackId ON STACKPERMISSIONS (stackId);
-- constraints
ALTER TABLE STACKPERMISSIONS ADD CONSTRAINT FK_STACKPERMISSIONS_USERID FOREIGN KEY (USERID) REFERENCES USR (ID);
ALTER TABLE STACKPERMISSIONS ADD CONSTRAINT FK_STACKPERMISSIONS_STACKID FOREIGN KEY (STACKID) REFERENCES STACK (ID);
--------------------------------------------------------------------------------


-- Stack permissions actions ---------------------------------------------------
CREATE TABLE StackPermissions_ACTIONS (
    StackPermissions_ID     VARCHAR,
    actions                 VARCHAR
);
-- indexes
CREATE INDEX INDEX_StackPermissions_ACTIONS_actions ON StackPermissions_ACTIONS (actions);
-- constraints
ALTER TABLE StackPermissions_ACTIONS ADD CONSTRAINT FK_StackPermissions_ACTIONS_StackPermissions_ID FOREIGN KEY (StackPermissions_ID) REFERENCES STACKPERMISSIONS (ID);
--------------------------------------------------------------------------------


-- Recipe permissions ----------------------------------------------------------
CREATE TABLE RECIPEPERMISSIONS (
    ID          VARCHAR         NOT NULL,
    RECIPEID    VARCHAR,
    USERID      VARCHAR,

    PRIMARY KEY (ID)
);
-- indexes
CREATE UNIQUE INDEX INDEX_RECIPEPERMISSIONS_userId_recipeId ON RECIPEPERMISSIONS (userId, recipeId);
CREATE INDEX INDEX_RECIPEPERMISSIONS_recipeId ON RECIPEPERMISSIONS (recipeId);
-- constraints
ALTER TABLE RECIPEPERMISSIONS ADD CONSTRAINT FK_RECIPEPERMISSIONS_USERID FOREIGN KEY (USERID) REFERENCES USR (ID);
ALTER TABLE RECIPEPERMISSIONS ADD CONSTRAINT FK_RECIPEPERMISSIONS_RECIPEID FOREIGN KEY (RECIPEID) REFERENCES RECIPE (ID);
--------------------------------------------------------------------------------


-- Recipe permissions actions --------------------------------------------------
CREATE TABLE RecipePermissions_ACTIONS (
    RecipePermissions_ID    VARCHAR,
    actions                 VARCHAR
);
-- indexes
CREATE INDEX INDEX_RecipePermissions_ACTIONS_actions ON RecipePermissions_ACTIONS (actions);
-- constraints
ALTER TABLE RecipePermissions_ACTIONS ADD CONSTRAINT FK_RecipePermissions_ACTIONS_RecipePermissions_ID FOREIGN KEY (RecipePermissions_ID) REFERENCES RECIPEPERMISSIONS (ID);
--------------------------------------------------------------------------------


-- Organization ----------------------------------------------------------------
CREATE TABLE ORGANIZATION (
    ID          VARCHAR         NOT NULL,
    PARENT      VARCHAR,
    ACCOUNT_ID  VARCHAR         NOT NULL,

    PRIMARY KEY (ID)
);
-- indexes
CREATE INDEX INDEX_ORGANIZATION_parent ON ORGANIZATION (parent);
-- constraints
ALTER TABLE ORGANIZATION ADD CONSTRAINT FK_ORGANIZATION_PARENT FOREIGN KEY (PARENT) REFERENCES ORGANIZATION (ID);
ALTER TABLE ORGANIZATION ADD CONSTRAINT FK_ORGANIZATION_ACCOUNT_ID FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNT (ID);
--------------------------------------------------------------------------------


-- Organization member ---------------------------------------------------------
CREATE TABLE MEMBER (
    ID              VARCHAR         NOT NULL,
    ORGANIZATIONID  VARCHAR,
    USERID          VARCHAR,

    PRIMARY KEY (ID)
);
-- indexes
CREATE UNIQUE INDEX INDEX_MEMBER_userId_organizationId ON MEMBER (userId, organizationId);
CREATE INDEX INDEX_MEMBER_organizationId ON MEMBER (organizationId);
-- constraints
ALTER TABLE MEMBER ADD CONSTRAINT FK_MEMBER_ORGANIZATIONID FOREIGN KEY (ORGANIZATIONID) REFERENCES ORGANIZATION (ID);
ALTER TABLE MEMBER ADD CONSTRAINT FK_MEMBER_USERID FOREIGN KEY (USERID) REFERENCES USR (ID);
--------------------------------------------------------------------------------


--Member actions ---------------------------------------------------------------
CREATE TABLE Member_ACTIONS (
    Member_ID       VARCHAR,
    actions         VARCHAR
);
-- indexes
CREATE INDEX INDEX_Member_ACTIONS_actions ON Member_ACTIONS (actions);
-- constraints
 ALTER TABLE Member_ACTIONS ADD CONSTRAINT FK_Member_ACTIONS_Member_ID FOREIGN KEY (Member_ID) REFERENCES MEMBER (ID);
--------------------------------------------------------------------------------


-- Resource --------------------------------------------------------------------
CREATE TABLE RESOURCE (
    ID          BIGINT          NOT NULL,
    AMOUNT      BIGINT,
    TYPE        VARCHAR         NOT NULL,
    UNIT        VARCHAR         NOT NULL,

    PRIMARY KEY (ID)
);
--------------------------------------------------------------------------------


-- Free resource limit ---------------------------------------------------------
CREATE TABLE FREERESOURCESLIMIT (
    ACCOUNTID       VARCHAR         NOT NULL,

    PRIMARY KEY (ACCOUNTID)
);
-- constraints
ALTER TABLE FREERESOURCESLIMIT ADD CONSTRAINT FK_FREERESOURCESLIMIT_ACCOUNTID FOREIGN KEY (ACCOUNTID) REFERENCES ACCOUNT (ID);
--------------------------------------------------------------------------------


-- Free resource limit resource ------------------------------------------------
CREATE TABLE FREERESOURCESLIMIT_RESOURCE (
    FreeResourcesLimit_ACCOUNTID        VARCHAR         NOT NULL,
    resources_ID                        BIGINT          NOT NULL,

    PRIMARY KEY (FreeResourcesLimit_ACCOUNTID, resources_ID)
);
-- constraints
ALTER TABLE FREERESOURCESLIMIT_RESOURCE ADD CONSTRAINT FK_FREERESOURCESLIMIT_RESOURCE_resources_ID FOREIGN KEY (resources_ID) REFERENCES RESOURCE (ID);
ALTER TABLE FREERESOURCESLIMIT_RESOURCE ADD CONSTRAINT FRRESOURCESLIMITRESOURCEFreResourcesLimitACCOUNTID FOREIGN KEY (FreeResourcesLimit_ACCOUNTID) REFERENCES FREERESOURCESLIMIT (ACCOUNTID);
--------------------------------------------------------------------------------
