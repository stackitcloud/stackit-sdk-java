#!/usr/bin/env bash

# This script creates the git tags and publishes the artifacts to Maven Central

# Immediate exit on failure
set -e

# VERSION contains only one single line which contains the version of the service
# in the following format e.g. 0.3.0

# iterate over all services and core by their VERSION files
for file in $(find . -printf '%P\n' | grep -E "(^services/[^/]+/VERSION$|^core/VERSION$)"); do

    # Extract the current version and build the expected tag
    dirpath=$(dirname "$file")
    version_path="$dirpath/VERSION"
    gradle_subproject="${dirpath//\//:}" # replace "/" with ":"
    version=$(<"$version_path")
    expected_tag="$dirpath/v$version"
    
    printf "\n============================================================================================\n\n"
    
    # Check if the tag already exists
    if git rev-parse --verify "$expected_tag" &> /dev/null; then
        echo "Tag '$expected_tag' already exists. Skipping tag creation and gradle publish..."
    else
        # Tag doesn't exist. Create a tag and push it.
        echo "Tag '$expected_tag' does not exist."
        git tag -a $expected_tag -m "Release $version"
        git push origin tag $expected_tag
        
        ./gradlew "${gradle_subproject}:publishToMavenCentral"
    fi
done
