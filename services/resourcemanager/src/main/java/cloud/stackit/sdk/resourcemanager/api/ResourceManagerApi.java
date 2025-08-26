package cloud.stackit.sdk.resourcemanager.api;

import cloud.stackit.sdk.core.config.CoreConfiguration;
import java.io.IOException;

public class ResourceManagerApi extends DefaultApi {
	public ResourceManagerApi() throws IOException {
		super();
	}

	public ResourceManagerApi(CoreConfiguration configuration) throws IOException {
		super(configuration);
	}
} 