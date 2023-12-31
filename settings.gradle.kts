rootProject.name = "integrationtest-demo"

plugins {
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "1.1.17"
}

gitHooks {
    commitMsg { conventionalCommits() }
    preCommit {
        tasks("spotlessCheck", requireSuccess = true)
    }
    createHooks()
}
