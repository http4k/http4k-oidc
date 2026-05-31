# Workflows

```mermaid
flowchart LR
    push(["📤 push"])
    workflowdispatch(["👤 workflow_dispatch"])
    pullrequest(["🔀 pull_request"])
    schedule(["⏰ schedule"])
    buildanddeployyml["Build & Deploy"]
    createprforhttp4kupgradeyml["Create PR for http4k upgrade"]
    handleprforhttp4kupgradeyml["Handle PR for http4k upgrade"]
    updatedependenciesyml["Update Dependencies"]
    repositorydispatchgithubrepository(["🔔 repository_dispatch<br/>→ this repo"])
    push --> buildanddeployyml
    workflowdispatch --> buildanddeployyml
    workflowdispatch --> updatedependenciesyml
    pullrequest -->|"(labeled)"|handleprforhttp4kupgradeyml
    schedule -->|"0 08 * * 1"|updatedependenciesyml
    repositorydispatchgithubrepository -->|"http4k-release"|createprforhttp4kupgradeyml
```

## Workflows

- [Build & Deploy](./build_and_deploy/)
- [Create PR for http4k upgrade](./create_pr_for_http4k_upgrade/)
- [Handle PR for http4k upgrade](./handle_pr_for_http4k_upgrade/)
- [Update Dependencies](./update-dependencies/)