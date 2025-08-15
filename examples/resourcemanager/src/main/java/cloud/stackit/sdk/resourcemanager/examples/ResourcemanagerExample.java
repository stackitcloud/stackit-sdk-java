package cloud.stackit.sdk.resourcemanager.examples;

import cloud.stackit.sdk.resourcemanager.ApiClient;
import cloud.stackit.sdk.resourcemanager.ApiException;
import cloud.stackit.sdk.resourcemanager.api.ResourceManagerApi;
import cloud.stackit.sdk.resourcemanager.model.CreateFolderPayload;
import cloud.stackit.sdk.resourcemanager.model.CreateProjectPayload;
import cloud.stackit.sdk.resourcemanager.model.FolderResponse;
import cloud.stackit.sdk.resourcemanager.model.Project;
import java.util.Map;
import java.util.UUID;

class ResourcemanagerExample {
	public static void main(String[] args) {
		ApiClient apiClient = new ApiClient();
		ResourceManagerApi resourceManagerApi = new ResourceManagerApi(apiClient);

		// replace this with something useful for real use
		UUID containerParentId = UUID.randomUUID();

		try {
			/* create a project */
			Project project =
					resourceManagerApi.createProject(
							new CreateProjectPayload()
									.containerParentId(containerParentId.toString())
									.labels(Map.ofEntries(Map.entry("foo", "bar"))));

			/* create a folder */
			FolderResponse folder =
					resourceManagerApi.createFolder(
							new CreateFolderPayload()
									.containerParentId(containerParentId.toString())
									.labels(Map.ofEntries(Map.entry("foo", "bar"))));
		} catch (ApiException e) {
			throw new RuntimeException(e);
		}
	}
}
