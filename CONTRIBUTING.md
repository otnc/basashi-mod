# 開発ガイド (CONTRIBUTING)

馬刺しMOD の開発・ビルド・リリースに関する情報をまとめます。MOD自体の仕様は [DESIGN.md](DESIGN.md)、リリース運用の詳細は [RELEASING.md](RELEASING.md) を参照してください。

## 構成（モノレポ）

各 Minecraft バージョンを `versions/<mc>/` 以下の**独立した Gradle プロジェクト**として持ちます（Gradle / Java / ForgeGradle のバージョンが版ごとに非互換なため、単一の Gradle マルチプロジェクトにはできない）。

| バージョン | ローダー | ビルド基盤 | Java | 前提MOD |
|---|---|---|---|---|
| `versions/1.20.1` | Forge（NeoForge互換） | Architectury Loom / Gradle 8.10.2 | 17 | Architectury API |
| `versions/1.16.5` | 素 Forge | ForgeGradle 5.1 / Gradle 7.6.4 | 8 | なし |
| `versions/1.12.2` | 素 Forge | ForgeGradle 2.3 / Gradle 4.10.3 | 8 | なし |

ルートの `scripts/build.mjs`（`pnpm build`）が各版を順にビルドし、成果物を `dist/` に集約します。

## 必要環境

- **JDK 17**（1.20.1 用）と **JDK 8**（1.16.5 / 1.12.2 用）
- **PowerShell 7 以上**（推奨）… mise の自動切替（フォルダ移動でツール切替）に必要。Windows PowerShell 5.1 だと `mise: chpwd functionality requires PowerShell version 7 or higher` という警告が出る（無害だが、PS7なら出ない）。
- 初回ビルド時はネット接続が必要（依存ライブラリを取得）

JDK のバージョン管理に [mise](https://mise.jdx.dev/) を使う場合、各 `versions/<mc>/.mise.toml` で必要な Java（`temurin-17` / `temurin-8`）を固定しています。両方の toolchain を入れておきます:

```powershell
mise use -g java@temurin-17 java@temurin-8   # もしくは各版ディレクトリで mise install
```

`pnpm build` は各版の `.mise.toml` に応じて `JAVA_HOME` を自動解決します。

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
pnpm build -v 1.1.0              # 全バージョンをビルド
pnpm build --mc 1.16.5 -v 1.1.0 # 特定バージョンのみ
```

→ `dist\basashi-<MC>-<バージョン>.jar` が各版ぶん出力される。`-v` 未指定時はバージョン `dev`。

> `pnpm` が `-v` を取り込んでしまう場合は `pnpm build -- -v 1.1.0` のように `--` を挟む。

特定バージョンを Gradle で直接叩く場合（その版ディレクトリで実行）:

```powershell
cd versions/1.16.5
.\gradlew build -Pmod_version=1.1.0   # 未指定時は dev
```

> リリース時のバージョンは GitHub Actions の Release で入力する。`*-sources.jar` / `*-dev-shadow.jar` は配布用ではない。
>
> JDK が PATH に無い場合は、その版に対応する JDK を `JAVA_HOME` に向ける（例: 1.16.5 / 1.12.2 は JDK 8）:
> ```powershell
> $env:JAVA_HOME = (mise where java@temurin-8)
> $env:Path = "$env:JAVA_HOME\bin;$env:Path"
> ```

## 開発実行（ゲーム内テスト）

各版ディレクトリで実行します（1.20.1 のみ `:forge` サブプロジェクト）:

```powershell
cd versions/1.20.1; .\gradlew :forge:runClient   # 1.20.1
cd versions/1.16.5; .\gradlew runClient           # 1.16.5
cd versions/1.12.2; .\gradlew runClient           # 1.12.2
```

NeoForge での動作確認は、ビルドした 1.20.1 の jar を NeoForge 1.20.1 環境の `mods` に入れて行う。

## ディレクトリ構成

```
versions/1.20.1/   … 1.20.1（Architectury: common + forge 構成）
versions/1.16.5/   … 1.16.5（素 Forge）
versions/1.12.2/   … 1.12.2（素 Forge）
scripts/           … build.mjs（全版ビルド）, gen-items.mjs（items.png 生成）
docs/img/          … README用の画像
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
- **やること**: 全バージョンをマトリクスでビルドし、各 jar を Actions の Artifacts に14日間保管
- **使い方**: Actions タブ → Build → **Run workflow**

### release.yml — リリース発行（手動・バージョン入力）
- **動くタイミング**: 手動実行のみ
- **やること**: 入力したMODバージョンで全版をビルド → `basashi-<MC>-<MOD>.jar` を生成 → 1つの GitHub Release に全版を添付 → Modrinth / CurseForge へ各版を公開
- **使い方**: Actions タブ → Release → **Run workflow** → 対象ブランチを選び、**MODバージョン**を入力（例 `1.1.0`）

## テクスチャ・調整メモ

- テクスチャは **16×16**。同名で上書きすれば差し替え可能（高解像度も可）。配置はバージョンで異なる:
  - 1.20.1 / 1.16.5: `assets/basashi/textures/item/*.png`（単数 `item`）
  - 1.12.2: `assets/basashi/textures/items/*.png`（複数 `items`）
- 食料の数値やレシピは [DESIGN.md](DESIGN.md) と各版の JSON / `ModItems.java` を編集して調整できる（バージョン間で揃えること）。
