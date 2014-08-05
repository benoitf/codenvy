/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.cdec.im;

import com.codenvy.cdec.artifacts.Artifact;
import com.codenvy.cdec.utils.HttpTransport;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.codenvy.cdec.utils.Commons.*;
import static com.codenvy.cdec.utils.Version.compare;

/**
 * Checks and downloads updates by schedule.
 *
 * @author Anatoliy Bazko
 */
@Singleton
public class UpdateChecker {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateChecker.class);

    private final String        codenvyApiEndpoint;
    private final String        codenvyUpdateEndpoint;
    private final Path          downloadDir;
    private final String        updateSchedule;
    private final boolean       downloadAutomatically;
    private final HttpTransport transport;
    private final Set<Artifact> artifacts;

    private Scheduler scheduler;

    @Inject
    public UpdateChecker(@Named("codenvy.installation-manager.codenvy_api_endpoint") String codenvyApiEndpoint,
                         @Named("codenvy.installation-manager.codenvy_update_endpoint") String codenvyUpdateEndpoint,
                         @Named("codenvy.installation-manager.download_dir") String downloadDir,
                         @Named("codenvy.installation-manager.check_update_schedule") String updateSchedule,
                         @Named("codenvy.installation-manager.download_automatically") boolean downloadAutomatically,
                         HttpTransport transport,
                         Set<Artifact> artifacts) throws IOException {
        this.codenvyApiEndpoint = codenvyApiEndpoint;
        this.codenvyUpdateEndpoint = codenvyUpdateEndpoint;
        this.downloadDir = Paths.get(downloadDir);
        this.updateSchedule = updateSchedule;
        this.downloadAutomatically = downloadAutomatically;
        this.transport = transport;
        this.artifacts = artifacts;

        if (!Files.exists(this.downloadDir)) {
            Files.createDirectories(this.downloadDir);
        }

        LOG.info("Download directory " + downloadDir);
    }

    @PostConstruct
    public void init() throws SchedulerException {
        scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();

        JobDetailImpl jobDetail = new JobDetailImpl();
        jobDetail.setKey(new JobKey(CheckUpdates.class.getName()));
        jobDetail.setJobClass(CheckUpdates.class);
        jobDetail.setDurability(true);

        scheduler.scheduleJob(jobDetail, TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(updateSchedule)).build());

    }

    @PreDestroy
    public void destroy() throws SchedulerException {
        scheduler.shutdown(true);
    }

    /**
     * Job to check updates.
     */
    public class CheckUpdates implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            LOG.info("Checking new updates started");

            try {
                Map<Artifact, String> newVersions = getNewVersions();
                if (!newVersions.isEmpty() && downloadAutomatically) {
                    downloadUpdates(newVersions);
                }
            } catch (Exception e) {
                throw new JobExecutionException(e);
            } finally {
                LOG.info("Checking new updates finished");
            }
        }

        /**
         * Downloads updates.
         */
        public void downloadUpdates(Map<Artifact, String> artifacts) throws IOException {
            for (Map.Entry<Artifact, String> entry : artifacts.entrySet()) {
                Artifact artifact = entry.getKey();
                String version = entry.getValue();

                if (!artifact.isValidSubscriptionRequired() || isValidSubscription(transport, codenvyApiEndpoint, "On-Premises")) {
                    transport
                            .download(combinePaths(codenvyUpdateEndpoint, "/repository/download/" + artifact.getName() + "/" + version), downloadDir);
                    LOG.info("Downloaded '" + artifact + "' version " + version);
                } else {
                    LOG.warn("Valid subscription is required to download " + artifact.getName());
                }
            }
        }

        /**
         * @return the list of artifacts with newer versions than currently installed
         * @throws IOException
         *         if any exception occurred
         * @throws IllegalArgumentException
         *         if can't parse version of artifact
         */
        public Map<Artifact, String> getNewVersions() throws IOException, IllegalArgumentException {
            Map<Artifact, String> newVersions = new HashMap<>();
            Map<Artifact, String> existed = getExistedArtifacts();
            Map<Artifact, String> available2Download = getAvailable2DownloadArtifacts();

            for (Map.Entry<Artifact, String> entry : available2Download.entrySet()) {
                Artifact artifact = entry.getKey();
                String newVersion = entry.getValue();

                if (!existed.containsKey(artifact) || compare(newVersion, existed.get(artifact)) > 0) {
                    newVersions.put(artifact, newVersion);
                    LOG.info("New version '" + artifact + "' " + newVersions.get(artifact) + " available to download");
                }
            }

            return newVersions;
        }

        /**
         * Scans all available artifacts and returns their last versions from Update Server.
         */
        public Map<Artifact, String> getAvailable2DownloadArtifacts() throws IOException {
            Map<Artifact, String> available2Download = new HashMap<>();

            for (Artifact artifact : artifacts) {
                try {
                    Map m = fromJson(transport.doGetRequest(combinePaths(codenvyUpdateEndpoint, "repository/version/" + artifact.getName())),
                                     Map.class);

                    if (m != null && m.containsKey("version")) {
                        available2Download.put(artifact, (String)m.get("version"));
                    }
                } catch (IOException e) {
                    LOG.error("Can't retrieve the last version of " + artifact, e);
                }
            }

            return available2Download;
        }

        /**
         * Scans all available artifacts and returns their current versions.
         */
        public Map<Artifact, String> getExistedArtifacts() throws IOException {
            Map<Artifact, String> existed = new HashMap<>();
            for (Artifact artifact : artifacts) {
                try {
                    existed.put(artifact, artifact.getCurrentVersion());
                } catch (IOException e) {
                    throw new IOException("Can't find out current version of " + artifact, e);
                }
            }

            return existed;
        }
    }
}
