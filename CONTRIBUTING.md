# 開発ガイド (CONTRIBUTING)

馬刺しMOD の開発・ビルド・リリースに関する情報をまとめます。MOD自体の仕様は [DESIGN.md](DESIGN.md)、リリース運用の詳細は [RELEASING.md](RELEASING.md) を参照してください。

## 技術スタック

- Minecraft 1.20.1 / Forge（生成jarは NeoForge 1.20.1 でも動作）
- Architectury（`common` + `forge` 構成）
- Java 17 / Gradle (Architectury Loom)

## 必要環境

- **JDK 17**（必須）
- **PowerShell 7 以上**（推奨）… mise の自動切替（フォルダ移動でツール切替）に必要。Windows PowerShell 5.1 だと `mise: chpwd functionality requires PowerShell version 7 or higher` という警告が出る（無害だが、PS7なら出ない）。
- 初回ビルド時はネット接続が必要（依存ライブラリを取得）

JDK のバージョン管理に [mise](https://mise.jdx.dev/) を使う場合、プロジェクト直下の `.mise.toml` で Java 17 を固定しています:

```powershell
mise install
```

### PowerShell 7 の導入

いずれかで導入（導入後は新しい `pwsh` 端末を開く）:

```powershell
winget install Microsoft.PowerShell
```

または [GitHub Releases](https://github.com/PowerShell/PowerShell/releases) から `PowerShell-7.x-win-x64.msi` を入手してインストール。VS Code の既定ターミナルを pwsh にするには、設定 `terminal.integrated.defaultProfile.windows` を `PowerShell`（pwsh）に。

> PS 5.1 のまま使う場合、警告は `MISE_PWSH_CHPWD_WARNING=0` で抑制できる（自動切替は効かないが shims でツール解決は可能）。

## 推奨開発環境（VS Code）

エディタは **Visual Studio Code** を推奨。

- 推奨拡張:
  - **Extension Pack for Java**（`vscjava.vscode-java-pack`）… Java の補完・ビルド・デバッグ
  - **Gradle for Java**（`vscjava.vscode-gradle`）
- 統合ターミナルは **PowerShell 7 (pwsh)** を推奨（mise の自動切替が効く）。
- ワークスペース設定（`.vscode/settings.json`）でフォルダのコンパクト表示・既定ターミナル pwsh を設定可能（`.vscode/` は gitignore のためローカル限定）。

## ビルド

パッケージ管理は **pnpm**（バージョン固定なし）。初回のみ依存を入れる:

```powershell
pnpm install
```

ローカルビルド（成果物を `dist/` に出力。テスト用・リリースしない）:

```powershell
pnpm build              # gradle.properties の mod_version を使用
pnpm build -v 1.1.0     # バージョンを指定（--version でも可）
```

→ `dist\basashi-<MC>-<バージョン>.jar` が出力される（Forge / NeoForge 1.20.1 共通）。

> `pnpm` が `-v` を取り込んでしまう場合は `pnpm build -- -v 1.1.0` のように `--` を挟む。

Gradle を直接叩く場合:

```powershell
.\gradlew build                 # mod_version は gradle.properties の既定値
.\gradlew build -Pmod_version=1.1.0
```

> リリース時のバージョンは GitHub Actions の Release で入力するため、`gradle.properties` の `mod_version` は普段の既定値にすぎない。`*-sources.jar` / `*-dev-shadow.jar` は配布用ではない。
>
> JDK が PATH に無い場合は、ビルド前に `JAVA_HOME` を JDK 17 に向ける:
> ```powershell
> $env:JAVA_HOME = (mise where java@temurin-17)
> $env:Path = "$env:JAVA_HOME\bin;$env:Path"
> ```

> JDK が PATH に無い場合は、ビルド前に `JAVA_HOME` を JDK 17 に向ける:
> ```powershell
> $env:JAVA_HOME = (mise where java@temurin-17)
> $env:Path = "$env:JAVA_HOME\bin;$env:Path"
> ```

## 開発実行（ゲーム内テスト）

```powershell
.\gradlew :forge:runClient
```

NeoForge での動作確認は、ビルドした jar を NeoForge 1.20.1 環境の `mods` に入れて行う。

## ディレクトリ構成

```
common/   … ローダー非依存の本体（アイテム・イベント・レシピ・モデル・言語・テクスチャ）
forge/    … Forge固有エントリ（生成jarはNeoForge 1.20.1でも動作）
docs/img/ … README用の画像
```

## コード整形

Prettier（`prettier-plugin-java`）で Java を整形:

```powershell
pnpm format       # 整形（上書き）
pnpm format:check # チェックのみ
```

## アイテム一覧画像の再生成

テクスチャを変更したら `docs/img/items.png` を作り直す:

```powershell
pnpm items
```

## CI / リリース（GitHub Actions）

ワークフローは目的別に2本。詳細な運用は [RELEASING.md](RELEASING.md) を参照。

### build.yml — ビルド検証（手動）
- **動くタイミング**: 手動実行のみ（push では走らない）
- **やること**: `./gradlew build` で検証し、jar を Actions の Artifacts に14日間保管
- **使い方**: Actions タブ → Build → **Run workflow**

### release.yml — リリース発行（手動・バージョン入力）
- **動くタイミング**: 手動実行のみ
- **やること**: 入力したMODバージョンでビルド → `basashi-<MC>-<MOD>.jar` を生成 → タグ `v<MOD>+<MC>` を作成 → GitHub Release を発行して添付
- **使い方**: Actions タブ → Release → **Run workflow** → 対象ブランチを選び、**MODバージョン**を入力（例 `1.0.0`）。MCバージョンはそのブランチの `gradle.properties` から自動取得

## テクスチャ・調整メモ

- テクスチャ（`common/.../textures/item/*.png`）は **16×16**。同名で上書きすれば差し替え可能（32×32等の高解像度も可）。
- 食料の数値やレシピは [DESIGN.md](DESIGN.md) と各 JSON / `ModItems.java` を編集して調整できる。
