package cloud.stackit.sdk.serverupdate.examples;

import cloud.stackit.sdk.core.KeyFlowAuthenticator;
import cloud.stackit.sdk.core.config.CoreConfiguration;
import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.serverupdate.api.ServerUpdateApi;
import cloud.stackit.sdk.serverupdate.model.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

final class ServerUpdateExample {

	private ServerUpdateExample() {}

	@SuppressWarnings({
		"PMD.CyclomaticComplexity",
		"PMD.CognitiveComplexity",
		"PMD.NPathComplexity",
		"PMD.NcssCount",
		"PMD.SystemPrintln",
		"PMD.AvoidThrowingRawExceptionTypes",
		"PMD.AvoidDuplicateLiterals"
	})
	public static void main(String[] args) throws IOException {
		// Credentials are read from the credentialsFile in `~/.stackit/credentials.json` or the env
		// STACKIT_SERVICE_ACCOUNT_KEY_PATH / STACKIT_SERVICE_ACCOUNT_KEY
		CoreConfiguration configuration = new CoreConfiguration();

		OkHttpClient httpClient = new OkHttpClient();
		KeyFlowAuthenticator authenticator = new KeyFlowAuthenticator(httpClient, configuration);
		httpClient =
				httpClient
						.newBuilder()
						.authenticator(authenticator)
						// Some create / update requests may take a few seconds.
						// To prevent a timeout, we increase the read timeout to 30 seconds
						.readTimeout(Duration.ofSeconds(30))
						.build();

		ServerUpdateApi serverUpdateApi = new ServerUpdateApi(httpClient, configuration);

		// the id of your STACKIT project, read from env var for this example
		String projectId = System.getenv("STACKIT_PROJECT_ID");
		if (projectId == null || projectId.isEmpty()) {
			System.err.println("Environment variable 'STACKIT_PROJECT_ID' not found.");
			return;
		}

		// the region which should be used to interact with serverupdate
		String region = "eu01"; // NOPMD

		// the id of your STACKIT server, read from env var for this example
		String serverId = System.getenv("STACKIT_SERVER_ID");
		if (serverId == null || serverId.isEmpty()) {
			System.err.println("Environment variable 'STACKIT_SERVER_ID' not found.");
			return;
		}

		try {
			/*
			 * ///////////////////////////////////////////////////////
			 * //           U P D A T E   P O L I C I E S           //
			 * ///////////////////////////////////////////////////////
			 */
			/* fetching all available update policies */
			System.out.println("List all available update policies:");
			GetUpdatePoliciesResponse listUpdatePolicies =
					serverUpdateApi.listUpdatePolicies(projectId);
			assert listUpdatePolicies.getItems() != null;
			for (UpdatePolicy policy : listUpdatePolicies.getItems()) {
				System.out.println("*************************");
				System.out.println("* Policy name: " + policy.getName());
				System.out.println("* Description: " + policy.getDescription());
				System.out.println("* Policy ID: " + policy.getId());
				System.out.println("* RRULE: " + policy.getRrule());
				System.out.println("* Enabled: " + policy.getEnabled());
				System.out.println("* Default: " + policy.getDefault());
			}

			/*
			 * ///////////////////////////////////////////////////////
			 * //         U P D A T E   E N A B L E M E N T         //
			 * ///////////////////////////////////////////////////////
			 */
			/* checking if update service is enabled for the server */
			System.out.println("\nChecking update service status for the server:");
			try {
				GetUpdateServiceResponse updateServiceStatus =
						serverUpdateApi.getServiceResource(projectId, serverId, region);
				assert updateServiceStatus.getEnabled() != null;
				System.out.println("* Update service enabled: " + updateServiceStatus.getEnabled());
			} catch (ApiException e) {
				// If response status is not found, update service is not enabled for the server
				if (e.getCode() == HttpURLConnection.HTTP_NOT_FOUND) {
					System.out.println("* Update service is not enabled for the server");
					System.out.println("* Enabling update service...");
					String policyIdString = listUpdatePolicies.getItems().get(0).getId();
					assert policyIdString != null;
					UUID policyId = UUID.fromString(policyIdString);
					serverUpdateApi.enableServiceResource(
							projectId,
							serverId,
							region,
							new EnableServiceResourcePayload().updatePolicyId(policyId));
					System.out.println("* Update service successful enabled for the server");
				}
			}

			/*
			 * ///////////////////////////////////////////////////////
			 * //           U P D A T E   S C H E D U L E           //
			 * ///////////////////////////////////////////////////////
			 */
			/* creating a new nightly update schedule for the server*/
			System.out.println("\nCreating a new update schedule:");
			UpdateSchedule newSchedule =
					serverUpdateApi.createUpdateSchedule(
							projectId,
							serverId,
							region,
							new CreateUpdateSchedulePayload()
									.name("Nightly 3 AM")
									.enabled(true)
									.maintenanceWindow(5)
									.rrule(
											"DTSTART;TZID=Europe/Berlin:20251210T030000 RRULE:FREQ=DAILY;INTERVAL=1"));
			System.out.println("* Schedule ID: " + newSchedule.getId());
			System.out.println("* Name: " + newSchedule.getName());
			System.out.println("* Enabled: " + newSchedule.getEnabled());
			System.out.println("* RRULE: " + newSchedule.getRrule());
			System.out.println("* Maintenance Window: " + newSchedule.getMaintenanceWindow());

			/* updating the created update schedule */
			System.out.println("\nUpdating the update schedule:");
			UpdateSchedule updatedSchedule =
					serverUpdateApi.updateUpdateSchedule(
							projectId,
							serverId,
							newSchedule.getId().toString(),
							region,
							new UpdateUpdateSchedulePayload()
									.name("Nightly 10 PM")
									.enabled(false)
									.maintenanceWindow(2)
									.rrule(
											"DTSTART;TZID=Europe/Berlin:20251210T220000 RRULE:FREQ=DAILY;INTERVAL=1"));
			System.out.println("* Schedule ID: " + updatedSchedule.getId());
			System.out.println("* Name: " + updatedSchedule.getName());
			System.out.println("* Enabled: " + updatedSchedule.getEnabled());
			System.out.println("* RRULE: " + updatedSchedule.getRrule());
			System.out.println("* Maintenance Window: " + updatedSchedule.getMaintenanceWindow());

			/* list all available update schedules of the server */
			System.out.println("\nList all update schedules");
			GetUpdateSchedulesResponse listUpdateSchedules =
					serverUpdateApi.listUpdateSchedules(projectId, serverId, region);
			assert listUpdateSchedules.getItems() != null;
			for (UpdateSchedule schedule : listUpdateSchedules.getItems()) {
				System.out.println("*************************");
				System.out.println("* Schedule ID: " + schedule.getId());
				System.out.println("* Name: " + schedule.getName());
				System.out.println("* Enabled: " + schedule.getEnabled());
				System.out.println("* RRULE: " + schedule.getRrule());
				System.out.println("* Maintenance Window: " + schedule.getMaintenanceWindow());
			}

			/* deleting the update schedule we created */
			System.out.println("\nDeleting update schedule:");
			serverUpdateApi.deleteUpdateSchedule(
					projectId, serverId, newSchedule.getId().toString(), region);
			System.out.println("* Deleted update schedule successful");

			/*
			 * ///////////////////////////////////////////////////////
			 * //                   U P D A T E S                   //
			 * ///////////////////////////////////////////////////////
			 */
			/* trigger manually an update of the server */
			System.out.println("\nTrigger a server update:");
			Update newUpdate =
					serverUpdateApi.createUpdate(
							projectId,
							serverId,
							region,
							new CreateUpdatePayload()
									.backupBeforeUpdate(false)
									.maintenanceWindow(11));
			System.out.println("* Update status: " + newUpdate.getStatus());

			/* wait for the update to complete */
			while (Objects.equals(
					serverUpdateApi
							.getUpdate(projectId, serverId, newUpdate.getId().toString(), region)
							.getStatus(),
					"running")) {
				System.out.println("Waiting for update to complete ...");
				TimeUnit.SECONDS.sleep(5);
			}
			System.out.println("* Update finished");

			/* fetch details of the update */
			System.out.println("\nUpdate details:");
			Update getUpdate =
					serverUpdateApi.getUpdate(
							projectId, serverId, newUpdate.getId().toString(), region);
			System.out.println("* ID: " + getUpdate.getId());
			System.out.println("* Status: " + getUpdate.getStatus());
			System.out.println("* Installed updates: " + getUpdate.getInstalledUpdates());
			System.out.println("* Failed updates: " + getUpdate.getFailedUpdates());
			if (getUpdate.getFailReason() != null && !getUpdate.getFailReason().isEmpty()) {
				System.out.println("* Fail reason: " + getUpdate.getFailReason());
			}
			System.out.println("* Start date: " + getUpdate.getStartDate());
			System.out.println("* End date: " + getUpdate.getEndDate());

			/* list all executed updated */
			System.out.println("\nList updates:");
			GetUpdatesListResponse listUpdates =
					serverUpdateApi.listUpdates(projectId, serverId, region);
			assert listUpdates.getItems() != null;
			for (Update update : listUpdates.getItems()) {
				System.out.println("*************************");
				System.out.println("* ID: " + update.getId());
				System.out.println("* Status: " + update.getStatus());
				System.out.println("* Installed updates: " + update.getInstalledUpdates());
				System.out.println("* Failed updates: " + update.getFailedUpdates());
				if (update.getFailReason() != null && !update.getFailReason().isEmpty()) {
					System.out.println("* Fail reason: " + update.getFailReason());
				}
				System.out.println("* Start date: " + update.getStartDate());
				System.out.println("* End date: " + update.getEndDate());
			}
		} catch (ApiException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
