import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Input
import org.gradle.api.GradleException

class VersionCheckTask extends DefaultTask {
    @Input
    def versionRegex = ~/^[0-1]\.\d+\.\d+$/

    @TaskAction
    def checkVersion() {
        File versionFile = project.file('VERSION')

        if (!versionFile.exists()) {
            logger.quiet("VERSION file not found in subproject: ${project.name}")
            return
        }

        def version = versionFile.text.trim()
        if (!(version =~ versionRegex)) {
            throw new GradleException("Version '${version}' in project '${project.name}' does not match regex: ${versionRegex}")
        }
    }
}
