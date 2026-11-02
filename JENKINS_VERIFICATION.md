# Jenkins automatic CI verification

## Verified run

- **Repository:** https://github.com/nousegit/DevOps-project
- **Branch:** `main`
- **Documentation commit:** `864f041c3e818198a4a0c3c5d9ad0369b42bcd60` (`docs: document Jenkins CI workflow`)
- **Push:** `git push origin main` succeeded; remote `main` was independently confirmed at the same SHA. Jenkins observed the SCM change at 00:46:24 IST on 3 Nov 2026.
- **Jenkins multibranch job:** `Daymark-CI/main`
- **Build:** `#4`, completed at 00:50 IST with `SUCCESS`.
- **Automatic trigger:** Console identifies `Started by an SCM change`. No Build Now or Scan Multibranch Pipeline Now action was used after the push.
- **Polling confirmation:** Build #4's polling log says the previously built `main` revision was `505b571cd9c58a6da689a017482b6c5aa4afb178`, the latest remote was `864f041c3e818198a4a0c3c5d9ad0369b42bcd60`, and `Changes found`.
- **Checkout:** Jenkins checked out revision `864f041c3e818198a4a0c3c5d9ad0369b42bcd60` from the repository's `main` branch.
- **Tests:** Test stage reported 5 run, 0 failures, 0 errors, 0 skipped.
- **Build:** Maven package stage ended with `BUILD SUCCESS`.
- **Artifacts:** `target/todo-app-1.0.0.jar` and `target/original-todo-app-1.0.0.jar`, each 14.44 MiB, listed in Jenkins build artifacts.

## Evidence

- `evidence/component-1/jenkins/Screenshot 2026-11-03 at 12.11.00 AM.png` — existing Multibranch repository branch-indexing log (prior evidence).
- `evidence/component-1/jenkins/jenkins-build-4-console-success.png` — genuine Jenkins console screenshot showing artifact archival and final `SUCCESS`.
- `evidence/component-1/jenkins/polling-log-excerpt.txt` — relevant lines transcribed from the Jenkins build #4 polling-log page.
- `evidence/component-1/jenkins/git-push-verification.log` — current main commit and remote ref verification output.
- The Jenkins build page and console were verified in the authenticated Jenkins browser session. Jenkins console text is not anonymously downloadable (`403 Authentication required`); the genuine screenshot is retained instead of fabricating a raw console log.
- The Jenkins Multibranch configuration page showed the repository URL and Jenkinsfile path. Its periodic indexing checkbox was off; build #4 was still automatically triggered by SCM polling, as confirmed by the build's polling log. Keep the existing trigger setup unchanged.

## Trigger delay

Build #4 started automatically after SCM detection. Jenkins first delayed GitHub API access because its anonymous API quota was over the configured threshold, then continued checkout, tests, packaging, and artifact archival successfully.
