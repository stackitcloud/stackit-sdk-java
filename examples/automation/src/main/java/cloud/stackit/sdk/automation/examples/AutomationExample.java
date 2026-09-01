package cloud.stackit.sdk.automation.examples;

import cloud.stackit.sdk.automation.v1betaapi.api.AutomationApi;
import cloud.stackit.sdk.automation.v1betaapi.model.*;
import cloud.stackit.sdk.automation.v1betaapi.model.SnapshotRetentionPolicyCount.KindEnum;
import cloud.stackit.sdk.automation.v1betaapi.model.VolumeExecutionResponse.StatusEnum;
import cloud.stackit.sdk.core.exception.ApiException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

final class AutomationExample {

	private AutomationExample() {}

	@SuppressWarnings({
		"PMD.CyclomaticComplexity",
		"PMD.CognitiveComplexity",
		"PMD.NPathComplexity",
		"PMD.NcssCount",
		"PMD.SystemPrintln",
		"PMD.AvoidDuplicateLiterals",
		"PMD.AvoidThrowingRawExceptionTypes"
	})
	public static void main(String[] args) throws IOException {
		// Credentials are read from the credentialsFile in
		// `~/.stackit/credentials.json` or the env
		// STACKIT_SERVICE_ACCOUNT_KEY_PATH / STACKIT_SERVICE_ACCOUNT_KEY
		AutomationApi automationApi = new AutomationApi();

		// the id of your STACKIT project, read from env var for this example
		String projectId = System.getenv("STACKIT_PROJECT_ID");
		if (projectId == null || projectId.isEmpty()) {
			System.err.println("Environment variable 'STACKIT_PROJECT_ID' not found.");
			return;
		}

		// the region which should be used to interact with the automation service
		String region = "eu01";

		try {
			/*
			 * ///////////////////////////////////////////////////////
			 * //          V O L U M E   T E M P L A T E S          //
			 * ///////////////////////////////////////////////////////
			 */
			/* list all available volume templates */
			ListTemplatesResponse listTemplates =
					automationApi.listVolumeTemplates(projectId, region, null, null);
			System.out.println("Listing volume templates:");
			Objects.requireNonNull(listTemplates.getItems());
			for (Template template : listTemplates.getItems()) {
				System.out.println("* Template ID: " + template.getId());
			}
			/* if there is a nextPageToken, fetch next page of results */
			while (listTemplates.getNextPageToken() != null
					&& !"".equals(listTemplates.getNextPageToken())) {
				listTemplates =
						automationApi.listVolumeTemplates(
								projectId, region, null, listTemplates.getNextPageToken());
				System.out.println("Listing next page of volume templates:");
				Objects.requireNonNull(listTemplates.getItems());
				for (Template template : listTemplates.getItems()) {
					System.out.println("* Template ID: " + template.getId());
				}
			}

			/* get one specific volume template */
			GetVolumeTemplateResponse template =
					automationApi.getVolumeTemplate(
							projectId, region, listTemplates.getItems().get(0).getId());
			System.out.println("\n\nFetched volume template:");
			System.out.println("* Template ID: " + template.getId());
			System.out.println("* Template name: " + template.getName());
			System.out.println("* Template description: " + template.getDescription());

			/*
			 * ///////////////////////////////////////////////////////
			 * //         V O L U M E   A U T O M A T I O N         //
			 * ///////////////////////////////////////////////////////
			 */
			/* create a volume automation with a schedule trigger (daily at 02:00) */
			VolumeAutomation volumeAutomation =
					automationApi.createVolumeAutomation(
							projectId,
							region,
							new CreateVolumeAutomationPayload()
									.description(
											"Creates daily recovery points for all volumes with specified label")
									.name("My Daily Volume Recovery Point Creation Automation")
									.templateId(UUID.fromString(template.getId()))
									.input(
											new VolumeAutomationInput(
													new VolumeRecoveryPointManagementInput()
															.inheritVolumeLabels(true)
															.kind("VolumeRecoveryPointManagement")
															.recoveryPointLabels(
																	Collections.singletonMap(
																			"exampleLabelKey1",
																			"exampleLabelValue1"))
															.snapshotRetentionPolicy(
																	new SnapshotRetentionPolicy(
																			new SnapshotRetentionPolicyCount()
																					.kind(
																							KindEnum
																									.COUNT)
																					.value(2)))
															.volumeLabelSelector(
																	"myLabelkey1=myLabelValue,myLabelKey2=myOtherLabelValue")))
									.triggers(
											new AutomationTriggers()
													.schedule(
															new AutomationScheduleTrigger()
																	.rrule(
																			"DTSTART;TZID=Europe/Sofia:20200803T023000\nRRULE:FREQ=DAILY;INTERVAL=1"))));
			System.out.println("\n\nCreated volume automation:");
			System.out.println("* Automation ID: " + volumeAutomation.getId());
			System.out.println("* Automation name: " + volumeAutomation.getName());
			System.out.println("* Automation description: " + volumeAutomation.getDescription());

			/* list all volume automations */
			ListAutomationsResponse listAutomations =
					automationApi.listVolumeAutomations(projectId, region, null, null);
			System.out.println("\n\nListing volume automations:");
			Objects.requireNonNull(listAutomations.getItems());
			for (ListAutomationsItem automation : listAutomations.getItems()) {
				System.out.println("* Automation ID: " + automation.getId());
			}
			/* if there is a nextPageToken, fetch next page of results */
			while (listAutomations.getNextPageToken() != null
					&& !"".equals(listAutomations.getNextPageToken())) {
				listAutomations =
						automationApi.listVolumeAutomations(
								projectId, region, null, listAutomations.getNextPageToken());
				System.out.println("\nListing next page of volume automations:");
				Objects.requireNonNull(listAutomations.getItems());
				for (ListAutomationsItem automation : listAutomations.getItems()) {
					System.out.println("* Automation ID: " + automation.getId());
				}
			}

			/* update an automation */
			VolumeAutomation updatedAutomation =
					automationApi.partialUpdateVolumeAutomation(
							projectId,
							region,
							volumeAutomation.getId().toString(),
							null,
							new PartialUpdateVolumeAutomationPayload()
									.description("Updated daily recovery points automation")
									.name(
											"Updated Daily Volume Recovery Point Creation Automation"));
			System.out.println("\n\nUpdated volume automation:");
			System.out.println("* Automation ID: " + updatedAutomation.getId());
			System.out.println("* Automation name: " + updatedAutomation.getName());
			System.out.println("* Automation description: " + updatedAutomation.getDescription());

			/* get one specific volume automation */
			VolumeAutomation fetchedAutomation =
					automationApi.getVolumeAutomation(
							projectId, region, volumeAutomation.getId().toString());
			System.out.println("\n\nFetched updated volume template:");
			System.out.println("* Automation ID: " + fetchedAutomation.getId());
			System.out.println("* Automation name: " + fetchedAutomation.getName());
			System.out.println("* Automation description: " + fetchedAutomation.getDescription());

			/*
			 * ///////////////////////////////////////////////////////
			 * //                E X E C U T I O N S                //
			 * ///////////////////////////////////////////////////////
			 */

			/* trigger an automation execution */
			VolumeExecutionResponse createdExecution =
					automationApi.createVolumeExecution(
							projectId, region, volumeAutomation.getId().toString());
			System.out.println("\n\nTriggered volume automation execution:");
			System.out.println("* Execution ID: " + createdExecution.getId());
			System.out.println("* Execution status: " + createdExecution.getStatus());

			/* wait for the automation executing to complete */
			while (createdExecution.getStatus() == StatusEnum.PENDING
					|| createdExecution.getStatus() == StatusEnum.RUNNING) {
				System.out.println("* Waiting for automation execution to complete ...");
				TimeUnit.SECONDS.sleep(2);
				createdExecution =
						automationApi.getVolumeExecution(
								projectId,
								region,
								volumeAutomation.getId().toString(),
								createdExecution.getId().toString());
			}
			System.out.println("* Automation execution completed");

			/* list all executions of a specifc volume automation */
			ListExecutionsResponse listExecutions =
					automationApi.listVolumeExecutions(
							projectId, region, volumeAutomation.getId().toString(), null, null);
			System.out.println("\n\nListing executions of the volume automation:");
			Objects.requireNonNull(listExecutions.getItems());
			for (ListExecutionsItem execution : listExecutions.getItems()) {
				System.out.println("* Execution ID: " + execution.getId());
			}

			/*
			 * ///////////////////////////////////////////////////////
			 * //                  D E L E T I O N                  //
			 * ///////////////////////////////////////////////////////
			 */
			/* trigger deletion of the created application load balancer instance */
			System.out.println("\n\nDeleting created volume automation");
			automationApi.deleteVolumeAutomation(
					projectId, region, volumeAutomation.getId().toString());
			System.out.printf(
					"* Successfully deleted automation with ID \"%s\"",
					volumeAutomation.getId().toString());
		} catch (ApiException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
