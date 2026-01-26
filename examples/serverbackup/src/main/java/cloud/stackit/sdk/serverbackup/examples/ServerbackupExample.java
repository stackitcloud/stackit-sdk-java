package cloud.stackit.sdk.serverbackup.examples;

import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.iaas.api.IaasApi;
import cloud.stackit.sdk.iaas.model.*;
import cloud.stackit.sdk.serverbackup.api.ServerBackupApi;
import cloud.stackit.sdk.serverbackup.model.*;
import cloud.stackit.sdk.serverbackup.model.Backup;
import cloud.stackit.sdk.serverbackup.model.CreateBackupPayload;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

final class ServerBackupExample {
	private static final String REGION = "eu01";

	private ServerBackupExample() {}

	@SuppressWarnings({
		"PMD.CyclomaticComplexity",
		"PMD.SystemPrintln",
		"PMD.AvoidThrowingRawExceptionTypes"
	})
	public static void main(String[] args) throws IOException {
		/*
		 * Credentials are read from the credentialsFile in `~/.stackit/credentials.json` or the env
		 * STACKIT_SERVICE_ACCOUNT_KEY_PATH / STACKIT_SERVICE_ACCOUNT_KEY
		 * */
		IaasApi iaasApi = new IaasApi();
		ServerBackupApi serverBackupApi = new ServerBackupApi();

		// the id of your STACKIT project, read from env var for this example
		String projectIdString = System.getenv("STACKIT_PROJECT_ID");
		if (projectIdString == null || projectIdString.isEmpty()) {
			System.err.println("Environment variable 'STACKIT_PROJECT_ID' not found.");
			return;
		}
		UUID projectId = UUID.fromString(projectIdString);

		// the id of your STACKIT server, read from env var for this example
		String serverIdString = System.getenv("STACKIT_SERVER_ID");
		if (serverIdString == null || serverIdString.isEmpty()) {
			System.err.println("Environment variable 'STACKIT_SERVER_ID' not found.");
			return;
		}
		UUID serverId = UUID.fromString(serverIdString);

		try {
			Server server = iaasApi.getServer(projectId, serverId, true);
			assert server.getId() != null;
			assert server.getVolumes() != null;

			// enable the backup service for the server
			serverBackupApi.enableServiceResource(
					projectIdString,
					server.getId().toString(),
					REGION,
					new EnableServiceResourcePayload());

			// BACKUP POLICIES

			// list backup policies
			GetBackupPoliciesResponse policies =
					serverBackupApi.listBackupPolicies(projectIdString);
			assert policies.getItems() != null;
			System.out.println("\nAvailable backup policies: ");
			for (BackupPolicy p : policies.getItems()) {
				System.out.println(p.getId() + " | " + p.getName());
			}

			// BACKUP SCHEDULES

			List<String> volumeIds =
					server.getVolumes().stream().map(UUID::toString).collect(Collectors.toList());

			// create new backup schedule
			BackupSchedule newBackupSchedule =
					serverBackupApi.createBackupSchedule(
							projectIdString,
							server.getId().toString(),
							REGION,
							new CreateBackupSchedulePayload()
									.name("java-sdk-example-backup-schedule")
									.enabled(true)
									.rrule(
											"DTSTART;TZID=Europe/Sofia:20200803T023000 RRULE:FREQ=DAILY;INTERVAL=1")
									.backupProperties(
											new BackupProperties()
													.name("java-sdk-example-backup")
													.retentionPeriod(5)
													.volumeIds(volumeIds)));

			// list backup schedules
			GetBackupSchedulesResponse backupSchedules =
					serverBackupApi.listBackupSchedules(
							projectIdString, server.getId().toString(), REGION);
			System.out.println("\nAvailable backup schedules: ");
			assert backupSchedules.getItems() != null;
			for (BackupSchedule s : backupSchedules.getItems()) {
				System.out.println(s.getId() + " | " + s.getName());
			}

			// delete backup schedule
			serverBackupApi.deleteBackupSchedule(
					projectIdString,
					server.getId().toString(),
					REGION,
					newBackupSchedule.getId().toString());

			// BACKUPS

			// create backup
			BackupJob newBackup =
					serverBackupApi.createBackup(
							projectIdString,
							server.getId().toString(),
							REGION,
							new CreateBackupPayload()
									.name("java-sdk-example-single-backup")
									.retentionPeriod(5)
									.volumeIds(volumeIds));

			// wait for creation of the backup
			System.out.println("\nWaiting for backup creation...");
			Backup.StatusEnum backupStatus;
			do {
				TimeUnit.SECONDS.sleep(30);
				Backup backup =
						serverBackupApi.getBackup(
								projectIdString,
								server.getId().toString(),
								REGION,
								newBackup.getId());
				backupStatus = backup.getStatus();
			} while (backupStatus == Backup.StatusEnum.IN_PROGRESS);
			System.out.println(backupStatus);

			// list backups
			GetBackupsListResponse backups =
					serverBackupApi.listBackups(projectIdString, server.getId().toString(), REGION);
			System.out.println("\nAvailable backups: ");
			assert backups.getItems() != null;
			for (Backup b : backups.getItems()) {
				System.out.println(b.getId() + " | " + b.getName());
			}

			// trigger restore of a backup
			serverBackupApi.restoreBackup(
					projectIdString,
					server.getId().toString(),
					REGION,
					newBackup.getId(),
					new RestoreBackupPayload().startServerAfterRestore(true).volumeIds(volumeIds));

			// wait for restore of the backup
			System.out.println("\nWaiting for backup restore...");
			do {
				TimeUnit.SECONDS.sleep(5);
				Backup backup =
						serverBackupApi.getBackup(
								projectIdString,
								server.getId().toString(),
								REGION,
								newBackup.getId());
				backupStatus = backup.getStatus();
			} while (backupStatus == Backup.StatusEnum.IN_PROGRESS);

			// delete backup
			serverBackupApi.deleteBackup(
					projectIdString, server.getId().toString(), REGION, newBackup.getId(), true);

			// disable the backup service for the server
			serverBackupApi.disableServiceResource(
					projectIdString, server.getId().toString(), REGION);

		} catch (ApiException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
