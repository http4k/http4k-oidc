# Build & Deploy (build_and_deploy.yml)

```mermaid
%%{init: {"flowchart": {"curve": "basis"}}}%%
flowchart TD
    push(["📤 push"])
    workflowdispatch(["👤 workflow_dispatch"])
    subgraph buildanddeployyml["Build & Deploy"]
        buildanddeployyml_build["build<br/>🐧 ubuntu-22.04"]
    end
    push --> buildanddeployyml_build
    workflowdispatch --> buildanddeployyml_build
```

## Job: build

| Job | OS | Dependencies | Config |
|-----|----|--------------|---------| 
| `build` | 🐧 ubuntu-22.04 | - | - |

### Steps

```mermaid
%%{init: {"flowchart": {"curve": "basis"}}}%%
flowchart TD
    step1["Step 1: Checkout"]
    style step1 fill:#f8f9fa,stroke:#495057
    action1["🎬 actions<br/>checkout"]
    style action1 fill:#e1f5fe,stroke:#0277bd
    step1 -.-> action1
    step2["Step 2: Build<br/>💻 bash"]
    style step2 fill:#f3e5f5,stroke:#7b1fa2
    step1 --> step2
    step3["Step 3: Deploy<br/>🔐 if: github.ref == 'refs/heads/main'"]
    style step3 fill:#f8f9fa,stroke:#495057
    action3["🎬 akhileshns<br/>heroku-deploy<br/><br/>📝 Inputs:<br/>• heroku_api_key: ${{ secrets.HTTP4K_OIDC_HEROKU...<br/>• heroku_app_name: http4k-oidc<br/>• heroku_email: ivan@gourame.com<br/>• healthcheck: https://http4k-oidc.herokuapp...."]
    style action3 fill:#e1f5fe,stroke:#0277bd
    step3 -.-> action3
    step2 --> step3
```

**Step Types Legend:**
- 🔘 **Step Nodes** (Gray): Workflow step execution
- 🔵 **Action Blocks** (Blue): External GitHub Actions
- 🔷 **Action Blocks** (Light Blue): Local repository actions
- 🟣 **Script Nodes** (Purple): Run commands/scripts
- **Solid arrows** (→): Step execution flow
- **Dotted arrows** (-.->): Action usage with inputs