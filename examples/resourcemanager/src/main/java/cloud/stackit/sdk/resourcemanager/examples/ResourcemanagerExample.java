package cloud.stackit.sdk.resourcemanager.examples;

import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.resourcemanager.api.ResourceManagerApi;
import cloud.stackit.sdk.resourcemanager.model.CreateFolderPayload;
import cloud.stackit.sdk.resourcemanager.model.CreateProjectPayload;
import cloud.stackit.sdk.resourcemanager.model.FolderResponse;
import cloud.stackit.sdk.resourcemanager.model.ListFoldersResponse;
import cloud.stackit.sdk.resourcemanager.model.ListProjectsResponse;
import cloud.stackit.sdk.resourcemanager.model.Member;
import cloud.stackit.sdk.resourcemanager.model.OrganizationResponse;
import cloud.stackit.sdk.resourcemanager.model.PartialUpdateFolderPayload;
import cloud.stackit.sdk.resourcemanager.model.PartialUpdateProjectPayload;
import cloud.stackit.sdk.resourcemanager.model.Project;
import cloud.stackit.sdk.resourcemanager.wait.ResourcemanagerWait;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

final class ResourcemanagerExample {

	/** Prevent instantiation */
	private ResourcemanagerExample() {}

	@SuppressWarnings({"PMD.SystemPrintln", "PMD.AvoidThrowingRawExceptionTypes"})
	public static void main(String[] args)
			throws IOException, ApiException, InterruptedException, ExecutionException {
		// Credentials are read from the credentialsFile in `~/.stackit/credentials.json` or the env
		// STACKIT_SERVICE_ACCOUNT_KEY_PATH / STACKIT_SERVICE_ACCOUNT_KEY
		ResourceManagerApi resourceManagerApi = new ResourceManagerApi();

		// Read the organization id and the member subject from the environment
		String organizationIdString = System.getenv("STACKIT_ORGANIZATION_ID");
		String memberSubjectString = System.getenv("STACKIT_MEMBER_SUBJECT");
		if (organizationIdString == null || organizationIdString.isEmpty()) {
			System.err.println("Environment variable 'STACKIT_ORGANIZATION_ID' not found.");
			return;
		}
		if (memberSubjectString == null || memberSubjectString.isEmpty()) {
			System.err.println("Environment variable 'STACKIT_MEMBER_SUBJECT' not found.");
			return;
		}
		UUID containerParentId = UUID.fromString(organizationIdString);

		Member member =
				new Member()
						.role("project.owner")
						.subject(memberSubjectString); // replace with an existing subject

		String labelName = "foo";
		/* create a project */
		Project project =
				resourceManagerApi.createProject(
						new CreateProjectPayload()
								.containerParentId(containerParentId.toString())
								.name("java-test-project")
								.addMembersItem(member)
								.labels(Collections.singletonMap(labelName, "bar")));
		System.out.println("Project:\n" + project.toString());

		/* list projects */
		ListProjectsResponse responseListProject =
				resourceManagerApi.listProjects(organizationIdString, null, null, null, null, null);
		System.out.println("Project List:\n" + responseListProject.toString());

		/* create a folder */
		FolderResponse folder =
				resourceManagerApi.createFolder(
						new CreateFolderPayload()
								.containerParentId(containerParentId.toString())
								.name("java-test-folder")
								.addMembersItem(member)
								.labels(Collections.singletonMap(labelName, "bar")));
		System.out.println("Folder: \n" + folder.toString());

		ResourcemanagerWait.createProjectWaitHandler(resourceManagerApi, project.getContainerId())
				.waitWithContextAsync()
				.get();

		/* list folders */
		ListFoldersResponse responseListFolders =
				resourceManagerApi.listFolders(organizationIdString, null, null, null, null, null);
		System.out.println("Folder List:\n" + responseListFolders.toString());

		/* delete a project label */
		resourceManagerApi.deleteProjectLabels(project.getContainerId(), Arrays.asList(labelName));

		/* delete a folder label */
		resourceManagerApi.deleteFolderLabels(folder.getContainerId(), Arrays.asList(labelName));

		/* update folder labels */
		resourceManagerApi.partialUpdateFolder(
				folder.getContainerId(),
				new PartialUpdateFolderPayload().labels(Collections.singletonMap("foo2", "bar2")));

		/* update project move to created folder */
		resourceManagerApi.partialUpdateProject(
				project.getContainerId(),
				new PartialUpdateProjectPayload().containerParentId(folder.getContainerId()));

		ResourcemanagerWait.updateProjectWaitHandler(resourceManagerApi, project.getContainerId())
				.waitWithContextAsync()
				.get();

		/* get organization details */
		OrganizationResponse organizationResponse =
				resourceManagerApi.getOrganization(organizationIdString);
		System.out.println("Organization List:\n" + organizationResponse.toString());

		/* since you cannot delete a folder when a project is present we need to move the project out again */
		resourceManagerApi.partialUpdateProject(
				project.getContainerId(),
				new PartialUpdateProjectPayload().containerParentId(organizationIdString));

		/* delete project */
		resourceManagerApi.deleteProject(project.getContainerId());

		/* wait for the project deletion to complete */
		ResourcemanagerWait.deleteProjectWaitHandler(resourceManagerApi, project.getContainerId())
				.waitWithContextAsync()
				.get();

		/* delete folder */
		resourceManagerApi.deleteFolder(folder.getContainerId(), null);
	}
}
