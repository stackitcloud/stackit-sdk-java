package cloud.stackit.sdk.core.wait;

import cloud.stackit.sdk.core.exception.ApiException;

// Since the Callable FunctionalInterface throws a generic Exception
// and the linter complains about catching a generic Exception this
// FunctionalInterface is needed.
@FunctionalInterface
public interface CheckFunction<V> {
	V execute() throws ApiException;
}
