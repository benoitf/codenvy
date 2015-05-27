/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2015] Codenvy, S.A.
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
package com.codenvy.im.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Is used when downloading is finished.
 * No progress info, just a final result as {@link com.codenvy.im.response.ResponseCode}
 *
 * @author Anatoliy Bazko
 */
@JsonPropertyOrder({"artifacts", "message", "status"})
public class DownloadDescriptor {

    private ResponseCode                     status;
    private String                           message;
    private List<DownloadArtifactDescriptor> artifacts;

    public DownloadDescriptor() {
    }

    public DownloadDescriptor(DownloadProgressDescriptor downloadProgressDescriptor) {
        this.artifacts = new ArrayList<>(downloadProgressDescriptor.getArtifacts());
        this.status = downloadProgressDescriptor.getStatus() == DownloadArtifactStatus.FAILED ? ResponseCode.ERROR : ResponseCode.OK;
        this.message = downloadProgressDescriptor.getMessage();
    }

    public DownloadDescriptor(ResponseCode status, List<DownloadArtifactDescriptor> artifacts) {
        this.status = status;
        this.artifacts = new ArrayList<>(artifacts);
        this.message = null;
    }

    public ResponseCode getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<DownloadArtifactDescriptor> getArtifacts() {
        return artifacts;
    }

    public void setStatus(ResponseCode status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setArtifacts(List<DownloadArtifactDescriptor> artifacts) {
        this.artifacts = artifacts;
    }
}
