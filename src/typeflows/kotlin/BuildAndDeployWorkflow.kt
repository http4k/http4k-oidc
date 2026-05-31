import io.typeflows.github.workflow.Job
import io.typeflows.github.workflow.RunsOn
import io.typeflows.github.workflow.StrExp
import io.typeflows.github.workflow.Workflow
import io.typeflows.github.workflow.step.RunCommand
import io.typeflows.github.workflow.step.UseAction
import io.typeflows.github.workflow.step.marketplace.Checkout
import io.typeflows.github.workflow.step.marketplace.JavaDistribution
import io.typeflows.github.workflow.step.marketplace.JavaVersion
import io.typeflows.github.workflow.step.marketplace.SetupJava
import io.typeflows.github.workflow.trigger.Push
import io.typeflows.github.workflow.trigger.WorkflowDispatch
import io.typeflows.util.Builder

class BuildAndDeployWorkflow : Builder<Workflow> {
    override fun build() = Workflow("build_and_deploy") {
        displayName = "Build & Deploy"

        on += Push()
        on += WorkflowDispatch()

        jobs += Job("build", RunsOn.of("ubuntu-22.04")) {
            displayName = "Build & Deploy"

            steps += Checkout()
            steps += SetupJava(JavaDistribution.Temurin, JavaVersion.V21) {
                with += mapOf("cache" to "gradle")
            }
            steps += RunCommand("./gradlew check") {
                name = "Build"
            }
            steps += UseAction("akhileshns/heroku-deploy@v3.13.15") {
                name = "Deploy"
                condition = StrExp.of("github.ref == 'refs/heads/main'")
                with += mapOf(
                    "heroku_api_key" to $$"${{ secrets.HTTP4K_OIDC_HEROKU_API_KEY }}",
                    "heroku_app_name" to "http4k-oidc",
                    "heroku_email" to "ivan@gourame.com",
                    "healthcheck" to "https://http4k-oidc.herokuapp.com/health"
                )
            }
        }
    }
}
