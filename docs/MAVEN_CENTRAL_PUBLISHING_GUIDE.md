# Maven Central & Sonatype Central Portal Publishing Guide

This guide provides a comprehensive, step-by-step walkthrough for publishing **KotlinFlow** (`io.github.joaonart:kotlinflow`) to **Maven Central** via the modern **Sonatype Central Portal** (`https://central.sonatype.com/`) using automated GitHub Actions CI/CD and GPG signing.

---

## Architecture Overview

KotlinFlow utilizes the official industry-standard Gradle publishing plugin:
- **Plugin**: [`com.vanniktech.maven.publish`](https://github.com/vanniktech/gradle-maven-publish-plugin)
- **Host**: `SonatypeHost.CENTRAL_PORTAL`
- **Coordinates**: `io.github.joaonart:kotlinflow:<version>`
- **Signing**: In-Memory PGP Signing with RSA 4096-bit keys
- **Automation**: GitHub Actions workflow at [`.github/workflows/publish.yml`](.github/workflows/publish.yml)

---

## Step 1: Claim Namespace on Sonatype Central

1. Create an account on [https://central.sonatype.com/](https://central.sonatype.com/).
2. Navigate to **Account** -> **Namespaces**.
3. Click **Add Namespace** and enter:
   ```text
   io.github.joaonart
   ```
4. Verify ownership using the verification code provided by Sonatype (by creating a public GitHub repository named with that code under your `joaonart` account).
5. Once verified, the namespace status will display **Verified**.

---

## Step 2: Generate Central Portal User Token

Do not use your personal portal password for CI/CD. Use a scoped User Token:

1. In [https://central.sonatype.com/](https://central.sonatype.com/), click your avatar (top right) -> **View Account**.
2. Select **Generate User Token**.
3. You will receive:
   - **Username** (a generated hash token)
   - **Password** (a generated secret token)
4. Save both values securely; you will add them to GitHub Secrets in Step 4.

---

## Step 3: Generate GPG Key Pair (RSA 4096)

> [!IMPORTANT]
> **CRITICAL GOTCHA: Use RSA, not Ed25519!**
> Modern macOS GnuPG (`brew install gnupg`) creates **Ed25519** keys by default with `gpg --gen-key`.
> Gradle and the Maven Central verification engine (BouncyCastle) **do not support Ed25519** for PGP artifact signatures and will fail with `Could not read PGP secret key`.
> You **MUST** generate an **RSA 4096-bit** key using `gpg --full-generate-key`.

### 1. Generate the RSA key

Run in Terminal:
```bash
gpg --full-generate-key
```

When prompted:
1. **Kind of key**: Select `(1) RSA and RSA` (default).
2. **Key size**: Type `4096`.
3. **Key validity**: Enter `0` (does not expire) or `2y` (2 years).
4. **Confirm validity**: Enter `y`.
5. **Real name**: Enter your name (e.g., `João Alves`).
6. **Email address**: Enter your email (e.g., `joao.alves64@gmail.com`).
7. **Comment**: Leave empty or enter `KotlinFlow Maven Central`.
8. **Passphrase**: Choose a secure passphrase and **save it** (this will be your `GPG_SIGNING_PASSPHRASE`).

### 2. Find your Key ID

List your secret keys:
```bash
gpg --list-secret-keys --keyid-format=long
```

Example output:
```text
sec   rsa4096/9876543210ABCDEF 2026-09-28 [SC]
      1234567890ABCDEF123456789876543210ABCDEF
uid                 [ultimate] João Alves <joao.alves64@gmail.com>
ssb   rsa4096/FEDCBA0123456789 2026-09-28 [E]
```

In the example above, `9876543210ABCDEF` is your **Key ID** (the 16 characters after `rsa4096/`).

### 3. Upload the Public Key to Keyservers

Maven Central verifies signatures against public keyservers. Upload your public key to both Ubuntu and OpenPGP servers:

```bash
# Replace 9876543210ABCDEF with your actual Key ID
gpg --keyserver keyserver.ubuntu.com --send-keys 9876543210ABCDEF
gpg --keyserver keys.openpgp.org --send-keys 9876543210ABCDEF
```

### 4. Export the Private Key for GitHub Actions

You can export the private key in standard ASCII-armored format or as **Base64** (recommended, as Base64 eliminates any accidental newline/space corruption when pasting into the GitHub Web UI):

**Option A (Recommended - Base64 single-line, 100% immune to newline mangling):**
```bash
/opt/homebrew/bin/gpg --armor --export-secret-keys 0C2347B9879543B9 | base64 | pbcopy
```

**Option B (Standard ASCII Armor):**
```bash
/opt/homebrew/bin/gpg --armor --export-secret-keys 0C2347B9879543B9 | pbcopy
```

The copied content is placed directly in your macOS clipboard ready to be pasted into the `GPG_SIGNING_KEY` secret.

---

## Step 4: Configure GitHub Repository Secrets

Open your repository settings:
👉 [https://github.com/joaonart/KotlinFlow/settings/secrets/actions](https://github.com/joaonart/KotlinFlow/settings/secrets/actions)

Add the following **4 Repository Secrets**:

| Secret Name | Source / Value | Description |
|---|---|---|
| `SONATYPE_TOKEN_USERNAME` | Central Portal User Token | The generated token username |
| `SONATYPE_TOKEN_PASSWORD` | Central Portal User Token | The generated token password |
| `GPG_SIGNING_KEY` | `pbcopy` from Step 3.4 | The complete ASCII-armored RSA private key block |
| `GPG_SIGNING_PASSPHRASE` | Passphrase from Step 3.1 | The passphrase protecting your GPG private key |

---

## Step 5: Publish via GitHub Actions

### Method A: Manual Trigger (Recommended for first release)

1. Go to the **Actions** tab: [https://github.com/joaonart/KotlinFlow/actions](https://github.com/joaonart/KotlinFlow/actions)
2. Select **Publish Package & Release** from the left sidebar.
3. Click **Run workflow**.
4. Enter the release version (e.g., `1.0.0`) and click **Run workflow**.

### Method B: Git Tag Release

Pushing a version tag automatically triggers compilation, testing, signing, and publishing:

```bash
git tag v1.0.0 -f
git push origin v1.0.0 -f
```

---

## What the CI Workflow Executes Automatically

1. **Test Verification**: Runs all unit test suites (`./gradlew :kotlinflow:test`).
2. **Binary Generation**:
   - `kotlinflow-release.aar` (Compiled Android library)
   - `kotlinflow-release-sources.jar` (Kotlin source code)
   - `kotlinflow-release-javadoc.jar` (API documentation generated via Dokka)
   - `pom-default.xml` (Maven POM metadata)
3. **Cryptographic Signing**: Digitally signs all artifacts with your RSA key using in-memory GPG.
4. **Bundle & Upload**: Packages the deployment ZIP bundle and sends it to the Sonatype Central Portal API (`https://central.sonatype.com/api/v1/publisher/upload`).
5. **Validation & Release**: Sonatype verifies the signature, checksums, and namespace, then automatically transitions the deployment to `PUBLISHED`.
6. **GitHub Release**: Creates a GitHub Release with version notes and attaches binary assets (`.aar`, `.jar`, `.apk`).

---

## Monitoring & Verifying the Deployment

1. Check the deployment status on Sonatype Central Portal:
   👉 [https://central.sonatype.com/publishing/deployments](https://central.sonatype.com/publishing/deployments)
2. Status transitions:
   - `VALIDATING` -> Checks POM, GPG signatures, and checksums.
   - `VALIDATED` -> Ready for distribution.
   - `PUBLISHING` -> Syncing across global Maven Central mirrors.
   - `PUBLISHED` -> Live worldwide!

> [!NOTE]
> Maven Central mirrors sync every **15 to 30 minutes**. Once published, anyone can consume your library without configuring any custom repositories:

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("io.github.joaonart:kotlinflow:1.0.0")
}
```

---

## Troubleshooting Reference

### 1. `Could not read PGP secret key`
- **Cause**: The GPG key is Ed25519 instead of RSA, or the private key block format was corrupted during copy-paste.
- **Solution**: Follow **Step 3** to generate an **RSA 4096** key using `gpg --full-generate-key`, export with `gpg --armor --export-secret-keys`, and update the `GPG_SIGNING_KEY` secret.

### 2. `401 Unauthorized` / Authentication Failed
- **Cause**: Invalid `SONATYPE_TOKEN_USERNAME` or `SONATYPE_TOKEN_PASSWORD`.
- **Solution**: Ensure you are using the generated User Token credentials from `https://central.sonatype.com/account`, NOT your personal login password.

### 3. `Signature verification failed` / `Key not found on keyservers`
- **Cause**: The public key has not propagated to keyservers yet.
- **Solution**: Re-run the send commands:
  ```bash
  gpg --keyserver keyserver.ubuntu.com --send-keys <KEY_ID>
  gpg --keyserver keys.openpgp.org --send-keys <KEY_ID>
  ```
  Wait 5–10 minutes for global keyserver replication before re-running the workflow.

### 4. `checksum mismatch at in checksum of 20 bytes`
- **Cause**: The passphrase entered in the `GPG_SIGNING_PASSPHRASE` secret is incorrect, has trailing spaces/newlines, or does not match the exported private key in `GPG_SIGNING_KEY`. BouncyCastle verifies the 20-byte SHA-1 hash of the decrypted key, and fails if the passphrase is not exact.
- **Verification Command (Run locally on macOS)**:
  Test your key and passphrase directly in your terminal (using `--pinentry-mode loopback` to avoid `Inappropriate ioctl for device`):
  ```bash
  echo "test" | /opt/homebrew/bin/gpg --batch --yes --pinentry-mode loopback --passphrase "YOUR_PASSPHRASE" --armor --detach-sign -u 0C2347B9879543B9
  ```
  If it succeeds (outputs a PGP signature block), update your GitHub Secret `GPG_SIGNING_PASSPHRASE` with that exact string (without extra spaces).
