package cloud.stackit.sdk.resourcemanager.examples;

import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.resourcemanager.api.ResourceManagerApi;
import cloud.stackit.sdk.resourcemanager.model.CreateFolderPayload;
import cloud.stackit.sdk.resourcemanager.model.CreateProjectPayload;
import cloud.stackit.sdk.resourcemanager.model.FolderResponse;
import cloud.stackit.sdk.resourcemanager.model.Project;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

class ResourcemanagerExample {
	public static void main(String[] args) throws IOException {
		// Credentials are read from the credentialsFile in `~/.stackit/credentials.json` or the env
		// STACKIT_SERVICE_ACCOUNT_KEY_PATH / STACKIT_SERVICE_ACCOUNT_KEY
		ResourceManagerApi resourceManagerApi = new ResourceManagerApi();

		// replace this with something useful for real use
		UUID containerParentId = UUID.randomUUID();

		try {
			/* create a project */
			Project project =
					resourceManagerApi.createProject(
							new CreateProjectPayload()
									.containerParentId(containerParentId.toString())
									.labels(Collections.singletonMap("foo", "bar")));

			/* create a folder */
			FolderResponse folder =
					resourceManagerApi.createFolder(
							new CreateFolderPayload()
									.containerParentId(containerParentId.toString())
									.labels(Collections.singletonMap("foo", "bar")));
		} catch (ApiException e) {
			throw new RuntimeException(e);
		}
	}
}
